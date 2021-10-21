package com.sdk.android.utils;
/*
 * @creator      dean_deng
 * @createTime   2019/6/13 14:57
 * @Desc         ${TODO}
 */


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.sdk.android.model.PhoneBean;

import java.util.ArrayList;
import java.util.List;

public class PhoneUtils {

    // 号码
    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

    //上下文对象
    private Context context;
    //联系人提供者的uri
    private Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    public PhoneUtils(Context context) {
        this.context = context;
    }

    //获取所有联系人
    public List<PhoneBean> getPhone() {
        List<PhoneBean> phoneDtos = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null, null);
        if (cursor != null)
            while (cursor.moveToNext()) {
                PhoneBean phoneDto = new PhoneBean(cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(NUM)));
                phoneDtos.add(phoneDto);
            }
        return phoneDtos;
    }

    public String getFormatPhone(String iso, String number, String prefix) {
        iso = iso.toLowerCase();
        if (iso.equals("do")) {
            iso = "dodo";
        }
        //US,CA,AU,GB
        String num0 = "";
        String num1 = "";
        String num2 = "";
        if (!TextUtils.isEmpty(number)) {
            if ("us".equals(iso) || "ca".equals(iso) || "au".equals(iso) || "gb".equals(iso)) {
                num0 = "+" + number.substring(0, 1);
                num1 = " (" + number.substring(1, 4) + ") ";
                num2 = number.substring(4, number.length());
            } else {
                num0 = "+" + prefix;
                num1 = " " + number.substring(prefix.length(), number.length());
            }
            if (!number.equals("国际呼叫")) {
                number = num0 + num1 + num2;
            }
        }
        if (iso.equals("cn")) {//如果是中国号码
            number = "+86 " + number;
        }
        return number;
    }

}
