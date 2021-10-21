package com.highsip.webrtc2sip.common;
/*
 * @creator      dean_deng
 * @createTime   2019/9/16 18:06
 * @Desc         ${TODO}
 */


public class EnumKey {

    public enum MsgTag {
        hb(String.class),//心跳
        login(String.class),//登录
        normal_msg(String.class),//单聊

        sip_calling(String.class),//呼叫
        sip_calling_res(String.class),//呼叫回执

        sip_calling_auto(String.class),//收到离线呼叫

        sip_cancel(String.class),//取消
        sip_cancel_res(String.class),

        sip_ringing(String.class),//振铃
        sip_ringing_res(String.class),//振铃回执

        sip_connected(String.class),//连接
        sip_connected_res(String.class),//连接

        sip_disconnected(String.class),//断开连接
        sip_disconnected_res(String.class),//断开连接

        sip_rejected(String.class),//断开连接
        sip_rejected_res(String.class),//断开连接

        sip_no_response(String.class),//未响应
        sip_no_response_res(String.class),//

        sip_user_busy(String.class),//用户忙

        sip_dtmf(String.class),//功能键

        conf_join(String.class),//加入会议

        conf_hangup(String.class);//挂断会议

        private Class<?> cls;

        private MsgTag(Class<?> cls) {
            this.cls = cls;
        }

        public Class<?> getReturnClass() {
            return cls;
        }
    }

    public enum MsgType {
        text(String.class);

        private Class<?> cls;

        private MsgType(Class<?> cls) {
            this.cls = cls;
        }

        public Class<?> getReturnClass() {
            return cls;
        }
    }


}
