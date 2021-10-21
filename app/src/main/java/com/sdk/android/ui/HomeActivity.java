package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/11/20 17:59
 * @Desc         ${TODO}
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.highsip.webrtc2sip.callback.OnGetConfInfoByRoomIDCallBack;
import com.highsip.webrtc2sip.callback.SipReceiveCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.model.ConfBean;
import com.highsip.webrtc2sip.model.SipBean;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.ui.frag.MeetingFragment;
import com.sdk.android.ui.frag.PhoneFragment;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.widget.MyFragmentTabHost;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    RelativeLayout mRlMainContent;
    MyFragmentTabHost mTabHost;

    private String[] tabNames = {"电话", "会议"};
    private int[] normalResId = {R.mipmap.call_off, R.mipmap.meeting_off};
    private int[] pressedResId = {R.mipmap.callon, R.mipmap.meeting_on};
    private Class[] cls = {PhoneFragment.class, MeetingFragment.class};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();


        //监听屏幕是息屏、亮屏、锁屏
        final IntentFilter screenFileter = new IntentFilter();
        // 屏幕灭屏广播
        screenFileter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        screenFileter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        screenFileter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        screenFileter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        registerReceiver(mBatInfoReceiver, screenFileter);

        WebRtc2SipInterface.setSipReceiveCallBack(new SipReceiveCallBack() {
            @Override
            public void onReceiveCall(SipBean bean) {
                Log.d(TAG, "onReceiveCall=" + bean.toString());
                String caller = bean.getCaller();
                String roomID = bean.getRoomID();
                String callee = bean.getCallee();
                String callType = bean.getCallType();
                String isSip = bean.getIsSip();
                String direction = bean.getDirection();
                if (!TextUtils.isEmpty(caller)) {
                    int length = caller.length();
                    if (length > 4) {
                        Intent intent = new Intent(HomeActivity.this, AudioChatActivity.class);
                        intent.putExtra(IMConstants.ROOMID, roomID);
                        intent.putExtra(IMConstants.CALLER, caller);
                        intent.putExtra(IMConstants.CALLEE, callee);
                        intent.putExtra(IMConstants.CALLTYPE, callType);
                        intent.putExtra(IMConstants.ISSIP, isSip);
                        intent.putExtra(IMConstants.DIRECTION, direction);
                        intent.putExtra(IMConstants.TYPE, 0);//呼入
                        intent.putExtra(IMConstants.TOKEN, bean.getToken());//
                        intent.putExtra(IMConstants.UID, bean.getUid());//
                        HomeActivity.this.startActivity(intent);
                    } else {
                        getConfNo(roomID, caller, callee, callType, isSip, direction);
                    }
                }
            }
        });


    }

    private void getConfNo(final String roomID, final String caller, final String callee, final String callType, final String isSip, final String direction) {
        WebRtc2SipInterface.getConfInfoByRoomID(roomID, new OnGetConfInfoByRoomIDCallBack() {
            @Override
            public void onGetConfInfo(String errCode, ConfBean confBean) {
                if (errCode.equals(IMConstants.SUCCESS)) {
                    if (confBean != null) {
                        Intent intent = new Intent(HomeActivity.this, ConversationActivity.class);
                        intent.putExtra(IMConstants.CONFNO, confBean.getConfNo());
                        intent.putExtra(IMConstants.ROOMID, roomID);
                        intent.putExtra(IMConstants.CALLER, caller);
                        intent.putExtra(IMConstants.CALLEE, callee);
                        intent.putExtra(IMConstants.CALLTYPE, callType);
                        intent.putExtra(IMConstants.ISSIP, isSip);
                        intent.putExtra(IMConstants.DIRECTION, direction);
                        intent.putExtra(IMConstants.CONFERENCE_UUID, confBean.getConference_uuid());
                        intent.putExtra(IMConstants.CALLTYPE, Constants.SIP);
                        startActivity(intent);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.shortShow("获取会议号失败");
                        }
                    });
                }
            }
        });
    }


    BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                WebRtc2SipInterface.reconnectTcp();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.d(TAG, "screen off");
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                WebRtc2SipInterface.reconnectTcp();
                Log.d(TAG, "screen unlock");
            } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                Log.d(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
            }
        }
    };


    private void initView() {
        mRlMainContent = findViewById(R.id.main_content);
        mTabHost = findViewById(R.id.frag_tabHost);
    }

    private void initData() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.main_content);
        for (int i = 0; i < tabNames.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabNames[i]).setIndicator(getView(i));
            mTabHost.addTab(tabSpec, cls[i], null);
        }
    }


    private View getView(int index) {
        View view = View.inflate(this, R.layout.tab_content, null);
        ImageView ivTab = view.findViewById(R.id.iv_tab);
        TextView tvTab = view.findViewById(R.id.tv_tab);
        tvTab.setText(tabNames[index]);
        final StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, getResources().getDrawable(pressedResId[index]));
        stateListDrawable.addState(new int[]{}, getResources().getDrawable(normalResId[index]));
        ivTab.setBackground(stateListDrawable);
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBatInfoReceiver != null) {
            unregisterReceiver(mBatInfoReceiver);
        }
        mBatInfoReceiver = null;
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                HomeActivity.this.finish();
                System.exit(0);
            } else {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }
}
