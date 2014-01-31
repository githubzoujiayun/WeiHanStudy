package com.fax.weihanstudy.bookreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.fax.weihanstudy.utils.MyUtil;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BookDownloader {  
	  
    private Handler handler;  
    private static final int Msg_Connecting=-1;
    private static final int Msg_StartDownload=0;
//    private static final int Msg_ContinueDownload=1;
    private static final int Msg_Downloading=2;
    private static final int Msg_FinishDownload=3;
    private static final int Msg_DownloadError=4;
    private static final int Msg_PauseDownload=5;
    private static final int Msg_ContinueDownload=6;
    private static final int Msg_StopDownload=7;
    private static final String Msg_Date_Speed="speed";
    private BookInfo info;
    boolean isPause;
    long speed;
    private String getDownloadUrl(){
    	return info.downloadPath;
    }
    private File getDownloadToFile(){
    	return info.getBookDlFile();
    }
    public int getInfoId(){
    	return info.id;
    }
    
    private static ArrayList<BookDownloader> downloadingList=new ArrayList<BookDownloader>();
    private BookDownloader(BookInfo bookinfo) {
    	handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case Msg_Connecting:
			    	try {
						if (!getDownloadToFile().exists())
							getDownloadToFile().createNewFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_StartDownload:
			    	info.saveInfoToSdcard();
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_Downloading:
					speed=msg.getData().getLong(Msg_Date_Speed);
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_FinishDownload:
					info.getBookDlFile().renameTo(info.getBookFile());
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_DownloadError:
					Toast.makeText(MyUtil.getContext(), "下载出错", Toast.LENGTH_SHORT).show();
					info.getBookDlFile().delete();
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_PauseDownload:
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_ContinueDownload:
			    	noticeAllListener(BookDownloader.this);
					break;
				case Msg_StopDownload:
					info.getBookDlFile().delete();
			    	noticeAllListener(BookDownloader.this);
					break;
				}
			}
    	};
		
		this.info=bookinfo;
        downloadingList.add(this);
    }
    
    static public boolean isUrlDownloading(String url){
    	BookDownloader downloader=getDownloader(url);
    	if(downloader!=null&&!downloader.isPause()) return true;
    	return false;
    }

	static public BookDownloader getDownloader(BookInfo info){
    	for(BookDownloader downloader:downloadingList){
    		if(downloader.info.id==info.id) return downloader;
    	}
    	return null;
    }
	static public BookDownloader getDownloader(String url){
    	for(BookDownloader downloader:downloadingList){
    		if(downloader.getDownloadUrl().equals(url)) return downloader;
    	}
    	return null;
    }
    static public BookDownloader download(BookInfo info){
    	BookDownloader downloader=getDownloader(info.downloadPath);
    	if(downloader!=null){
    		downloader.resume();
    		return null;
    	}
    	downloader=new BookDownloader(info);
    	downloader.startDownload();
    	Log.d("fax", "startdownload:"+info.getBookName());
    	return downloader;
    }
    static public BookDownloader continueDownload(BookInfo info){
    	BookDownloader downloader=getDownloader(info.downloadPath);
    	if(downloader!=null){
    		downloader.resume();
    		return null;
    	}
    	downloader=new BookDownloader(info);
    	downloader.startContinueDownload();
    	Log.d("fax", "continueDownload:"+info.getBookName());
    	return downloader;
    }
	private Thread downloadThread;
    public void startDownload(){
    	downloadThread=new Thread() {
			public void run() {
				try {
					download();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		downloadThread.start();
    }
    public void startContinueDownload(){
    	downloadThread=new Thread() {
			public void run() {
				try {
					continueDownload();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		downloadThread.start();
    }
    private void download() throws Exception {  
        handler.obtainMessage(Msg_Connecting).sendToTarget();
        
        URL url = new URL(getDownloadUrl());  
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	info.setFileSize(conn.getContentLength());
        downloadMain(conn);
    }
    private void continueDownload() throws Exception{
        handler.obtainMessage(Msg_Connecting).sendToTarget();
        
    	URL url = new URL(getDownloadUrl());  
    	
        if(info!=null){
        	Log.d("fax", "continueDownload:"+"bytes=" + info.getDone() + "-" + info.getFileSize()+"-file:"+getDownloadToFile().getName());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	info.setFileSize(conn.getContentLength());
            conn.setRequestProperty("Range", "bytes=" + info.getDone()+ "-" + info.getFileSize());
            downloadMain(conn);
        }
        
    }
    private void downloadMain(HttpURLConnection conn) throws Exception{
    	//设置超时时间   
        conn.setConnectTimeout(3000);
        int responseCode=conn.getResponseCode();
        if (responseCode >= 200&&responseCode<300) {
            
            handler.obtainMessage(Msg_StartDownload).sendToTarget();
  
            try {
//            	RandomAccessFile raf=new RandomAccessFile(getDownloadToFile(), "rws");
//            	raf.seek(info.getDone());
            	FileOutputStream fos=new FileOutputStream(getDownloadToFile(), true);
                //开始读写数据   
                InputStream in = conn.getInputStream();  
                byte[] buf = new byte[1024 * 4];
                long lastFillDataTime=0;
                int downloadedPart=0;
                int len;  
                while ((len = in.read(buf)) != -1&&!Thread.interrupted()) {
                    fos.write(buf, 0, len);
                    downloadedPart+=len;
                    long timeuse=System.currentTimeMillis()-lastFillDataTime;
                    if(timeuse>500){
                    	long speed=downloadedPart*1000/timeuse;
                        //新线程中用Handler发送消息，主线程接收消息   
                    	Message m=handler.obtainMessage(Msg_Downloading);
                    	m.getData().putLong(Msg_Date_Speed, speed);
                    	m.sendToTarget();
                    	
                    	downloadedPart=0;
                    	lastFillDataTime=System.currentTimeMillis();
                    }
                    

                    if (isPause()) {
                        //使用线程锁锁定该线程   
                        synchronized (handler) {  
                            try {  
                            	handler.wait();
                            } catch (InterruptedException e) {  
                                e.printStackTrace();  
                                break;
                            }  
                        }  
                    }
                }
                in.close();  
                fos.close();  
                if (downloadingList.contains(this)) {//提早被remove说明stop了
					Message m = handler.obtainMessage(Msg_FinishDownload);
					m.sendToTarget();
				}
				downloadingList.remove(this);
                
            } catch (IOException e) {  
                e.printStackTrace();
                downloadFail();
            }   
        } else {
        	downloadFail();
            Log.e("fax", "download fail,return:"+responseCode+",path: " + getDownloadUrl());  
        }  
    }
    private void downloadFail(){
    	if(downloadThread!=null) downloadThread.interrupt();
    	downloadingList.remove(this);
    	handler.obtainMessage(Msg_DownloadError).sendToTarget();
    }

    private boolean isPause() {
		return isPause;
	}
    //停止下载   
    public void stop() {
    	downloadingList.remove(this);
        handler.obtainMessage(Msg_StopDownload).sendToTarget();
    }  
    //暂停下载   
    public void pause() {  
    	isPause = true;  
        handler.obtainMessage(Msg_PauseDownload).sendToTarget();
    }  
    //继续下载   
    public void resume() {  
    	isPause = false;  
        handler.obtainMessage(Msg_ContinueDownload).sendToTarget();
        //恢复所有线程   
        synchronized (handler) {  
        	handler.notifyAll();  
        }
    }
    
    static private void noticeAllListener(BookDownloader downloader){
    	for(DownloadListener downloadListener : downloadListeners){
    		downloadListener.onDownloading(downloader);
    	}
    }
	private static ArrayList<DownloadListener> downloadListeners=new ArrayList<BookDownloader.DownloadListener>();
	public static void addDownloadListener(DownloadListener downloadListener) {
		downloadListeners.add(downloadListener);
	}
	public static void removeDownloadListener(DownloadListener downloadListener){
		downloadListeners.remove(downloadListener);
	}
	interface DownloadListener{
		public void onDownloading(BookDownloader downloader);
	}
}  