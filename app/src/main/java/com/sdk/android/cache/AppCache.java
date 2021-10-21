package com.sdk.android.cache;
/*
 * @creator      dean_deng
 * @createTime   2019/11/21 15:10
 * @Desc         ${TODO}
 */


import android.content.Context;

import com.sdk.android.model.PhoneBean;

import java.util.ArrayList;
import java.util.List;

public class AppCache {

    private final Context mContext;
    private static AppCache mInstance;

    private AppCache(Context context) {
        this.mContext = context;
    }

    public static AppCache getInstance() {
        return mInstance;
    }

    /**
     * 在ApplicationCache中初始化
     *
     * @param context
     */
    public static void setInstance(Context context) {
        mInstance = new AppCache(context);
    }

    public Context getContext() {
        return mContext;
    }

    public List<PhoneBean> mPhoneList = new ArrayList<>();

    public List<PhoneBean> getPhoneList() {
        return mPhoneList;
    }

    public void setPhoneList(List<PhoneBean> phoneList) {
        this.mPhoneList = phoneList;
    }

    public void addPhoneList(List<PhoneBean> phoneList){
        this.mPhoneList.addAll(phoneList);
    }

    /**
     * 聊天时长
     */
    private long second;

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }


    /**
     * 音视频
     */
    private boolean isCallConnect;

    public boolean isCallConnect() {
        return isCallConnect;
    }

    public void setCallConnect(boolean callConnect) {
        isCallConnect = callConnect;
    }

}
