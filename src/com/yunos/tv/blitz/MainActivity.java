package com.yunos.tv.blitz;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout.LayoutParams;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	static String TAG = "BlitzMainActivity";

	public static final int MSG_ID_UPDATE = 0;
	Handler mHandler;

	private BlitzContextWrapper mBlitzContext;

	
	
    static {
    	System.loadLibrary("freetype_tb_1.0.2");
    	System.loadLibrary("cximage_tb_1.0.2");
    	System.loadLibrary("curl_tb_1.0.3");
    	System.loadLibrary("lightcore_tb_1.0.6");
    	System.loadLibrary("blitzview");
        System.loadLibrary("window");
    }
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		mBlitzContext = new BlitzContextWrapper(this);
		mBlitzContext.setmEntryUrl("http://g.alicdn.com/yuntv/blitzjs/0.0.10/ui-demo/index.html");
//		mBlitzContext.setmEntryUrl("http://tv.qcast.cn/homepage/public/home.html");
//		mBlitzContext.setmEntryUrl("http://beta.html5test.com/");
		mBlitzContext.initContext();
		
		setContentView(R.layout.activity_main);
//setContentView(mBlitzContext.getmBlitzSurface());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		((ViewGroup)findViewById(R.id.root)).addView(mBlitzContext.getmBlitzSurface(),lp);
		mHandler = new UpdateHandler(this);

	}
	
	private void updateBlitzContext(){
		mBlitzContext.update();
	}
	
	
	

	@Override
	public void onBackPressed() {
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		mBlitzContext.onKeyEvent(keyCode,false);
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mBlitzContext.onKeyEvent(keyCode,true);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		Log.d(TAG,"onresume");
		mHandler.removeMessages(MSG_ID_UPDATE);
		Message updateMsg = mHandler.obtainMessage(MSG_ID_UPDATE);
		mHandler.sendMessage(updateMsg);
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.d(TAG,"onstop");
		stopUpdate();
		super.onStop();
	}

	
	


	@Override
	protected void onDestroy() {
		Log.d(TAG,"ondestroy");
		stopUpdate();
		mBlitzContext.deinitContext();
		super.onDestroy();
	}

	private void stopUpdate(){
		mHandler.removeMessages(MSG_ID_UPDATE);
	}

	static class UpdateHandler extends Handler {
		WeakReference<MainActivity> mWeakRefMainActivity = null;

		public UpdateHandler(MainActivity activity){
			mWeakRefMainActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID_UPDATE:
				//update surfaceview
				if(mWeakRefMainActivity.get() == null){
					return;
				}
				//first clear all update msg
				removeMessages(MSG_ID_UPDATE);
				//mWeakRefMainActivity.get().updateBlitzContext();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message updateMsg = obtainMessage(MSG_ID_UPDATE);
				sendMessage(updateMsg);
				break;
			}
			super.handleMessage(msg);
		}
	}

}