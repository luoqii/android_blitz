/**
 * $
 * PROJECT NAME: TvTaoBaoTest
 * PACKAGE NAME: com.yunos.tvtaobaotest.view
 * FILE NAME: LifeMediaController.java
 * CREATED TIME: 2015-3-6
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video;


import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.yunos.tv.blitz.video.data.VideoItem;
import com.yunos.tv.media.view.IVideo;
import com.yunos.tv.media.view.MediaController;
import com.yunos.tv.media.view.YunosVideoView;


public class LifeMediaController extends MediaController {

    private final String TAG = "LifeMediaController";
    private List<VideoItem> videos;

    private WindowManager.LayoutParams menuParams;
    private String mXuanjiItemClickFunc;
    private VideoItem mItem;
    private Integer mCurPos;

    public LifeMediaController(Context context) {
        super(context);
    }

    public LifeMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void destroy() {
        if (null != videos) {
            videos.clear();
            videos = null;
        }
        mItem = null;
        menuParams = null;
    }
    
    
    public void clear() {
        if (null != videos) {
            videos.clear();
            videos = null;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean dispatchKeyEvent(int from, KeyEvent event) {
        bringToFront();
        int keyCode = event.getKeyCode();
        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        boolean ret = super.dispatchKeyEvent(from, event);
        if (ret) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            if (null != mPlayer) {
                if (mPlayer instanceof YunosVideoView) {
                    YunosVideoView video = (YunosVideoView) mPlayer;
                    if (video.isFullScreen()) {
                        if (up) {
                            video.unFullScreen();
                        }
                        Log.i(TAG, TAG + ".dispatchKeyEvent unFullScreen");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void showXuanji() {
    	Log.i(TAG, TAG + ".showXuanji ");
        if (null == videos || videos.size() <= 0) {
            Log.d(TAG, TAG + ".showXuanji videos is null");
            return;
        }
        if (isShowing()) {
            hide();
        }
        if (null == mCurPos) {
            mCurPos = getPosition(mItem);
        }
    }

    private int getPosition(VideoItem item) {
        int pos = 0;
        if (null != videos && null != item && item.isUseFul()) {
            int length = videos.size();
            for (int i = 0; i < length; i++) {
                VideoItem v = videos.get(i);
                if ((!TextUtils.isEmpty(v.getId()) && v.getId().equals(item.getId()))
                        || (!TextUtils.isEmpty(v.getVideo()) && v.getVideo().equals(item.getVideo()))) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

    public IVideo getPlayer() {
        return mPlayer;
    }

    protected void initMenuParams() {
        menuParams = new WindowManager.LayoutParams();
        //        WindowManager.LayoutParams p = menuParams;
        menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        menuParams.gravity = Gravity.START;
        menuParams.format = PixelFormat.TRANSLUCENT;
        menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        menuParams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        menuParams.token = null;
        menuParams.windowAnimations = 0; // android.R.style.DropDownAnimationDown;
    }

    /**
     * 播放列表
     * @param items
     */
    public void setXuanjiVideoItems(List<VideoItem> items) {
    	Log.i(TAG, TAG + ".setXuanjiVideoItems items=" + items);
        if (null == items) {
        	Log.i(TAG, TAG + ".setXuanjiVideoItems items is null ", new Throwable());
        }
        videos = items;
    }

    public List<VideoItem> getXuanjiVideoItems() {
        return videos;
    }

    public void setXuanjiItemClick(String func) {
        mXuanjiItemClickFunc = func;
    }

    public String getXuanjiItemClick() {
        return mXuanjiItemClickFunc;
    }

    public void play(VideoItem item) {
        if (null == mPlayer || null == item || TextUtils.isEmpty(item.getVideo())) {
            return;
        }
        mPlayer.setVideoInfo(item.getVideo());
        mPlayer.start();
        setCurrentVideoItem(item);
    }

    public VideoItem getCurrenVideotItem() {
        return mItem;
    }

    public void setCurrentPos(Integer pos) {
        if (null == pos) {
            return;
        }
        mCurPos = pos;
        if (null != videos && pos >= 0 && pos < videos.size()) {
            mItem = videos.get(pos);
            setTitle(mItem.getTitle());
        }
    }

    public void setCurrentVideoItem(VideoItem item) {
        this.mItem = item;
        setTitle(item.getTitle());
        mCurPos = getPosition(item);
    }

}
