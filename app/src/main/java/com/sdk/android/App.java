package com.sdk.android;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 14:50
 * @Desc         ${TODO}
 */


import android.app.Activity;
import android.app.Application;

import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.sdk.android.cache.AppCache;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static App instance;

    private static List<Activity> lists = new ArrayList<>();

    public static synchronized App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化SDK
        WebRtc2SipInterface.init(this);

        AppCache.setInstance(getApplicationContext());
    }


    public void addActivity(Activity activity) {
        if (!lists.contains(activity))
            lists.add(activity);
    }

    public void clearActivity() {
        if (lists != null) {
            for (Activity activity : lists) {
                activity.finish();
            }
            lists.clear();
        }
    }

}
