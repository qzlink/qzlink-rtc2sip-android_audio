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
import com.sdk.android.model.CodeBean;
import com.sdk.android.utils.Utils;

import java.util.List;

public class CountrySortAdapter extends RecyclerView.Adapter<CountrySortAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<CodeBean> mData;
    private Context mContext;


    public CountrySortAdapter(Context context, List<CodeBean> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public CountrySortAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.country_code_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTag = view.findViewById(R.id.tag);
        viewHolder.tvName = view.findViewById(R.id.name);
        viewHolder.tvCode = view.findViewById(R.id.code);
        viewHolder.iv = view.findViewById(R.id.iv);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CountrySortAdapter.ViewHolder holder, final int position) {
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(mData.get(position).getLetters());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position, mData.get(position));
                }
            });

        }
        holder.tvName.setText(this.mData.get(position).getCountry_cn());
        holder.tvCode.setText("+" + mData.get(position).getCountryCode());
        String iso = mData.get(position).getIso();
        holder.iv.setImageResource(Utils.getResIdByName(iso, mContext));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position, CodeBean codeBean);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName, tvCode;
        ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<CodeBean> list) {
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
