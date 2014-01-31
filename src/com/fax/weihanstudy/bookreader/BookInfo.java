package com.fax.weihanstudy.bookreader;

import java.io.File;
import java.util.ArrayList;

import com.fax.weihanstudy.MyTextView;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BookInfo {
	int id;//唯一标识
	String bookName;//书名
	String author;//作者
	String downloadPath;//书下载路径
	String picPath;//图片路径
	long fileSize;//文件大小
	String bookDetail;//书简介
	
	public String getDetail() {
		return bookDetail;
	}
	public void setDetail(String detail) {
		this.bookDetail = detail;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String name) {
		this.bookName = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPath() {
		return downloadPath;
	}
	public void setPath(String path) {
		this.downloadPath = path;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	private View lastBindedView;
	//根据信息来修改view
	public void bindView(View view){
		this.lastBindedView=view;
		lastBindedView.setTag(this);
		
		((ImageView) lastBindedView.findViewById(R.id.bookreader_listitem_img)).setImageResource(R.drawable.loadimg_loading);
		if(picPath!=null&&picPath.length()>0)
			BitmapManager.getBitmapBackground(picPath, new BitmapManager.BitmapLoadingListener() {
				public void onBitmapLoadFinish(Bitmap bitmap, boolean isLoadSuccess) {
					if(checkTag()){
						((ImageView) lastBindedView.findViewById(R.id.bookreader_listitem_img)).setImageBitmap(bitmap);
					}
				}
			}); 
		else ((ImageView) lastBindedView.findViewById(R.id.bookreader_listitem_img)).setImageResource(R.drawable.loadimg_noimg);
		((MyTextView)lastBindedView.findViewById(R.id.bookreader_listitem_title)).setText(bookName);
		((MyTextView)lastBindedView.findViewById(R.id.bookreader_listitem_author)).setText(author);
		refreshDownloadInfo();
	}
	public void refreshDownloadInfo(){
		if(checkTag()){
			TextView fileSizeTv=((TextView)lastBindedView.findViewById(R.id.bookreader_listitem_filesize));
			fileSizeTv.setText(MyUtil.SizeToString(fileSize));
			Button controlBtn=(Button) lastBindedView.findViewById(R.id.bookreader_listitem_btn);
			
			if(isDownloaded()){
				controlBtn.setEnabled(true);
				controlBtn.setText("打开");
			}else if(isNeedDownLoad()){
				controlBtn.setEnabled(true);
				controlBtn.setText("下载");
			}else if(isDownloading()){
				BookDownloader downloader=getDownloader();
				if(downloader==null||downloader.isPause){
					controlBtn.setEnabled(true);
					controlBtn.setText("继续");
					fileSizeTv.setText("(已暂停)"+MyUtil.SizeToString(getDone())+"/"+MyUtil.SizeToString(fileSize));
				}else{
					controlBtn.setEnabled(false);
					int percent=getPercent();
					if(percent>=0){
						controlBtn.setText("下载中"+getPercent()+"%");
						fileSizeTv.setText("("+MyUtil.SizeToString(downloader.speed)+"/s)"+MyUtil.SizeToString(getDone())+"/"+MyUtil.SizeToString(fileSize));
					}
					else controlBtn.setText("下载中...");
				}
			}else{
				Log.e("fax", "unknow state");
			}
		}
	}
	private boolean checkTag(){
		if(lastBindedView!=null){
			return lastBindedView.getTag()==this;
		}
		return false;
	}
	public void startOrContinueDownload(){
		if(isDownloaded()){
			continueDownload();
		}else{
			startDownload();
		}
	}
	public void startDownload(){
		BookDownloader.download(this);
	}
	public void continueDownload(){
		BookDownloader.continueDownload(this);
	}
	public BookDownloader getDownloader(){
		return BookDownloader.getDownloader(this);
	}
	public void resume(){
		getDownloader().resume();
	}
	public void pause(){
		getDownloader().pause();
	}
	public void stop(){
		getDownloader().stop();
	}
	public boolean isDownloaded(){
		return isBookExists();
	}
	public boolean isNeedDownLoad(){
		if(!isBookExists()&&!isBookDlExists()){
			return true;
		}
		return false;
	}
	public boolean isDownloading(){
		if(isBookExists()){
			return false;
		}
		if(isBookDlExists()){
			return true;
		}
		return false;
	}
	public int getPercent(){
		if(fileSize<=0) return -1;
		return (int) (getDone()*100/fileSize);
	}
	public long getDone(){
		try {
			if(isBookDlExists()){
				return getBookDlFile().length();
			}else if(isBookExists()){
				return getBookFile().length();
			}
		} catch (Exception e) {
		}
		return 0;
	}
	private File bookInfoFile;
	File getBookInfoFile(){
		if(bookInfoFile==null) bookInfoFile=new File(MyUtil.getBookDir(), new File(downloadPath).getName()+".info");
		return bookInfoFile;
	}

	private File bookDlFile;
	File getBookDlFile(){
		if(bookDlFile==null) bookDlFile=new File(MyUtil.getBookDir(), new File(downloadPath).getName()+".dl");
		return bookDlFile;
	}
	private boolean isBookDlExists(){
		return getBookDlFile().exists();
	}
	private File bookFile;
	File getBookFile(){
		if(bookFile==null) bookFile=new File(MyUtil.getBookDir(), getFileName());
		return bookFile;
	}
	private boolean isBookExists(){
		return getBookFile().exists();
	}
	public String getFileName(){
		return  downloadPath.substring(downloadPath.lastIndexOf("/")+1);
	}
	public void deleteMe(){
		BookDownloader downloader=getDownloader();
		if(downloader!=null) downloader.stop();
		File bookFile=getBookFile();
		if(bookFile!=null) bookFile.delete();
		File bookDlFile=getBookDlFile();
		if(bookDlFile!=null) bookDlFile.delete();
		getInfoFile().delete();
//		File imageFile=BitmapManager.getImgFile(picPath);
//		if (imageFile!=null&&imageFile.exists()) {
//			imageFile.delete();
//		}
	}
	public static BookInfo createFromFile(File file){
		String json=MyUtil.readStrFromFile(file);
		return gson.fromJson(json, BookInfo.class);
	}
	public void saveInfoToSdcard(){
		if(getInfoFile().exists()) return;
		String json=gson.toJson(this);
		MyUtil.writeStrToFile(json, getInfoFile());
	}
	private File getInfoFile(){
		return new File(MyUtil.getBookDir(), getFileName()+".info");
	}
	public static Gson gson=new GsonBuilder().setExclusionStrategies(new BookInfoExclusionStrategy()).create();
	private static class BookInfoExclusionStrategy implements ExclusionStrategy{
	    public boolean shouldSkipField(FieldAttributes f) {
	        if("lastBindedView".equals(f.getName())) return true;
	        else if("gson".equals(f.getName())) return true;
	        else if("isPause".equals(f.getName())) return true;
	        else if("speed".equals(f.getName())) return true;
	        else if("bookDlFile".equals(f.getName())) return true;
	        else if("bookFile".equals(f.getName())) return true;
	        else if("bookInfoFile".equals(f.getName())) return true;
	        else if("downloadListener".equals(f.getName())) return true;
	        else if("allBookInfos".equals(f.getName())) return true;
	        return false;
	    }
	    public boolean shouldSkipClass(Class<?> clazz) {
	    	if(clazz==BookInfoExclusionStrategy.class) return true;
	        return false;
	    }
	}
}
