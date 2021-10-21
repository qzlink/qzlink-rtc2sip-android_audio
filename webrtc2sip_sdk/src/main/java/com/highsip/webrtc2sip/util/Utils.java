package com.highsip.webrtc2sip.util;
/*
 * @creator      dean_deng
 * @createTime   2019/9/10 15:38
 * @Desc         ${TODO}
 */


import java.util.List;

public class Utils {

    /**
     *
     * @param list
     * @return
     */
    public static boolean listIsEmpty(List<?> list) {
        if (list != null && !list.isEmpty())
            return false;
        return true;
    }

}
