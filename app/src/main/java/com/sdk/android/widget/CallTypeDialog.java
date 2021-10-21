package com.sdk.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.sdk.android.R;

public class CallTypeDialog extends Dialog {

    private TextView btn_phone;
    private TextView btn_vosip;

    public CallTypeDialog(Context context) {
        super(context, R.style.DialogStyleBottom);
        setContentView(R.layout.dialog_call_type);
        // 获取窗体的参数
        LayoutParams lp = getWindow().getAttributes();

        lp.width = LayoutParams.MATCH_PARENT;
        // 设置窗体的高度
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        // 点击屏幕下方，收起窗体
        this.setCanceledOnTouchOutside(true);
        initView();
        initEvent();
    }


    private void initView() {
        btn_phone = findViewById(R.id.btn_phone);
        btn_vosip = findViewById(R.id.btn_vosip);
    }

    private void initEvent() {

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onPhoneClick();
                dismiss();
            }
        });
        btn_vosip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onVoipSip();
                dismiss();
            }
        });

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onPhoneClick();

        void onVoipSip();
    }

}
