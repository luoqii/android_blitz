package com.yunos.tv.blitz.video;


import org.json.JSONException;
import org.json.JSONObject;

import yunos.media.drm.DrmManagerCreator;
import yunos.media.drm.interfc.DrmManager;
import yunos.media.drm.interfc.DrmManager.DrmErrorListener;
import yunos.media.drm.interfc.DrmManager.ICallBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceParams;
import com.intertrust.wasabi.media.PlaylistProxy.MediaSourceType;
import com.yunos.tv.blitz.video.data.ChannelVideoInfo;
import com.yunos.tv.blitz.video.data.LDebug;
import com.yunos.tv.blitz.video.data.MTopVideoInfo;
import com.yunos.tv.blitz.video.data.MtopReponse;
import com.yunos.tv.blitz.video.data.VideoItem;
import com.yunos.tv.media.MediaPlayer;
import com.yunos.tv.media.view.MediaCenterView;
import com.yunos.tv.media.view.YunosVideoView;

public class BzVideoView extends YunosVideoView {

    public static int EVENT_SEEK_COMPLETE = 7;
    public static int EVENT_VOLUME_CHANGED = 8;
    public static int EVENT_FULLSCREEN = 9;
    public static int EVENT_EXIT_FULLSCREEN = 10;

    //影视类型 1直播，2点播，3搜狐
    public static final int VIDEO_TYPE_ZHIBO = 1;
    public static final int VIDEO_TYPE_DIANBO = 2;
    public static final int VIDEO_TYPE_SOHU = 3;

    private final String TAG = "BzVideoView";

    private int mLastPosition = 0;
    private Object[] mLastParams;

    private byte[] mId;
    public static WakeLock mWakeLock;

    //播放DRM相关
    private DrmManager mDrmManager;

    public BzVideoView(Context context) {
        super(context);
    }

    public BzVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BzVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWebViewId(byte[] id) {
        mId = id;
    }

    public byte[] getWebViewId() {
        return mId;
    }

    @Override
    public void pause() {
        if (isAdPlaying()) { //广告不允许暂停
            LDebug.d(TAG, "invalid pause! ad is playing");
            return;
        }
        if (isPlaying()) {
            mLastPosition = getCurrentPosition();
            LDebug.i(TAG, TAG + ".pause mLastPosition=" + mLastPosition);
        }
        super.pause();
    }

    public void customError(int errorCode) {
        customError(errorCode, 0);
        setVideoViewBg();
    }

    public void play(VideoItem item) {
        LDebug.i(TAG, TAG + ".play item=" + item);
        if (null != item && ((null != item.getId())) || (null != item.getVideo())) {
            item.setStart(mLastPosition);
        } else {
            mLastPosition = 0;
        }
        mLastParams = null;

        if (null == item || TextUtils.isEmpty(item.getVideo())) {
            LDebug.w(TAG, TAG + ".play item=" + item);
            customError(MediaCenterView.ERRORCODE_CUSTOM_NO_VIDEO);
            return;
        }
        String video = item.getVideo();

        switch (item.getType()) {
            case BzVideoView.VIDEO_TYPE_ZHIBO: //直播
                LDebug.d(TAG, TAG + ".play zhibo");
                if (getVideoViewType() != YunosVideoView.VIDEOVIEW_TYPE_YUNOS) {
                    setVideoView(YunosVideoView.VIDEOVIEW_TYPE_YUNOS);
                }
                if (video.startsWith("{") && video.endsWith("}")) {
                    MtopReponse reponse = MtopReponse.fromJson(video);
                    if (null == reponse) {
                        LDebug.w(TAG, TAG + ".play json reponse is null ");
                        customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_ERROR);
                        return;
                    }
                    JSONObject obj = reponse.getResult();
                    if (null == obj) {
                        LDebug.w(TAG, TAG + ".play json reponse is null ");
                        customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR);
                        return;
                    }
                    ChannelVideoInfo info = ChannelVideoInfo.fromJson(obj);
                    if (TextUtils.isEmpty(info.getHttpUrl())) {
                        customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR);
                    }
                    setVideoInfo(info.getHttpUrl());
                } else {
                    setVideoInfo(video);
                }
                break;
            case BzVideoView.VIDEO_TYPE_DIANBO: //点播
                LDebug.d(TAG, TAG + ".play dianbo");
                if (getVideoViewType() != YunosVideoView.VIDEOVIEW_TYPE_YUNOS) {
                    setVideoView(YunosVideoView.VIDEOVIEW_TYPE_YUNOS);
                }
                //判断如果是json数据，则解析json数据
                if (video.startsWith("{") && video.endsWith("}")) {
                    LDebug.i(TAG, TAG + ".play json");
                    MtopReponse reponse = MtopReponse.fromJson(video);
                    if (null == reponse) {
                        LDebug.w(TAG, TAG + ".play json reponse is null ");
                        customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_ERROR);
                        return;
                    }
                    JSONObject obj = reponse.getResult();
                    if (null == obj) {
                        LDebug.w(TAG, TAG + ".play json info is null ");
                        customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_ERROR);
                        return;
                    }
                    MTopVideoInfo info = MTopVideoInfo.fromJson(obj);
                    if (!TextUtils.isEmpty(info.drmToken)) {
                        drmVideoPlay(info.getVideoUrl(), info.drmToken); //播放drm
                    } else {
                        if (getMediaPlayerType() == MediaPlayer.SYSTEM_MEDIA_PLAYER) {
                            String url = info.getVideoUrl();
                            if (TextUtils.isEmpty(url)) {
                                LDebug.w(TAG, TAG + ".play SYSTEM_MEDIA_PLAYER json error video=" + video);
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR);
                                return;
                            }
                            if (!TextUtils.isEmpty(item.getM3u8_data())) {
                                setVideoInfo(url, item.getStart(), item.getM3u8_data());
                            } else if (item.getStart() > 0) {
                                setVideoInfo(url, item.getStart());
                            } else {
                                setVideoInfo(url);
                            }
                        } else if (getMediaPlayerType() == MediaPlayer.ADO_MEDIA_PALER) {

                            if (TextUtils.isEmpty(info.hlsContent)) {
                                LDebug.w(TAG, TAG + ".play ADO_MEDIA_PALER json error ");
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR);
                                return;
                            }
                            setVideoInfo(Uri.parse("m3u8://string.m3u8"), item.getStart(), info.hlsContent);

                        }
                    }
                } else if (video.startsWith("#EXTM3U") || video.indexOf("#EXT-X-STREAM-INF") != -1) {
                    //如果是m3u8的数据类型

                    LDebug.i(TAG, TAG + ".play m3u8");
                    if (getMediaPlayerType() != MediaPlayer.ADO_MEDIA_PALER) {
                        setMediaPlayerType(MediaPlayer.ADO_MEDIA_PALER);
                    }
                    LDebug.i(TAG, TAG + ".play m3u8 video=" + video);
                    setVideoInfo(Uri.parse("m3u8://string.m3u8"), item.getStart(), video);
                } else {
                    //如果是其他，如http开头的

                    LDebug.i(TAG, TAG + ".play http");
                    if (!TextUtils.isEmpty(item.getM3u8_data())) {
                        setVideoInfo(video, item.getStart(), item.getM3u8_data());
                    } else if (item.getStart() > 0) {
                        setVideoInfo(video, item.getStart());
                    } else {
                        setVideoInfo(video);
                    }
                }
                LDebug.i(TAG, TAG + ".play to start");
                start();
                break;
            case BzVideoView.VIDEO_TYPE_SOHU: //搜狐
                LDebug.d(TAG, TAG + ".play sohu");
                if (!item.isUseFul()) {
                    customError(MediaCenterView.ERRORCODE_CUSTOM_NO_VIDEO);
                }
                if (getVideoViewType() != YunosVideoView.VIDEOVIEW_TYPE_SOHU) {
                    setVideoView(YunosVideoView.VIDEOVIEW_TYPE_SOHU);
                }
                if (!TextUtils.isEmpty(item.getVideo())) {
                    setVideoInfo(item.getVideo());
                } else if (!TextUtils.isEmpty(item.getId())) {
                    setVideoInfo(item.getId());
                }
                break;
            default:
                //setVideoInfo(video);
                LDebug.e(TAG, TAG + ".play other type http");
                JSONObject object = new JSONObject();
                try {
                    object.put("uri", video);
                    object.put("m3u8", item.getM3u8_data());
                    object.put("starttime", item.getStart());
                    object.put("vid", item.getId());
                    object.put("name", item.getTitle());

                    super.setVideoInfo(object.toString());
                } catch (JSONException e) {
                }
                start();
                break;
               
                
                
                
        }

    }

    @Override
    public void setVideoInfo(Object... params) {
        if (null != params && params.length > 0) {
            LDebug.i(TAG, TAG + ".setVideoInfo params=" + params[0]);
        }
        mLastParams = params;
        if (null == params) {
            return;
        }
        super.setVideoInfo(params);
    }

    @Override
    public void stopPlayback() {
        if (isPlaying()) {
            mLastPosition = getCurrentPosition();
            LDebug.i(TAG, TAG + ".stopPlayback mLastPosition=" + mLastPosition);
        }
        releaseDrm();
        super.stopPlayback();
    }

    /**
     * 停止播放
     */
    public void stop() {
        LDebug.i(TAG, TAG + ".stop ");
        this.stopPlayback();
    }

    /**
     * 重播，从头开始播
     */
    public void rePlay() {
        rePlay(false);
    }

    /**
     * 重播，是否从上次退出的位置续播（在VideoView对象未销毁前有效）
     * @param seek true 表示从上次退出的位置开始播放，false表示从头开始播
     */
    public void rePlay(boolean seek) {
        LDebug.i(TAG, TAG + ".rePlay seek=" + seek + ", mLastPosition=" + mLastPosition);
        if (!isPause()) {
            setVideoInfo(mLastParams);
            if (seek) {
                seekTo(mLastPosition);
            }
        }
        start();
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
        //TODO post seek complete to lightcore
    }

    @SuppressLint("Wakelock")
    @SuppressWarnings("deprecation")
    @Override
    public void setCurrentState(int state) {
        super.setCurrentState(state);

        LDebug.i(TAG, TAG + ".setCurrentState state=" + state);
        if (state == YunosVideoView.STATE_IDLE || state == YunosVideoView.STATE_ERROR) {
            if (null != mWakeLock && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock = null;
            LDebug.i(TAG, TAG + ".setCurrentState mWakeLock unlock");
        } else {
            if (null == mWakeLock) {
                PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE
                        | PowerManager.FULL_WAKE_LOCK, getClass().getSimpleName());
            }
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
            LDebug.i(TAG, TAG + ".setCurrentState mWakeLock lock");
        }
        switch (state) {
            case YunosVideoView.STATE_LOADING:
            case YunosVideoView.STATE_PREPARING:
            case YunosVideoView.STATE_PREPARED:
            case YunosVideoView.STATE_PLAYING:
                clearVideoViewBg();
                break;
            default:
                break;
        }
        //TODO post state to lightcore
        
    }

    @Override
    public void fullScreen() {
        super.fullScreen();
        requestFocus();
        LDebug.i(TAG, TAG + ".fullScreen end ");
    }


    private void drmVideoPlay(final String url, String drmToken) {

        try {
            mDrmManager = new DrmManagerCreator(url, getContext()).createDrmManager();
            mDrmManager.setOnDrmErrorListener(new DrmErrorListener() {

                @Override
                public void onErrorListener(DrmManager arg0, int arg1, int arg2, Object arg3) {
                    customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_AUTHORITY);
                }
            });
            mDrmManager.makeUrl(drmToken, MediaSourceType.HLS, new MediaSourceParams(), new ICallBack() {

                @Override
                public void onComplete(Uri uri, int errorCode) {
                    if (uri != null && uri.toString().length() > 0) {
                        if (getMediaPlayerType() == MediaPlayer.ADO_MEDIA_PALER) {
                            setVideoInfo(uri, mLastPosition);
                        } else {
                            setVideoInfo(uri);
                            seekTo(mLastPosition);
                        }
                    } else {
                        /*
                         * ERRORCODE_CUSTOMER_DRM_INIT = -55100; //drm，初始化失败
                         * public static final int ERRORCODE_CUSTOMER_DRM_SEVER
                         * = -20802; //drm，服务端错误 public static final int
                         * ERRORCODE_CUSTOMER_DRM_AUTHORITY = -100605;
                         * //drm，认证出错 public static final int
                         * ERRORCODE_CUSTOMER_DRM_OTHER = 2008; //drm，其他错误
                         */
                        LDebug.w(TAG, "drmManager Exception:errorCode=" + errorCode);
                        switch (errorCode) {
                            case MediaCenterView.ERRORCODE_CUSTOMER_DRM_INIT:
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_INIT);
                                sendTryMessage();
                                break;
                            case MediaCenterView.ERRORCODE_CUSTOMER_DRM_SEVER:
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_SEVER);
                                sendTryMessage();
                                break;
                            case MediaCenterView.ERRORCODE_CUSTOMER_DRM_AUTHORITY:
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_AUTHORITY);
                                sendTryMessage();
                                break;
                            default:
                                customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_OTHER);
                                sendTryMessage();
                                break;
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            customError(MediaCenterView.ERRORCODE_CUSTOMER_DRM_INIT);
            sendTryMessage();
            LDebug.w(TAG, "drmManager Exception");
        }
    }

    public void sendTryMessage() {
        //TODO
    }

    public void releaseDrm() {
        try {
            if (mDrmManager != null) {
                mDrmManager.shutDown();
            }
        } catch (Exception e) {
        }
    }
}
