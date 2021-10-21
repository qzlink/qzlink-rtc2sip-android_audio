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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.model.MemberBean;
import com.sdk.android.R;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private List<MemberBean> datas;
    private Context mContext;

    public MembersAdapter(List<MemberBean> datas, Context context) {
        this.datas = datas;
        mContext = context;
    }

    public void setDatas(List<MemberBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_members, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvAvatar = view.findViewById(R.id.tv_avatar);
        viewHolder.tvName = view.findViewById(R.id.tv_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final MemberBean bean = datas.get(position);
        String telPhone = bean.getPhoneNum();
        if (!TextUtils.isEmpty(telPhone)) {
            if (!TextUtils.isEmpty(telPhone)) {
                telPhone = telPhone.replace(" ", "");
                if (telPhone.length() > 4) {
                    telPhone = telPhone.substring(telPhone.length() - 4);
                }
            }
            viewHolder.tvAvatar.setText(telPhone);
            viewHolder.tvName.setText(telPhone);
        }

    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
