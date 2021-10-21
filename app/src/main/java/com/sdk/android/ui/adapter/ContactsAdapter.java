package com.sdk.android.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdk.android.R;
import com.sdk.android.model.PhoneBean;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<PhoneBean> mData;
    private Context mContext;


    public ContactsAdapter(Context context, List<PhoneBean> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }


    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_contacts, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTag = view.findViewById(R.id.tag);
        viewHolder.tvName = view.findViewById(R.id.tv_name);
        viewHolder.tvPhone = view.findViewById(R.id.tv_phone);
        viewHolder.tvAvatar = view.findViewById(R.id.tv_avatar);
        viewHolder.ivChoice = view.findViewById(R.id.iv_choice);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactsAdapter.ViewHolder holder, final int position) {
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(mData.get(position).getLetters());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }
        final PhoneBean phoneBean = mData.get(position);
        String name = phoneBean.getName();
        String telPhone = phoneBean.getTelPhone();
        boolean choosed = phoneBean.isChoosed();
        holder.tvName.setText(name);
        holder.tvPhone.setText(telPhone);
        if (!TextUtils.isEmpty(name)) {
            holder.tvAvatar.setText(name.substring(0, 1));
        } else {
            if (!TextUtils.isEmpty(telPhone)) {
                telPhone = telPhone.replace(" ", "");
                if (telPhone.length() > 4) {
                    holder.tvAvatar.setText(telPhone.substring(telPhone.length() - 4));
                }
            }
        }
        if (choosed) {
            holder.ivChoice.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoice.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(phoneBean, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(PhoneBean bean, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName, tvAvatar, tvPhone;
        ImageView ivChoice;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<PhoneBean> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        if (mData.get(position).getLetters() != null) {
            return mData.get(position).getLetters().charAt(0);
        }
        return -1;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            if (!TextUtils.isEmpty(sortStr)) {
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

}
