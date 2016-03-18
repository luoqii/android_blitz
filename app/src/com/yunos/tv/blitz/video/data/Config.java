/**
 * $
 * PROJECT NAME: K2WebViewSample
 * PACKAGE NAME: com.taobao.K2WebView.data
 * FILE NAME: Config.java
 * CREATED TIME: 2015-3-25
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video.data;


import android.util.Log;

import com.yunos.tv.blitz.video.VideoHelper;
import com.yunos.tv.media.MediaPlayer;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2015-3-25 上午11:07:23
 */
public class Config {

    private static final String TAG = "Config";

    //是否debug模式（日志开关）
    private static boolean debug = true;
    private static String videoSystemConfigName = "/system/media/yingshi/yingshi_performance_config.json";
    private static int mPlayerType = MediaPlayer.SYSTEM_MEDIA_PLAYER;
    public static String mDeviceMedia;

    static {
        //initPlayerType();
    }

    /**
     * 初始化播放器参数，因为联盟盒子不支持ado播放器，所以要根据系统配置来选择播放器
     */
    public static void initPlayerType() {
        mPlayerType = VideoHelper.getSystemPlayerType();
        switch (mPlayerType) {
            case MediaPlayer.SYSTEM_MEDIA_PLAYER:
            case MediaPlayer.ADO_MEDIA_PALER:
                break;
            default:
                mPlayerType = MediaPlayer.SYSTEM_MEDIA_PLAYER;
                break;
        }
        Log.i(TAG, TAG + ".initPlayerType mPlayerType=" + mPlayerType);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Config.debug = debug;
    }

    public static String getVideoSystemConfigName() {
        return videoSystemConfigName;
    }

    public static void setVideoSystemConfigName(String videoSystemConfigName) {
        Config.videoSystemConfigName = videoSystemConfigName;
    }

    public static int getPlayerType() {
        return mPlayerType;
    }

    public static void setPlayerType(int playerType) {
        Config.mPlayerType = playerType;
    }

}
