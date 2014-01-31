package com.fax.weihanstudy.dict;

import java.util.ArrayList;

import me.maxwin.view.XListView;

import com.fax.weihanstudy.R;
import com.fax.weihanstudy.Word;
import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class DictListView extends XListView implements AdapterView.OnItemClickListener{
	SQLiteDatabase mDb;
//	DictCursorAdapter dictCursorAdapter;
	DictListAdapter dictListAdapter;
	View loadingView;
	View noItemView;
	public DictListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View headView=View.inflate(context, R.layout.dictlist_head, null);
		loadingView=headView.findViewById(android.R.id.text1);
		noItemView=headView.findViewById(android.R.id.text2);
		addHeaderView(headView);

		setPullRefreshEnable(false);
		setPullLoadEnable(false);
		setXListViewListener(new IXListViewListener() {
			public void onRefresh() {
			}
			public void onLoadMore() {
				searchWordAsync=new SearchWordAsync();
				searchWordAsync.execute(nowSearchWord);
			}
		});
		
		setOnItemClickListener(this);
//		dictCursorAdapter=new DictCursorAdapter(context, dictManager.getAllWords());
//		setAdapter(dictCursorAdapter);
		dictListAdapter=new DictListAdapter(context);
		setAdapter(dictListAdapter);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		String title=((TextView)view.findViewById(R.id.list_word_item_title)).getText().toString();
		String content=((TextView)view.findViewById(R.id.list_word_item_content)).getText().toString();
		Word word=new Word(title, content);
		MyUtil.openWordView(getContext(),word);
	}
	
	SearchWordAsync searchWordAsync;
	private String nowSearchWord;
	private int nowPage;
	public void searchWord(final String pattern){
		this.searchWord(pattern, 1);
	}
	private void searchWord(final String pattern,int page){
		if(searchWordAsync!=null) searchWordAsync.cancel(true);
		if(pattern==null||pattern.length()==0){
			clearList();
			return;
		}
		nowSearchWord=pattern;
		nowPage=page;
		showLoading();
		searchWordAsync=new SearchWordAsync();
		searchWordAsync.execute(pattern,page+"");
	}
	class SearchWordAsync extends AsyncTask<String, Void, ArrayList<Word>>{
		@Override
		protected ArrayList<Word> doInBackground(String... params) {
//			long timeBrforeSearch=System.currentTimeMillis();
//			Cursor cursor=dictManager.searchList(params[0]);
//			try {
//				Thread.sleep(Math.max(0, 300-System.currentTimeMillis()+timeBrforeSearch));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			if(params.length>=2) return DictManager.getInstance().searchList(params[0],Integer.valueOf(params[1]));
			else return DictManager.getInstance().searchList(params[0],++nowPage);
		}
		@Override
		protected void onPostExecute(ArrayList<Word> words) {
//			changeCursor(cursor);
			if(words!=null){
				dictListAdapter.addWords(words);
				if(dictListAdapter.getWords().size()==0){
					showNoItem();
				}else{
					if(words.size()==DictManager.LocalDictPageCount) setPullLoadEnable(true);
					else setPullLoadEnable(false);
					dismissLoading();
				}
			}else{
				Toast.makeText(getContext(), "²éÑ¯Ê§°Ü,Çë¼ì²éÍøÂç!", Toast.LENGTH_SHORT).show();
				dismissLoading();
			}
			stopLoadMore();
		}
		
	}
	
	private void showLoading(){
		loadingView.setVisibility(View.VISIBLE);
		noItemView.setVisibility(View.GONE);
	}
	private void dismissLoading(){
		loadingView.setVisibility(View.GONE);
		noItemView.setVisibility(View.GONE);
	}
	private void showNoItem(){
		loadingView.setVisibility(View.GONE);
		noItemView.setVisibility(View.VISIBLE);
	}
//	private Cursor getAllWords(){
//		return dictManager.searchList("");
//	}
	public String getWordContent(String wordTitle){
//		return dictManager.getWordContent(wordTitle);
		for(Word word:dictListAdapter.getWords()){
			if(word.getWord().equals(wordTitle)){
				return word.getContent();
			}
		}
		return null;
	}
	public void clearList(){
//		changeCursor(null);
		dismissLoading();
		dictListAdapter.clearWords();
		setPullLoadEnable(false);
	}
	public boolean isHanyuInput(){
		return DictManager.getInstance().isHanWeiDict();
	}
//	private void changeCursor(Cursor cursor){
//		Cursor oldCursor=dictCursorAdapter.swapCursor(cursor);
//		if(oldCursor!=null&&oldCursor!=getAllWords()){
//			oldCursor.close();
//		}
//	}
}
