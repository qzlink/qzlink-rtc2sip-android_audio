package com.sdk.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.sdk.android.R;

public class AddressDialog extends Dialog {

    private ImageView ivClose;

    public AddressDialog(Context context) {
        super(context, R.style.DialogStyleBottom);
        setContentView(R.layout.dialog_address);
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
        ivClose = findViewById(R.id.iv_close);
    }

    private void initEvent() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
