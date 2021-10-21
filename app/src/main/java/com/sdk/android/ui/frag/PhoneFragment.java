package com.sdk.android.ui.frag;/*
 * @creator      dean_deng
 * @createTime   2019/11/20 18:20
 * @Desc         ${TODO}
 */


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.highsip.webrtc2sip.callback.BindPhoneCallBack;
import com.highsip.webrtc2sip.callback.SipCallCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.model.SipBean;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.ui.AudioChatActivity;
import com.sdk.android.ui.CountryCodeActivity;
import com.sdk.android.utils.MediaManager;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.LoadingDialog;
import com.sdk.android.widget.SwitchButton;

import java.lang.reflect.Method;

public class PhoneFragment extends Fragment implements View.OnClickListener {

    private View mView;

    private static final int REQ_COUNTRY_CODE = 0x11;

    private static final int PERMISSION_REQ_ID = 22;

    private static final String PLUS = "+";

    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
    private TextView mTvCode;
    private EditText mEtPhoneNum;
    private ImageView mIvCountry;
    private ImageView mIvCall;
    private ImageView mIvDelete;
    private SwitchButton mSBtn;
    private TextView mTvUid;

    private boolean mIsOpenSip = true;
    private String phoneNum = "";
    private String mCountryCode = "";
    private int cursorPosition;
    private TextView mTvBindPhone;
    private String myPhoneNum = "";
    private String callPrefix = "91";
    private String callPrefix_CN = "92";
    private LoadingDialog mDialog;
    private SwitchButton mSBtnVideo;
    private boolean mIsVideo = false;
    private String mCallType = IMConstants.AUDIO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_phone, null);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewAndData();
    }

    private void initViewAndData() {
        //获取权限
//        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)
//                && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
//        }
        initView();
        initListener();
        disableShowInput();
    }

    private void initView() {
        mTvOne = mView.findViewById(R.id.tv_one);
        mTvTwo = mView.findViewById(R.id.tv_two);
        mTvThree = mView.findViewById(R.id.tv_three);
        mTvFour = mView.findViewById(R.id.tv_four);
        mTvFive = mView.findViewById(R.id.tv_five);
        mTvSix = mView.findViewById(R.id.tv_six);
        mTvSeven = mView.findViewById(R.id.tv_seven);
        mTvEight = mView.findViewById(R.id.tv_eight);
        mTvNine = mView.findViewById(R.id.tv_nine);
        mTvZero = mView.findViewById(R.id.tv_zero);
        mTvStar = mView.findViewById(R.id.tv_star);
        mTvJing = mView.findViewById(R.id.tv_jing);

        mTvCode = mView.findViewById(R.id.tv_code);
        mEtPhoneNum = mView.findViewById(R.id.et_phoneNum);

        mTvBindPhone = mView.findViewById(R.id.tv_bind_phone);
        mTvUid = mView.findViewById(R.id.tv_uid);

        mIvCountry = mView.findViewById(R.id.iv_country);
        mIvCall = mView.findViewById(R.id.iv_call);
        mIvDelete = mView.findViewById(R.id.iv_delete);

        mSBtn = mView.findViewById(R.id.sBtn);
        mSBtnVideo = mView.findViewById(R.id.sBtn_video);

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
        mIvCall.setOnClickListener(this);
        mIvCountry.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);

    }

    private void initListener() {
        mTvZero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //长按0 变成+号
                if (TextUtils.isEmpty(phoneNum) && TextUtils.isEmpty(mCountryCode)) {
                    mEtPhoneNum.setText(PLUS);
                }
                return false;
            }
        });
        mEtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtPhoneNum.getText().toString().length() > 0) {
                    mEtPhoneNum.setSelection(cursorPosition);
                }
            }
        });
        mIvDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mEtPhoneNum.setText("");
                mTvCode.setText("");
                mCountryCode = "";
                phoneNum = "";
                cursorPosition = 0;
                return false;
            }
        });

        mSBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    mIsOpenSip = true;
                    mTvCode.setVisibility(View.VISIBLE);
                } else {
                    mIsOpenSip = false;
                    mTvCode.setVisibility(View.INVISIBLE);
                }
            }
        });
        mSBtnVideo.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    mIsVideo = true;
                } else {
                    mIsVideo = false;
                }
            }
        });
        String userid = WebRtc2SipInterface.getUserid();
        if (!TextUtils.isEmpty(userid))
            mTvUid.setText("UID:" + userid);
        WebRtc2SipInterface.getSmallNum(userid, new BindPhoneCallBack() {
            @Override
            public void getBindPhone(final String smallNum) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvBindPhone.setText("我的号码:" + smallNum);
                    }
                });
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                //播放按键音
                MediaManager.playSound(getActivity(), number);
                //拼接号码
                StringBuilder sb = new StringBuilder();
                sb.append(phoneNum).append(number);
                phoneNum = sb.toString();
                //00开头 变成加号
                if (phoneNum.startsWith("00")) {
                    phoneNum = "+" + phoneNum.substring(2);
                }
                //判断前三位是否含有国家代码，必须为加号开头才判断，否则当国内号码处理
                if (phoneNum.startsWith("+")) {
                    if (phoneNum.length() == 4 || phoneNum.length() == 3 || phoneNum.length() == 2) {
                        boolean hasCountryCode = Utils.isHasCountryCode(getContext(), phoneNum);
                        //如果匹配成功加个空格符
                        if (hasCountryCode) {
                            mCountryCode = phoneNum;
                            mTvCode.setText(mCountryCode);
                            phoneNum = "";
                        }
                    }
                }
                int length = phoneNum.length();
                if (length > 18) {
                    return;
                }
                mEtPhoneNum.setText(phoneNum);
                mEtPhoneNum.setSelection(length);
                if (TextUtils.isEmpty(mCountryCode)) {
                    mCountryCode = "86";
                    mTvCode.setText(PLUS + mCountryCode);
                }
                break;
            case R.id.iv_call:
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getActivity(), getString(R.string.input), Toast.LENGTH_LONG).show();
                    return;
                }
                String callNumber = "";
                if (mIsOpenSip) {//电话呼叫
                    if (!TextUtils.isEmpty(myPhoneNum)) {//小号不为空
                        if (mCountryCode.equals("86")) {//92+电话号码
                            callNumber = callPrefix_CN + phoneNum;
                        } else {//91+国家代码+电话号码
                            callNumber = callPrefix + mCountryCode + phoneNum;
                        }
                    } else {//91+国家代码+电话号码
//                        if (mCountryCode.equals("86")) {//小号为空呼叫国内则0735
//                            callNumber = "0735" + mCountryCode + phoneNum;
//                        } else {
                        callNumber = callPrefix + mCountryCode + phoneNum;
//                        }
                    }
                } else {
                    callNumber = phoneNum;
                }
                if (mIsVideo) {//内网呼叫且是视频呼叫
                    mCallType = IMConstants.VIDEO;
                } else {
                    mCallType = IMConstants.AUDIO;
                }
//                if (mDialog == null)
//                    mDialog = new LoadingDialog(getActivity(), "正在呼叫");
//                mDialog.show();
                Intent intent = new Intent(getActivity(), AudioChatActivity.class);
                intent.putExtra(Constants.SIPIP, "");//坐席信息
                intent.putExtra(Constants.PHONENUMBER, callNumber);
                intent.putExtra(Constants.ISOPENSIP, mIsOpenSip);
                intent.putExtra(Constants.CALLTYPE, mCallType);
                intent.putExtra(IMConstants.TYPE, 1);
                startActivity(intent);


                break;
            case R.id.iv_country:
//                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)
//                        && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
                Intent intent1 = new Intent(getActivity(), CountryCodeActivity.class);
                startActivityForResult(intent1, REQ_COUNTRY_CODE);
//                }
                break;
            case R.id.iv_delete:
                //如果号码已经为空
                if (TextUtils.isEmpty(phoneNum)) {
                    mCountryCode = "";
                    mTvCode.setText("");
                } else {
                    //获取光标位置
                    int selectionEnd = mEtPhoneNum.getSelectionEnd();
                    if (!TextUtils.isEmpty(phoneNum) && selectionEnd == 0) {
                        phoneNum = phoneNum.substring(0, phoneNum.length() - 1);
                        mEtPhoneNum.setText(phoneNum);
                        cursorPosition = phoneNum.length();
                    } else if (selectionEnd > 0) {
                        //截取0，到目标位
                        Editable editableText = mEtPhoneNum.getEditableText();
                        cursorPosition = selectionEnd - 1;
                        if (cursorPosition < 0) {
                            cursorPosition = 0;
                        } else if (cursorPosition > mEtPhoneNum.length()) {
                            cursorPosition = mEtPhoneNum.length();
                        }
                        editableText.delete(selectionEnd - 1, selectionEnd);
                        phoneNum = editableText.toString();
                    }
                }
                mEtPhoneNum.setText(phoneNum);
                break;
        }
    }


//    private boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), REQUESTED_PERMISSIONS, requestCode);
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQ_ID: {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
//                    getActivity().finish();
//                    break;
//                }
//                //复制数据库
////                loadDb();
//                break;
//            }
//        }
//    }

//    private void loadDb() {
//        ThreadExecutor.executeNormal(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    LocalDbUtils.CopySqliteFileFromRawToDatabases(getActivity());
//                    WebRtc2SipLogUtils.d("-------------CopySqliteFileFromRawToDatabases----------------");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public void disableShowInput() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEtPhoneNum.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(mEtPhoneNum, false);
            } catch (Exception e) {//TODO: handle exception
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(mEtPhoneNum, false);
            } catch (Exception e) {//TODO: handle exception
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_COUNTRY_CODE) {
            if (resultCode == CountryCodeActivity.RES_COUNTRY_CODE) {
                mCountryCode = data.getStringExtra(Constants.CODE);
                mTvCode.setText(PLUS + mCountryCode);
            }
        }
    }

}
