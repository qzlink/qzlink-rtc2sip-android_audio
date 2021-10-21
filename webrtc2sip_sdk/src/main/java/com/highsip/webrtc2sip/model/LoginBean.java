package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 16:43
 * @Desc         ${TODO}
 */


public class LoginBean {

    /**
     * errcode : 0
     * data : {"rtc":"c645a288dd6a46f895b6b24ade34a89c","balance":0,"name":"无名9976","usertoken":"8792139889069005","userid":"100029"}
     * msgid : login
     * errmsg :
     * msgtag : login
     */

    private String errcode;
    private UserBean data;
    private String msgid;
    private String errmsg;
    private String msgtag;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public UserBean getData() {
        return data;
    }

    public void setData(UserBean data) {
        this.data = data;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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

    @Override
    public String toString() {
        return "LoginBean{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                ", msgid='" + msgid + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", msgtag='" + msgtag + '\'' +
                '}';
    }
}
