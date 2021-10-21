package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/12/4 17:36
 * @Desc         ${TODO}
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.callback.OnGetConfHisDetailCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.model.ConfBean;
import com.highsip.webrtc2sip.model.ConfDetailBean;
import com.highsip.webrtc2sip.model.MemberBean;
import com.highsip.webrtc2sip.util.Md5Utils;
import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.ui.adapter.MembersAdapter;
import com.sdk.android.ui.adapter.RecordAdapter;
import com.sdk.android.utils.DateTimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvCallDate;
    private TextView mTvRunTime;
    private TextView mTvAvatar;
    private TextView mTvName;
    private RecyclerView mRvMembers;
    private List<MemberBean> mDatas = new ArrayList<>();
    private MembersAdapter mMembersAdapter;
    private List<String> fileList = new ArrayList<>();
    private String mConference_uuid;
    private RecyclerView mRvRecord;
    private RecordAdapter mRecordAdapter;
    private TextView mTvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_detail);
        initView();
        mConference_uuid = getIntent().getStringExtra(IMConstants.CONFERENCE_UUID);
        WebRtc2SipInterface.getConfHisDetail(mConference_uuid, new OnGetConfHisDetailCallBack() {
            @Override
            public void getConfHisDetail(final String errCode, final ConfDetailBean confDetailBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (IMConstants.SUCCESS.equals(errCode)) {
                            if (confDetailBean != null) {
                                String sponsor = confDetailBean.getSponsor();
                                if (!TextUtils.isEmpty(sponsor)) {
                                    sponsor = sponsor.replace(" ", "");
                                    if (sponsor.length() > 4) {
                                        sponsor = sponsor.substring(sponsor.length() - 4);
                                    }
                                    mTvAvatar.setText(sponsor);
                                    mTvName.setText(sponsor);
                                }
                                ConfBean confBean = confDetailBean.getConfBean();
                                if (confBean != null) {
                                    mTvCallDate.setText(confBean.getGmt_create());
                                    int run_time = confBean.getRun_time();
                                    String hmsTime = DateTimeUtil.getMSTime(run_time * 1000);
                                    mTvRunTime.setText(hmsTime);
                                }
                                List<MemberBean> list = confDetailBean.getList();
                                mDatas.addAll(list);
                                if (mMembersAdapter != null)
                                    mMembersAdapter.setDatas(mDatas);
                            }
                        }
                    }
                });
            }
        });
        getFileList();
    }

    private void initView() {
        mTvCallDate = findViewById(R.id.tv_call_date);
        mTvRunTime = findViewById(R.id.tv_run_time);
        mTvAvatar = findViewById(R.id.tv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvBack = findViewById(R.id.tv_back);

        mRvMembers = findViewById(R.id.rv_members);
        mRvRecord = findViewById(R.id.rv_record);

        mTvBack.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRvMembers.setLayoutManager(gridLayoutManager);
        mMembersAdapter = new MembersAdapter(mDatas, this);
        mRvMembers.setAdapter(mMembersAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRvRecord.setLayoutManager(manager);
        mRecordAdapter = new RecordAdapter(fileList, this);
        mRvRecord.setAdapter(mRecordAdapter);
    }

    private void getFileList() {
        File file = new File(Constants.PATH_AUDIO_RECORDING);        //获取其file对象
        File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
        if (fs != null) {
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory()) {    //若非目录(即文件)，则打印
                    System.out.println(f);
                    String filepath = f.toString();
                    if (!TextUtils.isEmpty(filepath)) {
                        if (filepath.contains(Md5Utils.md5(mConference_uuid))) {
                            fileList.add(f.toString());
                            mRecordAdapter.setDatas(fileList);
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
