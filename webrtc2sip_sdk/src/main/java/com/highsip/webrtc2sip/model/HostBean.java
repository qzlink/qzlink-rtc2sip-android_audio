package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 15:08
 * @Desc         ${TODO}
 */


import java.io.Serializable;

public class HostBean implements Serializable {

    private int balance;
    private String host;
    private long id;
    private int port;
    private int web_port;

    public int getWeb_port() {
        return web_port;
    }

    public void setWeb_port(int web_port) {
        this.web_port = web_port;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
