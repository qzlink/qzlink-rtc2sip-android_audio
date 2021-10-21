package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 15:10
 * @Desc         ${TODO}
 */


import java.util.List;

public class HostListBean {

    private String code;
    private int size;
    private List<HostBean> list;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<HostBean> getList() {
        return list;
    }

    public void setList(List<HostBean> list) {
        this.list = list;
    }

}
