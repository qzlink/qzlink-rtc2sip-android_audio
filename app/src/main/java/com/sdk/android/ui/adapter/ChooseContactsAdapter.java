package com.sdk.android.ui.adapter;
/*
 * @creator      dean_deng
 * @createTime   2019/1/3 15:03
 * @Desc         ${TODO}
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdk.android.R;
import com.sdk.android.model.PhoneBean;

import java.util.List;

public class ChooseContactsAdapter extends RecyclerView.Adapter<ChooseContactsAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<PhoneBean> datas;
    private int mWidth;

    public ChooseContactsAdapter(List<PhoneBean> datas, Context context) {
        mContext = context;
        mInfalter = LayoutInflater.from(context);
        this.datas = datas;
    }

    public void setDatas(List<PhoneBean> list) {
        this.datas = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInfalter.inflate(R.layout.item_choose_contacts, parent, false);
        return new ChooseContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final PhoneBean bean = datas.get(i);
        String name = bean.getName();
        String telPhone = bean.getTelPhone();
        if (TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(telPhone)) {
                telPhone = telPhone.replace(" ", "");
                if (telPhone.length() > 4) {
                    name = telPhone.substring(telPhone.length() - 4);
                }
            }
        }
        viewHolder.tvName.setText(name);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(i, bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, PhoneBean bean);
    }
}
