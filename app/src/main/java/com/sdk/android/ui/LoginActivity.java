package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/9/29 12:02
 * @Desc         ${TODO}
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.highsip.webrtc2sip.callback.ConnectIMCallBack;
import com.highsip.webrtc2sip.callback.OnLoginStatusCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.utils.PreferenceUtils;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.widget.AddressDialog;
import com.sdk.android.widget.LoadingDialog;

public class LoginActivity extends AppCompatActivity implements ConnectIMCallBack, OnLoginStatusCallBack {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mEtAppID;
    private EditText mEtAccount;
    private EditText mEtPwd;
    private Button mBtnLogin;
    private ImageView mIvClose;
    private TextView mTvOfficalURL;
    private LoadingDialog mLoadingDialog;
    private boolean isResume = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        initView();
        initData();
        String account = PreferenceUtils.getString(LoginActivity.this, Constants.ACCOUNT, "");
        String password = PreferenceUtils.getString(LoginActivity.this, Constants.PASSWORD, "");
        String appid = PreferenceUtils.getString(LoginActivity.this, Constants.APPID, "");
        if (!TextUtils.isEmpty(account)) {
            mEtAccount.setText(account);
            mEtAccount.setSelection(account.length());
        }
        if (!TextUtils.isEmpty(password)) {
            mEtPwd.setText(password);
        }
        if (!TextUtils.isEmpty(appid)) {
            mEtAppID.setText(appid);
        }
        mLoadingDialog = new LoadingDialog(LoginActivity.this, "正在登录");
    }


    private void initView() {
        mEtAppID = findViewById(R.id.et_appid);
        mEtAccount = findViewById(R.id.et_account);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        mIvClose = findViewById(R.id.iv_close);
        mTvOfficalURL = findViewById(R.id.tv_offical_url);
    }

    private void initData() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mEtAccount.getText().toString().trim();
                String appId = mEtAppID.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.input_account), Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.input_pwd), Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(appId)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.input_appid), Toast.LENGTH_LONG).show();
                    return;
                }
                mLoadingDialog.show();
                PreferenceUtils.putString(LoginActivity.this, Constants.ACCOUNT, account);
                PreferenceUtils.putString(LoginActivity.this, Constants.PASSWORD, pwd);
                PreferenceUtils.putString(LoginActivity.this, Constants.APPID, appId);
                //设置appId
                WebRtc2SipInterface.setAppID(appId);
                //二、设置账号
                WebRtc2SipInterface.setUUidAndPassword(account, pwd);
                //三、连接IM服务器
                WebRtc2SipInterface.connectIMServers();
                //连接IM服务器
                WebRtc2SipInterface.setOnConnectIMCallBack(LoginActivity.this);
                //登录回调
                WebRtc2SipInterface.setOnLoginStatusCallBack(LoginActivity.this);
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAccount.setText("");
            }
        });
        mTvOfficalURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressDialog dialog = new AddressDialog(LoginActivity.this);
                dialog.show();
            }
        });
        mEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (TextUtils.isEmpty(str)) {
                    mIvClose.setVisibility(View.GONE);
                } else {
                    mIvClose.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onConnectStatus(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 1) {
                    Log.d(TAG, "连接成功");
                } else {
                    if (mLoadingDialog != null)
                        mLoadingDialog.dismiss();
                    ToastUtil.shortShow("连接服务器失败");
                }
            }
        });
    }

    @Override
    public void onLoginStatus(final String errorCode, final String errorMsg) {
        if (IMConstants.SUCCESS.equals(errorCode)) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (isResume) {
                    if (IMConstants.SUCCESS.equals(errorCode)) {
                        Log.d(TAG, "登录成功");
                        ToastUtil.shortShow("登录成功");
                    } else if (IMConstants.ERROR.equals(errorCode)) {
                        WebRtc2SipInterface.disconnectTcp();
                        Log.d(TAG, "" + errorMsg);
                        ToastUtil.shortShow("" + errorMsg);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
    }
}
