package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/11/27 9:54
 * @Desc         ${TODO}
 */


public interface OnLoginStatusCallBack {

    /**
     * @param errorCode errorCode: 0(登录成功）, -1(登录失败)，-2(账号不合法)
     * @param errorMsg  错误信息
     */
    void onLoginStatus(String errorCode, String errorMsg);

}
