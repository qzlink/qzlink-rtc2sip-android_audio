package com.sdk.android.ui;/*
 * @creator      dean_deng
 * @createTime   2019/9/19 16:56
 * @Desc         ${TODO}
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.highsip.webrtc2sip.callback.SipCallCallBack;
import com.highsip.webrtc2sip.common.EnumKey;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.listener.OnReceiveMessageListener;
import com.highsip.webrtc2sip.model.SipBean;
import com.highsip.webrtc2sip.util.JSONUtil;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.utils.DateTimeUtil;
import com.sdk.android.utils.MediaManager;
import com.sdk.android.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
//import io.agora.rtc.video.VideoEncoderConfiguration;

public class AudioChatActivity extends AppCompatActivity
        implements View.OnClickListener, OnReceiveMessageListener {

    private static final String LOG_TAG = "AudioChatActivity";

    private static final String CONNECT_CODE = "183";

    private RtcEngine mRtcEngine;

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
            AudioChatActivity.this.finish();
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.shortShow("加入房间成功");
                }
            });
        }
    };

    private ImageView mIvDisconnect;
    private TextView mTvTips;
    private RelativeLayout mRlFunction;
    private TextView mTvKeyBoard;
    private ImageView mIvKeyBoard;
    private LinearLayout mLlKeyBoard;
    private TextView mTvOne;
    private TextView mTvTwo;
    private TextView mTvThree;
    private TextView mTvFour;
    private TextView mTvFive;
    private TextView mTvSix;
    private TextView mTvSeven;
    private TextView mTvEight;
    private TextView mTvNine;
    private TextView mTvZero;
    private TextView mTvStar;
    private TextView mTvJing;
    private TextView mTvInputNum;
    private TextView mTvPhoneNum;
    private ImageView mIvHeadIcon;

    private String phoneNum = "";
    private boolean isAudioRecording = false;
    private ImageView mIvHandsFree;
    private ImageView mIvMute;
    private ImageView mIvRecord;
    private String mRoomID;
    private String mToken;
    private String mUid;
    private String mConnectCode;
    private boolean isRinging;
    private boolean isConnected;
    private String mCaller;
    private String mCallee;
    private String mCallType;
    private String mIsSip;
    private String mDirection;

    private Handler mHandler = new Handler();

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/WebRtc2Sip";

    private long timeSecond = 0;

    private TimeRunnable mTimeRunnable;
    private ImageView mIvReject;
    private ImageView mIvAnswer;

    private MediaPlayer mMediaPlayer;
    private int mType;
    private RelativeLayout mMRlContainer;

    private long delay = 0;
    private boolean mMuted;
    private boolean isHandsFree;
    private TextView mTvRoomId;

    protected void connnectedCall() {
        startCountTime();
        stopAlarm();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //已接通挂断
                mIvDisconnect.setVisibility(View.VISIBLE);
                //未接通挂断
                mIvReject.setVisibility(View.GONE);
                //接通
                mIvAnswer.setVisibility(View.GONE);

                if (IMConstants.VIDEO.equals(mCallType)) {
                    mRlFunction.setVisibility(View.GONE);
                    mMRlContainer.setVisibility(View.VISIBLE);
                } else {
                    //功能栏
                    mRlFunction.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_chat);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WebRtc2SipInterface.setOnReceiveMessageListener(this);


        initView();
        mType = getIntent().getIntExtra(IMConstants.TYPE, 0);
        if (mType == 1) {//外呼
            String ip = getIntent().getStringExtra("ip");
            //callNumber，被叫号码，可以是手机号码或者SIP账号
            WebRtc2SipInterface.setOnSipCallCallBack(new SipCallCallBack() {
                @Override
                public void onSipCall(final SipBean bean, String roomid, String uid, String token) {
                    if (bean != null) {
                        if (IMConstants.SUCCESS.equals(bean.getErrcode())) {
                            if ("000172".equals(bean.getCode())) {

                            } else {
                                mCaller = bean.getCaller();
                                mCallee = bean.getCallee();
                                mCallType = bean.getCallType();
                                mIsSip = bean.getIsSip();
                                mDirection = bean.getDirection();
                                mRoomID = bean.getRoomID();
                                mUid = uid;
                                mToken = token;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTvRoomId.setText("" + mRoomID);
                                        initData();
                                        //声网
                                        initAgoraEngine();
//                                        onHandsFreeClicked();
                                    }
                                });
                            }
                        } else {

                        }
                    }

                }
            });
            WebRtc2SipInterface.sipCall(getIntent().getStringExtra(Constants.PHONENUMBER), getIntent().getBooleanExtra(Constants.ISOPENSIP, true), getIntent().getStringExtra(Constants.CALLTYPE));
        } else {
            Intent intent = getIntent();
            mRoomID = intent.getStringExtra(IMConstants.ROOMID);
            mUid = intent.getStringExtra(IMConstants.UID);
            mToken = intent.getStringExtra(IMConstants.TOKEN);
            mCaller = intent.getStringExtra(IMConstants.CALLER);
            mCallee = intent.getStringExtra(IMConstants.CALLEE);
            mCallType = intent.getStringExtra(IMConstants.CALLTYPE);
            mIsSip = intent.getStringExtra(IMConstants.ISSIP);
            mDirection = intent.getStringExtra(IMConstants.DIRECTION);
            initData();
            //声网
            initAgoraEngine();
//            onHandsFreeClicked();
        }
//        initData();
//        //声网
//        initAgoraEngine();
    }

    private void initView() {

        mIvDisconnect = findViewById(R.id.iv_disconnect);
        mTvTips = findViewById(R.id.tv_tips);
        mRlFunction = findViewById(R.id.rl_function);
        mTvKeyBoard = findViewById(R.id.tv_keyboard);
        mIvKeyBoard = findViewById(R.id.iv_keyboard);
        mLlKeyBoard = findViewById(R.id.ll_keyboard);

        mTvInputNum = findViewById(R.id.tv_inputNum);
        mTvPhoneNum = findViewById(R.id.tv_phoneNum);
        mIvHeadIcon = findViewById(R.id.iv_head_icon);

        mIvHandsFree = findViewById(R.id.iv_handsfree);
        mIvMute = findViewById(R.id.iv_mute);
        mIvRecord = findViewById(R.id.iv_record);

        mIvReject = findViewById(R.id.iv_reject);
        mIvAnswer = findViewById(R.id.iv_answer);

        mMRlContainer = findViewById(R.id.rl_container);

        mTvRoomId = findViewById(R.id.tv_roomid);

        mTvOne = findViewById(R.id.tv_one);
        mTvTwo = findViewById(R.id.tv_two);
        mTvThree = findViewById(R.id.tv_three);
        mTvFour = findViewById(R.id.tv_four);
        mTvFive = findViewById(R.id.tv_five);
        mTvSix = findViewById(R.id.tv_six);
        mTvSeven = findViewById(R.id.tv_seven);
        mTvEight = findViewById(R.id.tv_eight);
        mTvNine = findViewById(R.id.tv_nine);
        mTvZero = findViewById(R.id.tv_zero);
        mTvStar = findViewById(R.id.tv_star);
        mTvJing = findViewById(R.id.tv_jing);

        mTvOne.setOnClickListener(this);
        mTvTwo.setOnClickListener(this);
        mTvThree.setOnClickListener(this);
        mTvFour.setOnClickListener(this);
        mTvFive.setOnClickListener(this);
        mTvSix.setOnClickListener(this);
        mTvSeven.setOnClickListener(this);
        mTvEight.setOnClickListener(this);
        mTvNine.setOnClickListener(this);
        mTvZero.setOnClickListener(this);
        mTvStar.setOnClickListener(this);
        mTvJing.setOnClickListener(this);

        mIvDisconnect.setOnClickListener(this);
        mTvKeyBoard.setOnClickListener(this);
        mIvKeyBoard.setOnClickListener(this);

        mIvHandsFree.setOnClickListener(this);
        mIvMute.setOnClickListener(this);
        mIvRecord.setOnClickListener(this);

        mIvAnswer.setOnClickListener(this);
        mIvReject.setOnClickListener(this);
    }


    private void initData() {
        mTvRoomId.setText("" + mRoomID);
        WebRtc2SipInterface.setSipRoomID(mRoomID);

        //电话呼叫
        if (IMConstants.YES.equals(mIsSip)) {
            if (IMConstants.IN.equals(mDirection)) {//电话呼入
                startAlarm(R.raw.ringtone30);
                showUI();
                //振铃
                WebRtc2SipInterface.sipRinging(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
            } else if (IMConstants.OUT.equals(mDirection)) {//电话呼出
                mTvPhoneNum.setText(getPhoneNum(mCallee));
            }

        } else if (IMConstants.NO.equals(mIsSip)) {//内网呼叫

            if (mType == 0) {///如果是内网呼入
                startAlarm(R.raw.ringtone30);
                mTvPhoneNum.setText(getPhoneNum(mCallee));
                ///如果是内网呼入
                showUI();
            } else if (mType == 1) {///如果是内网呼出
                mTvPhoneNum.setText(getPhoneNum(mCallee));
            }
        }
    }

    private String getPhoneNum(String phoneNum) {
        if (phoneNum.startsWith("91") || phoneNum.startsWith("92")) {
            phoneNum = phoneNum.substring(2);
        }
        return phoneNum;
    }

    private void showUI() {
        mTvPhoneNum.setText(mCaller);
        //功能栏
        mRlFunction.setVisibility(View.GONE);
        //已接通挂断
        mIvDisconnect.setVisibility(View.GONE);
        //未接通挂断
        mIvReject.setVisibility(View.VISIBLE);
        //接通
        mIvAnswer.setVisibility(View.VISIBLE);
    }

    private void initAgoraEngine() {
        initializeAgoraEngine();
        if (null != mRtcEngine) {
            mRtcEngine.setChannelProfile(1);//
            mRtcEngine.setClientRole(1);//
        }
        if (null != mRtcEngine) {
            if (IMConstants.VIDEO.equals(mCallType)) {
                setupVideoProfile();
                setupLocalVideo();
            } else {
                mRtcEngine.setEnableSpeakerphone(false);
                mRtcEngine.setAudioProfile(2, 2);
            }
            mRtcEngine.setLogFilter(0);//不输出任何日志
        }

    }

    private void initializeAgoraEngine() {
        try {
//            try {
            String agora_app_id = WebRtc2SipInterface.getAgoraAppId();
            mRtcEngine = RtcEngine.create(getBaseContext(), agora_app_id, mRtcEventHandler);
//            } catch (IllegalArgumentException e) {
//
//            }
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            ToastUtil.shortShow("SDK初始化失败");
            finish();
//            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
//
//      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false); // Earlier than 2.3.0
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE));
    }

    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

        surfaceView.setTag(uid); // for mark purpose
    }

    private void onRemoteUserLeft() {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        if (surfaceView != null) {
            Object tag = surfaceView.getTag();
            if (tag != null && (Integer) tag == uid) {
                surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
            }
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

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

        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);
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
            if (!TextUtils.isEmpty(mToken) && !TextUtils.isEmpty(mUid)) {
                mRtcEngine.joinChannel(mToken, mRoomID, "Extra Optional Data", Integer.parseInt(mUid)); // if you do not specify the uid, we will generate the uid for you
            } else {
                mRtcEngine.joinChannel(null, mRoomID, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
            }
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

    protected void setEnableSpeakerphone(boolean flag) {
        if (mRtcEngine != null) {
            mRtcEngine.setEnableSpeakerphone(flag);
        }
    }

    protected void startAudioRecording(String filePath, int quality) {
        if (mRtcEngine != null) {
            mRtcEngine.startAudioRecording(filePath, quality);
        }
    }

    protected void stopAudioRecording() {
        if (mRtcEngine != null) {
            mRtcEngine.stopAudioRecording();
        }
    }

    protected void enableAudio() {
        if (mRtcEngine != null) {
            mRtcEngine.enableAudio();
        }
    }

    protected void disableAudio() {
        if (mRtcEngine != null) {
            mRtcEngine.disableAudio();
        }
    }

    @Override
    protected void onDestroy() {
        //重置当前呼叫状态
        WebRtc2SipInterface.setSipRoomID("");
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        isConnected = false;
        stopAlarm();
        super.onDestroy();
    }


    protected void disconnectedCall() {
        WebRtc2SipInterface.sipDisconnect(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_disconnect:
                disconnectedCall();
                break;
            case R.id.tv_keyboard:
                mLlKeyBoard.setVisibility(View.VISIBLE);
                mTvInputNum.setVisibility(View.VISIBLE);
                mTvPhoneNum.setVisibility(View.GONE);
                mIvHeadIcon.setVisibility(View.GONE);
                mIvKeyBoard.setVisibility(View.VISIBLE);
                mRlFunction.setVisibility(View.GONE);
                mTvTips.setVisibility(View.GONE);
                break;
            case R.id.iv_keyboard:
                mLlKeyBoard.setVisibility(View.GONE);
                mTvInputNum.setVisibility(View.GONE);
                mTvPhoneNum.setVisibility(View.VISIBLE);
                mIvHeadIcon.setVisibility(View.VISIBLE);
                mIvKeyBoard.setVisibility(View.GONE);
                mRlFunction.setVisibility(View.VISIBLE);
                mTvTips.setVisibility(View.VISIBLE);
                phoneNum = "";
                mTvInputNum.setText("");
                break;
            case R.id.tv_one:
            case R.id.tv_two:
            case R.id.tv_three:
            case R.id.tv_four:
            case R.id.tv_five:
            case R.id.tv_six:
            case R.id.tv_seven:
            case R.id.tv_eight:
            case R.id.tv_nine:
            case R.id.tv_zero:
            case R.id.tv_star:
            case R.id.tv_jing:
                String number = ((TextView) v).getText().toString();
                //加上DTMF
                WebRtc2SipInterface.sipDTMF(mCaller, mCallee, mCallType, mIsSip, mRoomID, number);
                //播放按键音
                MediaManager.playSound(AudioChatActivity.this, number);
                //拼接号码
                StringBuilder sb = new StringBuilder();
                sb.append(phoneNum).append(number);
                phoneNum = sb.toString();
                mTvInputNum.setText(phoneNum);
                break;
            case R.id.iv_handsfree:
                onHandsFreeClicked();
                break;
            case R.id.iv_record:
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (isAudioRecording) {
                    isAudioRecording = false;
                    mIvRecord.setImageResource(R.mipmap.record);
                    stopAudioRecording();
                } else {
                    String fileName = System.currentTimeMillis() + ".aac";
                    startAudioRecording(path + File.separator + fileName, 2);
                    isAudioRecording = true;
                    mIvRecord.setImageResource(R.mipmap.record_pressed);
                }
                break;
            case R.id.iv_mute:
                onLocalAudioMuteClicked();
                break;
            case R.id.iv_answer:
                if (!TextUtils.isEmpty(mCaller)) {
                    if (System.currentTimeMillis() - delay > 1000) {

                        WebRtc2SipInterface.sipAnswerCall(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);

                        delay = System.currentTimeMillis();
                    }
                }
                break;
            case R.id.iv_reject:
                WebRtc2SipInterface.sipReject(mCaller, mCallee, mCallType, mIsSip, mRoomID, mDirection);
                this.finish();
                break;
        }
    }

    public void onHandsFreeClicked() {
        isHandsFree = !isHandsFree;
        if (mRtcEngine != null)
            mRtcEngine.setEnableSpeakerphone(isHandsFree);
        int res = isHandsFree ? R.mipmap.blue_handsfree : R.mipmap.handsfree;
        mIvHandsFree.setImageResource(res);
    }

    public void onLocalAudioMuteClicked() {
        mMuted = !mMuted;
        if (mRtcEngine != null)
            mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.mipmap.blue_voice : R.mipmap.novoice;
        mIvMute.setImageResource(res);
    }


    /**
     * 开启手机系统自带铃声
     */
    private void startAlarm(int rawId) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(AudioChatActivity.this, rawId);
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

    private void stopAlarm() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void startCountTime() {
        if (mTimeRunnable == null) {
            mTimeRunnable = new TimeRunnable();
            mHandler.postDelayed(mTimeRunnable, 0);
        }
    }

    @Override
    public void onReceiveMessage(String msg) {
        Log.e("----msg---", msg + "");
        if (!TextUtils.isEmpty(msg)) {
            try {
                JSONObject object = new JSONObject(msg);
                if (object != null) {
                    String msgtag = JSONUtil.getString(object, IMConstants.MSGTAG, "");
                    String roomID = JSONUtil.getString(object, IMConstants.ROOMID, "");
                    mConnectCode = JSONUtil.getString(object, IMConstants.CODE, "");
                    if (!TextUtils.isEmpty(mRoomID))
                        if (!mRoomID.equals(roomID)) {
                            return;
                        }
                    if (EnumKey.MsgTag.sip_ringing.toString().equals(msgtag)) { //响铃
                        isRinging = true;
                        isConnected = true;
                        checkPermissionAndInit();
                        connnectedCall();
                    } else if (EnumKey.MsgTag.sip_ringing_res.toString().equals(msgtag)) {//响铃回执

                    } else if (EnumKey.MsgTag.sip_connected.toString().equals(msgtag)) {//通话连接
                        if (!CONNECT_CODE.equals(mConnectCode)) {
                            isConnected = true;
                            checkPermissionAndInit();
                            connnectedCall();
                        }
                    } else if (EnumKey.MsgTag.sip_connected_res.toString().equals(msgtag)) {//通话回执
                        isConnected = true;
                        checkPermissionAndInit();
                        connnectedCall();
                    } else if (EnumKey.MsgTag.sip_cancel.toString().equals(msgtag)
                            || (EnumKey.MsgTag.sip_cancel_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_rejected.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_rejected_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_disconnected.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_disconnected_res.toString().equals(msgtag)
                            || EnumKey.MsgTag.sip_no_response.toString().equals(msgtag))) {
                        AudioChatActivity.this.finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class TimeRunnable implements Runnable {
        @Override
        public void run() {
            timeSecond += 1000;
            if (mTvTips != null)
                mTvTips.setText(DateTimeUtil.getMSTime(timeSecond));
            mHandler.postDelayed(mTimeRunnable, 1000);
        }
    }

}
