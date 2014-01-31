package com.fax.weihanstudy.bookreader;

import java.net.URLEncoder;

import com.baidu.mobstat.StatActivity;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.dict.FloatDictActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class OnlineBookreaderActivity extends StatActivity {
	private static BookInfo staticBookInfo;
	private static final String BaseUrl="https://docs.google.com/viewer?url=";
	ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookreader_online_read);
		progressBar=(ProgressBar) findViewById(android.R.id.progress);
		
		WebView webview=(WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new MyChromeClient());
		webview.loadUrl(BaseUrl+URLEncoder.encode(staticBookInfo.getPath()));
       		
	}

	public void onclick(View view){
		switch(view.getId()){
		case R.id.bookreader_online_opendict:
			startActivity(new Intent(this, FloatDictActivity.class));
			break;
		}
	}
	
	public static void openOnlineBookreader(Context context,BookInfo bookInfo){
		OnlineBookreaderActivity.staticBookInfo=bookInfo;
		context.startActivity(new Intent(context, OnlineBookreaderActivity.class));
	}
	class MyWebViewClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			progressBar.setProgress(0);
		}
		
	}
	class MyChromeClient extends WebChromeClient{

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);
		}
		
	}
}
