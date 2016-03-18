package com.yunos.tv.blitz.video;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunos.tv.blitz.video.data.Config;
import com.yunos.tv.blitz.video.data.LDebug;
import com.yunos.tv.blitz.video.data.VideoItem;
import com.yunos.tv.blitz.video.data.VideoViewInfo;
import com.yunos.tv.media.IMediaPlayer;
import com.yunos.tv.media.view.YunosVideoView;
import com.yunos.tv.media.view.YunosVideoView.FullScreenChangedListener;

/**
 * 播放器管理
 * @version
 * @author hanqi
 * @data 2015-3-21 上午11:47:54
 */
public class VideoViewManager {

    private final String TAG = "VideoViewManager";

    private Map<String, VideoViewInfo> mViewMap = new HashMap<String, VideoViewInfo>();
    private List<VideoItem> mItems;
    private FrameLayout mVideoLayout;
    private FrameLayout mControllLayout;
    private Context mContext;
    //用户点击选集菜单项时回调的js方法
    private String mXuanjiItemClickFunc;
    private FrameLayout mWebView;

    private BzVideoView mVideoView;
    private LifeMediaController mController;
    private int mPlayerType = Config.getPlayerType();

    public void Init(FrameLayout webView) {
    	Log.d(TAG,"Init");
        mWebView = webView;
        mContext = webView.getContext();

        mVideoLayout = new FrameLayout(mContext);
        mVideoLayout.setBackgroundColor(Color.parseColor("#00000000"));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoLayout.setLayoutParams(lp);
        webView.addView(mVideoLayout, lp);
        
        videoNativeInit();
    }

    public BzVideoView getVideoView() {
        return mVideoView;
    }

    public LifeMediaController getMediacontroller() {
        return mController;
    }

    public void setXuanjiItemClickFunc(String func) {
        mXuanjiItemClickFunc = func;
        LDebug.i(TAG, TAG + ".setXuanjiItemClickFunc mXuanjiItemClickFunc=" + mXuanjiItemClickFunc);
        if (null != mController && mController instanceof LifeMediaController) {
            LifeMediaController lifeMediaController = (LifeMediaController) mController;
            lifeMediaController.setXuanjiItemClick(mXuanjiItemClickFunc);
        }
    }

    public void initControllLayout() {
        if (null == mControllLayout) {
            mControllLayout = new FrameLayout(mContext);
            mControllLayout.setBackgroundColor(Color.parseColor("#00000000"));
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mControllLayout.setLayoutParams(lp);
            mWebView.addView(mControllLayout, lp);
        }
    }

    public void initVideo(byte[] id) {
        LDebug.i(TAG, "initVideo id=" + id + ", mPlayerType=" + mPlayerType);
        mVideoView = new BzVideoView(mContext);
        mVideoView.setWebViewId(id);
        mVideoView.setDimensionFull();
        mVideoView.setFocusable(true);
        mVideoView.setBackgroundColor(Color.parseColor("#000000"));
        mVideoView.setVideoView(YunosVideoView.VIDEOVIEW_TYPE_YUNOS);
        mVideoView.setMediaPlayerType(mPlayerType);

        FrameLayout.LayoutParams videolp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        mVideoView.setLayoutParams(videolp);
        mVideoLayout.addView(mVideoView, 0, videolp);

        if (null == mControllLayout) {
            initControllLayout();
        }

        if (null == mController) {
            mController = new LifeMediaController(mContext);
        }

        mControllLayout.bringToFront();

        LDebug.i(TAG, TAG + ".intiVideo id=" + id + ", id=" + Arrays.toString(id));
        mVideoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LDebug.i(TAG, TAG + ".AddVideoView.video.OnClickListener.onClick videoView.toggleScreen fullscreen="
                        + mVideoView.isFullScreen() + " ==> " + (!mVideoView.isFullScreen()));
                mVideoView.toggleScreen();
            }
        });

        mVideoView.setFullScreenChangedListener(new FullScreenChangedListener() {

            @Override
            public void onBeforeUnFullScreen() {
            }

            @Override
            public void onBeforeFullScreen() {
            }

            @Override
            public void onAfterUnFullScreen() {//褰撻��鍑哄叏灞忔椂锛岃灏嗘帶鍒跺共鎺�
                LDebug.i(TAG, TAG + ".FullScreenChangedListener.onAfterUnFullScreen");

                if (null != mController) {
                    mController.hide();
                }
                mVideoView.setMediaController(null);
                mControllLayout.bringToFront();
            }

            @Override
            public void onAfterFullScreen() {
                LDebug.i(TAG, TAG + ".FullScreenChangedListener.onAfterFullScreen mController=" + mController);
                String key = Arrays.toString(mVideoView.getWebViewId());
                LDebug.i(TAG, TAG + ".FullScreenChangedListener.onAfterFullScreen key=" + key);
                if (mController != null) {
                    mVideoView.setMediaController(mController);
                    mController.setXuanjiItemClick(mXuanjiItemClickFunc);
                    mController.setXuanjiVideoItems(mItems);
                }

            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(Object mp, int what, int extra) {
                LDebug.w(TAG, TAG + ".intiVideo.OnErrorListener mp=" + mp + ", what=" + what + ", extra=" + extra);
                return false;
            }
        });

    }

    public void pause() {
        if (null != mVideoView) {
            mVideoView.pause();
        }
    }

    public void stop() {
        if (null != mVideoView) {
            mVideoView.stop();
        }
    }

    /**
     * 重播，从头开始播
     */
    public void rePlay() {
        if (null != mVideoView) {
            mVideoView.rePlay();
        }
    }

    /**
     * 重播，是否从上次退出的位置播（在VideoView对象未销毁前有效）
     * @param seek true 表示从上次退出的位置开始播放，false表示从头开始播
     */
    public void rePlay(boolean seek) {
        if (null != mVideoView) {
            mVideoView.rePlay(seek);
        }
    }
    
    public void clear() {
        LDebug.i(TAG, "clear");
        if (null != BzVideoView.mWakeLock) {
            if (BzVideoView.mWakeLock.isHeld()) {
                BzVideoView.mWakeLock.release();
            }
            BzVideoView.mWakeLock = null;
        }
        if (null != mVideoView) {
            stop();
            mVideoView = null;
            if (null != mController) {
                mController.clear();
            }
            
        }
        if (null != mVideoLayout) {
            mVideoLayout.removeAllViews();
            mVideoLayout = null;
        }
        if (null != mViewMap) {
            mViewMap.clear();
        }
        if (null != mItems) {
            mItems.clear();
        }
        if (null != mController) {
            mController.clear();
            mController = null;
        }
    }

    public void destroy() {
        LDebug.i(TAG, "destroy");
        if (null != BzVideoView.mWakeLock) {
            if (BzVideoView.mWakeLock.isHeld()) {
                BzVideoView.mWakeLock.release();
            }
            BzVideoView.mWakeLock = null;
        }
        if (null != mVideoView) {
            stop();
            mVideoView = null;
            if (null != mController) {
                mController.destroy();
            }
        }
        if (null != mViewMap) {
            mViewMap.clear();
        }
        if (null != mItems) {
            mItems.clear();
            mItems = null;
        }
        if (null != mController) {
            mController.destroy();
            mController = null;
        }
        mVideoLayout = null;
        mControllLayout = null;
        mWebView = null;
    }

    /**
     * 播放视频
     * @param item
     */
    public void play(VideoItem item, Integer position) {
        LDebug.i(TAG, TAG + ".play position=" + position + ", item=" + item);
        if (null != mController) {
            if (null != position) {
                mController.setCurrentPos(position);
            } else {
                mController.setCurrentVideoItem(item);
            }
        }
        if (null == mVideoView) {
            //            String msg = mContext.getString(R.string.unknow_error);
            //            showError(msg);
            return;
        }
        if (mVideoView.isPlaying()) {
            mVideoView.stop();
        }
        mVideoView.play(item);
        //        try {
        //            mVideoView.play(item);
        //        } catch (NoVideoPathException e) {
        //            LDebug.i(TAG, "play NoVideoPathException");
        //            //            mVideoView.setCurrentState(YunosVideoView.STATE_ERROR);
        //            //            String msg = mContext.getString(R.string.no_video_url);
        //            //            showError(msg);
        //            mVideoView.customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR, 0);
        //        }
    }

    //    /**
    //     * 显示视频播放错误
    //     * @param message
    //     */
    //    public void showError() {
    //        LDebug.i(TAG, TAG + ".showError message=");
    //        showVideoShade();
    //        if (null != mVideoView) {
    //            mVideoView.customError(MediaCenterView.ERRORCODE_CUSTOMER_MTOP_SEVER_CALLBACK_ERROR, 0);
    //        }
    //        //        if (null != mVideoView && mVideoView.isFullScreen() && null != mController) {
    //        //            LDebug.i(TAG, TAG + ".showError mController");
    //        //            mController.showError(message);
    //        //        } else if (null != mMediaCenterView) {
    //        //            LDebug.i(TAG, TAG + ".showError mMediaCenterView");
    //        //            mMediaCenterView.showError(message);
    //        //        }
    //    }

    /**
     * 因为WebView的SurfaceView的干扰，直接设置视频背景色，无法形成遮罩，所以采用new一个新的视频的方法
     * 当有错误提示时，需要用这个来遮盖视频，让视频看起来变成黑色，再在上面提示错误
     */
    //    protected void showVideoShade() {
    //        if (null != mWebView) {
    //            if (null != mVideoView) {
    //                mVideoView.setVideoViewBg();
    //            }
    //            mWebView.evaluateJavascript("document.getElementsByTagName(\"video\")[0].style.backgroundColor=\"black\";",
    //                    null);
    //        }
    //        LDebug.i(TAG, TAG + ".showVideoShade ");
    //    }

    //    protected void hideVideoShade() {
    //        if (null != mWebView) {
    //            if (null != mVideoView) {
    //                mVideoView.clearVideoViewBg();
    //            }
    //            mWebView.evaluateJavascript(
    //                    "document.getElementsByTagName(\"video\")[0].style.backgroundColor=\"transparent\";", null);
    //        }
    //        LDebug.i(TAG, TAG + ".hideVideoShade ");
    //    }

    /**
     * 添加一个VideoView
     * @param id
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void AddVideoView(final byte[] id, final float x, final float y, final float width, final float height) {
        LDebug.i(TAG, TAG + ".AddVideoView id=" + id + ", x=" + x + ", y=" + y + ", width=" + width + ", height="
                + height);
        if (null == mVideoLayout) {
            return;
        }
        Runnable tr = new Runnable() {

            public void run() {
                if (null == mVideoView) {
                    initVideo(id);
                }

                FrameLayout.LayoutParams lp = getLayoutParams(x, y, width, height);
                VideoViewInfo info = new VideoViewInfo(lp);
                mViewMap.put(Arrays.toString(id), info);
            }
        };
        mVideoLayout.post(tr);
    }

    /**
     * 设置全屏播放时的选集菜单项
     * @param menuInfo
     */
    public void setXuanjiVideoList(final String menuInfo) {
        LDebug.i("test", "setMenuInfo menuInfo=" + menuInfo);
        mItems = buildMenuInfoFromJson(menuInfo);
        if (null != mController && mController instanceof LifeMediaController) {
            LifeMediaController lifeMediaController = (LifeMediaController) mController;
            lifeMediaController.setXuanjiVideoItems(mItems);
        }
    }

    public void SetVideoPath(final byte[] id, final String path) {
    	Log.d(TAG,"SetVideoPath id"+Arrays.toString(id)+"path:"+path);
        if (null == mVideoLayout) {
            return;
        }
        Runnable tr = new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                    }
                    LDebug.i(TAG, TAG + ".SetVideoPath path=" + path);
                    VideoItem item = new VideoItem();
                    item.setVideo(path);
                    play(item, null);
                    mVideoView.setWebViewId(id);
                    VideoViewInfo info = mViewMap.get(Arrays.toString(id));
                    if (null != info && !mVideoView.isFullScreen()) {
                        mVideoView.setLayoutParams(info.getLayoutParams());
                    }
                    mVideoView.setVideoViewVisible();
                }
            }
        };
        mVideoLayout.post(tr);
    }

    public void SetAutoPlay(final byte[] id, final boolean autoPlay) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                //                YunosVideoView videoView = (YunosVideoView) mViewMap.get(Arrays.toString(id));
                //                if (null != videoView) {
                //                    //                    ((YunosVideoView) mViewMap.get(Arrays.toString(id))).mAutoPlay = autoPlay;
                //                }
            }
        });
    }

    public void SetLoop(final byte[] id, final boolean loop) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                //                YunosVideoView videoView = (YunosVideoView) mViewMap.get(Arrays.toString(id));
                //                if (null != videoView) {
                //                    //                  ((LifeVideoView) mViewMap.get(Arrays.toString(id))).mLoop = loop;
                //                }

            }
        });
    }

    public void Pause(final byte[] id) {
        LDebug.i("in Java", "Pause id=" + Arrays.toString(id));
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    if (Arrays.toString(id).equals(Arrays.toString(mVideoView.getWebViewId()))) {
                        return;
                    }
                    mVideoView.pause();
                }
            }
        });
    }

    public void Hide(final byte[] id) {
        LDebug.i("in Java", "hide id=" + Arrays.toString(id));
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.setVideoViewInvisible();
                }
            }
        });
    }

    public void Show(final byte[] id) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.setVideoViewVisible();
                }
            }
        });
    }

    public void Resume(final byte[] id) {
        LDebug.i("in Java", "Resume id=" + Arrays.toString(id));
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                rePlay();
            }
        });
    }

    public void Play(final byte[] id) {
        LDebug.i("in Java", "play --->");
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    if (Arrays.toString(id).equals(Arrays.toString(mVideoView.getWebViewId()))) {
                        LDebug.i(
                                TAG,
                                TAG + ".Play id=" + Arrays.toString(id) + "!="
                                        + Arrays.toString(mVideoView.getWebViewId()));
                        //                        return;
                    }
                    LDebug.i(TAG, TAG + ".Play id=" + Arrays.toString(id));
                    mVideoView.start();
                }
            }
        });
    }

    public void Stop(final byte[] id) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.stopPlayback();
                }
            }
        });
    }

    public int GetDuration(byte[] id) {
        int duration = 0;
        if (null != mVideoView) {
            duration = mVideoView.getDuration();
        }
        return duration;
    }

    public int GetCurrentPosition(byte[] id) {
        int currentPosition = 0;
        if (null != mVideoView) {
            currentPosition = mVideoView.getCurrentPosition();
        }
        return currentPosition;
    }

    public boolean IsPlaying(byte[] id) {
        boolean isPlaying = false;
        if (null != mVideoView) {
            isPlaying = mVideoView.isPlaying();
        }
        return isPlaying;
    }

    public boolean IsPause(byte[] id) {
        boolean isPause = false;
        if (null != mVideoView) {
            isPause = mVideoView.isPause();
        }
        return isPause;
    }

    public void Fullscreen(byte[] id) {
        LDebug.i(TAG, TAG + ".Fullscreen id=" + id);
        if (null != mVideoView) {
            mVideoView.fullScreen();
        }
    }

    public void ToggleScreen(byte[] id) {
        LDebug.i(TAG, TAG + ".ToggleScreen id=" + id);
        if (null != mVideoView) {
            LDebug.i(TAG, TAG + ".ToggleScreen2 id=" + id);
            mVideoView.toggleScreen();
        }
    }

    public void SetCurrentPosition(final byte[] id, final int pos) {
        LDebug.i("in Java", "SetCurrentPosition --->" + pos);
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.seekTo(pos);
                }
            }
        });
    }

    public void setAlpha(final byte[] id, final float alpha) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.setAlpha(alpha);
                }
            }
        });
    }

    public void setEnable(final byte[] id, final boolean isEnable) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView) {
                    mVideoView.setEnabled(isEnable);
                }
            }
        });
    }

    public void RemoveVideoView(final byte[] id) {
        if (null == mVideoLayout) {
            LDebug.w(TAG, TAG + ".RemoveVideoView RemoveVideoView is null");
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                if (null != mVideoView && null != mVideoView.getParent()) {
                    ((ViewGroup) mVideoView.getParent()).removeView(mVideoView);
                }
            }
        });
    }

    public void ResizeViewTo(final byte[] id, final float width, final float height) {
        LDebug.i(TAG, TAG + ".ResizeViewTo id=" + id + ", width=" + width + ", heith=" + height);
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                String key = Arrays.toString(id);
                VideoViewInfo info = mViewMap.get(key);
                if (null != info) {
                    int swidth = (int) (mVideoLayout.getWidth() * width);
                    int sheight = (int) (mVideoLayout.getHeight() * height);
                    FrameLayout.LayoutParams lp = info.getLayoutParams();
                    if (null == lp) {
                        lp = new FrameLayout.LayoutParams(swidth, sheight);
                    } else {
                        lp.width = swidth;
                        lp.height = sheight;
                    }
                    info.setLayoutParams(lp);
                    mViewMap.put(key, info);
                    if (null != mVideoView && key.equals(Arrays.toString(mVideoView.getWebViewId()))
                            && !mVideoView.isFullScreen()) {
                        LDebug.i(TAG, TAG + ".ResizeViewTo setLayoutParams lp=[" + lp.leftMargin + "," + lp.topMargin
                                + "," + lp.rightMargin + "," + lp.bottomMargin + "][" + lp.width + ", " + lp.height
                                + "]");
                        mVideoView.setLayoutParams(lp);
                    }
                }
            }
        });

    }

    public void MoveViewTo(final byte[] id, final float x, final float y, final float width, final float height) {
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                //                LifeVideoView view = (LifeVideoView) mViewMap.get(Arrays.toString(id));
                //                AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) view.mVideo.getLayoutParams();
                //                int swidth = (int) (mLayout.getWidth() * width);
                //                int sheight = (int) (mLayout.getHeight() * height);
                //                lp.x = (int) (mLayout.getWidth() * x) + swidth / 2;
                //                lp.y = (int) (mLayout.getHeight() * y) + sheight / 2;
                //                view.mVideo.setLayoutParams(lp);
                //
                //                YunosVideoView videoView = (YunosVideoView) mViewMap.get(Arrays.toString(id));
                //                if (null != videoView) {
                //                    int swidth = (int) (videoView.getWidth() * width);
                //                    int sheight = (int) (videoView.getHeight() * height);
                //                    ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                //                    lp.width = swidth;
                //                    lp.height = sheight;
                //                    lp.x = (int) (mLayout.getWidth() * x) + swidth / 2;
                //                    lp.y = (int) (mLayout.getHeight() * y) + sheight / 2;
                //                    videoView.setLayoutParams(lp);
                //                }
            }
        });

    }

    public void SetViewRect(final byte[] id, final float x, final float y, final float width, final float height) {
        LDebug.i(TAG, TAG + ".SetViewRect id=" + Arrays.toString(id) + ", width=" + width + ", heith=" + height);
        if (null == mVideoLayout) {
            return;
        }
        mVideoLayout.post(new Runnable() {

            public void run() {
                FrameLayout.LayoutParams lp = getLayoutParams(x, y, width, height);
                String key = Arrays.toString(id);
                VideoViewInfo info = mViewMap.get(key);
                if (null == info) {
                    info = new VideoViewInfo(lp);
                } else {
                    info.setLayoutParams(lp);
                }
                mViewMap.put(key, info);
                if (null != mVideoView && null != mVideoView.getWebViewId() && !mVideoView.isFullScreen()
                        && key.equals(Arrays.toString(mVideoView.getWebViewId()))) {
                    LDebug.i(
                            TAG,
                            TAG + ".SetViewRect setLayoutParams lp=[" + info.getLayoutParams().leftMargin + ","
                                    + info.getLayoutParams().topMargin + "," + info.getLayoutParams().rightMargin + ","
                                    + info.getLayoutParams().bottomMargin + "][" + info.getLayoutParams().width + ", "
                                    + info.getLayoutParams().height + "]");
                    mVideoView.setLayoutParams(info.getLayoutParams());
                }
            }
        });

    }

    private FrameLayout.LayoutParams getLayoutParams(float x, float y, float width, float height) {
        int swidth = (int) (mVideoLayout.getWidth() * width);
        int sheight = (int) (mVideoLayout.getHeight() * height);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(swidth, sheight);
        lp.width = swidth;
        lp.height = sheight;
        lp.leftMargin = (int) Math.round((mVideoLayout.getWidth() * x));
        lp.topMargin = (int) Math.round((mVideoLayout.getHeight() * y));
        return lp;
    }

    /**
     * 瑙ｆ瀽鑿滃崟娑堟伅
     * @param obj
     * @return
     * @throws JSONException
     */
    private List<VideoItem> buildMenuInfoFromJson(String menuInfo) {
        if (menuInfo == null) {
            return null;
        }
        List<VideoItem> videos = null;
        try {
            JSONObject obj = new JSONObject(menuInfo);
            // 鎺ㄨ崘鍒楄〃鏁版嵁
            JSONArray menujson = obj.optJSONArray("data");
            if (menujson != null) {
                videos = new ArrayList<VideoItem>();
                int count = menujson.length();
                for (int i = 0; i < count; i++) {
                    JSONObject itemJson = menujson.getJSONObject(i);
                    VideoItem item = VideoItem.fromJson(itemJson);
                    if (item != null) {
                        videos.add(item);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videos;
    }
    
    
    
    
    private native boolean videoNativeInit();
}
