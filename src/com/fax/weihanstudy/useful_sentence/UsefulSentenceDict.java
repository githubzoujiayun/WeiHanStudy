package com.fax.weihanstudy.useful_sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import com.fax.weihanstudy.Word;
import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.util.Log;


/**
 * 代表一本常用语数据(一本常用语)
 * @author linfaxin
 */
public class UsefulSentenceDict {
	ArrayList<Word> allWordList = new ArrayList<Word>(1000);

	public UsefulSentenceDict(Context context,File usefulsentenceFile){
		InputStream is=null;
		try {
			is = new FileInputStream(usefulsentenceFile);
			if(usefulsentenceFile.getName().endsWith(".zip")){
				ZipInputStream zis=new ZipInputStream(is);
				zis.getNextEntry();
				is=zis;
			}
		} catch (Exception e) {
		}
		if(is!=null){
			initDic(is);
		}
	}
	//初始化常用语数据
	private void initDic(InputStream is){
		BufferedReader br = null; 
		try {
			br = new BufferedReader(new InputStreamReader(is)); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String ch = "";
		String lastPart="";
		char[] chs=new char[2048];
		try {
			while ((br.read(chs)) != -1 ) {
				ch=lastPart+new String(chs);
				String[] ss=ch.split("\n");
				int length=ss.length;
				for(int i=0;i<length-1;i++){
					String[] wordInfo=ss[i].split("\t");
					if(wordInfo.length!=2) continue;
					Word word=new Word(wordInfo[0],wordInfo[1]);
					allWordList.add(word);
						
				}
				lastPart=ss[length-1];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 搜寻单词
	 * @param word 要搜寻的关键字
	 * @return 返回与关键字相关的所有Word
	 */
	public ArrayList<Word> searchList(String word){
		if(word.length()==0) return allWordList;
//		Log.d("fax", "startFind:"+MyUtil.getNowTimeFormated());
		HashMap<Integer,ArrayList<Word>> maps=new HashMap<Integer, ArrayList<Word>>();
		for(int i=0;i<10;i++) maps.put(i, new ArrayList<Word>());
		
		ArrayList<Word> getwordlist=new ArrayList<Word>();
		
		if(allWordList==null) return null;
		for(Word findword:allWordList){
			int value=findword.relateWith(word);
			if(value>=0&&value<10){
				maps.get(value).add(findword);
			}
		}
		for(int i=0;i<10;i++){
			getwordlist.addAll(maps.get(i));
		}
//		Log.d("fax", "endFind:"+MyUtil.getNowTimeFormated());
		return getwordlist;
	}

	/**
	 * 获得单词
	 * @param word 要获得的关键字
	 * @return 返回equals(关键字)的一个Word
	 */
	public Word getWordContent(String word){
		if(word.length()==0) return allWordList.get(0);
		if(allWordList==null) return null;
		for(Word findword:allWordList){
			if(findword.getWord().equals(word)){
				return findword;
			}
		}
		return null;
	}
}
