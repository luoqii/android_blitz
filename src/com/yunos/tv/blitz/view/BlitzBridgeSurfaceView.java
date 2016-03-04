package com.yunos.tv.blitz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yunos.tv.blitz.GLEnvironment;

/**
 * @hide
 */
public class BlitzBridgeSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	static String TAG = BlitzBridgeSurfaceView.class.getSimpleName();

	private static int STATE_ALLOCATED = 0;
	private static int STATE_CREATED = 1;
	private static int STATE_INITIALIZED = 2;

	private int mState = STATE_ALLOCATED;
	private SurfaceHolder.Callback mListener;
	private GLEnvironment mGLEnv;
	private int mFormat;
	private int mWidth;
	private int mHeight;
	private int mSurfaceId = -1;

	public BlitzBridgeSurfaceView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	public BlitzBridgeSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}
	
	
	


	public synchronized void bindToListener(SurfaceHolder.Callback listener,
			GLEnvironment glEnv) {
		// Make sure we are not bound already
		if (listener == null) {
			throw new NullPointerException(
					"Attempting to bind null filter to SurfaceView!");
		} else if (mListener != null && mListener != listener) {
			throw new RuntimeException("Attempting to bind filter " + listener
					+ " to SurfaceView with another open " + "filter "
					+ mListener + " attached already!");
		}

		// Set listener
		mListener = listener;

		// Set GLEnv
		if (mGLEnv != null && mGLEnv != glEnv) {
			mGLEnv.unregisterSurfaceId(mSurfaceId);
		}
		mGLEnv = glEnv;

		// Check if surface has been created already
		if (mState >= STATE_CREATED) {
			// Register with env (double registration will be ignored by GLEnv,
			// so we can simply
			// try to do it here).
			registerSurface();

			// Forward surface created to listener
			mListener.surfaceCreated(getHolder());

			// Forward surface changed to listener
			if (mState == STATE_INITIALIZED) {
				mListener.surfaceChanged(getHolder(), mFormat, mWidth, mHeight);
			}
		}
	}

	public synchronized void unbind() {
		mListener = null;
	}

	public synchronized int getSurfaceId() {
		return mSurfaceId;
	}

	public synchronized GLEnvironment getGLEnv() {
		return mGLEnv;
	}

	@Override
	public synchronized void surfaceCreated(SurfaceHolder holder) {
		mState = STATE_CREATED;

		
		// Register with GLEnvironment if we have it already
		if (mGLEnv != null) {
			registerSurface();
		}
		
		// Forward callback to listener
		if (mListener != null) {
			mListener.surfaceCreated(holder);
		}
	}

	@Override
	public synchronized void surfaceChanged(SurfaceHolder holder, int format,
			int width, int height) {
		// Remember these values
		mFormat = format;
		mWidth = width;
		mHeight = height;
		mState = STATE_INITIALIZED;
		Log.d(TAG,"surfacechanage width:"+mWidth+"height:"+mHeight);

		// Forward to renderer
		if (mListener != null) {
			mListener.surfaceChanged(holder, format, width, height);
		}
	}

	@Override
	public synchronized void surfaceDestroyed(SurfaceHolder holder) {
		mState = STATE_ALLOCATED;

		// Forward to renderer
		if (mListener != null) {
			mListener.surfaceDestroyed(holder);
		}

		// Get rid of internal objects associated with this surface
		unregisterSurface();
	}

	private void registerSurface() {
		mSurfaceId = mGLEnv.registerSurface(getHolder().getSurface());
		if (mSurfaceId < 0) {
			throw new RuntimeException("Could not register Surface: "
					+ getHolder().getSurface() + " in FilterSurfaceView!");
		}
	}

	private void unregisterSurface() {
		if (mGLEnv != null && mSurfaceId > 0) {
			mGLEnv.unregisterSurfaceId(mSurfaceId);
		}
	}

}
