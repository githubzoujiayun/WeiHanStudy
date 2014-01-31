package com.fax.weihanstudy.useful_sentence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.fax.weihanstudy.StudyActivity;
import com.fax.weihanstudy.Word;

import android.content.Context;

/**管理常用语，初始化以及search*/
public class UsefulSentenceManager {
	private ArrayList<UsefulSentenceDict> localDicts=new ArrayList<UsefulSentenceDict>();
	public static UsefulSentenceManager dictHelper;
	private File usefulSentenceFile;
	Context context;
	private UsefulSentenceManager(Context context){
		this.context=context;
		File dir=new File("data/data/"+context.getPackageName()+"/usefulsentence");
		dir.mkdirs();
		usefulSentenceFile=new File(dir, "usefulsentence.zip");
		if(!usefulSentenceFile.exists()){
			try {
				copyUsefulSentenceToDate(context.getAssets().open("usefulsentence.zip"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else initUsefulSentence();
	}
	public void copyUsefulSentenceToDate(InputStream is) throws IOException{
		BufferedInputStream bis=new BufferedInputStream(is);
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(usefulSentenceFile));
		byte[] buffer=new byte[1024];
		int length=0;
		while((length=bis.read(buffer))!=-1){
			bos.write(buffer,0,length);
		}
		bos.close();
		bis.close();

		initUsefulSentence();
	}
	private void initUsefulSentence(){
		localDicts.clear();
		UsefulSentenceDict localDict=new UsefulSentenceDict(context,usefulSentenceFile);
		localDicts.add(localDict);
		initAllWord();
		if(StudyActivity.handler!=null) StudyActivity.handler.obtainMessage(StudyActivity.Handler_Refresh_Usefulsentence).sendToTarget();
	}
	public static UsefulSentenceManager initMe(Context context){
		return getInstance(context);
	}
	public static UsefulSentenceManager getInstance(Context context){
		if(dictHelper==null) dictHelper=new UsefulSentenceManager(context);
		return dictHelper;
	}
	
	public Word getWordContent(String wordTitle){
		if(wordTitle.length()==0) return null;
		for(UsefulSentenceDict localDict:localDicts){
			Word word=localDict.getWordContent(wordTitle);
			if(word!=null) return word;
		}
		return null;
	}
	
	public ArrayList<Word> searchList(String word){
		if(word.length()==0) return getAllWords();
		
		ArrayList<Word> allFindingWordslist=new ArrayList<Word>();
		for(UsefulSentenceDict localDict:localDicts){
			ArrayList<Word> wordslist=localDict.searchList(word);
			if(wordslist!=null) allFindingWordslist.addAll(wordslist);
		}
		return allFindingWordslist;
	}
	private ArrayList<Word> allDictWords;
	public ArrayList<Word> getAllWords(){
		if(allDictWords!=null) return allDictWords;
		initAllWord();
		return allDictWords;
	}
	private void initAllWord(){
		allDictWords=new ArrayList<Word>();
		for(UsefulSentenceDict localDict:localDicts){
			ArrayList<Word> wordslist=localDict.searchList("");
			if(wordslist!=null) allDictWords.addAll(wordslist);
		}
	}
}
