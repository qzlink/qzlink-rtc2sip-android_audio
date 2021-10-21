package com.highsip.webrtc2sip.model;

import java.io.Serializable;

public class NewHostBean implements Serializable {

    private int sip_port;
    private String rtc_appid;
    private int im_tcp_port;
    private String valid_date;
    private String call_prefix;
    private String sip_ip;
    private String appid;
    private String im_ip;
    private int im_api_port;

    public int getSip_port() {
        return sip_port;
    }

    public void setSip_port(int sip_port) {
        this.sip_port = sip_port;
    }

    public String getRtc_appid() {
        return rtc_appid;
    }

    public void setRtc_appid(String rtc_appid) {
        this.rtc_appid = rtc_appid;
    }

    public int getIm_tcp_port() {
        return im_tcp_port;
    }

    public void setIm_tcp_port(int im_tcp_port) {
        this.im_tcp_port = im_tcp_port;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

    public String getCall_prefix() {
        return call_prefix;
    }

    public void setCall_prefix(String call_prefix) {
        this.call_prefix = call_prefix;
    }

    public String getSip_ip() {
        return sip_ip;
    }

    public void setSip_ip(String sip_ip) {
        this.sip_ip = sip_ip;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getIm_ip() {
        return im_ip;
    }

    public void setIm_ip(String im_ip) {
        this.im_ip = im_ip;
    }

    public int getIm_api_port() {
        return im_api_port;
    }

    public void setIm_api_port(int im_api_port) {
        this.im_api_port = im_api_port;
    }

}
