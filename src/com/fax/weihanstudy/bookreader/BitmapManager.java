package com.fax.weihanstudy.bookreader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
public class BitmapManager{
	static private Bitmap loadingFailBitmap;
	static private File imgDir;
	static final private String ImgFileNameExtension=".image"; 
	static public void init(Context context,Bitmap loadingFailBitmap,File imgDir){
		if(BitmapManager.imgDir!=null) return;
		final int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass(); 
		initMaxCache(1024 * 1024 * memClass / 8, 1024 * 1024 * memClass / 8);
		BitmapManager.loadingFailBitmap=loadingFailBitmap;
		BitmapManager.imgDir=imgDir;
		if(!BitmapManager.imgDir.exists()) BitmapManager.imgDir.mkdirs();
	}
	static private void initMaxCache(int bitmapsize,int bytesize){
		MAX_BitmapList_Size=bitmapsize;
		MAX_ImageByteList_Size=bytesize;
		Log.d("fax", "initMaxCache,bitmapsize:"+bitmapsize+",bytesize:"+bytesize);
	}
	private static int MAX_BitmapList_Size=5*1024*1024;
	private static int MAX_ImageByteList_Size=2*1024*1024;
	private static final LinkedHashMap<String,Bitmap> bitmapList=new LinkedHashMap<String,Bitmap>();
	private static final LinkedHashMap<String,byte[]> imagebytesList=new LinkedHashMap<String,byte[]>();
	
	public static void getBitmapBackground(String url,BitmapLoadingListener listen){
		new LoadBitmapAsync().execute(url,listen);
	}
	public static Bitmap getBitmap(String url){
		if(imgDir==null){
			Log.e("fax", "must call init before getBitmap");
			return loadingFailBitmap;
		}
//		Log.d("fax", "getBitmap,url:"+url);
		try {
			Bitmap bitmap = getFromBitmapList(url, true);
			if (bitmap == null)
				return loadingFailBitmap;
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return loadingFailBitmap;
		}
	}
	private static Bitmap getFromBitmapList(String url,boolean isSave){
		Bitmap bitmap=bitmapList.get(url);
		if(bitmap==null||bitmap.isRecycled()){//如果没找到，则从imagebytesList中找,并添加结果到bitmapList
			byte[] img_bytes=getFromImageBytesList(url,isSave);
			if(img_bytes==null) return null;
			try {
				bitmap = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length);
				if(isSave){
					bitmapList.put(url, bitmap);
					checkBitmapListSize();//检查大小是否超出
				}
			} catch (Exception e) {
				Log.e("faxgetFromBitmapList", "url:"+url);
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 从imagebyteslist中找，若没找到，则从disk或者net加载
	 * @param url 地址
	 * @param isSaveDisk 是否从储存卡读取(并写入),否则每次都从网络读取
	 * @return
	 */
	private static byte[] getFromImageBytesList(String url,boolean isSave){
		byte[] img_bytes=imagebytesList.get(url);
		if(img_bytes==null){//如果没找到，则从getImgBytesInDisk中找,并添加结果到imagebytesList
			if(isSave) img_bytes=getImgBytesInDisk(url);
			else img_bytes=getImgBytesInNet(url);
			if(img_bytes==null) return null;
			if(isSave){
				imagebytesList.put(url, img_bytes);
				checkImageBytesSize();
			}
		}
		return img_bytes;
	}
	public static File getImgFile(String urlStr){
		final String filename=convertToFileName(urlStr);
		final File imageFile = new File(imgDir, filename);
		return imageFile;
	}
	//从储存卡读取数据,同步以避免多次读取同一图片
	synchronized private static byte[] getImgBytesInDisk(String urlStr){
		//先尝试从本地读取数据
		final String filename=convertToFileName(urlStr);
		final File imageFile = new File(imgDir, filename);
		if (imageFile.exists() && imageFile.length() > 0) {
			try {
				byte[] bytes = getBytesFromInputStream(new BufferedInputStream(new FileInputStream(imageFile)));
				if (bytes == null){
					imageFile.delete();
				}else return bytes;
			} catch (Exception e) {
				e.printStackTrace();
				imageFile.delete();
			}
		}else imageFile.delete();

		// 若本地不存在，则从网络获取并写入本地
		byte[] bytes = getImgBytesInNet(urlStr);
		if(bytes==null) return null;
		
		//写入本地
		try {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(imageFile));
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			byte[] temp = new byte[10 * 1024];
			int length;
			while ((length = bais.read(temp)) != -1) {
				fos.write(temp, 0, length);
			}
			fos.close();
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	//从网络获取图片
	synchronized private static byte[] getImgBytesInNet(String urlStr){
		try {
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			byte[] imgbytes = getBytesFromInputStream(is);
			return imgbytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private static byte[] getBytesFromInputStream(InputStream is){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[16 * 1024];
			int length;
			while ((length = is.read(bytes)) != -1) {
				baos.write(bytes, 0, length);
			}
			byte[] imgbytes = baos.toByteArray();
			baos.close();
			is.close();
			return imgbytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private static String convertToFileName(String urlStr){
		try {
			URL url=new URL(urlStr);
			String fileName=new File(url.getPath()).getName()+ImgFileNameExtension;
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return urlStr.replace("\\", "-").replace("/", "-").replace(":", "-").replace("*", "-").replace("?", "-")
					  .replace("\"", "-").replace("<", "-").replace(">", "-").replace("|", "-");
	}
	private static void checkBitmapListSize(){
		int allBitmapSize = 0;
		for(Bitmap bitmap:bitmapList.values()){
			if(bitmap==null||bitmap.isRecycled()) continue;
			allBitmapSize+=(bitmap.getRowBytes()*bitmap.getHeight());
		}
		if(allBitmapSize>MAX_BitmapList_Size){//去除最后一张图片
			try {
				Log.d("fax", "MAX_BitmapList_Size!remove a bitmap...bitmaplist.size:"+bitmapList.size()+",allBitmapByteSize:" + allBitmapSize);
				int size = bitmapList.keySet().size();
				if(size>0){
					Bitmap bitmap=bitmapList.remove(bitmapList.keySet().iterator().next());
					bitmap.recycle();
					bitmap=null;
				}
				checkBitmapListSize();
			} catch (Exception e) {
				Log.d("fax", "MAX_BitmapList_Size!Exception:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	private static void checkImageBytesSize(){
		int allBytesSize = 0;
		for(byte[] bytes:imagebytesList.values()){
			allBytesSize+=(bytes.length);
		}
		if(allBytesSize>MAX_ImageByteList_Size){//去除最后一个
			try {
				Log.d("fax", "MAX_ImageByteList_Size!remove a  ImageByte...imagebytesList.size:"+imagebytesList.size()+",allImageByteSize:" + allBytesSize);
				int size = imagebytesList.keySet().size();
				if(size>0){
					String key=imagebytesList.keySet().iterator().next();
					if(imagebytesList.remove(key)==null){
						Log.e("fax", "MAX_ImageByteList_Size!no map key in list:"+key);
					}
				}else{
					Log.d("fax", "MAX_ImageByteList_Size!Exception:size==0");
				}
				checkImageBytesSize();
			} catch (Exception e) {
				Log.d("fax", "MAX_ImageByteList_Size!Exception:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public static byte[] compressBitmap(Bitmap bitmap){
		return compressBitmap(bitmap, 80, false);
	}
	public static byte[] compressBitmap(Bitmap bitmap,int quality,boolean isRecycle){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		if(isRecycle) bitmap.recycle();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	public interface BitmapLoadingListener{
		public void onBitmapLoadFinish(Bitmap bitmap,boolean isLoadSuccess);
	}
	private static class LoadBitmapAsync extends AsyncTask<Object, Void, Bitmap>{
		BitmapLoadingListener bitmapLoadingListener;
		@Override
		protected Bitmap doInBackground(Object... params) {
			this.bitmapLoadingListener=(BitmapLoadingListener) params[1];
			return getBitmap((String) params[0]);
		}
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			bitmapLoadingListener.onBitmapLoadFinish(bitmap, bitmap==loadingFailBitmap);
		}
		
	}
}
