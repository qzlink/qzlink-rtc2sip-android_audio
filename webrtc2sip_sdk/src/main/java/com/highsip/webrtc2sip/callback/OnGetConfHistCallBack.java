package com.highsip.webrtc2sip.callback;
/*
 * @creator      dean_deng
 * @createTime   2019/12/4 11:14
 * @Desc         ${TODO}
 */


import com.highsip.webrtc2sip.model.ConfBean;

import java.util.List;

public interface OnGetConfHistCallBack {

    /**
     *
     * @param errCode 错误码
     * @param lastPage 是否最后一页
     * @param list 会议记录
     */
    void getConfHis(String errCode,boolean lastPage, List<ConfBean> list);

}
