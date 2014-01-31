package com.fax.weihanstudy.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.fax.weihanstudy.dict.DictManager;
import com.fax.weihanstudy.useful_sentence.UsefulSentenceManager;
import com.fax.weihanstudy.utils.DownloadInfo;
import com.fax.weihanstudy.utils.Downloader;
import com.fax.weihanstudy.utils.DownloaderShower;
import com.fax.weihanstudy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
/**
 * 传参context，dic_update,usefulsentence_update
 * @author linfaxin
 */
public class UpdateTask extends AsyncTask<Object, Void, ArrayList<UpdateInfo>>{
	Context context;
	@Override
	protected ArrayList<UpdateInfo> doInBackground(Object... params) {
		try {
			context=(Context) params[0];
					
			String result=MyUtil.readStrFromInputStream(new URL("http://weihanstudy.duapp.com/updateinfo").openStream());
			return new Gson().fromJson(result, new TypeToken<ArrayList<UpdateInfo>>(){}.getType());  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<UpdateInfo> updateInfos) {
		if(updateInfos==null){
			return;
		}
		final SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
		
		for(final UpdateInfo updateInfo:updateInfos){
			Log.d("fax", "checkUpdate:"+updateInfo.getName());
			
			//更新apk
			if(updateInfo.getName().equals("apk") && MyUtil.getAppVersion(context)<updateInfo.getVersion()){
				new AlertDialog.Builder(context).setTitle("提示").setMessage("发现软件新版本，是否更新?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String filename=MyUtil.getFileNameInDownloadUrl(updateInfo.getPath());
							if(filename==null) filename="update.apk";
							DownloadInfo downloadInfo=new DownloadInfo(new File(MyUtil.getAppPath(), filename), updateInfo.getPath());
							final Downloader downloader=Downloader.continueDownload(downloadInfo);
							DownloaderShower.showDownloadingDialog(downloader, context, 
									new DownloaderShower.CompleteListener() {
										public void onComplete(File downloadFile) {
											Intent intent = new Intent();  
											intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
											intent.setAction(android.content.Intent.ACTION_VIEW);  
											Uri uri = Uri.fromFile(downloadFile);
											intent.setDataAndType(uri,"application/vnd.android.package-archive"); 
											context.startActivity(intent);
										}
									});
						}
					})
					.setNegativeButton("取消", null).create().show();
				
				
				
			//更新常用语
			}else if(updateInfo.getName().equals("usefulsentence")&&sp.getInt("version_usefulsentence", 3)<updateInfo.getVersion()){
				new AlertDialog.Builder(context).setTitle("提示").setMessage("检测到常用语词库有更新，是否更新?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String filename=MyUtil.getFileNameInDownloadUrl(updateInfo.getPath());
						if(filename==null) filename="usefulsentence.zip";
						DownloadInfo downloadInfo=new DownloadInfo(new File(MyUtil.getAppPath(), filename), updateInfo.getPath());
						final Downloader downloader=Downloader.continueDownload(downloadInfo);
						DownloaderShower.showDownloadingDialog(downloader, context, new DownloaderShower.CompleteListener() {
							public void onComplete(File downloadFile) {
								try {
									UsefulSentenceManager.getInstance(context).copyUsefulSentenceToDate(new FileInputStream(downloadFile));
									downloadFile.delete();
									sp.edit().putInt("version_usefulsentence", updateInfo.getVersion()).commit();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				})
				.setNegativeButton("取消", null).create().show();
				
				
				
			//更新维汉离线词典
			}else if(updateInfo.getName().equals("weihandict")&&sp.getInt("version_weihandict", 99999)<updateInfo.getVersion()){
				if(DictManager.getInstance().weiHanDictFile.exists())
				new AlertDialog.Builder(context).setTitle("提示").setMessage("发现维汉离线词典有更新，是否更新?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String filename=MyUtil.getFileNameInDownloadUrl(updateInfo.getPath());
						if(filename==null) filename="dictTemp.zip";
						DownloadInfo downloadInfo=new DownloadInfo(new File(MyUtil.getAppPath(), filename), updateInfo.getPath());
						final Downloader downloader=Downloader.continueDownload(downloadInfo);
						DownloaderShower.showDownloadingDialog(downloader, context, 
								DictManager.getInstance().createCompleteListener(context, updateInfo));
					}
				})
				.setNegativeButton("取消", null).create().show();
				
				
			//更新汉维离线词典	
			}else if(updateInfo.getName().equals("hanweidict")&&sp.getInt("version_hanweidict", 99999)<updateInfo.getVersion()){
				if(DictManager.getInstance().hanWeiDictFile.exists())
				new AlertDialog.Builder(context).setTitle("提示").setMessage("发现汉维离线词典有更新，是否更新?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String filename=MyUtil.getFileNameInDownloadUrl(updateInfo.getPath());
						if(filename==null) filename="dictTemp.zip";
						DownloadInfo downloadInfo=new DownloadInfo(new File(MyUtil.getAppPath(), filename), updateInfo.getPath());
						final Downloader downloader=Downloader.continueDownload(downloadInfo);
						DownloaderShower.showDownloadingDialog(downloader, context, 
								DictManager.getInstance().createCompleteListener(context, updateInfo));
					}
				})
				.setNegativeButton("取消", null).create().show();
			} 
		}
	}
	
}
