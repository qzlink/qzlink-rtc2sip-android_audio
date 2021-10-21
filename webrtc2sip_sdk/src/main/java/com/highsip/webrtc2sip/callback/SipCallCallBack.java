package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 17:53
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.SipBean;

public interface SipCallCallBack {

    void onSipCall(SipBean bean,String roomID,String uid,String token);

}
