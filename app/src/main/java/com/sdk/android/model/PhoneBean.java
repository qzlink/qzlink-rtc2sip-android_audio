package com.sdk.android.model;
/*
 * @creator      dean_deng
 * @createTime   2019/6/13 14:58
 * @Desc         ${TODO}
 */


public class PhoneBean {

    private String name;        //联系人姓名
    private String telPhone;    //电话号码
    private String letters;//显示拼音的首字母
    private boolean isChoosed;
    private int type;//1是加号，0不是
    private String callstate;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCallstate() {
        return callstate;
    }

    public void setCallstate(String callstate) {
        this.callstate = callstate;
    }

    public PhoneBean(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PhoneBean(String telPhone) {
        this.telPhone = telPhone;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public PhoneBean() {
    }

    public PhoneBean(String name, String telPhone) {
        this.name = name;
        this.telPhone = telPhone;
    }


    @Override
    public String toString() {
        return "PhoneBean{" +
                "name='" + name + '\'' +
                ", telPhone='" + telPhone + '\'' +
                ", letters='" + letters + '\'' +
                ", isChoosed=" + isChoosed +
                ", type=" + type +
                ", callstate='" + callstate + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

}
