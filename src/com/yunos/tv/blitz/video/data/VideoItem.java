/**
 * $
 * PROJECT NAME: TvTaoBaoTest
 * PACKAGE NAME: com.yunos.tvtaobaotest.view
 * FILE NAME: MenuItem.java
 * CREATED TIME: 2015-3-6
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video.data;


import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 瑙嗛鑿滃崟椤�
 * @version
 * @author hanqi
 * @data 2015-3-6 涓嬪崍4:28:45
 */
public class VideoItem {

    private String id;
    /**
     * {@link Constants.VIDEO_CHANNEL_TYPE, @link Constants.VIDEO_YINGSHI_TYPE}
     */
    private int type = 0; //影视类型，0默认值，无效，1直播，2是点播 3点播
    private String title;
    private String icon;
    private String video;
    private int start = 0;
    private String m3u8_data;

    public static VideoItem fromJson(String json) {
        if (null == json) {
            return null;
        }
        VideoItem item = null;
        try {
            JSONObject obj = new JSONObject(json);
            item = fromJson(obj);
        } catch (JSONException e) {
        }
        return item;
    }

    public static VideoItem fromJson(JSONObject obj) {
        VideoItem item = new VideoItem();
        item.id = obj.optString("id");
        item.type = obj.optInt("type");
        item.title = obj.optString("title");
        item.icon = obj.optString("icon");
        item.video = obj.optString("video");
        item.start = obj.optInt("start", 0);
        item.m3u8_data = obj.optString("m3u8_data");
        return item;
    }

    public String toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("type", type);
            jsonObj.put("title", title);
            jsonObj.put("icon", icon);
            jsonObj.put("video", video);
            jsonObj.put("start", start);
            jsonObj.put("m3u8_data", m3u8_data);
        } catch (JSONException e) {
        }
        return jsonObj.toString();
    }

    public boolean isUseFul() {
        if (TextUtils.isEmpty(getId()) && TextUtils.isEmpty(getVideo())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MenuItem [id=" + id + ", type=" + type + ", title=" + title + ", icon=" + icon + ", video="
                + video.replaceAll("\\n", " ") + ", start=" + start + ", m3u8_data=" + m3u8_data + "]";
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the video
     */
    public String getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the m3u8_data
     */
    public String getM3u8_data() {
        return m3u8_data;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @param m3u8_data the m3u8_data to set
     */
    public void setM3u8_data(String m3u8_data) {
        this.m3u8_data = m3u8_data;
    }

}
