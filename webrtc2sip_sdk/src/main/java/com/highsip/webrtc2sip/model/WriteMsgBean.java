package com.highsip.webrtc2sip.model;

import com.highsip.webrtc2sip.socket.SendBack;

import java.io.Serializable;

public class WriteMsgBean implements Serializable {

    private String msg;
    private SendBack sendBack;

    public WriteMsgBean() {

    }

    public WriteMsgBean(String msg, SendBack sendBack) {
        this.msg = msg;
        this.sendBack = sendBack;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SendBack getSendBack() {
        return sendBack;
    }

    public void setSendBack(SendBack sendBack) {
        this.sendBack = sendBack;
    }
}
