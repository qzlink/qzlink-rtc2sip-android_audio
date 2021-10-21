package com.sdk.android.utils;
/*
 * @creator      dean_deng
 * @createTime   2019/9/23 11:17
 * @Desc         ${TODO}
 */


import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static final String TIME_MS_FORMAT = "mm:ss";

    private static final String TIME_HMS_FORMAT = "HH:mm:ss";

    public static String getMSTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat spf = new SimpleDateFormat(TIME_MS_FORMAT);
        String datetime = spf.format(date);
        return datetime;
    }

    public static String getHMSTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat spf = new SimpleDateFormat(TIME_HMS_FORMAT);
        String datetime = spf.format(date);
        return datetime;
    }
}
