package com.fax.weihanstudy.bookreader;

import com.baidu.mobstat.StatActivity;
import com.fax.weihanstudy.MyTextView;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookDetailActivity extends StatActivity implements BookDownloader.DownloadListener{
	private static BookInfo staticBookInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BookDownloader.addDownloadListener(this);
		setContentView(R.layout.bookreader_detail);
		((MyTextView)findViewById(R.id.bookreader_detail_bookname)).setText(staticBookInfo.getBookName());
		((MyTextView)findViewById(R.id.bookreader_detail_anthor)).setText(staticBookInfo.getAuthor());
		((MyTextView)findViewById(R.id.bookreader_detail_info)).setText(staticBookInfo.getDetail());
		
		final BookInfo bookInfo=staticBookInfo;
		if(staticBookInfo.getPicPath()!=null&&staticBookInfo.getPicPath().length()>0)
			BitmapManager.getBitmapBackground(staticBookInfo.getPicPath(), new BitmapManager.BitmapLoadingListener() {
				public void onBitmapLoadFinish(Bitmap bitmap, boolean isLoadSuccess) {
					if(bookInfo==staticBookInfo){
						((ImageView) findViewById(R.id.bookreader_detail_img)).setImageBitmap(bitmap);
					}
				}
			}); 
		else ((ImageView) findViewById(R.id.bookreader_detail_img)).setImageResource(R.drawable.loadimg_noimg);
		
		refreshDownloadInfo(staticBookInfo.getDownloader());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BookDownloader.removeDownloadListener(this);
	}

	@Override
	public void onDownloading(BookDownloader downloader) {
		if(BookDetailActivity.staticBookInfo.id==downloader.getInfoId()){
			refreshDownloadInfo(downloader);
		}
	}
	public void refreshDownloadInfo(BookDownloader downloader){
			Button controlBtn=(Button) findViewById(R.id.bookreader_detail_control_btn);
			if(staticBookInfo.isDownloaded()){
				findViewById(R.id.bookreader_detail_btn_continue).setVisibility(View.GONE);
				findViewById(R.id.bookreader_detail_btn_stop).setVisibility(View.GONE);
				findViewById(R.id.bookreader_detail_btn_pause).setVisibility(View.GONE);
				controlBtn.setEnabled(true);
				controlBtn.setText("打开");
				((ProgressBar)findViewById(R.id.bookreader_detail_progress)).setVisibility(View.INVISIBLE);
			}else if(staticBookInfo.isNeedDownLoad()){
				findViewById(R.id.bookreader_detail_btn_continue).setVisibility(View.GONE);
				findViewById(R.id.bookreader_detail_btn_stop).setVisibility(View.GONE);
				findViewById(R.id.bookreader_detail_btn_pause).setVisibility(View.GONE);
				controlBtn.setEnabled(true);
				controlBtn.setText("下载");
				((ProgressBar)findViewById(R.id.bookreader_detail_progress)).setVisibility(View.INVISIBLE);
			}else if(staticBookInfo.isDownloading()){
				findViewById(R.id.bookreader_detail_btn_stop).setVisibility(View.VISIBLE);
				controlBtn.setEnabled(false);
				int percent=staticBookInfo.getPercent();
				((ProgressBar)findViewById(R.id.bookreader_detail_progress)).setVisibility(View.VISIBLE);
				((ProgressBar)findViewById(R.id.bookreader_detail_progress)).setProgress(percent);
				
				if(downloader==null||downloader.isPause){
					findViewById(R.id.bookreader_detail_btn_continue).setVisibility(View.VISIBLE);
					findViewById(R.id.bookreader_detail_btn_pause).setVisibility(View.GONE);
					controlBtn.setText("(已暂停)"+MyUtil.SizeToString(staticBookInfo.getDone())+"/"+MyUtil.SizeToString(staticBookInfo.fileSize));
				}else{
					findViewById(R.id.bookreader_detail_btn_continue).setVisibility(View.GONE);
					findViewById(R.id.bookreader_detail_btn_pause).setVisibility(View.VISIBLE);
//					if(percent>=0){
						controlBtn.setText("("+MyUtil.SizeToString(downloader.speed)+"/s)"+MyUtil.SizeToString(staticBookInfo.getDone())+"/"+MyUtil.SizeToString(staticBookInfo.fileSize));
//					}else controlBtn.setText("下载中...");
				}
			}
	}
	public static void openBookDetail(Context context,BookInfo bookInfo){
		Log.d("fax", "openBookDetail:"+bookInfo.getBookName());
		BookDetailActivity.staticBookInfo=bookInfo;
		context.startActivity(new Intent(context, BookDetailActivity.class));
	}
	public void onclick(View view){
		switch(view.getId()){
		case R.id.bookreader_detail_btn_continue:
			staticBookInfo.continueDownload();
			break;
		case R.id.bookreader_detail_btn_pause:
			staticBookInfo.pause();
			break;
		case R.id.bookreader_detail_btn_stop:
			staticBookInfo.stop();
			break;
		case R.id.bookreader_detail_control_btn:
			if(staticBookInfo.isDownloaded()){
				MyUtil.openBook(this, staticBookInfo.getBookFile());
			}else if(staticBookInfo.isNeedDownLoad()){
				staticBookInfo.startDownload();
			}
			break;
		case R.id.bookreader_online_reader:
			OnlineBookreaderActivity.openOnlineBookreader(this, staticBookInfo);
			break;
		}
	}
}
