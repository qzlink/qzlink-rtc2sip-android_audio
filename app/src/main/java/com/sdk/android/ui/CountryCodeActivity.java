package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/9/19 10:07
 * @Desc         ${TODO}
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sdk.android.Constants;
import com.sdk.android.R;
import com.sdk.android.model.CodeBean;
import com.sdk.android.ui.adapter.CountrySortAdapter;
import com.sdk.android.utils.CodeComparator;
import com.sdk.android.utils.PinyinUtils;
import com.sdk.android.utils.Utils;
import com.sdk.android.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryCodeActivity extends AppCompatActivity implements
        SideBar.OnTouchingLetterChangedListener, View.OnClickListener
        , CountrySortAdapter.OnItemClickListener {

    public static final int RES_COUNTRY_CODE = 0x21;

    private RecyclerView mRecyclerView;

    private List<CodeBean> mDatas = new ArrayList<>();

    private CodeComparator pinyinComparator;
    private SideBar mSideBar;
    private TextView mTvDialog;
    private CountrySortAdapter mAdapter;
    private LinearLayoutManager manager;
    private TextView mTvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);
        initView();
        initData();
    }


    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSideBar = findViewById(R.id.sideBar);
        mTvDialog = findViewById(R.id.dialog);
        mTvBack = findViewById(R.id.back);
    }

    private void initData() {
        pinyinComparator = new CodeComparator();
        String json = Utils.getJson(this, Constants.COUNTRY_CODE_JSON);
        List<CodeBean> list = Utils.getCodeList(json);
        mDatas.addAll(list);
        if (!Utils.listIsEmpty(mDatas)) {
            mDatas = filledData(mDatas);
            if (!Utils.listIsEmpty(mDatas))
                Collections.sort(mDatas, pinyinComparator);
        }
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new CountrySortAdapter(CountryCodeActivity.this, mDatas);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mSideBar.setOnTouchingLetterChangedListener(this);
        mSideBar.setTextView(mTvDialog);
        mTvBack.setOnClickListener(this);
    }

    private List<CodeBean> filledData(List<CodeBean> data) {

        List<CodeBean> mSortList = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            CodeBean sortModel = new CodeBean();
            String name = data.get(i).getCountry_cn();
            sortModel.setCountry_cn(name);
            sortModel.setIso(data.get(i).getIso());
            sortModel.setCountryCode(data.get(i).getCountryCode());
            sortModel.setCountry_us(data.get(i).getCountry_us());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            manager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position, CodeBean codeBean) {
        String countryCode = codeBean.getCountryCode();
        String iso = codeBean.getIso();
        Intent data = new Intent();
        data.putExtra(Constants.ISO, iso);
        data.putExtra(Constants.CODE, countryCode);
        setResult(RES_COUNTRY_CODE, data);
        CountryCodeActivity.this.finish();
    }

}
