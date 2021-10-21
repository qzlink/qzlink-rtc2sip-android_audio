package com.highsip.webrtc2sip.util;

import android.text.TextUtils;

import com.highsip.webrtc2sip.common.IMConstants;
import com.highsip.webrtc2sip.model.HostBean;
import com.highsip.webrtc2sip.model.HostListBean;
import com.highsip.webrtc2sip.model.LoginBean;
import com.highsip.webrtc2sip.model.ResponseBean;
import com.highsip.webrtc2sip.model.SipBean;
import com.highsip.webrtc2sip.model.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class JSONUtil {

    /**
     * 检查key是否存在，且value不为null
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean validate(JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            try {
                if (jsonObject.get(key) == JSONObject.NULL || jsonObject.isNull(key)) {
                    return false;
                } else {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return false;
        }
    }


    public static HashMap<String, Object> fromJson(String jsonStr) {
        try {
            if (jsonStr.startsWith("[")
                    && jsonStr.endsWith("]")) {
                jsonStr = "{\"fakelist\":" + jsonStr + "}";
            }

            JSONObject json = new JSONObject(jsonStr);
            return fromJson(json);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new HashMap<String, Object>();
    }

    public static HashMap<String, Object> fromJson(JSONObject json)
            throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        Iterator<String> iKey = json.keys();
        while (iKey.hasNext()) {
            String key = iKey.next();
            Object value = json.opt(key);
            if (JSONObject.NULL.equals(value)) {
                value = null;
            }
            if (value != null) {
                if (value instanceof JSONObject) {
                    value = fromJson((JSONObject) value);
                } else if (value instanceof JSONArray) {
                    value = fromJson((JSONArray) value);
                }
                map.put(key, value);
            }
        }
        return map;
    }

    public static ArrayList<Object> fromJson(JSONArray array)
            throws JSONException {
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0, size = array.length(); i < size; i++) {
            Object value = array.opt(i);
            if (value instanceof JSONObject) {
                value = fromJson((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = fromJson((JSONArray) value);
            }
            list.add(value);
        }
        return list;
    }

    //#if def{lang} == cn

    /**
     * 将指定的 {@link HashMap}<String, Object>对象转成json数据
     */
    //#endif
    public static String fromHashMap(HashMap<String, Object> map) {
        try {
            return getJSONObject(map).toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getJSONObject(HashMap<String, Object> map)
            throws JSONException {
        JSONObject json = new JSONObject();
        for (Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof HashMap<?, ?>) {
                value = getJSONObject((HashMap<String, Object>) value);
            } else if (value instanceof ArrayList<?>) {
                value = getJSONArray((ArrayList<Object>) value);
            }
            json.put(entry.getKey(), value);
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    public static JSONArray getJSONArray(ArrayList<Object> list)
            throws JSONException {
        JSONArray array = new JSONArray();
        for (Object value : list) {
            if (value instanceof HashMap<?, ?>) {
                value = getJSONObject((HashMap<String, Object>) value);
            } else if (value instanceof ArrayList<?>) {
                value = getJSONArray((ArrayList<Object>) value);
            }
            array.put(value);
        }
        return array;
    }

    //#if def{lang} == cn

    /**
     * 格式化一个json串
     */
    //#endif
    public static String format(String jsonStr) {
        try {
            return format("", fromJson(jsonStr));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public static String format(String sepStr, HashMap<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        for (Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr).append('\"').append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof HashMap<?, ?>) {
                sb.append(format(mySepStr, (HashMap<String, Object>) value));
            } else if (value instanceof ArrayList<?>) {
                sb.append(format(mySepStr, (ArrayList<Object>) value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append('}');
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static String format(String sepStr, ArrayList<Object> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[\n");
        String mySepStr = sepStr + "\t";
        int i = 0;
        for (Object value : list) {
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append(mySepStr);
            if (value instanceof HashMap<?, ?>) {
                sb.append(format(mySepStr, (HashMap<String, Object>) value));
            } else if (value instanceof ArrayList<?>) {
                sb.append(format(mySepStr, (ArrayList<Object>) value));
            } else if (value instanceof String) {
                sb.append('\"').append(value).append('\"');
            } else {
                sb.append(value);
            }
            i++;
        }
        sb.append('\n').append(sepStr).append(']');
        return sb.toString();
    }

    /**
     * 从jsonObject中获取name对应的字符串value
     *
     * @param jsonObject
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getString(JSONObject jsonObject, String name, String defaultValue) {
        if (jsonObject != null && !TextUtils.isEmpty(name) && !jsonObject.isNull(name)) {
            Object obj = jsonObject.opt(name);
            if (obj != null && obj instanceof String) {
                return (String) obj;
            }
        }
        return defaultValue;
    }


    public static LoginBean getLoginBean(String json) throws JSONException {
        LoginBean loginBean = new LoginBean();
        if (!TextUtils.isEmpty(json)) {
            JSONObject obj = new JSONObject(json);
            if (obj != null) {
                if (obj.has(IMConstants.ERRCODE) && !obj.isNull(IMConstants.ERRCODE)) {
                    loginBean.setErrcode((obj.getString(IMConstants.ERRCODE)));
                }
                if (obj.has(IMConstants.ERRMSG) && !obj.isNull(IMConstants.ERRMSG)) {
                    loginBean.setErrmsg((obj.getString(IMConstants.ERRMSG)));
                }
                if (obj.has(IMConstants.DATA) && !obj.isNull(IMConstants.DATA)) {
                    JSONObject jsonObject = obj.getJSONObject(IMConstants.DATA);
                    if (jsonObject != null) {
                        UserBean userBean = new UserBean();
                        if (jsonObject.has(IMConstants.RTC) && !jsonObject.isNull(IMConstants.RTC)) {
                            userBean.setRtc((jsonObject.getString(IMConstants.RTC)));
                        }
                        if (jsonObject.has(IMConstants.BALANCE) && !jsonObject.isNull(IMConstants.BALANCE)) {
                            userBean.setBalance(jsonObject.getDouble(IMConstants.BALANCE));
                        }
                        if (jsonObject.has(IMConstants.NAME) && !jsonObject.isNull(IMConstants.NAME)) {
                            userBean.setName((jsonObject.getString(IMConstants.NAME)));
                        }
                        if (jsonObject.has(IMConstants.USERTOKEN) && !jsonObject.isNull(IMConstants.USERTOKEN)) {
                            userBean.setUsertoken((jsonObject.getString(IMConstants.USERTOKEN)));
                        }
                        if (jsonObject.has(IMConstants.USERID) && !jsonObject.isNull(IMConstants.USERID)) {
                            userBean.setUserid((jsonObject.getString(IMConstants.USERID)));
                        }
                        loginBean.setData(userBean);
                    }
                }
            }
        }
        return loginBean;
    }

    public static HostListBean getHostListBean(String json) throws JSONException {
        HostListBean hostListBean = new HostListBean();
        if (!TextUtils.isEmpty(json)) {
            JSONObject obj = new JSONObject(json);
            if (obj != null) {
                if (obj.has(IMConstants.CODE) && !obj.isNull(IMConstants.CODE)) {
                    hostListBean.setCode((obj.getString(IMConstants.CODE)));
                }
                if (obj.has(IMConstants.SIZE) && !obj.isNull(IMConstants.SIZE)) {
                    hostListBean.setSize(obj.getInt(IMConstants.SIZE));
                }
                if (obj.has(IMConstants.LIST) && !obj.isNull(IMConstants.LIST)) {
                    JSONArray jsonArray = obj.getJSONArray(IMConstants.LIST);
                    if (jsonArray != null) {
                        List<HostBean> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject != null) {
                                HostBean hostBean = new HostBean();
                                if (jsonObject.has(IMConstants.HOST) && !jsonObject.isNull(IMConstants.HOST)) {
                                    hostBean.setHost((jsonObject.getString(IMConstants.HOST)));
                                }
                                if (jsonObject.has(IMConstants.PORT) && !jsonObject.isNull(IMConstants.PORT)) {
                                    hostBean.setPort(jsonObject.getInt(IMConstants.PORT));
                                }
                                if (jsonObject.has(IMConstants.WEB_PORT) && !jsonObject.isNull(IMConstants.WEB_PORT)) {
                                    hostBean.setWeb_port(jsonObject.getInt(IMConstants.WEB_PORT));
                                }
                                list.add(hostBean);
                            }
                        }
                        hostListBean.setList(list);
                    }

                }
            }
        }
        return hostListBean;
    }

    public static SipBean getSipBean(String json) throws JSONException {
        SipBean sipBean = new SipBean();
        if (!TextUtils.isEmpty(json)) {
            JSONObject obj = new JSONObject(json);
            if (obj != null) {
                if (obj.has(IMConstants.ERRCODE) && !obj.isNull(IMConstants.ERRCODE)) {
                    sipBean.setErrcode((obj.getString(IMConstants.ERRCODE)));
                }
                if (obj.has(IMConstants.CALLER) && !obj.isNull(IMConstants.CALLER)) {
                    sipBean.setCaller((obj.getString(IMConstants.CALLER)));
                }
                if (obj.has(IMConstants.APPID) && !obj.isNull(IMConstants.APPID)) {
                    sipBean.setAppid((obj.getString(IMConstants.APPID)));
                }
                if (obj.has(IMConstants.CALLEE) && !obj.isNull(IMConstants.CALLEE)) {
                    sipBean.setCallee((obj.getString(IMConstants.CALLEE)));
                }
                if (obj.has(IMConstants.ERRMSG) && !obj.isNull(IMConstants.ERRMSG)) {
                    sipBean.setErrmsg((obj.getString(IMConstants.ERRMSG)));
                }
                if (obj.has(IMConstants.MSGTAG) && !obj.isNull(IMConstants.MSGTAG)) {
                    sipBean.setMsgtag((obj.getString(IMConstants.MSGTAG)));
                }
                if (obj.has(IMConstants.ISSIP) && !obj.isNull(IMConstants.ISSIP)) {
                    sipBean.setIsSip((obj.getString(IMConstants.ISSIP)));
                }
                if (obj.has(IMConstants.USERID) && !obj.isNull(IMConstants.USERID)) {
                    sipBean.setUserid((obj.getString(IMConstants.USERID)));
                }
                if (obj.has(IMConstants.CALLTYPE) && !obj.isNull(IMConstants.CALLTYPE)) {
                    sipBean.setCallType((obj.getString(IMConstants.CALLTYPE)));
                }
                if (obj.has(IMConstants.ROOMID) && !obj.isNull(IMConstants.ROOMID)) {
                    sipBean.setRoomID((obj.getString(IMConstants.ROOMID)));
                }
                if (obj.has(IMConstants.DIRECTION) && !obj.isNull(IMConstants.DIRECTION)) {
                    sipBean.setDirection((obj.getString(IMConstants.DIRECTION)));
                }
                if (obj.has(IMConstants.CODE) && !obj.isNull(IMConstants.CODE)) {
                    sipBean.setCode((obj.getString(IMConstants.CODE)));
                }
                if (obj.has(IMConstants.MSG) && !obj.isNull(IMConstants.MSG)) {
                    sipBean.setMsg((obj.getString(IMConstants.MSG)));
                }
            }
        }
        return sipBean;
    }

    public static ResponseBean getResponseBean(String json) throws JSONException {
        ResponseBean responseBean = new ResponseBean();
        if (!TextUtils.isEmpty(json)) {
            JSONObject obj = new JSONObject(json);
            if (obj != null) {
                if (obj.has(IMConstants.ERRCODE) && !obj.isNull(IMConstants.ERRCODE)) {
                    responseBean.setErrcode((obj.getString(IMConstants.ERRCODE)));
                }
                if (obj.has(IMConstants.ERRMSG) && !obj.isNull(IMConstants.ERRMSG)) {
                    responseBean.setErrmsg((obj.getString(IMConstants.ERRMSG)));
                }
            }
        }
        return responseBean;
    }

} 


