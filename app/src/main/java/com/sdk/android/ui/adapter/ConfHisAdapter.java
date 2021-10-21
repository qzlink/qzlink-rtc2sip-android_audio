package com.sdk.android.ui.adapter;
/*
 * @creator      dean_deng
 * @createTime   2019/1/3 15:03
 * @Desc         ${TODO}
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.model.ConfBean;
import com.sdk.android.R;

import java.util.List;

public class ConfHisAdapter extends RecyclerView.Adapter<ConfHisAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<ConfBean> datas;
    private int mWidth;

    public ConfHisAdapter(List<ConfBean> datas, Context context) {
        mContext = context;
        mInfalter = LayoutInflater.from(context);
        this.datas = datas;
    }

    public void setDatas(List<ConfBean> list) {
        this.datas = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInfalter.inflate(R.layout.item_conf_his, parent, false);
        return new ConfHisAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ConfBean confBean = datas.get(i);
        String call_date = confBean.getCall_date();
        String conference_name = confBean.getConference_name();
        final String conference_uuid = confBean.getConference_uuid();
        viewHolder.tvConfNo.setText("会议号: " + conference_name);
        viewHolder.tvCallDate.setText(call_date);
        viewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(i, conference_uuid);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvConfNo, tvCallDate;
        ImageView ivDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConfNo = itemView.findViewById(R.id.tv_confNo);
            tvCallDate = itemView.findViewById(R.id.tv_call_date);
            ivDetail = itemView.findViewById(R.id.iv_detail);
        }
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, String conference_uuid);
    }
}
