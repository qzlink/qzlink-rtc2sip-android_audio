package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/12/5 9:27
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.ConfBean;

public interface OnGetConfInfoByRoomIDCallBack {

    /**
     *
     * @param errCode
     * @param confBean
     */
    void onGetConfInfo(String errCode, ConfBean confBean);

}
