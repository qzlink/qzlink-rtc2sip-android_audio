package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/11/22 15:27
 * @Desc         ${TODO}
 */


public class ResponseBean {

    /**
     * errcode : 0
     * data : {"code":0}
     * errmsg :
     */

    private String errcode;
    private String errmsg;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
