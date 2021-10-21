package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/9/9 17:53
 * @Desc         ${TODO}
 */


public interface ConnectIMCallBack {

    /**
     * @param statusCode 1（连接成功）
     *                   2（连接关闭）
     *                   0（连接错误）
     *                   3 (连接中)
     *                   4 (连接超时)
     */
    void onConnectStatus(int statusCode);


}
