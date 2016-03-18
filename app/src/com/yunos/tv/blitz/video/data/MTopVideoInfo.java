package com.yunos.tv.blitz.video.data;


import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 点播mtop接口返回的数据对象
 * @version
 * @author 程肖
 * @author hanqi
 * @data 2015-4-1 下午2:29:44
 */
public class MTopVideoInfo implements Parcelable {

    public static final String TAG = "MTopVideoInfo";

    public String v4k = "";
    public String v1080 = "";// 超清
    public String v720 = "";// 高清
    public String v480 = "";// 清晰
    public String v320 = "";// 流畅

    public boolean trial = true;// 是否是试看
    public boolean tokenValid = false;// token是否失效
    // public String mp4v1080 = "";
    // public String mp4v720 = "";
    // public String mp4v480 = "";
    // public String mp4v320 = "";
    //
    // public String moretvUrl = "";// moretv的url，根据这个url再去请求各个清晰度的播放地址
    // #EXT-X-HTTPDNS:VERSION=1.0,METHOD=GET, DNS=d.tv.taobao.com,
    // URI="http://httpdns.danuoyi.tbcache.com/getip.json"
    public String hlsContent = "";
    // httpdns
    public String method = "";
    public String tvHost = "";
    public String dnsAddress = "";

    public String drmToken = "";//drmtoken用于传给播放器播放drm视频

    // ---

    public static MTopVideoInfo fromJson(JSONObject obj) {
        MTopVideoInfo item = null;
        if (null != obj) {
            item = new MTopVideoInfo(obj);
        }
        return item;
    }

    public MTopVideoInfo() {
    }

    /**
     * @param ext
     * @param from
     *            0:淘tv 1:华数 2:聚合
     */
    public MTopVideoInfo(JSONObject ext) {
        try {
            this.trial = ext.optBoolean("trial");
            this.tokenValid = ext.optBoolean("tokenValid");

            if (ext.has("hlsContent")) {
                this.hlsContent = ext.optString("hlsContent");
            }

            if (ext.has("drmToken")) {
                this.drmToken = ext.optString("drmToken");
            }

            if (ext.has("httpDns")) {
                JSONObject httpdns = ext.optJSONObject("httpDns");
                this.method = httpdns.optString("method");
                this.tvHost = httpdns.optString("tvHost");
                this.dnsAddress = httpdns.optString("dnsAddress");
            }

            if (ext.has("sourceInfo")) {
                JSONObject sourceInfo = ext.optJSONObject("sourceInfo");
                this.v1080 = sourceInfo.optString("v1080tv");
                this.v720 = sourceInfo.optString("v720tv");
                this.v480 = sourceInfo.optString("v720");
                this.v320 = sourceInfo.optString("v480");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        // if (ext.has("mp4SourceInfo")) {
        // JSONObject sourceInfo = ext.optJSONObject("mp4SourceInfo");
        // this.mp4v1080 = sourceInfo.optString("v1080");
        // this.mp4v720 = sourceInfo.optString("v720");
        // this.mp4v480 = sourceInfo.optString("v480");
        // this.mp4v320 = sourceInfo.optString("v320");
        // }
        //
        // if (ext.has("moreTVResources")) {
        // JSONArray sourceInfo = ext.optJSONArray("moreTVResources");
        // if (sourceInfo.length() > 0) {
        // this.moretvUrl = sourceInfo.opt(0).toString();
        // }
        // }
        //
    }

    /**
     * 返回播放地址
     * 按 “高清--清晰---流畅--超清--4k” 的顺序优先选择播放地址
     * @return
     */
    public String getVideoUrl() {
        String url = null;
        if (!TextUtils.isEmpty(v720)) {
            url = v720;
        } else if (!TextUtils.isEmpty(v480)) {
            url = v480;
        } else if (!TextUtils.isEmpty(v320)) {
            url = v320;
        } else if (!TextUtils.isEmpty(v1080)) {
            url = v1080;
        } else if (!TextUtils.isEmpty(v4k)) {
            url = v4k;
        }
        return url;
    }

    public boolean isEmpty() {
        if (!TextUtils.isEmpty(this.v1080)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.v720)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.v480)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.v320)) {
            return false;
        }
        if (!TextUtils.isEmpty(v4k)) {
            return false;
        }
        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(v4k);
        dest.writeString(v1080);
        dest.writeString(v720);
        dest.writeString(v480);
        dest.writeString(v320);

        // dest.writeString(mp4v1080);
        // dest.writeString(mp4v720);
        // dest.writeString(mp4v480);
        // dest.writeString(mp4v320);
        // dest.writeString(moretvUrl);
        //
        // dest.writeString(hlsContent);
    }

    public static final Parcelable.Creator<MTopVideoInfo> CREATOR = new Parcelable.Creator<MTopVideoInfo>() {

        @Override
        public MTopVideoInfo createFromParcel(Parcel source) {
            // 写入parcel和反序列化parcel时顺序一定要相同，不然数据会出错
            MTopVideoInfo p = new MTopVideoInfo();
            p.v4k = source.readString();
            p.v1080 = source.readString();
            p.v720 = source.readString();
            p.v480 = source.readString();
            p.v320 = source.readString();

            // p.mp4v1080 = source.readString();
            // p.mp4v720 = source.readString();
            // p.mp4v480 = source.readString();
            // p.mp4v320 = source.readString();
            // p.moretvUrl = source.readString();
            //
            // p.hlsContent = source.readString();
            return p;
        }

        @Override
        public MTopVideoInfo[] newArray(int size) {
            return new MTopVideoInfo[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }
}
