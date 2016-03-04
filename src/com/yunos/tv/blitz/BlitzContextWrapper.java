package com.yunos.tv.blitz;

import java.lang.ref.WeakReference;

import com.yunos.tv.blitz.video.VideoViewManager;
import com.yunos.tv.blitz.view.BlitzBridgeSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class BlitzContextWrapper implements Callback {

	WeakReference<Context> mContext = null;
	static String TAG = BlitzContextWrapper.class.getSimpleName();
	private BlitzBridgeSurfaceView mBlitzSurface;
	AsyncUpdateThread mThread;

	String mEntryUrl;
	GLEnvironment mGlEnv;
	boolean bSurfaceReady = false;
	boolean bUpdating = false;

	static final int MSG_ID_CREATE_VIDEO_VIEW = 1;
	static final int MSG_ID_LAST_PAGE_QUIT = 2;
	Handler mContextWrapperHandler;
	VideoViewManager mVideoViewMgr = null;

	public String getmEntryUrl() {
		return mEntryUrl;
	}

	public void setmEntryUrl(String mEntryUrl) {
		this.mEntryUrl = mEntryUrl;
	}

	public BlitzContextWrapper(Context context) {
		mContext = new WeakReference<Context>(context);
		mBlitzSurface = new BlitzBridgeSurfaceView(context);
		mBlitzSurface.setZOrderMediaOverlay(true);
		mBlitzSurface.getHolder().setFormat(PixelFormat.RGBA_8888);

		mContextWrapperHandler = new BlitzContextWrapperHandler(this);
	}

	public void initContext() {

		mGlEnv = new GLEnvironment(this, mContext);
		if (mGlEnv == null) {
			Log.e(TAG, "glenvironment create fail!!");
			return;
		}
		mGlEnv.initWithNewContext();
		mGlEnv.setEntryUrl(mEntryUrl);
		mBlitzSurface.bindToListener(this, mGlEnv);
		mThread = new AsyncUpdateThread();
		bUpdating = true;
		// mThread.start();

	}

	public void deinitContext() {
		if (mGlEnv != null) {
			mGlEnv.deactivate();
		}
		bUpdating = false;
	}

	public SurfaceView getmBlitzSurface() {
		return mBlitzSurface;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");
		bSurfaceReady = true;
		mGlEnv.surfaceChanged(mBlitzSurface.getSurfaceId(), width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");

		mGlEnv.surfaceCreated(mBlitzSurface.getSurfaceId());

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "sufacedestroyed");
		bUpdating = false;
		mGlEnv.surfaceDestroyed();
	}

	public void update() {
		// update blitzview
		if (mGlEnv != null && bSurfaceReady) {
			// mGlEnv.updateSurface();
		}
	}

	class AsyncUpdateThread extends Thread {

		@Override
		public void run() {
			super.run();
			if (mGlEnv != null && !mGlEnv.isActive()) {
				mGlEnv.activate();
			}
			while (bUpdating) {
				if (mGlEnv != null && bSurfaceReady) {
					mGlEnv.activateSurfaceWithId(mBlitzSurface.getSurfaceId());
					mGlEnv.updateSurface();
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// deactive
			if (mGlEnv != null) {
				mGlEnv.deactivate();
			}

		}

	}

	public void onKeyEvent(int keyCode, boolean isDown) {
		if (mGlEnv != null) {
			mGlEnv.keyeventToSurface(mBlitzSurface.getSurfaceId(), keyCode,
					isDown);
		}

	}

	public void createVideoView() {
		if (mContext.get() == null) {
			return;
		}
		if (mVideoViewMgr == null) {
			Activity activity = (Activity) mContext.get();
			mVideoViewMgr = new VideoViewManager();
			mVideoViewMgr.Init((FrameLayout) activity.findViewById(R.id.root));
		}
	}

	public void lastPageQuit() {
		android.os.Process.killProcess(android.os.Process.myPid());
		/*
		if (mContext.get() == null) {
			return;
		}
		Activity activity = (Activity) mContext.get();
		activity.finish();*/
	}

	public void postCreateVideoView() {
		Message msg = mContextWrapperHandler
				.obtainMessage(MSG_ID_CREATE_VIDEO_VIEW);
		mContextWrapperHandler.sendMessage(msg);
	}

	public void postlastPageQuit() {
		Message msg = mContextWrapperHandler
				.obtainMessage(MSG_ID_LAST_PAGE_QUIT);
		mContextWrapperHandler.sendMessage(msg);

	}

	static class BlitzContextWrapperHandler extends Handler {
		BlitzContextWrapper mContextWrapper = null;

		public BlitzContextWrapperHandler(BlitzContextWrapper ctxWrapper) {
			mContextWrapper = ctxWrapper;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID_CREATE_VIDEO_VIEW:
				if (mContextWrapper != null) {
					mContextWrapper.createVideoView();
				}
				break;
			case MSG_ID_LAST_PAGE_QUIT:
				if (mContextWrapper != null) {
					mContextWrapper.lastPageQuit();
				}
				break;
			}
			super.handleMessage(msg);
		}
	}

}
