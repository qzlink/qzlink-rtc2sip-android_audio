package com.sdk.android.ui.adapter;
/*
 * @creator      dean_deng
 * @createTime   2019/1/3 15:03
 * @Desc         ${TODO}
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.highsip.webrtc2sip.util.Md5Utils;
import com.sdk.android.R;
import com.sdk.android.utils.Utils;

import java.io.File;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<String> datas;

    public RecordAdapter(List<String> datas, Context context) {
        mContext = context;
        mInfalter = LayoutInflater.from(context);
        this.datas = datas;
    }

    public void setDatas(List<String> list) {
        this.datas = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInfalter.inflate(R.layout.item_record_detail, parent, false);
        return new RecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final String filepath = datas.get(i);
        File file = new File(filepath);
        if (file.exists()) {
            viewHolder.tvName.setText("录音文件" + Md5Utils.md5(filepath));
            viewHolder.tvSize.setText(Utils.FormetFileSize(file.length()));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(filepath);
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, "video/*");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSize = itemView.findViewById(R.id.tv_size);
        }
    }

}
