package com.highsip.webrtc2sip.util;
/*
 * @creator      dean_deng
 * @createTime   2019/8/27 16:01
 * @Desc         ${TODO}
 */


import android.util.Log;

public class WebRtc2SipLogUtils {

    private static final String TAG = "WEBRTC2SIP";

    private static boolean debug = true;

    /**
     * 打印提示日志
     *
     * @param msg
     */
    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * 打印数据日志
     *
     * @param msg
     */
    public static void i(String msg) {
        if (debug)
            Log.i(TAG, msg);
    }

}
