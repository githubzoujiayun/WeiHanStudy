package com.fax.weihanstudy.bookreader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;

import me.maxwin.view.XListView;

import com.fax.weihanstudy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class BookReaderListView extends XListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,BookDownloader.DownloadListener {
	
	private BookReaderAdapter bookReaderAdapter;
	public BookReaderAdapter getAdapter(){
		return bookReaderAdapter;
	}
	private boolean isFromNet;
	private int page=0;
	private static final String baseUrl="http://weihanstudy.duapp.com/books";
	public BookReaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		BookDownloader.addDownloadListener(this);
		ArrayList<BookInfo> bookInfos=new ArrayList<BookInfo>();
		bookReaderAdapter=new BookReaderAdapter(this,bookInfos);
		
		setPullRefreshEnable(false);
		
		isFromNet=getTag().equals("net");
		if(isFromNet){
			setPullLoadEnable(true);
			
			setXListViewListener(new IXListViewListener() {
				public void onRefresh() {
				}
				public void onLoadMore() {
					new LoadMoreAsync().execute(page+1);
				}
			});
			startLoadMore();
		}else{
			setPullLoadEnable(false);
			setOnItemLongClickListener(this);
			refreshLocalAdapter();
			
		}

		setAdapter(bookReaderAdapter);
		setOnItemClickListener(this);
	}
	@Override
	public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1, final int position, final long arg3) {
		new AlertDialog.Builder(getContext()).setItems(new String[]{"查看详情","删除图书"}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:
					onItemClick(arg0, arg1, position, arg3);
					break;
				case 1:
					new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("确定删除图书吗？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								bookReaderAdapter.getItem(position).deleteMe();
								
								bookReaderAdapter.bookInfos.remove(position);
								bookReaderAdapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton("取消", null).create().show();
					break;
				}
			}
		}).create().show();
		return false;
	}
	public void openBookDetail(BookInfo bookInfo){
		BookDetailActivity.openBookDetail(getContext(), bookInfo);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
		BookInfo bookInfo=bookReaderAdapter.getItem(position);
//		if(bookInfo.isDownloaded()){
			openBookDetail(bookInfo);
//		}
	}
//	private void openBook(int position){
//		MyUtil.openBook(getContext(),bookReaderAdapter.getItem(position).getBookFile());
//	}

	private FileFilter infoFilter=new FileFilter() {
		public boolean accept(File pathname) {
			if(pathname.getName().endsWith(".info")) return true;
			return false;
		}
	};
	public void refreshLocalAdapter(){
		if(!isFromNet){
			File[] files=MyUtil.getBookDir().listFiles(infoFilter);
			if(files.length!=bookReaderAdapter.getCount()){

				ArrayList<BookInfo> bookInfos=new ArrayList<BookInfo>();
				for(File file:files){
					try {
						BookInfo bookInfo=BookInfo.createFromFile(file);
						if(bookInfo!=null) bookInfos.add(bookInfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				bookReaderAdapter=new BookReaderAdapter(this,bookInfos);
				setAdapter(bookReaderAdapter);
			}
		}
	}
	class LoadMoreAsync extends AsyncTask<Integer, Void, ArrayList<BookInfo>>{

		@Override
		protected ArrayList<BookInfo> doInBackground(Integer... params) {
			int page=params[0];
			String url=baseUrl+"?page="+page;
			try {
				String result=MyUtil.readStrFromInputStream(new URL(url).openStream());
				Log.d("fax", "url:"+url);
				return BookInfo.gson.fromJson(result, new TypeToken<ArrayList<BookInfo>>(){}.getType());  
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<BookInfo> addedBookInfos) {
			stopLoadMore();
			if(addedBookInfos==null){
				Toast.makeText(getContext(), "数据加载出错", Toast.LENGTH_SHORT).show();
				return;
			}
			
			page++;
			
			bookReaderAdapter.bookInfos.addAll(addedBookInfos);
			bookReaderAdapter.notifyDataSetChanged();
			if(addedBookInfos.size()<10){
				setPullLoadEnable(false);
				Toast.makeText(getContext(), "数据加载完毕", Toast.LENGTH_SHORT).show();
			}
		}
	}
	@Override
	public void onDownloading(BookDownloader downloader) {
		int lastVisiblePosition=getLastVisiblePosition();
		for(int i=getFirstVisiblePosition();i<=lastVisiblePosition&&i<bookReaderAdapter.getCount();i++){
			BookInfo bookInfo=	bookReaderAdapter.getItem(i);
			if(bookInfo.id==downloader.getInfoId()){
				bookReaderAdapter.getItem(i).refreshDownloadInfo();
			}
		}
	}
}
