package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/11/26 9:50
 * @Desc         ${TODO}
 */


public interface OnGetConfCallBack {

    /**
     *
     * @param errCode
     * @param errMsg
     * @param confNo
     */
    void onGetConf(String errCode, String errMsg, String confNo);

}
