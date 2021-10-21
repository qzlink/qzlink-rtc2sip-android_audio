package com.sdk.android.ui;/*
 * @creator      dean_deng
 * @createTime   2019/11/19 11:30
 * @Desc         ${TODO}
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.callback.OnGetConfCallBack;
import com.highsip.webrtc2sip.callback.SipCallCallBack;
import com.highsip.webrtc2sip.callback.SponsorConfCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.model.SipBean;
import com.sdk.android.App;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.cache.AppCache;
import com.sdk.android.model.PhoneBean;
import com.sdk.android.ui.adapter.ChooseContactsAdapter;
import com.sdk.android.ui.adapter.ContactsAdapter;
import com.sdk.android.utils.PhoneUtils;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity implements
        View.OnClickListener, ChooseContactsAdapter.OnClickListener
        , ContactsAdapter.OnItemClickListener {

    private static final String LOG_TAG = "CallActivity";

    private static final int REQ_COUNTRY_CODE = 0x11;

    public static final int RES_ADD_CONTACTS_CODE = 0x12;

    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private static final int PERMISSION_REQ_ID = 22;

    private TextView mTvBack;
    private TextView mTvConfirm;
    private ImageView mIvCountry;
    private TextView mTvCountryCode;
    private EditText mEtPhoneNum;
    private TextView mTvInputNum;
    private RecyclerView mRvSearchContacts;
    private ContactsAdapter mContactsAdapter;
    private ChooseContactsAdapter mChooseContactsAdapter;
    private RecyclerView mRvChooseContacts;

    private static final String PLUS = "+";
    private String mCountryCode = "86";
    private String mIso = "CN";
    private List<PhoneBean> mDatas = new ArrayList<>();
    private List<PhoneBean> mChoosePhoneList = new ArrayList<>();
    private List<PhoneBean> mPhoneList;
    private int mType;
    private LinearLayout mLlVoip;
    private LinearLayout mLlPhone;
    private EditText mEtUid;
    private String mCallTypeName;
    private String mConfNo;
    private LoadingDialog mDialog;
    private RelativeLayout mRlInputNum;
    private ImageView mIvChoice;
    private List<PhoneBean> mFilterDateList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        mType = getIntent().getIntExtra(Constants.TYPE, 0);
        mCallTypeName = getIntent().getStringExtra(Constants.CALLTYPE_NAME);
        mConfNo = getIntent().getStringExtra(IMConstants.CONFNO);

        initView();
        if (mCallTypeName.equals(Constants.SIP)) {
            mTvBack.setText("内部呼叫");
            mLlPhone.setVisibility(View.GONE);
            mLlVoip.setVisibility(View.VISIBLE);
        } else {
            if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
                getLocalPhoneData();
            }
        }
        initListener();

        mRvSearchContacts.setLayoutManager(new LinearLayoutManager(this));
        mContactsAdapter = new ContactsAdapter(this, mDatas);
        mContactsAdapter.setOnItemClickListener(this);
        mRvSearchContacts.setAdapter(mContactsAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvChooseContacts.setLayoutManager(manager);

        mChooseContactsAdapter = new ChooseContactsAdapter(mChoosePhoneList, this);
        mChooseContactsAdapter.setOnClickListener(this);
        mRvChooseContacts.setAdapter(mChooseContactsAdapter);

    }

    private void initView() {
        mTvBack = findViewById(R.id.tv_back);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mIvCountry = findViewById(R.id.iv_country);
        mTvCountryCode = findViewById(R.id.tv_countryCode);
        mEtPhoneNum = findViewById(R.id.et_phoneNum);
        mTvInputNum = findViewById(R.id.tv_input_phoneNum);
        mRvSearchContacts = findViewById(R.id.rv_search_contacts);
        mRvChooseContacts = findViewById(R.id.rv_choose_contacts);
        mLlVoip = findViewById(R.id.ll_voip);
        mLlPhone = findViewById(R.id.ll_phone);
        mEtUid = findViewById(R.id.et_uid);
        mRlInputNum = findViewById(R.id.rl_input_num);
        mIvChoice = findViewById(R.id.iv_choice);

        mTvBack.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mIvCountry.setOnClickListener(this);
        mTvInputNum.setOnClickListener(this);

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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CallActivity.this, "Need permissions " + Manifest.permission.READ_CONTACTS
                        , Toast.LENGTH_LONG).show();
//                finish();
                return;
            }
            getLocalPhoneData();
        }
    }

    private void initListener() {
        mEtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputNum = s.toString();
                filterPhoneData(inputNum);

                if (!Utils.listIsEmpty(mChoosePhoneList)) {
                    for (PhoneBean bean :
                            mChoosePhoneList) {
                        if (bean.getTelPhone().equals(inputNum)) {
                            mIvChoice.setVisibility(View.VISIBLE);
                        } else {
                            mIvChoice.setVisibility(View.GONE);
                        }
                    }
                } else {
                    mIvChoice.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputNum = s.toString();
                showInputNum(true);

                if (!Utils.listIsEmpty(mChoosePhoneList)) {
                    for (PhoneBean bean :
                            mChoosePhoneList) {
                        if (bean.getTelPhone().equals(inputNum)) {
                            mIvChoice.setVisibility(View.VISIBLE);
                        } else {
                            mIvChoice.setVisibility(View.GONE);
                        }
                    }
                } else {
                    mIvChoice.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getLocalPhoneData() {
        mPhoneList = new PhoneUtils(CallActivity.this).getPhone();
    }

    private void filterPhoneData(String filterStr) {
        mFilterDateList = new ArrayList<>();
        if (!TextUtils.isEmpty(filterStr)) {
            if (!Utils.listIsEmpty(mPhoneList)) {
                mFilterDateList.clear();
                for (PhoneBean phone : mPhoneList) {
                    String telPhone = phone.getTelPhone().replace(" ", "");
                    //根据名字或者号码
                    if (telPhone.indexOf(filterStr) != -1) {
                        mFilterDateList.add(phone);
                    }
                }
            }
            mContactsAdapter.updateList(mFilterDateList);
            if (!Utils.listIsEmpty(mFilterDateList)) {
                showInputNum(false);
            } else {
                showInputNum(true);
            }
        } else {
            showInputNum(true);
            mContactsAdapter.updateList(null);
        }
    }

    private void showInputNum(boolean isShow) {
        if (mCallTypeName.equals(Constants.SIP)) {
            mTvInputNum.setText(mEtUid.getText().toString().trim());
        } else {
            mTvInputNum.setText(mEtPhoneNum.getText().toString().trim());
        }
        if (isShow)
            mRlInputNum.setVisibility(View.VISIBLE);
        else
            mRlInputNum.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        if (mDialog == null)
            mDialog = new LoadingDialog(this, "呼叫中");
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                if (Utils.listIsEmpty(mChoosePhoneList)) {
                    Toast.makeText(this, "请先选择联系人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mType == Constants.TYPE_CONF_ADD_MEMBER) {//添加会议人
                    List<String> list = new ArrayList<>();
                    for (PhoneBean bean :
                            mChoosePhoneList) {
                        list.add(bean.getTelPhone());
                    }
                    WebRtc2SipInterface.sponsorConf(mConfNo, list, mCallTypeName, new SponsorConfCallBack() {
                        @Override
                        public void onSponsorConf(String errCode, final String errMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mDialog != null)
                                        mDialog.dismiss();
                                }
                            });
                            if (IMConstants.SUCCESS.equals(errCode)) {
                                AppCache.getInstance().addPhoneList(mChoosePhoneList);
                                setResult(RES_ADD_CONTACTS_CODE);
                                CallActivity.this.finish();
                                App.getInstance().clearActivity();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.shortShow(errMsg);
                                    }
                                });
                            }
                        }
                    });
                    if (mDialog != null)
                        mDialog.show();
                } else {//发起会议
                    AppCache.getInstance().setPhoneList(mChoosePhoneList);
                    if (mDialog != null)
                        mDialog.show();
                    //获取房间号
                    WebRtc2SipInterface.getConfNo(new OnGetConfCallBack() {
                        @Override
                        public void onGetConf(String errCode, final String errMsg, String confNo) {
                            if (IMConstants.SUCCESS.equals(errCode)) {
                                mConfNo = confNo;

                                //app用户先加入会议室
                                WebRtc2SipInterface.sipCall("9186" + mConfNo, true, IMConstants.AUDIO);
                                WebRtc2SipInterface.setOnSipCallCallBack(new SipCallCallBack() {
                                    @Override
                                    public void onSipCall(final SipBean bean,String roomid,String uid,String token) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mDialog != null)
                                                    mDialog.dismiss();
                                            }
                                        });
                                        if (bean != null) {
                                            if (IMConstants.SUCCESS.equals(bean.getErrcode())) {
                                                if ("000172".equals(bean.getCode())) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.shortShow(bean.getErrmsg());
                                                        }
                                                    });
                                                } else {

                                                    Intent intent = new Intent(CallActivity.this, ConversationActivity.class);
                                                    if (TextUtils.isEmpty(roomid)) {
                                                        intent.putExtra(IMConstants.ROOMID, bean.getRoomID());
                                                    }else {
                                                        intent.putExtra(IMConstants.ROOMID, roomid);
                                                        intent.putExtra(IMConstants.UID, uid);
                                                        intent.putExtra(IMConstants.TOKEN, token);
                                                    }
                                                    intent.putExtra(IMConstants.CONFNO, mConfNo);
                                                    intent.putExtra(IMConstants.CALLER, bean.getCaller());
                                                    intent.putExtra(IMConstants.CALLEE, bean.getCallee());
                                                    intent.putExtra(IMConstants.CALLTYPE, bean.getCallType());
                                                    intent.putExtra(IMConstants.ISSIP, bean.getIsSip());
                                                    intent.putExtra(IMConstants.DIRECTION, bean.getDirection());
                                                    intent.putExtra(IMConstants.CALLTYPE, mCallTypeName);
                                                    startActivity(intent);
                                                    CallActivity.this.finish();
                                                    App.getInstance().clearActivity();
                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.shortShow(bean.getErrmsg());
                                                    }
                                                });
                                            }
                                        }
                                    }


                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.shortShow(errMsg);
                                        if (mDialog != null)
                                            mDialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }
                break;
            case R.id.iv_country:
                intent = new Intent(this, CountryCodeActivity.class);
                startActivityForResult(intent, REQ_COUNTRY_CODE);
                break;
            case R.id.tv_input_phoneNum:
                String phoneNum = mTvInputNum.getText().toString().trim();
                if (!Utils.listIsEmpty(mChoosePhoneList)) {
                    for (PhoneBean bean : mChoosePhoneList) {
                        if (!bean.getTelPhone().equals(phoneNum)) {
                            mChoosePhoneList.add(new PhoneBean(phoneNum));
                            mChooseContactsAdapter.setDatas(mChoosePhoneList);
                            mIvChoice.setVisibility(View.VISIBLE);
                        } else {
                            mIvChoice.setVisibility(View.GONE);
                            mChoosePhoneList.remove(bean);
                            mChooseContactsAdapter.setDatas(mChoosePhoneList);
                        }
                    }
                } else {
                    mIvChoice.setVisibility(View.VISIBLE);
                    mChoosePhoneList.add(new PhoneBean(phoneNum));
                    mChooseContactsAdapter.setDatas(mChoosePhoneList);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_COUNTRY_CODE) {
            if (resultCode == CountryCodeActivity.RES_COUNTRY_CODE) {
                mCountryCode = data.getStringExtra(Constants.CODE);
                mIso = data.getStringExtra(Constants.ISO);
                int resId = Utils.getResIdByName(mIso, CallActivity.this);
                mIvCountry.setImageResource(resId);
                mTvCountryCode.setText(PLUS + mCountryCode);
            }
        }
    }

    @Override
    public void onClick(int position, PhoneBean bean) {
        if (!Utils.listIsEmpty(mChoosePhoneList)) {
            mChoosePhoneList.remove(position);
        }
        mChooseContactsAdapter.setDatas(mChoosePhoneList);
    }

    @Override
    public void onItemClick(PhoneBean bean, int position) {
        //匹配到的联系人
        if (!mChoosePhoneList.contains(bean)) {
            mChoosePhoneList.add(bean);
        } else {
            mChoosePhoneList.remove(bean);
        }
        mChooseContactsAdapter.setDatas(mChoosePhoneList);

        if (!Utils.listIsEmpty(mFilterDateList)) {
            mFilterDateList.get(position).setChoosed(!bean.isChoosed());
        }
        mContactsAdapter.updateList(mFilterDateList);
    }


}
