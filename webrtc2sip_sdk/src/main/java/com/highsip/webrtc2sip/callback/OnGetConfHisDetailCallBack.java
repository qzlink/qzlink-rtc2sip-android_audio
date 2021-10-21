package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/12/4 16:59
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.ConfDetailBean;

public interface OnGetConfHisDetailCallBack {

    /**
     *
     * @param errCode
     * @param bean
     */
    void getConfHisDetail(String errCode, ConfDetailBean bean);

}
