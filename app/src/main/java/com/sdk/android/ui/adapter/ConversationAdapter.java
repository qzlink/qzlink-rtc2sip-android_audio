package com.sdk.android.ui.adapter;
/*
 * @creator      dean_deng
 * @createTime   2019/3/1 9:59
 * @Desc         ${TODO}
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.common.IMConstants;
import com.sdk.android.R;
import com.sdk.android.model.PhoneBean;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private List<PhoneBean> datas;
    private Context mContext;

    public ConversationAdapter(List<PhoneBean> datas, Context context) {
        this.datas = datas;
        mContext = context;
    }

    public void setDatas(List<PhoneBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_conversation, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvAvatar = view.findViewById(R.id.tv_avatar);
        viewHolder.tvName = view.findViewById(R.id.tv_name);
        viewHolder.ivAdd = view.findViewById(R.id.iv_add);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final PhoneBean bean = datas.get(position);
        String name = bean.getName();
        String telPhone = bean.getTelPhone();
        if (TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(telPhone)) {
                telPhone = telPhone.replace(" ", "");
                if (telPhone.length() > 4) {
                    name = telPhone.substring(telPhone.length() - 4);
                }
            }
            viewHolder.tvAvatar.setText(name);
        } else {
            viewHolder.tvAvatar.setText(name.substring(0, 1));
        }
        viewHolder.tvName.setText(name);

        String callstate = bean.getCallstate();
        if (IMConstants.ACTIVE.equals(callstate)) {
            viewHolder.tvAvatar.setText(name);
            viewHolder.tvAvatar.setBackgroundResource(R.drawable.shape_oval);
        } else if (IMConstants.HANGUP.equals(callstate)) {
            viewHolder.tvAvatar.setText("已挂断");
            viewHolder.tvAvatar.setBackgroundResource(R.drawable.shape_oval_gray);
        } else {
//            viewHolder.tvAvatar.setText("呼叫中");
//            viewHolder.tvAvatar.setBackgroundResource(R.drawable.shape_oval_gray);
            viewHolder.tvAvatar.setText(name);
            viewHolder.tvAvatar.setBackgroundResource(R.drawable.shape_oval);
        }

        if (bean.getType() == 1) {
            viewHolder.ivAdd.setVisibility(View.VISIBLE);
            viewHolder.tvAvatar.setVisibility(View.GONE);
            viewHolder.tvName.setVisibility(View.GONE);
        } else {
            viewHolder.ivAdd.setVisibility(View.GONE);
            viewHolder.tvAvatar.setVisibility(View.VISIBLE);
            viewHolder.tvName.setVisibility(View.VISIBLE);
        }
        if (mOnItemClickListener != null) {
            viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onAddClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAvatar;
        ImageView ivAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvAvatar = itemView.findViewById(R.id.tv_avatar);
//            tvName = itemView.findViewById(R.id.tv_name);
//            ivAdd = itemView.findViewById(R.id.iv_add);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position, PhoneBean bean);

        void onAddClick();

    }
}
