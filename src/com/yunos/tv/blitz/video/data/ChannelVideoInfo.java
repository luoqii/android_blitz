/**
 * $
 * PROJECT NAME: K2WebViewSample
 * PACKAGE NAME: com.taobao.K2WebView.VideoView
 * FILE NAME: ChannelVideoInfo.java
 * CREATED TIME: 2015-4-2
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video.data;


import java.io.Serializable;

import org.json.JSONObject;

/**
 * 直播视频信息数据对象
 * @version
 * @author hanqi
 * @data 2015-4-2 下午8:07:13
 */
public class ChannelVideoInfo implements Serializable {

    private static final long serialVersionUID = 6628258462312986516L;
    private String httpUrl;
    private String channelID;

    public static ChannelVideoInfo fromJson(JSONObject obj) {
        ChannelVideoInfo item = null;
        if (null != obj) {
            item = new ChannelVideoInfo();
            item.httpUrl = obj.optString("httpUrl");
            item.channelID = obj.optString("channelID");
        }
        return item;
    }

    @Override
    public String toString() {
        return "ChannelVideoInfo [httpUrl=" + httpUrl + ", channelID=" + channelID + "]";
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

}
