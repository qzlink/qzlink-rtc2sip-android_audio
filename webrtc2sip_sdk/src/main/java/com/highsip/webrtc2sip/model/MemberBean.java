package com.highsip.webrtc2sip.model;
/*
 * @creator      dean_deng
 * @createTime   2019/11/29 16:30
 * @Desc         ${TODO}
 */

public class MemberBean {

    private String phoneNum;//电话号码
    private String callstate;//呼叫状态
    private String uuid;//用户uuid
    private String conference_uuid;//会议uuid

    public String getConference_uuid() {
        return conference_uuid;
    }

    public void setConference_uuid(String conference_uuid) {
        this.conference_uuid = conference_uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCallstate() {
        return callstate;
    }

    public void setCallstate(String callstate) {
        this.callstate = callstate;
    }

    public MemberBean() {
    }


    @Override
    public String toString() {
        return "MemberBean{" +
                "phoneNum='" + phoneNum + '\'' +
                ", callstate='" + callstate + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
