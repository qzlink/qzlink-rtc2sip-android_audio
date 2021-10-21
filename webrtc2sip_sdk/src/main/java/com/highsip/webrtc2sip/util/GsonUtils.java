//package com.highsip.webrtc2sip.util;
///*
// * @creator      dean_deng
// * @createTime   2019/9/27 16:32
// * @Desc         ${TODO}
// */
//
//
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class GsonUtils {
//    private static final String TAG = "GsonUtils";
//
//    public GsonUtils() {
//    }
//
//    public static <T> T getResponse(String jsonString, Class<T> cls) {
//        T t = null;
//        try {
//            Gson gson = new Gson();
//            t = gson.fromJson(jsonString, cls);
//        } catch (Exception e) {
//            Log.d(TAG, "解析JSON出错");
//        }
//        return t;
//    }
//
//    public static <T> List<T> getResponses(String jsonString, Class<T> cls) {
//        List<T> list = new ArrayList<T>();
//        try {
//            Gson gson = new Gson();
//            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
//            }.getType());
//        } catch (Exception e) {
//            Log.d(TAG, "解析JSON出错");
//        }
//        return list;
//    }
//
//    public static <T> List<T> stringToArray(String s, Class<T[]> cls) {
//        T[] arr = new Gson().fromJson(s, cls);
//        return Arrays.asList(arr); //或者返回Arrays.asList（new Gson（）。fromJson（s，clazz））;
//    }
//
//}
