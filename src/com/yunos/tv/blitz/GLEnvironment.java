package com.yunos.tv.blitz;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.lang.ref.WeakReference;

import com.yunos.tv.blitz.video.VideoViewManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;
import android.media.MediaRecorder;

/**
 * @hide
 */
public class GLEnvironment {

    private int glEnvId;

    private boolean mManageContext = true;
    
    WeakReference<Context> mContext = null;
    
    BlitzContextWrapper mBlitzContextWrapper = null;
    
    

    public GLEnvironment( BlitzContextWrapper contxtWraper,WeakReference<Context> context) {
    	mBlitzContextWrapper = contxtWraper;
    	mContext = context;
        nativeAllocate();
    }
    
    private GLEnvironment(NativeAllocatorTag tag) {
    }

    
    
    
    
	public void lastPageQuit(){
		Log.d("GLEnvironment","lastPageQuit");
		mBlitzContextWrapper.postlastPageQuit();
	}
	
	
	public void createVideoView(){
		Log.d("GLEnvironment","createVideoView");
		
		mBlitzContextWrapper.postCreateVideoView();
		
	}
    
    
    
    
    
    
    public synchronized void tearDown() {
        if (glEnvId != -1) {
            nativeDeallocate();
            glEnvId = -1;
        }
    }
    

    @Override
    protected void finalize() throws Throwable {
        tearDown();
    }

    public void initWithNewContext() {
        mManageContext = true;
        if (!nativeInitWithNewContext()) {
            throw new RuntimeException("Could not initialize GLEnvironment with new context!");
        }
    }

    public void initWithCurrentContext() {
        mManageContext = false;
        if (!nativeInitWithCurrentContext()) {
            throw new RuntimeException("Could not initialize GLEnvironment with current context!");
        }
    }

    public boolean isActive() {
        return nativeIsActive();
    }

    public boolean isContextActive() {
        return nativeIsContextActive();
    }

    public static boolean isAnyContextActive() {
        return nativeIsAnyContextActive();
    }

    public void activate() {
        if (Looper.myLooper() != null && Looper.myLooper().equals(Looper.getMainLooper())) {
            Log.e("FilterFramework", "Activating GL context in UI thread!");
        }
        if (mManageContext && !nativeActivate()) {
            throw new RuntimeException("Could not activate GLEnvironment!");
        }
    }

    public void deactivate() {
        if (mManageContext && !nativeDeactivate()) {
            throw new RuntimeException("Could not deactivate GLEnvironment!");
        }
    }

    public void swapBuffers() {
        if (!nativeSwapBuffers()) {
            throw new RuntimeException("Error swapping EGL buffers!");
        }
    }
    
    
    public void updateSurface(){
    	if(!nativeUpdateSurface()){
    		throw new RuntimeException("Error update surface !");
    	}
    }

    public int registerSurface(Surface surface) {
        int result = nativeAddSurface(surface);
        if (result < 0) {
            throw new RuntimeException("Error registering surface " + surface + "!");
        }
        return result;
    }

    public int registerSurfaceTexture(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        int result = nativeAddSurfaceWidthHeight(surface, width, height);
        surface.release();
        if (result < 0) {
            throw new RuntimeException("Error registering surfaceTexture " + surfaceTexture + "!");
        }
        return result;
    }

    public int registerSurfaceFromMediaRecorder(MediaRecorder mediaRecorder) {
        int result = nativeAddSurfaceFromMediaRecorder(mediaRecorder);
        if (result < 0) {
            throw new RuntimeException("Error registering surface from "
                                    + "MediaRecorder" + mediaRecorder + "!");
        }
        return result;
    }

    public void activateSurfaceWithId(int surfaceId) {
        if (!nativeActivateSurfaceId(surfaceId)) {
            throw new RuntimeException("Could not activate surface " + surfaceId + "!");
        }
    }

    public void unregisterSurfaceId(int surfaceId) {
        if (!nativeRemoveSurfaceId(surfaceId)) {
            throw new RuntimeException("Could not unregister surface " + surfaceId + "!");
        }
    }

    public void setSurfaceTimestamp(long timestamp) {
        if (!nativeSetSurfaceTimestamp(timestamp)) {
            throw new RuntimeException("Could not set timestamp for current surface!");
        }
    }
    
    
    public void surfaceCreated(int surfaceid) {
        if (!nativeSurfaceCreated(surfaceid)) {
            throw new RuntimeException("Could not nativeSurfaceCreated!");
        }
    }
    
    public void surfaceChanged(int surfaceId,int width,int height) {
        if (!nativeSurfaceChanged(surfaceId,width,height)) {
            throw new RuntimeException("Could not nativeSurfaceChanged!");
        }
    }
    
    public void surfaceDestroyed() {
        if (!nativeSurfaceDestroyed()) {
            throw new RuntimeException("Could not nativeSurfaceDestroyed!");
        }
    }
    
    
    public void keyeventToSurface(int surfaceid,int keycode,boolean isDown){
        if (!nativeKeyeventToSurface(surfaceid,keycode,isDown)) {
            throw new RuntimeException("Could not nativeKeyeventToSurface!");
        }
    }
    
    



    private native boolean nativeInitWithNewContext();

    private native boolean nativeInitWithCurrentContext();

    private native boolean nativeIsActive();

    private native boolean nativeIsContextActive();

    private static native boolean nativeIsAnyContextActive();

    private native boolean nativeActivate();

    private native boolean nativeDeactivate();

    private native boolean nativeSwapBuffers();

    private native boolean nativeAllocate();

    private native boolean nativeDeallocate();

    private native int nativeAddSurface(Surface surface);

    private native int nativeAddSurfaceWidthHeight(Surface surface, int width, int height);

    private native int nativeAddSurfaceFromMediaRecorder(MediaRecorder mediaRecorder);

    private native boolean  nativeDisconnectSurfaceMediaSource(MediaRecorder mediaRecorder);

    private native boolean nativeActivateSurfaceId(int surfaceId);

    private native boolean nativeRemoveSurfaceId(int surfaceId);

    private native boolean nativeSetSurfaceTimestamp(long timestamp);
    
    private native boolean nativeUpdateSurface();
    
    private native boolean nativeSurfaceCreated(int surfaceid);
    private native boolean nativeSurfaceChanged(int surfaceId,int width,int height);
    private native boolean nativeSurfaceDestroyed();
    
    
    private native boolean nativeSetEntryUrl(String entryUrl);
    
    private native boolean nativeKeyeventToSurface(int surfaceid,int keycode , boolean isDown);

	public void setEntryUrl(String mEntryUrl) {
		nativeSetEntryUrl(mEntryUrl);
	}
}

