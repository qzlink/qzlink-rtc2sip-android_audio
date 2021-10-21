package com.highsip.webrtc2sip.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadExecutor {
	private final static int LOW_THREAD_COUNT = 2;
	
	private static final Executor LOW_EXECUTOR = Executors.newFixedThreadPool(LOW_THREAD_COUNT,new LowPriorityThreadFactory());
	private static final Executor NORMAL_EXECUTOR = Executors.newSingleThreadExecutor();
	
	private ThreadExecutor(){
	}

	/**
	 * 执行普通的异步操作(优先级比正常稍低)
	 */
	public static void executeLow(Runnable command){
		LOW_EXECUTOR.execute(command);
	}
	/**
	 * 执行普通的异步操作(优先级正常)
	 */
	public static void executeNormal(Runnable command){
		NORMAL_EXECUTOR.execute(command);
	}
	
	private static class LowPriorityThreadFactory implements ThreadFactory {
	    public Thread newThread(Runnable r) {
	       Thread t = new Thread(r);
	       t.setPriority(Thread.NORM_PRIORITY-1);
	       return t;
	    }
	}
	public static Executor getExecutor(){
		return LOW_EXECUTOR;
	}
}