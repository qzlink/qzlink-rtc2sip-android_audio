package com.sdk.android.utils;
import com.sdk.android.model.PhoneBean;

import java.util.Comparator;

public class PhoneComparator implements Comparator<PhoneBean> {

    public int compare(PhoneBean o1, PhoneBean o2) {
        if (o1.getLetters().equals("@")
                || o2.getLetters().equals("#")) {
            return -1;
        } else if (o1.getLetters().equals("#")
                || o2.getLetters().equals("@")) {
            return 1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }

}
