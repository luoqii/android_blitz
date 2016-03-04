/**
 * $
 * PROJECT NAME: K2WebViewSample
 * PACKAGE NAME: com.taobao.K2WebView.VideoView
 * FILE NAME: VideoViewInfo.java
 * CREATED TIME: 2015-3-19
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.blitz.video.data;


import android.widget.FrameLayout;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2015-3-19 下午3:09:29
 */
public class VideoViewInfo {

    private FrameLayout.LayoutParams layoutParams;

    public VideoViewInfo(FrameLayout.LayoutParams lp) {
        layoutParams = lp;
    }

    public FrameLayout.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(FrameLayout.LayoutParams lp) {
        layoutParams = lp;
    }

}
