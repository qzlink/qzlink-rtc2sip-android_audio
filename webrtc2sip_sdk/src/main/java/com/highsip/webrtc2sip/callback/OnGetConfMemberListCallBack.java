package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/11/29 16:34
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.MemberBean;

import java.util.ArrayList;

public interface OnGetConfMemberListCallBack {

    /**
     *
     * @param errCode
     * @param errMsg
     * @param list
     */
    void onGetConfMemberList(String errCode, String errMsg, ArrayList<MemberBean> list);

}
