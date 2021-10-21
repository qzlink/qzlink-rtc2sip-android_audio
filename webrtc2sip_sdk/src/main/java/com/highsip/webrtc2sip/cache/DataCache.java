package com.highsip.webrtc2sip.cache;
/*
 * @creator      dean_deng
 * @createTime   2019/8/27 16:08
 * @Desc         ${TODO}
 */


import android.content.Context;
import android.text.TextUtils;

import com.highsip.webrtc2sip.common.EnumKey;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.util.DESUtil;
import com.highsip.webrtc2sip.util.Md5Utils;
import com.highsip.webrtc2sip.util.WebRtc2SipLogUtils;

import org.json.JSONObject;

public class DataCache {

    private Context mContext;

    private static DataCache mInstance;

    private String appid;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    private DataCache(Context context) {
        this.mContext = context;
    }

    public static DataCache getInstance() {
        return mInstance;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 在ApplicationCache中初始化
     *
     * @param context
     */
    public static void setInstance(Context context) {
        mInstance = new DataCache(context);
    }


    /**
     * userid
     */
    private String userid;

    public String getUserid() {
        return userid;
    }

    /**
     * token
     */
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * token
     */
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * rtc 声网appid
     */
    private String rtc;

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    /**
     * balance 余额
     */
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * name 昵称
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * usertoken
     */
    private String usertoken;

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    /**
     * size tcplist
     */
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * host
     */
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * port
     */
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * uuid
     */
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * password
     */
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * roomID
     */
    private String roomID;

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    /**
     * web_port
     */
    private int web_port;

    public int getWeb_port() {
        return web_port;
    }

    public void setWeb_port(int web_port) {
        this.web_port = web_port;
    }

    /**
     * 登录状态
     */
    private boolean isLogin;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    /**
     * 心跳
     */
    public String getHBReq() {
        try {
            JSONObject obj = new JSONObject();
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.USERID, DataCache.getInstance().getUserid());
            obj.put(IMConstants.MSGTAG, EnumKey.MsgTag.hb.toString());
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 登录
     */
    public String getLoginReq() {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String msgtag = EnumKey.MsgTag.login.toString();
            String uuid = WebRtc2SipInterface.getUUid();
//            uuid = Md5Utils.md5(uuid + appid);
            String appId = appid;
            obj.put(IMConstants.APPID, appId);
            obj.put(IMConstants.PASSWORD, DESUtil.encrypt(password, ""));
            obj.put(IMConstants.MSGTAG, msgtag);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.OS, IMConstants.ANDROID);
            obj.put(IMConstants.UUID, uuid);
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 发送消息
     *
     * @param targetid
     * @param content
     * @param msgType
     * @return
     */
    public String getSendMsgReq(String targetid, String content, String msgType) {
        try {
            DataCache dataCache = DataCache.getInstance();
            //用户id
            String userid = dataCache.getUserid();
            //用户Nick
            String nick = dataCache.getName();
            //发送时间
            String time = System.currentTimeMillis() + "";
            //消息id
            String msgid = getRandomID();
            //消息种类
            String msgtag = EnumKey.MsgTag.normal_msg.toString();
            //消息加密标志
            String sign = msgid + msgtag + userid + targetid;

            String usertoken = dataCache.getUsertoken();

            JSONObject obj = new JSONObject();
            obj.put(IMConstants.TARGETID, targetid);
            obj.put(IMConstants.ACK, "1");
            obj.put(IMConstants.SIGN, DESUtil.encrypt(sign, usertoken));
            obj.put(IMConstants.MSGID, msgid);
            obj.put(IMConstants.RAW, "");
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.CONTENT, content);
            obj.put(IMConstants.NICK, nick);
            obj.put(IMConstants.TIME, time);
            obj.put(IMConstants.MSGTAG, msgtag);//消息种类：单聊，群聊
            obj.put(IMConstants.MSGTYPE, msgType);//消息类型:文本text
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            return "";
        }
    }


    public String sipCall(String callee, String callType, String isSip, String direction, String roomID) {
        appidNull();
        try {
            DataCache dataCache = DataCache.getInstance();
            String userid = dataCache.getUserid();
            String name = dataCache.getName();
            String msgtag = EnumKey.MsgTag.sip_calling.toString();
            String sign = Md5Utils.md5(appid + msgtag + userid);
            String host = dataCache.getHost();
//            String roomID = userid + SnowFlake.me(host).getRoomID();
            if (TextUtils.isEmpty(roomID)) {
                roomID = userid + getRandomRoomID();
            }
            JSONObject obj = new JSONObject();
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgtag);
            obj.put(IMConstants.CALLER, userid);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            obj.put(IMConstants.CALLERNICK, name);
            obj.put(IMConstants.DIRECTION, direction);
            obj.put(IMConstants.CALLER_IP, "");
            obj.put(IMConstants.ORIGINIP, "");
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipRinging(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_ringing.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);//对方userid
            obj.put(IMConstants.CALLEE, callee);//本人userid
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
//            obj.put(IMConstants.CALLERNICK, getName());
//            obj.put(IMConstants.CALLER_IP, "");
//            obj.put(IMConstants.ORIGINIP, "");
            if (IMConstants.YES.equals(isSip)) {
                obj.put(IMConstants.DIRECTION, direction);
            }
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipConnent(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_connected.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            if (IMConstants.YES.equals(isSip)) {
                obj.put(IMConstants.DIRECTION, direction);
            }
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipDisconnect(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_disconnected.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            if (IMConstants.YES.equals(isSip)) {
                obj.put(IMConstants.DIRECTION, direction);
                obj.put(IMConstants.EVENT, "APP");
            }
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipCancel(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_cancel.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            if (IMConstants.YES.equals(isSip)) {
                obj.put(IMConstants.DIRECTION, direction);
                obj.put(IMConstants.EVENT, "APP");
            }
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipReject(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_rejected.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            if (IMConstants.YES.equals(isSip)) {
                obj.put(IMConstants.DIRECTION, direction);
                obj.put(IMConstants.EVENT, "APP");
            }
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipUserBusy(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_user_busy.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.MSGID, getRandomID());
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.SIGN, sign);
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sipDTMF(String caller, String callee, String callType, String isSip, String roomID, String dtmf) {
        appidNull();
        try {
            JSONObject obj = new JSONObject();
            String userid = getUserid();
            String msgTag = EnumKey.MsgTag.sip_dtmf.toString();
            String sign = Md5Utils.md5(appid + msgTag + userid);
            obj.put(IMConstants.APPID, appid);
            obj.put(IMConstants.USERID, userid);
            obj.put(IMConstants.MSGTAG, msgTag);
            obj.put(IMConstants.CALLER, caller);
            obj.put(IMConstants.CALLEE, callee);
            obj.put(IMConstants.CALLTYPE, callType);
            obj.put(IMConstants.ISSIP, isSip);
            obj.put(IMConstants.ROOMID, roomID);
            obj.put(IMConstants.DTMF, dtmf);
            obj.put(IMConstants.SIGN, sign);
            return obj.toString() + IMConstants.SUFFIX_JSON;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void appidNull() {
        if (TextUtils.isEmpty(appid)) {
            WebRtc2SipLogUtils.d("appid is null");
            return;
        }
    }

    /**
     * 消息id
     *
     * @return
     */
    public String getRandomID() {
        //当前时间的毫秒数+6位随机数
        String num = (int) ((Math.random() * 9 + 1) * 100000) + "";
        return System.currentTimeMillis() + num;
    }

    /**
     * 4位随机数
     *
     * @return
     */
    public String getRandomRoomID() {
        //当前时间的毫秒数+6位随机数
        String num = (int) ((Math.random() * 9 + 1) * 1000) + "";
        return System.currentTimeMillis() + num;
    }


}
