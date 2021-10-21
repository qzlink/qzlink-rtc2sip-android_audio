package com.sdk.android.utils;
/*
 * @creator      dean_deng
 * @createTime   2019/9/19 10:12
 * @Desc         ${TODO}
 */


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.highsip.webrtc2sip.util.StringUtils;
import com.sdk.android.Constants;
import com.sdk.android.model.CodeBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5F);
    }

    public static int getResIdByName(String name, Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        if ("DO".equals(name)) {
            name = "DODO";
        }
        int resID = context.getResources().getIdentifier(name.toLowerCase(), "drawable", appInfo.packageName);
        return resID;
    }

    public static boolean listIsEmpty(List<?> list) {
        if (list != null && !list.isEmpty())
            return false;
        return true;
    }

    public static String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<CodeBean> getCodeList(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                List<CodeBean> list = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (jsonObject != null) {
                            CodeBean bean = new CodeBean();
                            if (jsonObject.has(Constants.COUNTRY) && !jsonObject.isNull(Constants.COUNTRY)) {
                                String country = jsonObject.getString(Constants.COUNTRY);
                                bean.setCountry_us(country);
                            }
                            if (jsonObject.has(Constants.COUNTRY_CODE) && !jsonObject.isNull(Constants.COUNTRY_CODE)) {
                                String countryCode = jsonObject.getString(Constants.COUNTRY_CODE);
                                bean.setCountryCode(countryCode);
                            }
                            if (jsonObject.has(Constants.CN_NAME) && !jsonObject.isNull(Constants.CN_NAME)) {
                                String uuid = jsonObject.getString(Constants.CN_NAME);
                                bean.setCountry_cn(uuid);
                            }
                            if (jsonObject.has(Constants.AREA_CODE) && !jsonObject.isNull(Constants.AREA_CODE)) {
                                String uuid = jsonObject.getString(Constants.AREA_CODE);
                                bean.setIso(uuid);
                            }
                            list.add(bean);
                        }
                    }
                    return list;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 是否含有国家代码
     *
     * @param number
     * @return
     */
    public static boolean isHasCountryCode(Context context, String number) {
        if (!TextUtils.isEmpty(number))
            if (number.contains("+")) {
                number = number.replace("+", "");
            }
        String json = getJson(context, Constants.COUNTRY_CODE_JSON);
        if (!TextUtils.isEmpty(json)) {
            List<CodeBean> list = getCodeList(json);
            if (!Utils.listIsEmpty(list)) {
                for (CodeBean bean : list) {
                    if (bean.getCountryCode().equals(number)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
