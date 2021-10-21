package com.sdk.android.model;
/*
 * @creator      dean_deng
 * @createTime   2019/9/18 17:18
 * @Desc         ${TODO}
 */


public class CodeBean {

    private String country_us;
    private String country_cn;
    private String iso;
    private String countryCode;
    private String letters;//显示拼音的首字母

    public CodeBean(){}

    public CodeBean(String country_us, String country_cn, String iso, String countryCode) {
        this.country_us = country_us;
        this.country_cn = country_cn;
        this.iso = iso;
        this.countryCode = countryCode;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getCountry_us() {
        return country_us;
    }

    public void setCountry_us(String country_us) {
        this.country_us = country_us;
    }

    public String getCountry_cn() {
        return country_cn;
    }

    public void setCountry_cn(String country_cn) {
        this.country_cn = country_cn;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "CodeBean{" +
                "country_us='" + country_us + '\'' +
                ", country_cn='" + country_cn + '\'' +
                ", iso='" + iso + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
