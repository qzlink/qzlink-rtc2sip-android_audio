package com.sdk.android;
/*
 * @creator      dean_deng
 * @createTime   2019/9/18 16:47
 * @Desc         ${TODO}
 */


import android.os.Environment;

import java.io.File;

public interface Constants {

    String COUNTRY_CODE_JSON = "code.json";

    public static final String SIPIP = "sipip";
    public static final String PHONENUMBER = "phonenumber";
    public static final String ISOPENSIP = "IsOpenSip";
    public static final String CALLTYPE = "calltype";
    public static final String INTENT_CUSTOMER_NAME = "intent_customer_name";
    public static final String INTENT_CUSTOMERID = "intent_customerid";

    String ONE = "1";
    String TWO = "2";
    String THREE = "3";
    String FOUR = "4";
    String FIVE = "5";
    String SIX = "6";
    String SEVEN = "7";
    String EIGHT = "8";
    String NINE = "9";
    String ZERO = "0";
    String STAR = "*";
    String JING = "#";

    String ISO = "iso";
    String CODE = "code";

    String TYPE = "type";
    String CALLTYPE_NAME = "calltype_name";
    String PHONE = "phone";
    String SMALLNUM = "smallNum";
    String SIP = "sip";
    String DATA = "data";

    String ACCOUNT = "account";
    String PASSWORD = "password";
    String APPID = "appid";

    String COUNTRY = "country";
    String CN_NAME = "cn_name";
    String AREA_CODE = "area_code";
    String COUNTRY_CODE = "country_code";

    String PATH_AUDIO_RECORDING = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "WebRtc2Sip" + File.separator + "audio";

    int TYPE_CONF_SPONSOR = 0;

    int TYPE_CONF_ADD_MEMBER = 1;

}
