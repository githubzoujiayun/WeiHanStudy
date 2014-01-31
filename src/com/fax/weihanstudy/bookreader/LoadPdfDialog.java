package com.fax.weihanstudy.bookreader;

import org.vudroid.pdfdroid.PdfViewerActivity;

import com.fax.weihanstudy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadPdfDialog extends Activity implements OnGlobalLayoutListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.splash_screen);

		getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this);
		
//		new AlertDialog.Builder(this).setMessage("正在载入图书...").setCancelable(false).create().show();
		LinearLayout ll=new LinearLayout(this);
		ll.setGravity(Gravity.CENTER);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		
		ProgressBar pb=new ProgressBar(this);
		ll.addView(pb);
		
		TextView tv=new TextView(this);
		tv.setText("正在载入图书...");
//		tv.setTextColor(Color.WHITE);
		ll.addView(tv);
		
		setContentView(ll);
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

	@Override
	public void onGlobalLayout() {
		getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Intent intent=getIntent();
		        intent.setClass(LoadPdfDialog.this, PdfViewerActivity.class);
		        startActivity(intent);
			}
		}, 300);
	}
}