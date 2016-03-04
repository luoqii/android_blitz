/**
 * $
 * PROJECT NAME: K2WebViewSample
 * PACKAGE NAME: com.taobao.K2WebView.common
 * FILE NAME: VideoUtil.java
 * CREATED TIME: 2015-4-1
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.yunos.tv.blitz.video.data.Config;
import com.yunos.tv.lib.SystemProUtils;
import com.yunos.tv.media.MediaPlayer;

/**
 * 视频相关的静态函数
 * @version
 * @author hanqi
 * @data 2015-4-1 下午1:49:36
 */
public class VideoHelper {

    private static final String TAG = "VideoHelper";

    /**
     * 获取系统配置，device media
     * @param context
     * @param playerType
     * @return
     */
    public static String getDeviceMedia(Context context) {
        if (TextUtils.isEmpty(Config.mDeviceMedia)) {
            Config.mDeviceMedia = SystemProUtils.getMediaParams() + ",drm";
            try {
                if (Config.getPlayerType() == MediaPlayer.ADO_MEDIA_PALER) {
                    String isH265 = isAdoH265(context);
                    if (!TextUtils.isEmpty(isH265)) {
                        Config.mDeviceMedia = (Config.mDeviceMedia + "," + isAdoH265(context));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG,
                    TAG + ".getDeviceMedia value=" + Config.mDeviceMedia + ", playerType=" + Config.getPlayerType());
        }
        return Config.mDeviceMedia;
    }

    private static String isAdoH265(Context context) {
        try {
            // 获取其他程序对应的Context
            Context adoContext = context.getApplicationContext().createPackageContext("com.yunos.adoplayer.service",
                    Context.CONTEXT_IGNORE_SECURITY);
            // 使用其他程序的COntext获取对应的SharedPreferences
            @SuppressWarnings("deprecation")
            SharedPreferences ps = adoContext.getSharedPreferences("adoplayer_ability_sharedpreferences",
                    Context.MODE_WORLD_READABLE);
            String isH265 = ps.getString("adoplayer.ability.h265.soft", "");
            return isH265;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 优先读取assets/config/yingshi_performance_config.json，如果没有，则读取/system/media/yingshi_performance_config.json，再没有，
     * 则采用代码中定义的默认值.
     * 一般地，在assets/config下不要放置文件，除非是某款盒子特殊走自升级，同时涉及到配置文件也有更新的情况，要单独为这次升级出个asset下带文件的影视包。
     */
    public static int getSystemPlayerType() {
        String configFile = Config.getVideoSystemConfigName();
        int playerType = MediaPlayer.SYSTEM_MEDIA_PLAYER;
        try {
            InputStream inputStream = null;
            // 读取system/media/yingshi/下
            File file = new File(configFile);
            if (file.exists()) {
            	Log.i(TAG, "readJson system config exit!" + configFile);
                try {
                    inputStream = new FileInputStream(file);
                } catch (Exception e) {
                	Log.e(TAG, "getAssets().open exception", e);
                    return playerType;
                }
            } else {
            	Log.i(TAG, "readJson fail! system config not exit!" + configFile);
            }

            if (inputStream != null) {
                JSONObject json = new JSONObject(IOUtils.readString(inputStream));
                Log.d(TAG, "readJson performance json content:" + json.toString());

                playerType = json.optInt("player_type", MediaPlayer.ADO_MEDIA_PALER);
            } else {
            	Log.e(TAG, "readJson fail! inputStream is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerType;
    }
}
