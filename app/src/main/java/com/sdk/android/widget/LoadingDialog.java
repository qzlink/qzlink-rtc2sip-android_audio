package com.sdk.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sdk.android.R;

public class LoadingDialog extends Dialog {

    private TextView mTvStatus;
    private String message;

    public LoadingDialog(Context context, String message) {
        super(context, R.style.DialogStyleBottom);
        setContentView(R.layout.dialog_loading);
        // 获取窗体的参数
        LayoutParams lp = getWindow().getAttributes();

        lp.width = LayoutParams.MATCH_PARENT;
        // 设置窗体的高度
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        // 点击屏幕下方，收起窗体
        this.setCanceledOnTouchOutside(false);
        this.message = message;
        initView();
    }

    public void showMsg(String msg){
        mTvStatus.setText(msg);
    }

    private void initView() {
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvStatus.setText(message);
    }
}
