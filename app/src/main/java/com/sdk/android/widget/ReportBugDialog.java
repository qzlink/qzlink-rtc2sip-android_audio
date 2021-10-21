package com.sdk.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.sdk.android.R;
import com.sdk.android.utils.ToastUtil;

public class ReportBugDialog extends Dialog {


    private EditText mEtPhoneNum;
    private EditText mEtDesc;
    private TextView mTvSend;

    public ReportBugDialog(Context context) {
        super(context, R.style.DialogStyleBottom);
        setContentView(R.layout.dialog_report_bug);
        // 获取窗体的参数
        LayoutParams lp = getWindow().getAttributes();

        lp.width = LayoutParams.MATCH_PARENT;
        // 设置窗体的高度
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        // 点击屏幕下方，收起窗体
        this.setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }


    private void initView() {
        mEtPhoneNum = findViewById(R.id.et_phoneNum);
        mEtDesc = findViewById(R.id.et_desc);
        mTvSend = findViewById(R.id.tv_send);
    }

    private void initEvent() {
        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = mEtDesc.getText().toString().trim();
                String phoneNum = mEtPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(desc)) {
                    ToastUtil.shortShow("请输入反馈内容");
                    return;
                }
                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtil.shortShow("请输入电话号码");
                    return;
                }
                if (mOnReportClickListener != null) {
                    mOnReportClickListener.onReportClick(phoneNum, desc);
                }
                dismiss();
            }
        });
    }

    private OnReportClickListener mOnReportClickListener;

    public void setOnReportClickListener(OnReportClickListener onReportClickListener) {
        this.mOnReportClickListener = onReportClickListener;
    }

    public interface OnReportClickListener {
        void onReportClick(String phoneNum, String desc);
    }

}
