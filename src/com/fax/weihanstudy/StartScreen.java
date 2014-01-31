package com.fax.weihanstudy;

import com.baidu.mobstat.StatActivity;
import com.fax.weihanstudy.bookreader.BitmapManager;
import com.fax.weihanstudy.dict.DictManager;
import com.fax.weihanstudy.utils.MyUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class StartScreen extends StatActivity implements OnGlobalLayoutListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.splash_screen);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
//		Log.d("fax", "onGlobalLayout");
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
		        MyUtil.setContext(StartScreen.this);
		        BitmapManager.init(StartScreen.this, BitmapFactory.decodeResource(getResources(), R.drawable.loadimg_fail), 
		        		MyUtil.getBookImgDir());
				DictManager.initMe(StartScreen.this);
				startActivity(new Intent(StartScreen.this, StudyActivity.class));
			}
		}, 1000);
	}
	
	
}
