package com.fax.weihanstudy.dict;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * 代表一个本地词典数据(一本词典)
 * @author linfaxin
 */
public class LocalDict {
	SQLiteDatabase mDb;

	private Cursor allwordcursor;
	private File dictFile;
	public File getDictFile(){
		return dictFile;
	}
    public static final String WORDS_BOOK_ROWID = "_id";
	public static final String WORDS_BOOK_TITLE = "word_title";
	public static final String WORDS_BOOK_CONTENT = "word_content";
	private static final String WORDS_BOOK_DATABASE_TABLE = "WORDS";
	public LocalDict(File dictFile){
		this.dictFile=dictFile;
		openDatabase();
		getAllWords();
	}
	private void openDatabase(){
		mDb=SQLiteDatabase.openDatabase(dictFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
	}
	
	private Cursor lastSearchCursor;
	private String lastSearchWord;
    public Cursor searchWord(String pattern) {
    	if(pattern != null&&pattern.equals(lastSearchWord)) return lastSearchCursor;
    	if(lastSearchCursor!=allwordcursor&&lastSearchCursor!=null) lastSearchCursor.close();
    	lastSearchWord=pattern;
    	if ((pattern != null) && (pattern.length() > 0)) {	
    		
    		pattern = pattern + "%";    	
			lastSearchCursor = mDb.query(WORDS_BOOK_DATABASE_TABLE, new String[] { WORDS_BOOK_ROWID, WORDS_BOOK_TITLE, WORDS_BOOK_CONTENT },
					WORDS_BOOK_TITLE + " LIKE '" + pattern + "'", null, null, null, null, "100");
    		return lastSearchCursor;
    	} else {
    		return getAllWords();
    	}
    	
    }
	

    public Cursor getAllWords() {
    	if(allwordcursor==null||allwordcursor.isClosed()){
    		allwordcursor=mDb.query(WORDS_BOOK_DATABASE_TABLE,
				new String[] {WORDS_BOOK_ROWID, WORDS_BOOK_TITLE, WORDS_BOOK_CONTENT},
				null, null, null, null, null);
    	}
		return allwordcursor;
    }
	

    public String getWordsContentByTitle(String title) {
    	String result = null;
    	
    	Cursor cursor = mDb.query(WORDS_BOOK_DATABASE_TABLE, new String[] {WORDS_BOOK_CONTENT}, WORDS_BOOK_TITLE + " = \"" + title + "\"", null, null, null, null);
    	
    	if (cursor.moveToFirst()) {
    		
    		result = cursor.getString(cursor.getColumnIndex(WORDS_BOOK_CONTENT));    		    		    		
    	}
    	
    	cursor.close();
    	
    	return result;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private File dictFile;
//	public File getDictFile() {
//		return dictFile;
//	}
//
//	ArrayList<Word> allWordList = new ArrayList<Word>(329050);
//	private final int listSplitBase=400;//帮助快速索引
//	TreeMap<Integer,ArrayList<Word>> allWordListTreeMap=new TreeMap<Integer,ArrayList<Word>>();//快速索引
//	private boolean dictFileExist=false;
//
//	public LocalDict(File idxsFile,File dictFile){
//		this.dictFile=dictFile;
//		
//		if(idxsFile.exists()){
//			try {
//				if(dictFile.exists()){
//					dictFileExist=true;
//				}
//				initDic(new FileInputStream(idxsFile));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	public LocalDict(InputStream idxsIs,File dictFile){
//		this.dictFile=dictFile;
//		if(dictFile.exists()){
//			dictFileExist=true;
//		}
//		initDic(idxsIs);
//	}
//	public boolean isDictFileExist(){
//		return dictFileExist;
//	}
//	//初始化词典数据
//	private void initDic(InputStream file){
//		final long timeRec=System.currentTimeMillis();//if(timeRec>0) return;
//		Log.d("fax", "startInitLocalDict:"+MyUtil.getNowTimeFormated());
//		BufferedReader br = null; 
//		try {
//			br = new BufferedReader(new InputStreamReader(file)); 
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		String ch = "";
//		String lastPart="";
//		char[] chs=new char[2048];
//		ArrayList<Word> treelist = null;
//		int lastIndex=-1;
//		int i=0;
//		try {
//			
//
//			HanWeiDbAdapter initDb = new HanWeiDbAdapter(StartScreen.startScreen);
//			initDb.open();
//			initDb.mDb.beginTransaction();
////			while ((br.read(chs)) != -1 ) {
//			while ((ch=br.readLine()) != null ) {	
////						int listIndex=ch.charAt(0)/listSplitBase;
////						if (lastIndex!=listIndex) {
////							lastIndex=listIndex;
////							treelist = allWordListTreeMap.get(listIndex);
////							if (treelist == null) {
////								treelist = new ArrayList<Word>();
////								allWordListTreeMap.put(listIndex, treelist);
////							}
////						}
//						Word word=(new Word(ch, this));
//						
//						initDb.updateWordsBook(word.getWord(), word.getContent());
//						i++;
//						if(i%100==0){
//							String loginfo="nowIndex:"+i;
//							Log.d("fax",loginfo);
//						}
//				
////				ch=new StringBuffer(lastPart).append(chs).toString();
////				String[] ss=ch.split("\n");
////				int length=ss.length;
////				for(int i=0;i<length-1;i++){
////						int listIndex=ss[i].charAt(0)/listSplitBase;
////						if (lastIndex!=listIndex) {
////							lastIndex=listIndex;
////							treelist = allWordListTreeMap.get(listIndex);
////							if (treelist == null) {
////								treelist = new ArrayList<Word>();
////								allWordListTreeMap.put(listIndex, treelist);
////							}
////						}
////						treelist.add(new Word(ss[i], this));
////				}
////				lastPart=ss[length-1];
//			}
//			
//
//			initDb.mDb.setTransactionSuccessful();
//			initDb.mDb.endTransaction();
//			Log.d("fax", "endedInitDb");
//			initDb.close();
//			
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		try {
//			br.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		
//		for(ArrayList<Word> list:allWordListTreeMap.values()){
//			if(list==null) continue;
//			allWordList.addAll(list);
//		}
////		allWords=all_word.toArray(new Word[all_word.size()]);
//		Log.d("fax", "endInitLocalDict:"+(System.currentTimeMillis()-timeRec)+",word size:"+allWordList.size());
//	}
//	/**
//	 * 搜寻单词
//	 * @param word 要搜寻的关键字
//	 * @return 返回startWith(关键字)的所有Word
//	 */
//	public ArrayList<Word> searchList(String word){
//		if(word.length()==0) return allWordList;
//		Log.d("fax", "startFind:"+MyUtil.getNowTimeFormated());
//		ArrayList<Word> getwordlist=new ArrayList<Word>();
//		ArrayList<Word> findlist=allWordListTreeMap.get(word.charAt(0)/listSplitBase);
//		if(findlist==null) return null;
//		for(Word findword:findlist){
//			if(findword.getWord().startsWith(word)){
//				getwordlist.add(findword);
//			}
//		}
//		Log.d("fax", "endFind:"+MyUtil.getNowTimeFormated()+",findSize:"+getwordlist.size());
//		return getwordlist;
//	}
//
//	/**
//	 * 获得单词
//	 * @param word 要获得的关键字
//	 * @return 返回equals(关键字)的一个Word
//	 */
//	public Word getWordContent(String word){
//		if(word.length()==0) return allWordList.get(0);
//		ArrayList<Word> findlist=allWordListTreeMap.get(word.charAt(0)/listSplitBase);
//		if(findlist==null) return null;
//		for(Word findword:findlist){
//			if(findword.getWord().equals(word)){
//				return findword;
//			}
//		}
//		return null;
//	}
}
