package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/17 15:36
 * @Desc         ${TODO}
 */

public class SipBean {

    private String errcode;
    private String caller;
    private String appid;
    private String callee;
    private String errmsg;
    private String msgtag;
    private String isSip;
    private String userid;
    private String callType;
    private String roomID;
    private String direction;
    private String code;
    private String msg;
    private String sign;
    private String msgid;
    private String callerIP;
    private String callerNick;
    private String sys_time;
    private String originIP;
    private String uid;
    private String token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SipBean(String errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getMsgtag() {
        return msgtag;
    }

    public void setMsgtag(String msgtag) {
        this.msgtag = msgtag;
    }

    public String getIsSip() {
        return isSip;
    }

    public void setIsSip(String isSip) {
        this.isSip = isSip;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SipBean() {
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getCallerIP() {
        return callerIP;
    }

    public void setCallerIP(String callerIP) {
        this.callerIP = callerIP;
    }

    public String getCallerNick() {
        return callerNick;
    }

    public void setCallerNick(String callerNick) {
        this.callerNick = callerNick;
    }

    public String getSys_time() {
        return sys_time;
    }

    public void setSys_time(String sys_time) {
        this.sys_time = sys_time;
    }

    public String getOriginIP() {
        return originIP;
    }

    public void setOriginIP(String originIP) {
        this.originIP = originIP;
    }

    @Override
    public String toString() {
        return "SipBean{" +
                "errcode='" + errcode + '\'' +
                ", caller='" + caller + '\'' +
                ", appid='" + appid + '\'' +
                ", callee='" + callee + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", msgtag='" + msgtag + '\'' +
                ", isSip='" + isSip + '\'' +
                ", userid='" + userid + '\'' +
                ", callType='" + callType + '\'' +
                ", roomID='" + roomID + '\'' +
                ", direction='" + direction + '\'' +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", sign='" + sign + '\'' +
                ", msgid='" + msgid + '\'' +
                ", callerIP='" + callerIP + '\'' +
                ", callerNick='" + callerNick + '\'' +
                ", sys_time='" + sys_time + '\'' +
                ", originIP='" + originIP + '\'' +
                ", token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
