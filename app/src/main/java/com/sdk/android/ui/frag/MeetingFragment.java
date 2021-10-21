package com.sdk.android.ui.frag;
/*
 * @creator      dean_deng
 * @createTime   2019/11/20 18:20
 * @Desc         ${TODO}
 */


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.highsip.webrtc2sip.callback.BindPhoneCallBack;
import com.highsip.webrtc2sip.callback.OnGetConfHistCallBack;
import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.common.WebRtc2SipInterface;
import com.highsip.webrtc2sip.model.ConfBean;
import com.sdk.android.Constants;
import com.sdk.android.R;

import com.sdk.android.ui.CallActivity;
import com.sdk.android.ui.ConfDetailActivity;
import com.sdk.android.ui.ContactsActivity;
import com.sdk.android.ui.adapter.ConfHisAdapter;
import com.sdk.android.utils.ToastUtil;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.CallTypeDialog;
import java.util.ArrayList;
import java.util.List;

public class MeetingFragment extends Fragment implements View.OnClickListener, ConfHisAdapter.OnClickListener {

    private View mView;
    private RecyclerView mRvCallRecord;
    private TextView mTvInitiateCall;
    private TextView mTvPhoneNum;
    private int pageSize = 10;
    private int pageNumber = 1;
    private List<ConfBean> mDatas = new ArrayList<>();
    private ConfHisAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_meeting, null);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewAndData();
    }

    private void initViewAndData() {
        mRvCallRecord = mView.findViewById(R.id.rv_call_record);
        mTvInitiateCall = mView.findViewById(R.id.tv_initiate_call);
        mTvPhoneNum = mView.findViewById(R.id.tv_phoneNum);
        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        mTvInitiateCall.setOnClickListener(this);
        String userid = WebRtc2SipInterface.getUserid();
        WebRtc2SipInterface.getSmallNum(userid, new BindPhoneCallBack() {
            @Override
            public void getBindPhone(final String smallNum) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvPhoneNum.setText("我的号码:" + smallNum);
                    }
                });
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRvCallRecord.setLayoutManager(manager);
        mAdapter = new ConfHisAdapter(mDatas, getContext());
        mAdapter.setOnClickListener(this);
        mRvCallRecord.setAdapter(mAdapter);

        getConfHisDatas();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getConfHisDatas();
                    }
                }, 1000);
            }
        });
    }

    private void getConfHisDatas() {
        WebRtc2SipInterface.confMemberHisPage(String.valueOf(pageNumber), String.valueOf(pageSize), "desc", new OnGetConfHistCallBack() {
            @Override
            public void getConfHis(String errCode, final boolean lastPage, final List<ConfBean> list) {
                if (IMConstants.SUCCESS.equals(errCode)) {
                    mDatas.addAll(list);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!Utils.listIsEmpty(list)) {
                                ToastUtil.shortShow("加载成功");
                                pageNumber += 1;
                            } else {
                                ToastUtil.shortShow("没有更多数据了");
                            }
                            if (mAdapter != null) {
                                mAdapter.setDatas(mDatas);
                            }
                            if (mRefreshLayout != null)
                                mRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_initiate_call:
                CallTypeDialog dialog = new CallTypeDialog(getContext());
                dialog.setOnItemClickListener(new CallTypeDialog.OnItemClickListener() {
                    @Override
                    public void onPhoneClick() {
                        //电话呼叫--发起会议
                        Intent intent = new Intent(getContext(), ContactsActivity.class);
                        intent.putExtra(Constants.CALLTYPE_NAME, Constants.PHONE);
                        intent.putExtra(Constants.TYPE, Constants.TYPE_CONF_SPONSOR);
                        startActivity(intent);
                    }

                    @Override
                    public void onVoipSip() {
                        //内部呼叫--发起会议
                        Intent intent = new Intent(getContext(), CallActivity.class);
                        intent.putExtra(Constants.CALLTYPE_NAME, Constants.SIP);
                        intent.putExtra(Constants.TYPE, Constants.TYPE_CONF_SPONSOR);
                        startActivity(intent);
                    }
                });
                dialog.show();

                break;
        }
    }

    @Override
    public void onClick(int position, String conference_uuid) {
        Intent intent = new Intent(getContext(), ConfDetailActivity.class);
        intent.putExtra(IMConstants.CONFERENCE_UUID,conference_uuid);
        startActivity(intent);
    }
}
