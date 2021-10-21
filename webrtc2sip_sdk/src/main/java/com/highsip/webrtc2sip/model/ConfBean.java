package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/12/4 11:06
 * @Desc         ${TODO}
 */


public class ConfBean {

    private String call_date;
    private String conference_uuid;
    private String conference_name;
    private String gmt_create;
    private int run_time;
    private String confNo;
    private String roomID;

    public String getConfNo() {
        return confNo;
    }

    public void setConfNo(String confNo) {
        this.confNo = confNo;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(String gmt_create) {
        this.gmt_create = gmt_create;
    }

    public int getRun_time() {
        return run_time;
    }

    public void setRun_time(int run_time) {
        this.run_time = run_time;
    }

    public String getCall_date() {
        return call_date;
    }

    public void setCall_date(String call_date) {
        this.call_date = call_date;
    }

    public String getConference_uuid() {
        return conference_uuid;
    }

    public void setConference_uuid(String conference_uuid) {
        this.conference_uuid = conference_uuid;
    }

    public String getConference_name() {
        return conference_name;
    }

    public void setConference_name(String conference_name) {
        this.conference_name = conference_name;
    }

    @Override
    public String toString() {
        return "ConfBean{" +
                "call_date='" + call_date + '\'' +
                ", conference_uuid='" + conference_uuid + '\'' +
                ", conference_name='" + conference_name + '\'' +
                ", gmt_create='" + gmt_create + '\'' +
                ", run_time='" + run_time + '\'' +
                '}';
    }
}
