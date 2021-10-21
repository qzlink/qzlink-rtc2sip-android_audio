package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 16:43
 * @Desc         ${TODO}
 */


public class UserBean {

    private String rtc;
    private double balance;
    private String name;
    private String usertoken;
    private String userid;

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "rtc='" + rtc + '\'' +
                ", balance=" + balance +
                ", name='" + name + '\'' +
                ", usertoken='" + usertoken + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
