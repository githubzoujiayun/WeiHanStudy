package com.fax.weihanstudy.dict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.fax.weihanstudy.StudyActivity;
import com.fax.weihanstudy.Word;
import com.fax.weihanstudy.update.UpdateInfo;
import com.fax.weihanstudy.utils.DownloadInfo;
import com.fax.weihanstudy.utils.DownloaderShower;
import com.fax.weihanstudy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DictManager {
	static public final String WeiHanWordsDownloadUrl="http://bcs.duapp.com/weihanstudy-dict/WeiHanWords.db";
	LocalDict[] localdicts=new LocalDict[2];
	private static DictManager dictManager;
	File dictDir;
	public File hanWeiDictFile;
	public File weiHanDictFile;
	private DictManager(Context context){
		File AppPath=MyUtil.getAppPath();
		if (AppPath!=null) {
			dictDir=new File(AppPath.getPath()+"/DICT");
			dictDir.mkdirs();
			hanWeiDictFile=new File(dictDir, "HanWeiWords.db");
			weiHanDictFile=new File(dictDir, "WeiHanWords.db");
			initAllLocalDicts();
		}
	}
	private void initAllLocalDicts(){
		if(hanWeiDictFile.exists()) localdicts[0]=new LocalDict(hanWeiDictFile);
		if(weiHanDictFile.exists()) localdicts[1]=new LocalDict(weiHanDictFile);
		
	}
	private void closeAllLocalDicts(){
		for(LocalDict localDict:localdicts){
			if(localDict!=null){
				localDict.mDb.close();
			}
		}
	}
	public void downloadNowSelectedDict(Context context){
		if(isNowSelectDictExist()) return;
		new DownloadDictTask(context).execute();
	}
	private class DownloadDictTask extends AsyncTask<Void, Void, UpdateInfo>{
		Context context;
		ProgressDialog pd;
		public DownloadDictTask(Context context){
			this.context=context;
			pd=new ProgressDialog(context);
			pd.setTitle("请稍后");
			pd.setMessage("正在获取词典信息...");
			pd.show();
		}
		@Override
		protected UpdateInfo doInBackground(Void... params) {
			try {
				String result = MyUtil.readStrFromInputStream(new URL("http://weihanstudy.duapp.com/updateinfo").openStream());
				ArrayList<UpdateInfo> updateinfos = new Gson().fromJson(result, new TypeToken<ArrayList<UpdateInfo>>() {}.getType());
				for(UpdateInfo updateInfo:updateinfos){
					String dictname="hanweidict";
					if(nowSelectIndex==1) dictname="weihandict";
					if(updateInfo.getName().equals(dictname)){
						return updateInfo;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(final UpdateInfo updateInfo) {
			pd.dismiss();
			String filename=MyUtil.getFileNameInDownloadUrl(updateInfo.getPath());
			if(filename==null) filename="dictTemp.zip";
			DownloadInfo downloadInfo=new DownloadInfo(new File(dictDir, filename), updateInfo.getPath());
			DownloaderShower.startDownloadAndShowDialog(downloadInfo, context, 
					createCompleteListener(context, updateInfo), "开始下载离线词典吗?");
		}
	}
	public DownloaderShower.CompleteListener createCompleteListener(final Context context,final UpdateInfo updateInfo){
		return new DownloaderShower.CompleteListener() {
			public void onComplete(final File downloadFile) {
				closeAllLocalDicts();
					final ProgressDialog pd=new ProgressDialog(context);
					pd.setMessage("正在解压离线词典");
					pd.show();
					new AsyncTask<Void, Void, Boolean>(){
						@Override
						protected Boolean doInBackground(Void... params) {
							try {
								MyUtil.unzip(new FileInputStream(downloadFile), dictDir.getPath());
								downloadFile.delete();
								return true;
							} catch (IOException e) {
								e.printStackTrace();
							}
							return false;
						}
						@Override
						protected void onPostExecute(Boolean result) {
							pd.dismiss();
							if(result){
								SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
								sp.edit().putInt("version_"+updateInfo.getName(), updateInfo.getVersion()).commit();
								Toast.makeText(context, "解压完成", Toast.LENGTH_SHORT).show();
								StudyActivity.handler.obtainMessage(StudyActivity.Handler_Refresh_DownloadDictBtn).sendToTarget();
							}else{
								Toast.makeText(context, "解压失败", Toast.LENGTH_SHORT).show();
							}

							initAllLocalDicts();
						}
						
					}.execute();
					
					
			}
		};
	}
	private int nowSelectIndex=0;//0为汉维，1为维汉
	private LocalDict getNowSelectDict(){
		return localdicts[nowSelectIndex];
	}
	public boolean isNowSelectDictExist(){
		return getNowSelectDict()!=null;
	}
	private boolean changeToHanWeiDict(){
//		if(hanWeiDictFile!=null&&hanWeiDictFile.exists()){
			nowSelectIndex=0;
			return true;
//		}
//		return false;
	}
	private boolean changeToWeiHanDict(){
//		if(weiHanDictFile!=null&&weiHanDictFile.exists()){
			nowSelectIndex=1;
			return true;
//		}
//		return false;
	}
	public boolean isHanWeiDict(){
		return nowSelectIndex==0;
	}
	public boolean changeDict(){
		if(nowSelectIndex==0) return changeToWeiHanDict();
		else return changeToHanWeiDict();
	}
	
	public static void initMe(Context context){
		dictManager=new DictManager(context);
		dictManager.nowSelectIndex=0;
	}
	public static DictManager getInstance(){
		return dictManager;
	}
	
//	public String getWordContent(String wordTitle){
//		if(wordTitle.length()==0) return null;
//		try {
//			return getNowSelectDict().getWordsContentByTitle(wordTitle);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static final int LocalDictPageCount=10;
	public ArrayList<Word> searchList(String word,int page){
		if(word==null) return null;
		if(isNowSelectDictExist()){//从本地词典获取,出错则返回空
				try {
					Cursor cursor=null;
					Log.d("fax", "start searchList");
					if(word.length()==0) cursor=getNowSelectDict().getAllWords();
					else cursor=getNowSelectDict().searchWord(word);
					
					Log.d("fax", "cursor.getCount():"+cursor.getCount());
					if(cursor.moveToPosition((page-1)*LocalDictPageCount)){
						ArrayList<Word> words=new ArrayList<Word>();
						words.add(new Word(cursor.getString(1), cursor.getString(2)));
						while(cursor.moveToNext()&&words.size()<LocalDictPageCount){
							words.add(new Word(cursor.getString(1), cursor.getString(2)));
						}
						return words;
					}else{
						Log.e("fax", "cursor.moveToPosition fail:"+((page-1)*LocalDictPageCount));
						return new ArrayList<Word>();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return new ArrayList<Word>();
				}
		}else{//网络查询，出错返回null
			try {
				String path="http://weihanstudy.duapp.com/words?"
						+ "dict="+nowSelectIndex+"&"
						+ "pattern="+URLEncoder.encode(word, "UTF-8")+"&"
						+ "page="+page;
				URLConnection urlConn=new URL(path).openConnection();
				urlConn.setConnectTimeout(5000);
				urlConn.setReadTimeout(5000);
				String result = MyUtil.readStrFromInputStream(urlConn.getInputStream());
				return new Gson().fromJson(result, new TypeToken<ArrayList<Word>>() { }.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
//	public Cursor getAllWords(){
//		try {
//			return getNowSelectDict().getAllWords();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}
}
