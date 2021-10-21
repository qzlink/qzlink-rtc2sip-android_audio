package com.highsip.webrtc2sip.util;

/**
 * Twitter的分布式自增ID雪花算法 snowflake
 * 
 * Twitter的分布式雪花算法 SnowFlake <br>
 * 每秒自增生成26个万个可排序的ID<br>
 * 1、twitter的SnowFlake生成ID能够按照时间有序生成<br>
 * 2、SnowFlake算法生成id的结果是一个64bit大小的整数<br>
 * 3、分布式系统内不会产生重复id（用有datacenterId和machineId来做区分）<br>
 * datacenterId（分布式）（服务ID 1，2，3.....） 每个服务中写死<br>
 * machineId（用于集群） 机器ID 读取机器的环境变量MACHINEID 部署时每台服务器ID不一样<br>
 **/
public class SnowFlake {

	/**
	 * 起始的时间戳
	 */
	private final static long START_STMP = 1480166465631L;

	/**
	 * 每一部分占用的位数
	 */
	private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
	private final static long MACHINE_BIT = 5; // 机器标识占用的位数
	private final static long DATACENTER_BIT = 5;// 数据中心占用的位数

	/**
	 * 每一部分的最大值
	 */
	private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
	private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
	private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

	/**
	 * 每一部分向左的位移
	 */
	private final static long MACHINE_LEFT = SEQUENCE_BIT;
	private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
	private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

	private static long datacenterId = 10l; // 数据中心
	private static long machineId = 10l; // 机器标识
	

	private long sequence = 0L; // 序列号
	private long lastStmp = -1L;// 上一次时间戳

	public SnowFlake(long datacenterId1, long machineId1) {

		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
		}
		if (datacenterId1 > 100)
			datacenterId = datacenterId1;
		if (machineId1 > 100)
			machineId = machineId1;
	}

	/**
	 * 产生下一个ID
	 *
	 * @return
	 */
	public synchronized long nextId() {
		long currStmp = getNewstmp();
		if (currStmp < lastStmp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currStmp == lastStmp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & MAX_SEQUENCE;
			// 同一毫秒的序列数已经达到最大
			if (sequence == 0L) {
				currStmp = getNextMill();
			}
		} else {
			// 不同毫秒内，序列号置为0
			sequence = 0L;
		}

		lastStmp = currStmp;

		return (currStmp - START_STMP) << TIMESTMP_LEFT // 时间戳部分
				| datacenterId << DATACENTER_LEFT // 数据中心部分
				| machineId << MACHINE_LEFT // 机器标识部分
				| sequence; // 序列号部分
	}

	private long getNextMill() {
		long mill = getNewstmp();
		while (mill <= lastStmp) {
			mill = getNewstmp();
		}
		return mill;
	}

	private long getNewstmp() {
		return System.currentTimeMillis();
	}

	private static SnowFlake instance = null;

	public static SnowFlake me(String ip) {
		if (instance == null) {
			if (null == ip || "".equals(ip)) {
				ip = "112.332.124.156";
			}
			ip = ip.replace(".", "");
			long datacenter_id = Long.parseLong(ip.substring(0, 5));
			long machine_id = Long.parseLong(ip.substring(6, ip.length()));
			instance = new SnowFlake(datacenter_id, machine_id);
		}
		return instance;
	}

	public long getRoomID() {
		if (null != instance) {
			return instance.nextId();
		} else {
			me(null);
			return instance.nextId();
		}
	}

}