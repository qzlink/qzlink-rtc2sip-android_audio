package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/12/4 17:02
 * @Desc         ${TODO}
 */


import java.util.List;

public class ConfDetailBean {

    private String sponsor;

    private ConfBean mConfBean;

    private List<MemberBean> list;

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public ConfBean getConfBean() {
        return mConfBean;
    }

    public void setConfBean(ConfBean confBean) {
        mConfBean = confBean;
    }

    public List<MemberBean> getList() {
        return list;
    }

    public void setList(List<MemberBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ConfDetailBean{" +
                "sponsor='" + sponsor + '\'' +
                ", mConfBean=" + mConfBean +
                ", list=" + list +
                '}';
    }
}
