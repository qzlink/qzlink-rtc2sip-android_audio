package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/11/26 18:48
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.SipBean;

public interface SipReceiveCallBack {

    void onReceiveCall(SipBean bean);

//    void onReceiveOfflineCall(SipBean bean);

}
