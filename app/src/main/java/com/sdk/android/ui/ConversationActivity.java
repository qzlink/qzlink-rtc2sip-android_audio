package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/11/19 11:42
 * @Desc         ${TODO}
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.callback.OnConfMemberChangeCallBack;
import com.highsip.webrtc2sip.callback.OnConfMemberStatusCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfMemberListCallBack;
import com.highsip.webrtc2sip.callback.OnReportBugCallBack;
import com.highsip.webrtc2sip.callback.SponsorConfCallBack;
import com.highsip.webrtc2sip.common.EnumKey;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.listener.OnReceiveMessageListener;
import com.highsip.webrtc2sip.model.MemberBean;
import com.highsip.webrtc2sip.util.JSONUtil;
import com.highsip.webrtc2sip.util.Md5Utils;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.cache.AppCache;
import com.sdk.android.model.PhoneBean;
import com.sdk.android.service.FloatVideoWindowService;
import com.sdk.android.ui.adapter.ConversationAdapter;
import com.sdk.android.utils.DateTimeUtil;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.CallTypeDialog;
import com.sdk.android.widget.ReportBugDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener
        , ConversationAdapter.OnItemClickListener, OnReceiveMessageListener, OnConfMemberStatusCallBack,
        OnConfMemberChangeCallBack {

    private static final String TAG = ConversationActivity.class.getSimpleName();

    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int PERMISSION_REQ_ID = 22;

    private static final String CONNECT_CODE = "183";

    private static final int REQ_ADD_CONTACTS = 0x11;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    //在此执行定时操作
                    getConfMemberList();
                    break;
                default:
                    break;
            }
        }
    };

    private TextView mTvSendError;
    private TextView mTvConversationTime;
    private RecyclerView mRvConversation;
    private List<PhoneBean> allDatas = new ArrayList<>();
    private List<PhoneBean> datas = new ArrayList<>();
    private ConversationAdapter mAdapter;
    private ImageView mIvHangup;
    private RtcEngine mRtcEngine;

    private String mConnectCode;

    private boolean isAudioRecording = false;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            ConversationActivity.this.finish();
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
        }
    };
    private String mRoomID;
    private boolean isConnected;
    private MediaPlayer mMediaPlayer;
    private Runnable mTimeRunnable;
    private long timeSecond;
    private boolean isRinging;
    private boolean isHandsFree;
    private ImageView mIvHandsFree;
    private String mConf;
    private String mCaller;
    private String mCallee;
    private String mCallType;
    private String mIsSip;
    private String mDirection;
    private TextView mTvConf;
    private ImageView mIvNoVoice;
    private ImageView mIvRecord;
    private boolean mMuted = false;
    private TextView mTvRecord;
    private TextView mTvNovoice;
    private TextView mTvHandsFree;
    private TextView mTvDiD;
    private boolean isSponsor = true;

    private OnReportBugCallBack mOnReportBugCallBack = new OnReportBugCallBack() {
        @Override
        public void onReportBug(String errCode, final String errMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(errMsg)) {
                        ToastUtil.shortShow(errMsg);
                    }
                }
            });
        }
    };
    private static final long delay = 5 * 1000;
    private long timeDelay = 1000;
    private String mConference_uuid;
    private boolean mIsConnected = false;
    private Intent serviceIntent;
    private ImageView mIvReject;
    private ImageView mIvAnswer;
    private LinearLayout mLlFunction;

    protected void connectedCall() {
        mIsConnected = true;
        startCountTime();
//        stopAlarm();
        if (!isSponsor) {
            getConfMemberList();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showUI(true);
                }
            });
        }
    }

    private static final int TIMER = 999;
    private MyTimeTask task;

    private long timeMillis = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRoomID = getIntent().getStringExtra(IMConstants.ROOMID);
        mCaller = getIntent().getStringExtra(IMConstants.CALLER);
        mCallee = getIntent().getStringExtra(IMConstants.CALLEE);
        mCallType = getIntent().getStringExtra(IMConstants.CALLTYPE);
        Log.d(TAG, "mCallType=" + mCallType);
        mIsSip = getIntent().getStringExtra(IMConstants.ISSIP);
        mDirection = getIntent().getStringExtra(IMConstants.DIRECTION);

        String callType = getIntent().getStringExtra(IMConstants.CALLTYPE);
        mConf = getIntent().getStringExtra(IMConstants.CONFNO);

        String confUUID = getIntent().getStringExtra(IMConstants.CONFERENCE_UUID);

        WebRtc2SipInterface.setOnReceiveMessageListener(this);
        initView();

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRvConversation.setLayoutManager(manager);
        if (isSponsor)
            allDatas.add(new PhoneBean(1));
        mAdapter = new ConversationAdapter(allDatas, this);
        mAdapter.setOnItemClickListener(this);
        mRvConversation.setAdapter(mAdapter);

        //声网
        initAgoraEngine();

        List<String> list = new ArrayList<>();
        List<PhoneBean> phoneList = AppCache.getInstance().getPhoneList();

        for (int i = 0; i < phoneList.size(); i++) {
            list.add(phoneList.get(i).getTelPhone());
        }

        if (!TextUtils.isEmpty(confUUID)) {
            //不是发起人
            isSponsor = false;
            mConference_uuid = confUUID;
            WebRtc2SipInterface.sipRinging(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
            showUI(false);
        } else {//发起人
            WebRtc2SipInterface.sponsorConf(mConf, list, callType, new SponsorConfCallBack() {
                @Override
                public void onSponsorConf(String errCode, String errMsg) {

                }
            });
//            getConfMemberList();
            showUI(true);
        }

//        WebRtc2SipInterface.getConfDiD("3000", new OnGetConfDiDCallBack() {
//
//            @Override
//            public void getConfDiD(final String did, String errCode, String errMsg) {
//                if (IMConstants.SUCCESS.equals(errCode)) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            mTvDiD.setText("固话接入号：" + did);
//                        }
//                    });
//                }
//            }
//        });

        WebRtc2SipInterface.setOnConfMemberStatusCallBack(this);
        WebRtc2SipInterface.setOnMemberChangeCallBack(this);
    }

    private void initView() {
        mTvSendError = findViewById(R.id.tv_send_error);
        mTvConversationTime = findViewById(R.id.tv_conversation_time);
        mRvConversation = findViewById(R.id.rv_conversation);
        mIvHangup = findViewById(R.id.iv_hangup);
        mIvHandsFree = findViewById(R.id.iv_handsfree);
        mTvConf = findViewById(R.id.tv_confNo);
        mIvNoVoice = findViewById(R.id.iv_novoice);
        mIvRecord = findViewById(R.id.iv_record);
        mTvRecord = findViewById(R.id.tv_record);
        mTvNovoice = findViewById(R.id.tv_novoice);
        mTvHandsFree = findViewById(R.id.tv_handsfree);
        mTvDiD = findViewById(R.id.tv_did);
        mIvReject = findViewById(R.id.iv_reject);
        mIvAnswer = findViewById(R.id.iv_answer);
        mLlFunction = findViewById(R.id.ll_function);

        mTvSendError.setOnClickListener(this);
        mIvHangup.setOnClickListener(this);
        mIvHandsFree.setOnClickListener(this);
        mIvNoVoice.setOnClickListener(this);
        mIvRecord.setOnClickListener(this);
        mTvConversationTime.setOnClickListener(this);
        mIvReject.setOnClickListener(this);
        mIvAnswer.setOnClickListener(this);

        mTvConf.setText("内部接入号：" + mConf);
    }

    private void showUI(boolean isConnected) {
        mIvAnswer.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        mIvReject.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        mIvHangup.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        mLlFunction.setVisibility(isConnected ? View.VISIBLE : View.GONE);
    }

    private void getConfMemberList() {
        WebRtc2SipInterface.getConfMemberList(mConf, new OnGetConfMemberListCallBack() {
            @Override
            public void onGetConfMemberList(String errCode, String errMsg, ArrayList<MemberBean> arrayList) {
                if (IMConstants.SUCCESS.equals(errCode)) {
                    Log.d(TAG, "获取会议成员列表====" + arrayList.toString());
                    if (!Utils.listIsEmpty(arrayList)) {
                        for (MemberBean bean : arrayList) {
                            PhoneBean phoneBean = getPhoneBean(bean.getCallstate(), bean.getPhoneNum(), bean.getUuid());
                            datas.add(phoneBean);
                        }
                        updateList();
                    }
                }
            }
        });
    }

    private void updateList() {
        int size = datas.size();
        if (size < 8) {
            allDatas.clear();
            allDatas.addAll(datas);
            if (isSponsor)
                allDatas.add(new PhoneBean(1));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null)
                    mAdapter.setDatas(allDatas);
            }
        });
    }

    private PhoneBean getPhoneBean(String callState, String telphone, String uuid) {
        PhoneBean phoneBean = new PhoneBean();
        phoneBean.setCallstate(callState);
        phoneBean.setTelPhone(telphone);
        phoneBean.setUuid(uuid);
        return phoneBean;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send_error:
                ReportBugDialog reportBugDialog = new ReportBugDialog(this);
                reportBugDialog.setOnReportClickListener(new ReportBugDialog.OnReportClickListener() {
                    @Override
                    public void onReportClick(String phoneNum, String desc) {
                        WebRtc2SipInterface.reportBug(phoneNum, desc, mOnReportBugCallBack);
                    }
                });
                reportBugDialog.show();
                break;
            case R.id.ll_keyboard:
                break;
            case R.id.iv_hangup:
                WebRtc2SipInterface.sipDisconnect(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
                finish();
                break;
            case R.id.iv_handsfree:
                onHandsFreeClicked();
                break;
            case R.id.iv_novoice:
                onLocalAudioMuteClicked();
                break;
            case R.id.iv_record:
                File file = new File(Constants.PATH_AUDIO_RECORDING);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (isAudioRecording) {
                    if (mRtcEngine != null) {
                        isAudioRecording = false;
                        mIvRecord.setImageResource(R.mipmap.record);
                        mRtcEngine.stopAudioRecording();
                    }
                } else {
                    if (mRtcEngine != null) {
                        String fileName = Md5Utils.md5(mConference_uuid) + System.currentTimeMillis() + ".aac";
                        mRtcEngine.startAudioRecording(Constants.PATH_AUDIO_RECORDING + File.separator + fileName, 2);
                        isAudioRecording = true;
                        mIvRecord.setImageResource(R.mipmap.blue_record);
                    }
                }
                String color = isAudioRecording ? "#ff005bac" : "#838383";
                mTvRecord.setTextColor(Color.parseColor(color));
                break;
            case R.id.tv_conversation_time:
                if (Build.VERSION.SDK_INT >= 23) { // Android6.0及以后需要动态申请权限
                    if (!Settings.canDrawOverlays(ConversationActivity.this)) {
                        //启动Activity让用户授权
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1010);
                    } else {
                        // 弹出悬浮窗
                        showFloatWindow();
                    }
                } else {
                    // 弹出悬浮窗
                    showFloatWindow();
                }
                break;
            case R.id.iv_reject:
                WebRtc2SipInterface.sipReject(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
                this.finish();
                break;
            case R.id.iv_answer:
                if (!TextUtils.isEmpty(mCaller)) {
                    if (System.currentTimeMillis() - timeDelay > 1000) {
                        WebRtc2SipInterface.sipAnswerCall(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
                        timeDelay = System.currentTimeMillis();
                    }
                }
                break;

        }
    }

    private void showFloatWindow() {
        AppCache.getInstance().setSecond(timeSecond);
        AppCache.getInstance().setCallConnect(mIsConnected);
        // 弹出悬浮窗
        moveTaskToBack(true);
        //开启服务显示悬浮框
        if (serviceIntent == null)
            serviceIntent = new Intent(getApplicationContext(), FloatVideoWindowService.class);

        startService(serviceIntent);

        Intent data = new Intent(ConversationActivity.this, HomeActivity.class);
        startActivity(data);
    }

    @Override
    public void onItemClick(int position, PhoneBean bean) {

    }

    @Override
    public void onAddClick() {
        CallTypeDialog dialog = new CallTypeDialog(ConversationActivity.this);
        dialog.setOnItemClickListener(new CallTypeDialog.OnItemClickListener() {
            @Override
            public void onPhoneClick() {
                //电话呼叫---添加会议人
                Intent intent = new Intent(ConversationActivity.this, ContactsActivity.class);
                intent.putExtra(Constants.CALLTYPE_NAME, Constants.PHONE);
                intent.putExtra(Constants.TYPE, Constants.TYPE_CONF_ADD_MEMBER);
                intent.putExtra(IMConstants.CONFNO, mConf);//会议号
                startActivityForResult(intent, REQ_ADD_CONTACTS);
            }

            @Override
            public void onVoipSip() {
                //内部呼叫--添加会议人
                Intent intent = new Intent(ConversationActivity.this, CallActivity.class);
                intent.putExtra(Constants.CALLTYPE_NAME, Constants.SIP);
                intent.putExtra(Constants.TYPE, Constants.TYPE_CONF_ADD_MEMBER);
                intent.putExtra(IMConstants.CONFNO, mConf);//会议号
                startActivityForResult(intent, REQ_ADD_CONTACTS);
            }
        });
        dialog.show();
    }

    private void initAgoraEngine() {
        initializeAgoraEngine();
        if (null != mRtcEngine) {
            mRtcEngine.setEnableSpeakerphone(false);
            mRtcEngine.setAudioProfile(2, 2);
            mRtcEngine.setLogFilter(0);//不输出任何日志
        }
    }

    private void initializeAgoraEngine() {
        try {
            String agora_app_id = WebRtc2SipInterface.getAgoraAppId();
            mRtcEngine = RtcEngine.create(getBaseContext(), agora_app_id, mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);
        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                    break;
                }
                joinChannel();
                break;
            }
        }
    }

    private void joinChannel() {
        if (!TextUtils.isEmpty(mRoomID)) {
            if (mRtcEngine != null)
                mRtcEngine.joinChannel(null, mRoomID, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
        }
    }

    private void checkPermissionAndInit() {
        //连接视频
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            joinChannel();
        }
    }

    private void leaveChannel() {
        if (mRtcEngine != null)
            mRtcEngine.leaveChannel();
    }


    public void onHandsFreeClicked() {
        isHandsFree = !isHandsFree;
        if (mRtcEngine != null)
            mRtcEngine.setEnableSpeakerphone(isHandsFree);
        int res = isHandsFree ? R.mipmap.blue_handsfree : R.mipmap.handsfree;
        mIvHandsFree.setImageResource(res);
        String color = isHandsFree ? "#ff005bac" : "#838383";
        mTvHandsFree.setTextColor(Color.parseColor(color));
    }


    public void onLocalAudioMuteClicked() {
        mMuted = !mMuted;
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.mipmap.blue_voice : R.mipmap.novoice;
        mIvNoVoice.setImageResource(res);
        String color = mMuted ? "#ff005bac" : "#838383";
        mTvNovoice.setTextColor(Color.parseColor(color));
    }

    /**
     * 开启手机系统自带铃声
     */
    private void startAlarm(int rawId) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(ConversationActivity.this, rawId);
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (Exception e) {

        }
        mMediaPlayer.start();
    }

//    private void stopAlarm() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }

    private void startCountTime() {
        if (mTimeRunnable == null) {
            mTimeRunnable = new TimeRunnable();
            mHandler.postDelayed(mTimeRunnable, 0);
        }
    }

    private void stopTimer() {
        if (task != null)
            task.stop();
    }

    @Override
    protected void onDestroy() {
        stopTimer();
        //重置当前呼叫状态
        WebRtc2SipInterface.setSipRoomID("");
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        isConnected = false;
//        stopAlarm();
        AppCache.getInstance().setPhoneList(new ArrayList<PhoneBean>());
        if (serviceIntent != null)
            stopService(serviceIntent);
        //将计时设置为0,连接为false
        AppCache.getInstance().setSecond(0);
        AppCache.getInstance().setCallConnect(false);
        super.onDestroy();
    }

    @Override
    public void onMemberAdd(MemberBean memberBean) {
        mConference_uuid = memberBean.getConference_uuid();
        PhoneBean phoneBean = getPhoneBean(memberBean.getCallstate(), memberBean.getPhoneNum(), memberBean.getUuid());
        datas.add(phoneBean);
        updateList();
    }

    @Override
    public void onStatus(String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            if (!Utils.listIsEmpty(allDatas)) {
                for (int i = 0; i < allDatas.size(); i++) {
                    if (uuid.equals(allDatas.get(i).getUuid())) {
                        allDatas.get(i).setCallstate(IMConstants.HANGUP);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null)
                            mAdapter.setDatas(allDatas);
                    }
                });
            }
        }
    }

    private class TimeRunnable implements Runnable {
        @Override
        public void run() {
            timeSecond += 1000;
            if (mTvConversationTime != null)
                mTvConversationTime.setText("通话中(" + DateTimeUtil.getMSTime(timeSecond) + ")");
            mHandler.postDelayed(mTimeRunnable, 1000);
        }
    }

    @Override
    public void onReceiveMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            try {
                JSONObject object = new JSONObject(msg);
                if (object != null) {
                    String msgtag = JSONUtil.getString(object, IMConstants.MSGTAG, "");
                    String roomID = JSONUtil.getString(object, IMConstants.ROOMID, "");
                    mConnectCode = JSONUtil.getString(object, IMConstants.CODE, "");
                    Log.d(TAG, "msgtag=" + msgtag);
                    Log.d(TAG, "roomID=" + roomID);
                    if (!TextUtils.isEmpty(mRoomID))
                        if (!mRoomID.equals(roomID)) {
                            return;
                        }
                    if (EnumKey.MsgTag.sip_ringing.toString().equals(msgtag)) { //响铃
                        isRinging = true;
                        if (CONNECT_CODE.equals(mConnectCode)) {//住呼叫有彩铃的情况先接通进入房间号
                            isConnected = true;
                            checkPermissionAndInit();
                        }
                    } else if (EnumKey.MsgTag.sip_ringing_res.toString().equals(msgtag)) {//响铃回执
                    } else if (EnumKey.MsgTag.sip_connected.toString().equals(msgtag)) {//通话连接
                        if (!CONNECT_CODE.equals(mConnectCode)) {
                            isConnected = true;
                            checkPermissionAndInit();
                            connectedCall();
                        }
                    } else if (EnumKey.MsgTag.sip_connected_res.toString().equals(msgtag)) {//通话回执
                        isConnected = true;
                        checkPermissionAndInit();
                        connectedCall();
                    } else if (EnumKey.MsgTag.sip_cancel.toString().equals(msgtag)
                            || (EnumKey.MsgTag.sip_cancel_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_rejected.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_rejected_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_disconnected.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_disconnected_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_no_response.toString().equals(msgtag))) {
                        ConversationActivity.this.finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class MyTimeTask {
        private Timer timer;
        private TimerTask task;
        private long time;

        public MyTimeTask(long time, TimerTask task) {
            this.task = task;
            this.time = time;
            if (timer == null) {
                timer = new Timer();
            }
        }

        public void start() {
            timer.schedule(task, 0, time);//每隔time时间段就执行一次
        }

        public void stop() {
            if (timer != null) {
                timer.cancel();
                if (task != null) {
                    task.cancel();  //将原任务从队列中移除
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
