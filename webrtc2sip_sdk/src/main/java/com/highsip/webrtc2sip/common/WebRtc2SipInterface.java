package com.highsip.webrtc2sip.common;
/*
 * @creator      dean_deng
 * @createTime   2019/8/27 15:52
 * @Desc         ${TODO}
 */


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.highsip.webrtc2sip.cache.DataCache;
import com.highsip.webrtc2sip.callback.BindPhoneCallBack;
import com.highsip.webrtc2sip.callback.ConnectIMCallBack;
import com.highsip.webrtc2sip.callback.OnConfMemberChangeCallBack;
import com.highsip.webrtc2sip.callback.OnConfMemberStatusCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfDiDCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfHisDetailCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfHistCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfInfoByRoomIDCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfMemberListCallBack;
import com.highsip.webrtc2sip.callback.OnLoginStatusCallBack;
import com.highsip.webrtc2sip.callback.OnReportBugCallBack;
import com.highsip.webrtc2sip.callback.SipCallCallBack;
import com.highsip.webrtc2sip.callback.SipReceiveCallBack;
import com.highsip.webrtc2sip.callback.SponsorConfCallBack;
import com.highsip.webrtc2sip.executor.ThreadExecutor;
import com.highsip.webrtc2sip.listener.OnReceiveMessageListener;
import com.highsip.webrtc2sip.model.ConfBean;
import com.highsip.webrtc2sip.model.ConfDetailBean;
import com.highsip.webrtc2sip.model.HostBean;
import com.highsip.webrtc2sip.model.HostListBean;
import com.highsip.webrtc2sip.model.LoginBean;
import com.highsip.webrtc2sip.model.MemberBean;
import com.highsip.webrtc2sip.model.NewHostBean;
import com.highsip.webrtc2sip.model.ResponseBean;
import com.highsip.webrtc2sip.model.SipBean;
import com.highsip.webrtc2sip.model.UserBean;
import com.highsip.webrtc2sip.socket.Request;
import com.highsip.webrtc2sip.socket.SendBack;
import com.highsip.webrtc2sip.socket.SocketConnectManage;
import com.highsip.webrtc2sip.socket.SocketListener;
import com.highsip.webrtc2sip.util.GetPostUtil;
import com.highsip.webrtc2sip.util.JSONUtil;
import com.highsip.webrtc2sip.util.Utils;
import com.highsip.webrtc2sip.util.WebRtc2SipLogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WebRtc2SipInterface {

    private static List<HostBean> mHostBeanList = new ArrayList<>();

    private static int index = 0;

    private static ConnectIMCallBack mOnConnectIMCallBack;

    private static SipCallCallBack mOnSipCallCallBack;

    private static OnReceiveMessageListener mOnReceiveMessageListener = null;

    private static boolean isReGetHost;

    private static SipReceiveCallBack mSipReceiveCallBack;

    private static OnLoginStatusCallBack mOnLoginStatusCallBack;

    private static OnConfMemberChangeCallBack mOnMemberChangeCallBack;

    private static OnConfMemberStatusCallBack mOnConfMemberStatusCallBack;

    public static void setOnConfMemberStatusCallBack(OnConfMemberStatusCallBack onConfMemberStatusCallBack) {
        mOnConfMemberStatusCallBack = onConfMemberStatusCallBack;
    }

    public static void setOnMemberChangeCallBack(OnConfMemberChangeCallBack onMemberChangeCallBack) {
        mOnMemberChangeCallBack = onMemberChangeCallBack;
    }

    public static void setOnLoginStatusCallBack(OnLoginStatusCallBack onLoginStatusCallBack) {
        mOnLoginStatusCallBack = onLoginStatusCallBack;
    }

    public static void setSipReceiveCallBack(SipReceiveCallBack sipReceiveCallBack) {
        mSipReceiveCallBack = sipReceiveCallBack;
        mOnReceiveMessageListener = null;
    }

    public static void setOnSipCallCallBack(SipCallCallBack onSipCallCallBack) {
        mOnSipCallCallBack = onSipCallCallBack;
    }

    public static void setOnConnectIMCallBack(ConnectIMCallBack onConnectIMCallBack) {
        mOnConnectIMCallBack = onConnectIMCallBack;
    }

    public static void setOnReceiveMessageListener(OnReceiveMessageListener onReceiveMessageListener) {
        mOnReceiveMessageListener = onReceiveMessageListener;
    }

    private static SocketListener socketListener = new SocketListener() {
        @Override
        public void onOpen(Socket socket, Request request) {
            WebRtc2SipLogUtils.i("onOpen");
            DataCache.getInstance().setHost(request.getHost());
            DataCache.getInstance().setPort(request.getPort());

            mOnConnectIMCallBack.onConnectStatus(1);

            login();
        }

        @Override
        public void onMessage(Socket socket, String text) {
            try {
                receiveMsg(text);
            } catch (InterruptedException e) {
                e.printStackTrace();
                WebRtc2SipLogUtils.i("error1");
            } catch (JSONException e) {
                e.printStackTrace();
                WebRtc2SipLogUtils.i("error2");
            }
        }

        @Override
        public void onIdle(Socket socket) {
        }

        @Override
        public void onClosing(Socket socket) {
        }

        @Override
        public void onClosed(Socket socket) {
            WebRtc2SipLogUtils.i("onClosed");
            mOnConnectIMCallBack.onConnectStatus(2);
        }

        @Override
        public void onFailure(Socket socket) {
            WebRtc2SipLogUtils.i("onFailure");
            DataCache.getInstance().setLogin(false);

            mOnConnectIMCallBack.onConnectStatus(0);

            handlerConnectFailSwitchLine();
        }
    };


    /**
     * 处理连接失败后切换线路继续连接
     */
    private static void handlerConnectFailSwitchLine() {
        //连接失败，使用下一组ip,端口进行连接
        index++;
        int size = DataCache.getInstance().getSize();
        if (index < size) {
            connect2server();
        } else {
            //超出了tcp list的大小
            mHostBeanList.clear();
            if (isReGetHost) {
                //重新获取ip,端口
                connectIMServersWithIPOrAppId();
            }
            //如果还是连接失败则不再获取
            isReGetHost = false;
        }
    }

    private static void receiveMsg(String message) throws InterruptedException, JSONException {
        if (!TextUtils.isEmpty(message)) {
            WebRtc2SipLogUtils.i("--------------------------WebRtcInterface------------------------\n" + message);
            Thread.sleep(500);
            JSONObject object = new JSONObject(message);
            String msgTag = JSONUtil.getString(object, IMConstants.MSGTAG, "");
            if (EnumKey.MsgTag.login.toString().equals(msgTag)) {
                LoginBean loginBean = JSONUtil.getLoginBean(message);
                if (loginBean != null) {
                    String errmsg = loginBean.getErrmsg();
                    String errcode = loginBean.getErrcode();
                    if (IMConstants.SUCCESS.equals(loginBean.getErrcode())) {
                        //登录成功
                        DataCache.getInstance().setLogin(true);

                        UserBean data = loginBean.getData();
                        if (data != null) {
//                                    WebRtc2SipLogUtils.d("loginBean=" + loginBean.toString());
                            String userid = data.getUserid();
                            DataCache.getInstance().setUserid(userid);
                            DataCache.getInstance().setBalance(data.getBalance());
                            DataCache.getInstance().setName(data.getName());
                            DataCache.getInstance().setRtc(data.getRtc());
                            DataCache.getInstance().setUsertoken(data.getUsertoken());
                        }
                    }
                    if (mOnLoginStatusCallBack != null)
                        mOnLoginStatusCallBack.onLoginStatus(errcode, errmsg);
                }
            } else if (EnumKey.MsgTag.sip_calling_res.toString().equals(msgTag)) {//呼叫回执
                SipBean bean = JSONUtil.getSipBean(message);
                if (bean != null) {
                    WebRtc2SipLogUtils.i("sip_calling_res=" + message + "---" + bean.toString());
                    WebRtc2SipLogUtils.d("calling res");
                    if (mOnSipCallCallBack != null)
                        mOnSipCallCallBack.onSipCall(bean, DataCache.getInstance().getRoomID(),
                                DataCache.getInstance().getUid(), DataCache.getInstance().getToken());
                }
            } else if (EnumKey.MsgTag.sip_calling.toString().equals(msgTag)) {
                String tempRoomID = DataCache.getInstance().getRoomID();
                final SipBean bean = JSONUtil.getSipBean(message);
                if (bean != null) {
                    if (TextUtils.isEmpty(tempRoomID)) {//临时roomID为空，则说明未收到来电
                        if (mSipReceiveCallBack != null) {
                            ThreadExecutor.executeNormal(new Runnable() {
                                @Override
                                public void run() {
                                    int web_port = DataCache.getInstance().getWeb_port();
                                    String host = DataCache.getInstance().getHost();
                                    if (TextUtils.isEmpty(host) && web_port < 0) {
                                        WebRtc2SipLogUtils.d("not logged in");
                                        return;
                                    }
                                    String param = "roomid" + "=" + bean.getRoomID();
                                    String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/api/app/getTokenForRoomid", param);
                                    Log.e("----response---", response + "");
                                    if (!TextUtils.isEmpty(response)) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            String token = (object.getString("token"));
                                            String roomID = (object.getString("roomID"));
                                            String uid = (object.getString("uid"));
                                            bean.setToken(token);
                                            bean.setRoomID(roomID);
                                            bean.setUid(uid);
                                            mSipReceiveCallBack.onReceiveCall(bean);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            mSipReceiveCallBack.onReceiveCall(bean);
                        }
                        sipRinging(bean.getCaller(), bean.getCallee()
                                , bean.getCallType(), bean.getIsSip(), bean.getRoomID(), bean.getDirection());
                    } else {//存在，则发送用户正忙
                        sipUserBusy(bean.getCaller(), bean.getCallee()
                                , bean.getCallType(), bean.getIsSip(), bean.getRoomID(), bean.getDirection());
                    }
                }
            } else if (EnumKey.MsgTag.sip_calling_auto.toString().equals(msgTag)) {
                String tempRoomID = DataCache.getInstance().getRoomID();
                SipBean bean = JSONUtil.getSipBean(message);
                if (bean != null) {
                    if (TextUtils.isEmpty(tempRoomID)) {//临时roomID为空，则说明未收到来电
                        if (mSipReceiveCallBack != null) {
//                            mSipReceiveCallBack.onReceiveOfflineCall(bean);
                        }
                    } else {//存在，则发送用户正忙
                        sipUserBusy(bean.getCaller(), bean.getCallee()
                                , bean.getCallType(), bean.getIsSip(), bean.getRoomID(), bean.getDirection());
                    }
                }
            } else if (EnumKey.MsgTag.conf_join.toString().equals(msgTag)) {
                if (object != null) {
                    if (object.has(IMConstants.ADDMEMBER) && !object.isNull(IMConstants.ADDMEMBER)) {
                        JSONObject obj = object.getJSONObject(IMConstants.ADDMEMBER);
                        if (obj != null) {
                            MemberBean memberBean = new MemberBean();
                            if (obj.has(IMConstants.CALLER_ID_NUMBER) && !obj.isNull(IMConstants.CALLER_ID_NUMBER)) {
                                String callerId_number = obj.getString(IMConstants.CALLER_ID_NUMBER);
                                memberBean.setPhoneNum(callerId_number);
                            }
                            if (obj.has(IMConstants.CALL_STATE) && !obj.isNull(IMConstants.CALL_STATE)) {
                                String callstate = obj.getString(IMConstants.CALL_STATE);
                                memberBean.setCallstate(callstate);
                            }
                            if (obj.has(IMConstants.UUID) && !obj.isNull(IMConstants.UUID)) {
                                memberBean.setUuid((obj.getString(IMConstants.UUID)));
                            }
                            if (obj.has(IMConstants.CONFERENCE_UUID) && !obj.isNull(IMConstants.CONFERENCE_UUID)) {
                                memberBean.setConference_uuid((obj.getString(IMConstants.CONFERENCE_UUID)));
                            }
                            if (mOnMemberChangeCallBack != null)
                                mOnMemberChangeCallBack.onMemberAdd(memberBean);
                        }
                    }
                }
            } else if (EnumKey.MsgTag.conf_hangup.toString().equals(msgTag)) {
                String delMemberUUID = JSONUtil.getString(object, IMConstants.DELMEMBER_UUID, "");
                if (mOnConfMemberStatusCallBack != null)
                    mOnConfMemberStatusCallBack.onStatus(delMemberUUID);
            }
            if (mOnReceiveMessageListener != null) {
                mOnReceiveMessageListener.onReceiveMessage(message);
            }
        }
    }

    /**
     * @param context
     */
    public static void init(Context context) {
        DataCache.setInstance(context);
    }


    /**
     * @param uuid sdk账号
     */
    public static void setUUidAndPassword(String uuid, String pwd) {
        DataCache instance = DataCache.getInstance();
        if (instance == null) {
            WebRtc2SipLogUtils.d("uninitialized WebRtc2Sip sdk");
            return;
        }
        instance.setUuid(uuid);
        instance.setPassword(pwd);
    }

    public static String getUUid() {
        return DataCache.getInstance().getUuid();
    }

    /**
     * 登录之后返回
     */
    public static String getUserid() {
        return DataCache.getInstance().getUserid();
    }

    /**
     * 连接服务器
     */
    public static void connectIMServers() {
        DataCache instance = DataCache.getInstance();
        if (instance == null) {
            WebRtc2SipLogUtils.d("uninitialized WebRtc2Sip sdk");
            return;
        }
        String uuid = instance.getUuid();
        if (TextUtils.isEmpty(uuid)) {
            WebRtc2SipLogUtils.d("uuid is empty");
            return;
        }
        if (SocketConnectManage.isConnection()) {
            login();
        } else {
            ThreadExecutor.executeNormal(new Runnable() {
                @Override
                public void run() {
                    String param = IMConstants.APPID + "=" + DataCache.getInstance().getAppid();
                    String result = GetPostUtil.sendGet("http://api.qzlink.com:9898/getIpList", param);
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            WebRtc2SipLogUtils.i(result);
                            HostListBean hostListBean = JSONUtil.getHostListBean(result);
//                        WebRtc2SipLogUtils.d(hostListBean.toString());
                            if (hostListBean != null) {
                                String code = hostListBean.getCode();
                                if (IMConstants.SUCCESS.equals(code)) {
                                    List<HostBean> list = hostListBean.getList();
                                    if (!Utils.listIsEmpty(list)) {
                                        int size = hostListBean.getSize();
                                        DataCache.getInstance().setSize(size);
                                        //存ip,端口
                                        for (HostBean hostBean : list) {
                                            mHostBeanList.add(hostBean);
                                        }
                                    }
                                    //重置index
                                    index = 0;
                                    //连接服务器
                                    connect2server();
                                } else {
                                    if (mOnConnectIMCallBack != null) {
                                        mOnConnectIMCallBack.onConnectStatus(0);
                                    }
                                }
                            }
                        } else {
                            if (mOnConnectIMCallBack != null) {
                                mOnConnectIMCallBack.onConnectStatus(4);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void connectIMServersWithIPOrAppId() {
        DataCache instance = DataCache.getInstance();
        if (instance == null) {
            WebRtc2SipLogUtils.d("uninitialized WebRtc2Sip sdk");
            return;
        }
        String uuid = instance.getUuid();
        if (TextUtils.isEmpty(uuid)) {
            WebRtc2SipLogUtils.d("uuid is empty");
            return;
        }

        if (SocketConnectManage.isConnection()) {
            login();
        } else {
            ThreadExecutor.executeNormal(new Runnable() {
                @Override
                public void run() {
                    String appIdOrHost = DataCache.getInstance().getAppid();
                    if (appIdOrHost == null)
                        return;
                    String param;
                    if (appIdOrHost.contains(".")) {
                        param = IMConstants.NEWAPPID + "=" + appIdOrHost;
                    } else {
                        param = IMConstants.NEWAPPID + "=" + appIdOrHost;
                    }
                    String result = GetPostUtil.sendGet("http://api.qzlink.com:10089/api/app/imGetAppInfo", param);
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            WebRtc2SipLogUtils.i(result);
                            com.alibaba.fastjson.JSONObject dataJson = com.alibaba.fastjson.JSONObject.parseObject(result);
                            String code = dataJson.getString("code");
                            if (!TextUtils.isEmpty(code) && IMConstants.SUCCESS.equals(code)) {
                                String data = dataJson.getString("data");
                                if (data != null) {
                                    NewHostBean newHostBean = com.alibaba.fastjson.
                                            JSONObject.parseObject(data, NewHostBean.class);

                                    if (newHostBean != null) {
                                        DataCache.getInstance().setSize(1);

                                        HostBean hostBean = new HostBean();
                                        hostBean.setHost(newHostBean.getIm_ip());
                                        hostBean.setPort(newHostBean.getIm_tcp_port());
                                        hostBean.setWeb_port(newHostBean.getIm_api_port());
                                        mHostBeanList.add(hostBean);

                                        DataCache.getInstance().setRtc(newHostBean.getRtc_appid());
                                    }
                                    index = 0;
                                    //连接服务器
                                    connect2server();
                                }
                            } else {
                                if (mOnConnectIMCallBack != null) {
                                    mOnConnectIMCallBack.onConnectStatus(0);
                                }
                            }
                        } else {
                            if (mOnConnectIMCallBack != null) {
                                mOnConnectIMCallBack.onConnectStatus(0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void connect2server() {
        if (!Utils.listIsEmpty(mHostBeanList)) {
            int size = DataCache.getInstance().getSize();
            WebRtc2SipLogUtils.i("connect2server");
            if (size > index) {
                HostBean hostBean = mHostBeanList.get(index);
                if (hostBean != null) {
                    String host = hostBean.getHost();
                    int port = hostBean.getPort();
                    int web_port = hostBean.getWeb_port();
                    WebRtc2SipLogUtils.i("m ip" + host);
                    WebRtc2SipLogUtils.i("m port" + port);
                    WebRtc2SipLogUtils.i("m web_port" + web_port);

                    DataCache.getInstance().setWeb_port(web_port);

                    Request.Builder builder = new Request.Builder()
                            .setHost(host, port);
                    SocketConnectManage.connect(builder.build(), socketListener);
                }
            }
        }
    }

    public static void login() {
        if (SocketConnectManage.isConnection()) {

            String loginReq = DataCache.getInstance().getLoginReq();

            SocketConnectManage.sendMessage(loginReq);
        }
    }

    public static boolean getConnectStatus() {
        return SocketConnectManage.isConnection();
    }

    /**
     * 发送消息
     *
     * @param targetid 目标ID
     * @param content  消息内容
     * @param msgType  消息类型
     */
    public static void sendMsg(String targetid, String content, String msgType) {
        String sendMsgReq = DataCache.getInstance().getSendMsgReq(targetid, content, msgType);
        SocketConnectManage.sendMessage(sendMsgReq);
    }

    /**
     * @param phoneNum  电话号码
     * @param isOpenSip 是否为Sip呼叫
     * @param callType  AUDIO为音频，VIDEO为视频
     */
    public static void sipCall(String phoneNum, boolean isOpenSip, final String callType) {
        boolean connectStatus = SocketConnectManage.isConnection();
        boolean login = DataCache.getInstance().isLogin();
        WebRtc2SipLogUtils.i("connectStatus=" + connectStatus + "loginStatus=" + login);
        WebRtc2SipLogUtils.i("phoneNum=" + phoneNum + "isOpenSip=" + isOpenSip + "callType=" + callType);
        if (connectStatus && login) {
            String isSip = "";
            if (isOpenSip) {
                isSip = IMConstants.YES;
            } else {
                isSip = IMConstants.NO;
            }
            if (phoneNum.contains("+")) {
                phoneNum = phoneNum.replace("+", "");
            }

            final String finalPhoneNum = phoneNum;
            final String finalIsSip = isSip;
            ThreadExecutor.executeNormal(new Runnable() {
                @Override
                public void run() {
                    int web_port = DataCache.getInstance().getWeb_port();
                    String host = DataCache.getInstance().getHost();
                    if (TextUtils.isEmpty(host) && web_port < 0) {
                        WebRtc2SipLogUtils.d("not logged in");
                        return;
                    }
                    String param = "caller" + "=" + DataCache.getInstance().getUuid() + "&callee" + "=" + finalPhoneNum + "&deviceType" + "=" + "app";
                    String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/api/app/getRoomID", param);
                    if (!TextUtils.isEmpty(response)) {
                        WebRtc2SipLogUtils.i("response=" + response);
                        try {
                            String errCode = "";
                            String errMsg = "";
                            String confNo = "";
                            String roomID = "";
                            String uid = "";
                            String token = "";
                            JSONObject object = new JSONObject(response);
                            if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                                errCode = (object.getString(IMConstants.ERRCODE));
                            }
                            if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                                errMsg = (object.getString(IMConstants.ERRMSG));
                            }
                            if (object.has(IMConstants.CONFNO) && !object.isNull(IMConstants.CONFNO)) {
                                confNo = (object.getString(IMConstants.CONFNO));
                            }
                            roomID = (object.getString("roomID"));
                            uid = (object.getString("uid"));
                            token = (object.getString("token"));
                            DataCache.getInstance().setUid(uid);
                            DataCache.getInstance().setToken(token);
                            DataCache.getInstance().setRoomID(roomID);
                            String req = DataCache.getInstance().sipCall(finalPhoneNum, callType,
                                    finalIsSip, IMConstants.OUT, roomID);
//                            Log.e("----req----",req);
                            WebRtc2SipLogUtils.i(req);
                            SocketConnectManage.sendMessage(req, new SendBack() {
                                @Override
                                public void onBack(int state) {
                                    if (mOnSipCallCallBack != null && state == 0) {
                                        mOnSipCallCallBack.onSipCall(new SipBean("-1", "呼叫失败，请稍后再试"), "", "", "");
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        DataCache.getInstance().setUid("");
                        DataCache.getInstance().setToken("");
                        DataCache.getInstance().setRoomID("");
                        String req = DataCache.getInstance().sipCall(finalPhoneNum, callType, finalIsSip, IMConstants.OUT, "");
                        WebRtc2SipLogUtils.i(req);
                        SocketConnectManage.sendMessage(req, new SendBack() {
                            @Override
                            public void onBack(int state) {
                                if (mOnSipCallCallBack != null && state == 0) {
                                    mOnSipCallCallBack.onSipCall(new SipBean("-1", "呼叫失败，请稍后再试"), "", "", "");
                                }
                            }
                        });
//                        if (mOnSipCallCallBack != null) {
//                            mOnSipCallCallBack.onSipCall(new SipBean("-1", "网络连接错误，请稍后再试"), "", "", "");
//                        }
                    }
                }
            });


        } else {
            if (mOnSipCallCallBack != null) {
                mOnSipCallCallBack.onSipCall(new SipBean("-2", "网络连接错误，请稍后再试"), "", "", "");
            }
            if (!connectStatus) {
                reconnectTcp();
            }
            if (!login) {
                String loginReq = DataCache.getInstance().getLoginReq();
                WebRtc2SipLogUtils.i("重连成功，开始登录==" + loginReq);
                WebRtc2SipLogUtils.d("sdk relogin");
                SocketConnectManage.sendMessage(loginReq);
            }
        }
    }

    /**
     * @param caller    主叫
     * @param callee    被叫
     * @param callType  呼叫类型
     * @param isSip     是否Sip呼叫
     * @param roomID    房间号
     * @param direction 呼入呼出
     */
    public static void sipAnswerCall(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipConnent(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    public static void sipDisconnect(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipDisconnect(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    public static void sipCancel(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipCancel(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    public static void sipRinging(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipRinging(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    public static void sipReject(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipReject(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    public static void sipUserBusy(String caller, String callee, String callType, String isSip, String roomID, String direction) {
        String json = DataCache.getInstance().sipUserBusy(caller, callee, callType, isSip, roomID, direction);
        SocketConnectManage.sendMessage(json);
    }

    /**
     * sip发送DTMF消息
     *
     * @param caller   主叫
     * @param callee   被叫
     * @param callType 呼叫类型
     * @param isSip    是否Sip呼叫
     * @param roomID   房间号
     * @param dtmf     dtmf消息
     */
    public static void sipDTMF(String caller, String callee, String callType, String isSip, String roomID, String dtmf) {
        String json = DataCache.getInstance().sipDTMF(caller, callee, callType, isSip, roomID, dtmf);
        SocketConnectManage.sendMessage(json);
    }

    public static void setSipRoomID(String roomID) {
        DataCache.getInstance().setRoomID(roomID);
    }

    public static String getAgoraAppId() {
        String rtc = DataCache.getInstance().getRtc();
        if (TextUtils.isEmpty(rtc)) {
            WebRtc2SipLogUtils.d("agora app id is null");
            return null;
        }
        return rtc;
    }

    public static void setAppID(String appId) {
        DataCache.getInstance().setAppid(appId);
    }

    /**
     * @param limit 重连的最大次数
     */
    public static void setMaxReconnectionLimit(int limit) {
        SocketConnectManage.setMaxReconnectCount(limit);
    }

    /**
     * 重连
     */
    public static void reconnectTcp() {
        SocketConnectManage.reconnect();
    }

    /**
     * 断开连接
     */
    public static void disconnectTcp() {
        SocketConnectManage.disconnect();
    }

    public static void getSmallNum(final String userid, final BindPhoneCallBack bindPhoneCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                String param = IMConstants.USERID + "=" + userid;
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/getBindPhone", param);
                if (!TextUtils.isEmpty(response)) {
                    String number = "";
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.NUMBER) && !object.isNull(IMConstants.NUMBER)) {
                            number = (object.getString(IMConstants.NUMBER));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bindPhoneCallBack != null) {
                        bindPhoneCallBack.getBindPhone(number);
                    }
                } else {
                    if (bindPhoneCallBack != null) {
                        bindPhoneCallBack.getBindPhone("");
                    }
                }
            }
        });
    }

    /**
     * @param onGetConfCallBack
     */
    public static void getConfNo(final OnGetConfCallBack onGetConfCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/getConfNo", "");
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        String confNo = "";
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                            errCode = (object.getString(IMConstants.ERRCODE));
                        }
                        if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                            errMsg = (object.getString(IMConstants.ERRMSG));
                        }
                        if (object.has(IMConstants.CONFNO) && !object.isNull(IMConstants.CONFNO)) {
                            confNo = (object.getString(IMConstants.CONFNO));
                        }
                        if (onGetConfCallBack != null) {
                            onGetConfCallBack.onGetConf(errCode, errMsg, confNo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (onGetConfCallBack != null) {
                        onGetConfCallBack.onGetConf(IMConstants.ERROR, "网络连接错误，请稍后再试", "");
                    }
                }

            }
        });
    }

    /**
     * @param confNo
     * @param phoneList
     * @param type
     * @param sponsorConfCallBack
     */
    public static void sponsorConf(final String confNo, final List<String> phoneList, final String type, final SponsorConfCallBack sponsorConfCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                String userid = DataCache.getInstance().getUserid();
                String destNum = "";
                String prefix = !IMConstants.SIP.equals(type) ? "9186" : "";
                for (int i = 0; i < phoneList.size(); i++) {
                    String phone = prefix + phoneList.get(i);
                    phone = phone.replace(" ", "");
                    destNum += phone + ",";
                }
                String params = IMConstants.CONFNO + "=" + confNo
                        + "&" + IMConstants.USERID + "=" + userid
                        + "&" + IMConstants.TYPE + "=" + type
                        + "&" + IMConstants.DESTNUM + "=" + destNum;
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/confAddMember", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("sponsorConf=" + response);
                    try {
                        ResponseBean responseBean = JSONUtil.getResponseBean(response);
                        if (responseBean != null) {
                            if (sponsorConfCallBack != null) {
                                sponsorConfCallBack.onSponsorConf(responseBean.getErrcode(), responseBean.getErrmsg());
                            }
                        }
                    } catch (Exception e) {

                    }
                } else {
                    if (sponsorConfCallBack != null) {
                        sponsorConfCallBack.onSponsorConf(IMConstants.ERROR, "网络连接错误，请稍后再试");
                    }
                }
            }
        });
    }

    /**
     * @param confNo
     * @param onGetConfDiDCallBack
     */
    public static void getConfDiD(final String confNo, final OnGetConfDiDCallBack onGetConfDiDCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String params = IMConstants.CONFNO + "=" + confNo;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/getConfDID", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        String did = "";
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                            errCode = (object.getString(IMConstants.ERRCODE));
                        }
                        if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                            errMsg = (object.getString(IMConstants.ERRMSG));
                        }
                        if (object.has(IMConstants.INFO) && !object.isNull(IMConstants.INFO)) {
                            JSONArray jsonArray = object.getJSONArray(IMConstants.INFO);
                            if (jsonArray != null) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject != null) {
                                    if (jsonObject.has(IMConstants.DID) && !jsonObject.isNull(IMConstants.DID)) {
                                        did = (jsonObject.getString(IMConstants.DID));
                                    }
                                }
                            }
                        }
                        if (onGetConfDiDCallBack != null) {
                            onGetConfDiDCallBack.getConfDiD(did, errCode, errMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (onGetConfDiDCallBack != null) {
                        onGetConfDiDCallBack.getConfDiD("", IMConstants.ERROR, "网络连接错误，请稍后再试");
                    }
                }

            }
        });
    }

    /**
     * @param confNo
     * @param callBack
     */
    public static void getConfMemberList(final String confNo, final OnGetConfMemberListCallBack callBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String appid = DataCache.getInstance().getAppid();
                String params = IMConstants.CONFNO + "=" + confNo + "&" + IMConstants.APPID + "=" + appid;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/confMemberList", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        ArrayList<MemberBean> list = new ArrayList<>();

                        JSONObject object = new JSONObject(response);
                        if (object != null) {
                            if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                                errCode = (object.getString(IMConstants.ERRCODE));
                            }
                            if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                                errMsg = (object.getString(IMConstants.ERRMSG));
                            }
                            if (object.has(IMConstants.DATA) && !object.isNull(IMConstants.DATA)) {
                                JSONObject obj = object.getJSONObject(IMConstants.DATA);
                                if (obj != null) {
                                    if (obj.has(IMConstants.DATA) && !obj.isNull(IMConstants.DATA)) {
                                        JSONObject o = obj.getJSONObject(IMConstants.DATA);
                                        if (obj != null) {
                                            if (o.has(IMConstants.LIST) && !o.isNull(IMConstants.LIST)) {
                                                JSONArray array = o.getJSONArray(IMConstants.LIST);
                                                if (array != null) {
                                                    for (int i = 0; i < array.length(); i++) {
                                                        JSONObject jsonObject = array.getJSONObject(i);
                                                        if (jsonObject != null) {
                                                            MemberBean memberBean = new MemberBean();
                                                            if (jsonObject.has(IMConstants.CALLER_ID_NUMBER) && !jsonObject.isNull(IMConstants.CALLER_ID_NUMBER)) {
                                                                String callerId_number = (jsonObject.getString(IMConstants.CALLER_ID_NUMBER));
                                                                memberBean.setPhoneNum(callerId_number);
                                                            }
                                                            if (jsonObject.has(IMConstants.CALL_STATE) && !jsonObject.isNull(IMConstants.CALL_STATE)) {
                                                                String callstate = (jsonObject.getString(IMConstants.CALL_STATE));
                                                                memberBean.setCallstate(callstate);
                                                            }
                                                            if (jsonObject.has(IMConstants.UUID) && !jsonObject.isNull(IMConstants.UUID)) {
                                                                String uuid = (jsonObject.getString(IMConstants.UUID));
                                                                memberBean.setUuid(uuid);
                                                            }
                                                            list.add(memberBean);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        WebRtc2SipLogUtils.i(list.toString());
                        if (callBack != null) {
                            callBack.onGetConfMemberList(errCode, errMsg, list);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        WebRtc2SipLogUtils.d("parsing error");
                    }
                } else {
                    if (callBack != null) {
                        callBack.onGetConfMemberList(IMConstants.ERROR, "网络连接错误，请稍后再试", null);
                    }
                }

            }
        });
    }

    public static void reportBug(final String phone, final String desc, final OnReportBugCallBack onReportBugCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
//                String params = "phone=" + phone + "&desc=" + desc;
                String params = IMConstants.PHONE + "=" + phone
                        + "&" + IMConstants.DESC + "=" + desc;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/reportBug", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                            errCode = (object.getString(IMConstants.ERRCODE));
                        }
                        if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                            errMsg = (object.getString(IMConstants.ERRMSG));
                        }

                        if (onReportBugCallBack != null) {
                            onReportBugCallBack.onReportBug(errCode, errMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (onReportBugCallBack != null) {
                        onReportBugCallBack.onReportBug(IMConstants.ERROR, "网络连接错误，请稍后再试");
                    }
                }

            }
        });
    }


    /**
     * @param pageNumber
     * @param pageSize
     * @param orderDirection
     * @param onGetConfHistCallBack
     */
    public static void confMemberHisPage(final String pageNumber, final String pageSize, final String orderDirection, final OnGetConfHistCallBack onGetConfHistCallBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String appid = DataCache.getInstance().getAppid();
                String userid = DataCache.getInstance().getUserid();
                String params = IMConstants.APPID + "=" + appid
                        + "&" + IMConstants.PAGENUMBER + "=" + pageNumber
                        + "&" + IMConstants.PAGESIZE + "=" + pageSize
                        + "&" + IMConstants.USERID + "=" + userid
                        + "&" + IMConstants.ORDERDIRECTION + "=" + orderDirection;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/confMemberHisPage", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        boolean lastPage = true;
                        JSONObject object = new JSONObject(response);
                        List<ConfBean> list = new ArrayList<>();
                        if (object != null) {
                            if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                                errCode = (object.getString(IMConstants.ERRCODE));
                            }
                            if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                                errMsg = (object.getString(IMConstants.ERRMSG));
                            }
                            if (object.has(IMConstants.INFO) && !object.isNull(IMConstants.INFO)) {
                                JSONObject obj = object.getJSONObject(IMConstants.INFO);
                                if (obj != null) {
                                    if (obj.has(IMConstants.DATA) && !obj.isNull(IMConstants.DATA)) {
                                        JSONObject o = obj.getJSONObject(IMConstants.DATA);
                                        if (o != null) {
                                            if (o.has(IMConstants.LASTPAGE) && !o.isNull(IMConstants.LASTPAGE)) {
                                                lastPage = o.getBoolean(IMConstants.LASTPAGE);
                                            }
                                            if (o.has(IMConstants.LIST) && !o.isNull(IMConstants.LIST)) {
                                                JSONArray array = o.getJSONArray(IMConstants.LIST);
                                                if (array != null) {
                                                    for (int i = 0; i < array.length(); i++) {
                                                        JSONObject jsonObject = new JSONObject(array.getString(i));
                                                        ConfBean confBean = new ConfBean();
                                                        if (jsonObject != null) {
                                                            if (jsonObject.has(IMConstants.CALL_DATE) && !jsonObject.isNull(IMConstants.CALL_DATE)) {
                                                                confBean.setCall_date((jsonObject.getString(IMConstants.CALL_DATE)));
                                                            }
                                                            if (jsonObject.has(IMConstants.CONFERENCE_NAME) && !jsonObject.isNull(IMConstants.CONFERENCE_NAME)) {
                                                                confBean.setConference_name((jsonObject.getString(IMConstants.CONFERENCE_NAME)));
                                                            }
                                                            if (jsonObject.has(IMConstants.CONFERENCE_UUID) && !jsonObject.isNull(IMConstants.CONFERENCE_UUID)) {
                                                                confBean.setConference_uuid((jsonObject.getString(IMConstants.CONFERENCE_UUID)));
                                                            }
                                                            list.add(confBean);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (onGetConfHistCallBack != null) {
                            onGetConfHistCallBack.getConfHis(errCode, lastPage, list);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (onGetConfHistCallBack != null) {
                        onGetConfHistCallBack.getConfHis(IMConstants.ERROR, false, null);
                    }
                }

            }
        });
    }

    /**
     * @param conferenceUUID 会议uuid
     * @param callBack
     */
    public static void getConfHisDetail(final String conferenceUUID, final OnGetConfHisDetailCallBack callBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String appid = DataCache.getInstance().getAppid();
                String params = IMConstants.CONFERENCEUUID + "=" + conferenceUUID
                        + "&" + IMConstants.APPID + "=" + appid;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/confHisDetail", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        ConfDetailBean bean = new ConfDetailBean();
                        String errCode = "";
                        String errMsg = "";
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                            errCode = (object.getString(IMConstants.ERRCODE));
                        }
                        if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                            errMsg = (object.getString(IMConstants.ERRMSG));
                        }
                        if (object.has(IMConstants.INFO) && !object.isNull(IMConstants.INFO)) {
                            JSONObject obj = object.getJSONObject(IMConstants.INFO);
                            if (obj != null) {
                                if (obj.has(IMConstants.DATA) && !obj.isNull(IMConstants.DATA)) {
                                    JSONObject o = obj.getJSONObject(IMConstants.DATA);
                                    if (o != null) {
                                        if (o.has(IMConstants.SPONSOR) && !o.isNull(IMConstants.SPONSOR)) {
                                            bean.setSponsor((o.getString(IMConstants.SPONSOR)));
                                        }
                                        if (o.has(IMConstants.CONFERENCE) && !o.isNull(IMConstants.CONFERENCE)) {
                                            JSONObject jsonObject = o.getJSONObject(IMConstants.CONFERENCE);
                                            if (jsonObject != null) {
                                                ConfBean confBean = new ConfBean();
                                                if (jsonObject.has(IMConstants.GMT_CREATE) && !jsonObject.isNull(IMConstants.GMT_CREATE)) {
                                                    confBean.setGmt_create((jsonObject.getString(IMConstants.GMT_CREATE)));
                                                }
                                                if (jsonObject.has(IMConstants.RUN_TIME) && !jsonObject.isNull(IMConstants.RUN_TIME)) {
                                                    confBean.setRun_time(jsonObject.getInt(IMConstants.RUN_TIME));
                                                }
                                                bean.setConfBean(confBean);
                                            }
                                        }
                                        if (o.has(IMConstants.MEMBERLIST) && !o.isNull(IMConstants.MEMBERLIST)) {
                                            JSONArray jsonArray = o.getJSONArray(IMConstants.MEMBERLIST);
                                            if (jsonArray != null) {
                                                List<MemberBean> memberBeanList = new ArrayList<>();
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    if (jsonObject != null) {
                                                        MemberBean memberBean = new MemberBean();
                                                        if (jsonObject.has(IMConstants.CALLER_ID_NUMBER) && !jsonObject.isNull(IMConstants.CALLER_ID_NUMBER)) {
                                                            memberBean.setPhoneNum((jsonObject.getString(IMConstants.CALLER_ID_NUMBER)));
                                                        }
                                                        memberBeanList.add(memberBean);
                                                        bean.setList(memberBeanList);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        WebRtc2SipLogUtils.i("errCode=" + errCode + " bean=" + bean.toString());
                        if (callBack != null)
                            callBack.getConfHisDetail(errCode, bean);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (callBack != null)
                        callBack.getConfHisDetail(IMConstants.ERROR, null);
                }
            }
        });
    }

    /**
     * @param roomid   房间号
     * @param callBack
     */
    public static void getConfInfoByRoomID(final String roomid, final OnGetConfInfoByRoomIDCallBack callBack) {
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                int web_port = DataCache.getInstance().getWeb_port();
                String host = DataCache.getInstance().getHost();
                String appid = DataCache.getInstance().getAppid();
                String params = IMConstants.APPID + "=" + appid
                        + "&" + IMConstants.ROOMID + "=" + roomid;
                if (TextUtils.isEmpty(host) && web_port < 0) {
                    WebRtc2SipLogUtils.d("not logged in");
                    return;
                }
                String response = GetPostUtil.sendGet("http://" + host + ":" + web_port + "/getConfInfoByRoomID", params);
                if (!TextUtils.isEmpty(response)) {
                    WebRtc2SipLogUtils.i("response=" + response);
                    try {
                        String errCode = "";
                        String errMsg = "";
                        JSONObject object = new JSONObject(response);
                        if (object.has(IMConstants.ERRCODE) && !object.isNull(IMConstants.ERRCODE)) {
                            errCode = (object.getString(IMConstants.ERRCODE));
                        }
                        if (object.has(IMConstants.ERRMSG) && !object.isNull(IMConstants.ERRMSG)) {
                            errMsg = (object.getString(IMConstants.ERRMSG));
                        }
                        if (object.has(IMConstants.INFO) && !object.isNull(IMConstants.INFO)) {
                            JSONObject obj = object.getJSONObject(IMConstants.INFO);
                            if (obj != null) {
                                ConfBean confBean = new ConfBean();
                                if (obj.has(IMConstants.CONFNO) && !obj.isNull(IMConstants.CONFNO)) {
                                    confBean.setConfNo((obj.getString(IMConstants.CONFNO)));
                                }
                                if (obj.has(IMConstants.CONFUUID) && !obj.isNull(IMConstants.CONFUUID)) {
                                    confBean.setConference_uuid((obj.getString(IMConstants.CONFUUID)));
                                }
                                if (callBack != null) {
                                    callBack.onGetConfInfo(errCode, confBean);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (callBack != null) {
                        callBack.onGetConfInfo(IMConstants.ERROR, null);
                    }
                }

            }
        });
    }

}
