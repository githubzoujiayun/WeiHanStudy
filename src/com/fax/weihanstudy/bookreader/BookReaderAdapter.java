package com.fax.weihanstudy.bookreader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BookReaderAdapter extends BaseAdapter implements OnClickListener {
//	private File[] files;
	ArrayList<BookInfo> bookInfos;
	BookReaderListView listView;
	public BookReaderAdapter(BookReaderListView listView, ArrayList<BookInfo> bookInfos){
		this.listView=listView;
		this.bookInfos=bookInfos;
	}
	@Override
	public int getCount() {
		return bookInfos.size();
	}

//	private File[] getBooks(Context context){
//		File dirPath=MyUtil.getAppPath(context);
//		if (dirPath!=null) {
//			File bookDir = new File(dirPath.getPath() + "/BOOKS");
//			if (!bookDir.exists())
//				bookDir.mkdirs();
//			return bookDir.listFiles(filter);
//		}
//		return new File[0];
//	}
	@Override
	public BookInfo getItem(int position) {
		return bookInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) convertView=View.inflate(parent.getContext(),R.layout.bookreader_listitem, null);
		getItem(position).bindView(convertView);
		
		Button controlBtn=((Button) convertView.findViewById(R.id.bookreader_listitem_btn));
		controlBtn.setTag(getItem(position));
		controlBtn.setOnClickListener(this);
		return convertView;
	}
	@Override
	public void onClick(View v) {
		if(v.getTag()==null) return;
		Log.d("fax", "click controlBtn");
		Button controlBtn=(Button) v;
		BookInfo bookInfo=(BookInfo) v.getTag();
		if(bookInfo.isDownloaded()){
			MyUtil.openBook(listView.getContext(), bookInfo.getBookFile());
		}else if(bookInfo.isNeedDownLoad()){
			bookInfo.startDownload();
		}else if(bookInfo.isDownloading()){
			if(controlBtn.getText().equals("¼ÌÐø")){
				bookInfo.continueDownload();
			}else{
				bookInfo.pause();
			}
		}
	}
	
}
