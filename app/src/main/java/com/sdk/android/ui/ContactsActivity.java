package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/11/18 16:28
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
import android.widget.LinearLayout;
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
import com.sdk.android.utils.PhoneComparator;
import com.sdk.android.utils.PhoneUtils;
import com.sdk.android.utils.PinyinUtils;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.CustomEditText;
import com.sdk.android.widget.LoadingDialog;
import com.sdk.android.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener
        , SideBar.OnTouchingLetterChangedListener, ContactsAdapter.OnItemClickListener
        , ChooseContactsAdapter.OnClickListener {

    private static final String LOG_TAG = "ContactsActivity";
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.READ_CONTACTS};
    private static final int PERMISSION_REQ_ID = 22;

    public static final int RES_ADD_CONTACTS_CODE = 0x12;

    private TextView mTvBack;
    private TextView mTvCall;
    private List<PhoneBean> mPhoneBeanList;
    private ArrayList<PhoneBean> mChoosePhoneList = new ArrayList<>();
    private LinearLayout mLlNoContacts;
    private RecyclerView mRvContacts;
    private ContactsAdapter mContactsAdapter;
    private CustomEditText mEt;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String keyword = s.toString();
            filterPhoneData(keyword);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private SideBar mSideBar;
    private LinearLayoutManager mManager;
    private RecyclerView mRvChooseContacts;
    private ChooseContactsAdapter mChooseContactsAdapter;
    private TextView mTvConfirm;
    private String mConfNo;
    private LoadingDialog mDialog;
    private String mCallTypeName;//呼叫类型sip,phone
    private int mType;//会议类型，0发起1添加


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        App.getInstance().addActivity(this);

        mCallTypeName = getIntent().getStringExtra(Constants.CALLTYPE_NAME);
        mType = getIntent().getIntExtra(Constants.TYPE, 0);
        mConfNo = getIntent().getStringExtra(IMConstants.CONFNO);


        initView();

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
            getDatas();
        }

        initChooseContactsRv();

    }

    private void initView() {
        mTvBack = findViewById(R.id.tv_back);
        mTvCall = findViewById(R.id.tv_call);
        mLlNoContacts = findViewById(R.id.ll_no_contacts);
        mRvContacts = findViewById(R.id.rv_contacts);
        mEt = findViewById(R.id.et);
        mSideBar = findViewById(R.id.sideBar);
        mRvChooseContacts = findViewById(R.id.rv_choose_contacts);
        mTvConfirm = findViewById(R.id.tv_confirm);

        mTvBack.setOnClickListener(this);
        mTvCall.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);

        mEt.addTextChangedListener(mTextWatcher);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    private void getDatas() {
        mLlNoContacts.setVisibility(View.GONE);
        mPhoneBeanList = new PhoneUtils(this).getPhone();
        mPhoneBeanList = filledData(mPhoneBeanList);
        PhoneComparator pinyinComparator = new PhoneComparator();
        Collections.sort(mPhoneBeanList, pinyinComparator);
        Log.d(LOG_TAG, mPhoneBeanList.toString());

        mManager = new LinearLayoutManager(this);
        mRvContacts.setLayoutManager(mManager);
        mContactsAdapter = new ContactsAdapter(ContactsActivity.this, mPhoneBeanList);
        mContactsAdapter.setOnItemClickListener(this);
        mRvContacts.setAdapter(mContactsAdapter);
    }

    private List<PhoneBean> filledData(List<PhoneBean> data) {

        List<PhoneBean> mSortList = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            PhoneBean bean = new PhoneBean();
            String name = data.get(i).getName();
            bean.setName(name);
            bean.setTelPhone(data.get(i).getTelPhone());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                bean.setLetters(sortString.toUpperCase());
            } else {
                bean.setLetters("#");
            }

            mSortList.add(bean);
        }
        return mSortList;

    }

    private void filterPhoneData(String filterStr) {
        List<PhoneBean> filterDateList = new ArrayList<>();
        if (!TextUtils.isEmpty(filterStr)) {
            if (!Utils.listIsEmpty(mPhoneBeanList)) {
                filterDateList.clear();
                for (PhoneBean phone : mPhoneBeanList) {
                    String name = phone.getName();
                    String telPhone = phone.getTelPhone();
                    //根据名字或者号码
                    if (name.indexOf(filterStr.toString()) != -1
                            || PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                            //不区分大小写
                            || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                            || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                            || telPhone.indexOf(filterStr.toString()) != -1) {
                        filterDateList.add(phone);
                    }
                }
            }
            mContactsAdapter.updateList(filterDateList);
        } else {
            mContactsAdapter.updateList(mPhoneBeanList);
        }
    }

    /**
     * 已选择的联系人
     */
    private void initChooseContactsRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvChooseContacts.setLayoutManager(manager);

        mChooseContactsAdapter = new ChooseContactsAdapter(mChoosePhoneList, this);
        mChooseContactsAdapter.setOnClickListener(this);
        mRvChooseContacts.setAdapter(mChooseContactsAdapter);
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
                Toast.makeText(ContactsActivity.this, "Need permissions " + Manifest.permission.READ_CONTACTS
                        , Toast.LENGTH_LONG).show();
//                finish();
                return;
            }
            getDatas();
        }
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
            case R.id.tv_call:
                intent = new Intent(ContactsActivity.this, CallActivity.class);
                intent.putExtra(Constants.CALLTYPE_NAME, mCallTypeName);
                intent.putExtra(Constants.TYPE, mType);
                intent.putExtra(IMConstants.CONFNO, mConfNo);//会议号
                startActivity(intent);
//                CallTypeDialog dialog = new CallTypeDialog(ContactsActivity.this);
//                dialog.setOnItemClickListener(new CallTypeDialog.OnItemClickListener() {
//                    @Override
//                    public void onPhoneClick() {
//                    }
//
//                    @Override
//                    public void onVoipSip() {
//                        Intent intent = new Intent(ContactsActivity.this, CallActivity.class);
//                        intent.putExtra(Constants.CALLTYPE_NAME, mCallTypeName);
//                        intent.putExtra(Constants.TYPE, mType);
//                        intent.putExtra(IMConstants.CONFNO,mConfNo);//会议号
//                        startActivity(intent);
//                    }
//                });
//                dialog.show();
                break;
            case R.id.tv_confirm:
                if (Utils.listIsEmpty(mChoosePhoneList)) {
                    ToastUtil.shortShow("请先选择联系人");
                    return;
                }
                if (mType == Constants.TYPE_CONF_ADD_MEMBER) {
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
                                ContactsActivity.this.finish();
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
                } else {
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
                                    public void onSipCall(final SipBean bean, String roomid, String uid, String token) {
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
                                                            ToastUtil.shortShow(bean.getMsg());
                                                        }
                                                    });
                                                } else {
                                                    Intent intent = new Intent(ContactsActivity.this, ConversationActivity.class);
                                                    intent.putExtra(IMConstants.CONFNO, mConfNo);
                                                    if (TextUtils.isEmpty(roomid)) {
                                                        intent.putExtra(IMConstants.ROOMID, bean.getRoomID());
                                                    }else {
                                                        intent.putExtra(IMConstants.ROOMID, roomid);
                                                        intent.putExtra(IMConstants.UID, uid);
                                                        intent.putExtra(IMConstants.TOKEN, token);
                                                    }
                                                    intent.putExtra(IMConstants.CALLER, bean.getCaller());
                                                    intent.putExtra(IMConstants.CALLEE, bean.getCallee());
                                                    intent.putExtra(IMConstants.CALLTYPE, bean.getCallType());
                                                    intent.putExtra(IMConstants.ISSIP, bean.getIsSip());
                                                    intent.putExtra(IMConstants.DIRECTION, bean.getDirection());
                                                    intent.putExtra(IMConstants.CALLTYPE, Constants.PHONE);
                                                    startActivity(intent);
                                                    ContactsActivity.this.finish();
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
        }
    }


    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置 q
        int position = mContactsAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void onItemClick(PhoneBean bean, int position) {
//        PhoneBean bean = mPhoneBeanList.get(position);
        boolean choosed = bean.isChoosed();
        mPhoneBeanList.get(position).setChoosed(!choosed);
        mContactsAdapter.updateList(mPhoneBeanList);

        if (mChoosePhoneList.contains(bean)) {
            mChoosePhoneList.remove(bean);
        } else {
            mChoosePhoneList.add(bean);
        }
        mChooseContactsAdapter.setDatas(mChoosePhoneList);
    }

    @Override
    public void onClick(int position, PhoneBean bean) {
        //更新已选联系人
        if (!Utils.listIsEmpty(mChoosePhoneList)) {
            mChoosePhoneList.remove(position);
        }
        mChooseContactsAdapter.setDatas(mChoosePhoneList);
        //
        if (!Utils.listIsEmpty(mPhoneBeanList)) {
            for (int i = 0; i < mPhoneBeanList.size(); i++) {
                if (mPhoneBeanList.get(i).equals(bean)) {
                    mPhoneBeanList.get(i).setChoosed(false);
                }
            }
        }
        mContactsAdapter.updateList(mPhoneBeanList);

    }

}
