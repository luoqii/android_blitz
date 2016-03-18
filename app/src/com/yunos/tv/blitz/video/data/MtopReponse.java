/**
 * $
 * PROJECT NAME: K2WebViewSample
 * PACKAGE NAME: com.taobao.K2WebView.VideoView
 * FILE NAME: MtopReponse.java
 * CREATED TIME: 2015-4-1
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video.data;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

/**
 * mtop接口返回的数据对象
 * @version
 * @author hanqi
 * @data 2015-4-1 下午3:05:27
 */
public class MtopReponse {

    //api名称
    private String api;
    //api版本
    private String v;
    // 执行的结果和错误信息
    private SparseArray<String> ret;
    // 
    private Map<String, JSONObject> data;

    public static MtopReponse fromJson(String reponse) {
        MtopReponse item = null;
        if (null == reponse) {
            return item;
        }
        try {
            JSONObject obj = new JSONObject(reponse);
            item = new MtopReponse();
            item.api = obj.optString("api");
            item.v = obj.optString("v");
            JSONArray array = obj.optJSONArray("ret");
            if (null != array && array.length() > 0) {
                SparseArray<String> rets = new SparseArray<String>();
                for (int i = 0; i < array.length(); i++) {
                    String ret = array.optString(i);
                    rets.put(i, ret);
                }
            }
            if (obj.has("data")) {
                JSONObject data = obj.optJSONObject("data");
                if (null != data) {
                    item.data = new HashMap<String, JSONObject>();
                    JSONObject result = data.optJSONObject("result");
                    item.data.put("result", result);
                }
            }
        } catch (JSONException e) {
        }

        return item;
    }

    public JSONObject getResult() {
        JSONObject info = null;
        if (null != data) {
            info = data.get("result");
        }
        return info;
    }

    public boolean isSuccess() {
        boolean success = false;
        if (null != ret && ret.size() > 0) {
            String s = ret.get(0);
            if (s.startsWith("SUCCESS::")) {
                success = true;
            }
        }
        return success;
    }

    public String getApi() {
        return api;
    }

    public String getV() {
        return v;
    }

    public SparseArray<String> getRet() {
        return ret;
    }

    public Map<String, JSONObject> getData() {
        return data;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setV(String v) {
        this.v = v;
    }

    public void setRet(SparseArray<String> ret) {
        this.ret = ret;
    }

    public void setData(Map<String, JSONObject> data) {
        this.data = data;
    }

}
