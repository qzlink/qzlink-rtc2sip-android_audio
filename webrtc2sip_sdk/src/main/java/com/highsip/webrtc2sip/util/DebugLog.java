package com.highsip.webrtc2sip.util;

import android.util.Log;

public class DebugLog {

    private static final boolean debug = false;

    public static void v(String TAG, String msg) {
        if (debug)
            Log.v(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (debug)
            Log.d(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (debug)
            Log.i(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (debug)
            Log.e(TAG, msg);
    }
}
