package com.fax.weihanstudy;

import java.io.File;
import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.fax.weihanstudy.bookreader.BookReaderAdapter;
import com.fax.weihanstudy.bookreader.BookReaderListView;
import com.fax.weihanstudy.bookreader.BooksInCloudListActivity;
import com.fax.weihanstudy.dict.DictManager;
import com.fax.weihanstudy.games.InputGameActivity;
import com.fax.weihanstudy.update.UpdateTask;
import com.fax.weihanstudy.useful_sentence.UsefulSentenceManager;
import com.fax.weihanstudy.useful_sentence.UsefulSentenceListAdapter;
import com.fax.weihanstudy.utils.MyUtil;
import com.fax.weihanstudy.wordsbook.AddCustomWordActivity;
import com.fax.weihanstudy.wordsbook.WordsBookDbHelper;
import com.fax.weihanstudy.wordsbook.WordsBookListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

//主界面：负责词典、常用语、单词本的展示，和书库，游戏的入口
public class StudyActivity extends FragmentActivity {

    LinearLayout content_views_dic;
	View content_views_useful_sentence;
	View content_views_wordsbook;
	View content_views_bookreader;
	View content_views_game;
	
	ViewPager menuviewsPager;
	WeiInputFragment weiInputFragment;
	
	public static Handler handler;
	public static final int Handler_Refresh_WordsBookView=1;
	public static final int Handler_Refresh_BookReader=2;
	public static final int Handler_Refresh_DownloadDictBtn=3;
	public static final int Handler_Refresh_Usefulsentence=4;
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        initViews();
        handler=new Handler(){
			public void handleMessage(Message msg) {
				switch(msg.what){
				case Handler_Refresh_WordsBookView:
					refreshWordsBookView();
					break;
				case Handler_Refresh_BookReader:
					refreshBookReaderView();
					break;
				case Handler_Refresh_DownloadDictBtn:
					refreshDownloadDictBtn();
					break;
				case Handler_Refresh_Usefulsentence:
					refreshUsefulSentence();
					break;
				}
			}
        	
        };
        
        checkUpdate();
    }
    private void checkUpdate(){
    	new UpdateTask().execute(this);
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
		wordsBookDbAdapter.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshBookReaderView();
		StatService.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
	public void initViews(){
    	initDicView();
    	initUsefulSentenceView();
    	initWordsBookView();
    	initBookReaderView();
    	initGameView();
    	
    	initContentViews();
    	initInputBord();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if(weiInputFragment!=null&&weiInputFragment.isShowingInputBord()){
    			weiInputFragment.hideInputBord();
    			return true;
    		}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void initContentViews() {
		
		final ArrayList<View> viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(content_views_dic);
		viewList.add(content_views_useful_sentence);
		viewList.add(content_views_wordsbook);
		viewList.add(content_views_bookreader);
		viewList.add(content_views_game);
		menuviewsPager = (ViewPager) findViewById(R.id.activity_views_context);
		menuviewsPager.setAdapter(new MyViewPagerAdapter(viewList));
		menuviewsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				LinearLayout title_layout=(LinearLayout) findViewById(R.id.activity_titles);
				for(int i=0;i<title_layout.getChildCount();i++){
					if(i==index) title_layout.getChildAt(i).setEnabled(false);
					else title_layout.getChildAt(i).setEnabled(true);;
				}
				
				
				EditText et = (EditText) viewList.get(index).findViewById(R.id.dic_input);
				if (et == null) {
					weiInputFragment.removeInputBord();
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(viewList.get(index).getWindowToken(), 0);
				}
					
					
				if(index==0){//如果是在词典页面
					if (dic_list.isHanyuInput()) {//是否是汉语输入状态
						weiInputFragment.removeInputBord();
					}else{//维语输入状态
						weiInputFragment.showInputBord(et);
					}
				}
				if(index==1){//如果是在常用语页面
					if (weiInputFragment.isShowingInputBord()) {//是否是汉语输入状态
						weiInputFragment.showInputBord(et);
					}
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	public void click_in_title(View view) {
		menuviewsPager.setCurrentItem( ((ViewGroup)view.getParent()).indexOfChild(view),true);
	}
	
	public void initInputBord(){
//		if(inputBord==null) inputBord=View.inflate(this, R.layout.wei_input_view, null);
//		inputBordContain=(ViewGroup) findViewById(R.id.activity_inputbord_contain);

        weiInputFragment=new WeiInputFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_inputbord_contain, weiInputFragment).commit();
	}
//	EditText inputingEditText;
//	ViewGroup inputBordContain;
//	View inputBord;
//	public void showInputBord(EditText et){
//		Log.d("fax", "showInputBord");
//		if(isShowingInputBord()){
//			hideInputBord();
//		}
//		et.setEnabled(false);
//		et.clearFocus();
//
//		InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//		inputManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
//		inputingEditText=et;
//		ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(-1, -2);
//		inputBordContain.addView(inputBord, params);
//	}
//	public boolean isShowingInputBord(){
//		if(inputBord==null) return false;
//		if(inputBord.getParent()!=null) return true;
//		return false;
//	}
//	public void hideInputBord(){
//		if(isShowingInputBord()){
//			Log.d("fax", "hideInputBord");
//			inputingEditText.setEnabled(true);
//			inputingEditText.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					showInputBord(inputingEditText);
//				}
//			});
//			((ViewGroup)inputBord.getParent()).removeView(inputBord);
//		}
//	}
//	public void removeInputBord(){
//		Log.d("fax", "removeInputBord");
//		if (inputingEditText!=null) {
//			inputingEditText.setEnabled(true);
//			inputingEditText.setOnClickListener(null);
//		}
//		if(inputBord.getParent()!=null) ((ViewGroup)inputBord.getParent()).removeView(inputBord);
//	}
//	public void click_in_inputbord(View view){
//		String inputingText=inputingEditText.getText().toString();
//		switch(view.getId()){
//		case R.id.input_wei_bord:
//			inputingEditText.setText(inputingText+((Button)view).getText());
//			break;
//		case R.id.input_wei_bord_back:
//			if(inputingText.length()==0) return;
//			inputingEditText.setText(inputingText.substring(0, inputingText.length()-1));
//			break;
//		case R.id.input_wei_bord_hide:
//			weiInputFragment.hideInputBord();
//			break;
//		case R.id.input_wei_bord_fn:
//			break;
//		}
//		inputingEditText.setSelection(0, inputingEditText.getText().length());
//	}
	private com.fax.weihanstudy.dict.DictListView dic_list;
	private void initDicView(){
		content_views_dic = (LinearLayout) View.inflate(this, R.layout.content_dic, null);
		dic_list=(com.fax.weihanstudy.dict.DictListView)content_views_dic.findViewById(R.id.dic_listview);
		
		final MyEditText dic_input=(MyEditText)content_views_dic.findViewById(R.id.dic_input);
		dic_input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				dic_list.clearList();
				dic_list.searchWord(dic_input.getString());
			}
		});
		

		refreshDownloadDictBtn();
	}
	private void refreshDownloadDictBtn(){
		if(DictManager.getInstance().isNowSelectDictExist()){
			content_views_dic.findViewById(R.id.dic_download_dict).setVisibility(View.GONE);
		}else{
			content_views_dic.findViewById(R.id.dic_download_dict).setVisibility(View.VISIBLE);
		}
	}
    
    public void click_in_dic_view(View view){
		switch(view.getId()){
		case R.id.dic_search:
			final String wordtitle=((EditText)content_views_dic.findViewById(R.id.dic_input)).getText().toString();
			String wordContent=dic_list.getWordContent(wordtitle);
			if(wordContent!=null) MyUtil.openWordView(this,wordtitle,wordContent);
			break;
		case R.id.dic_change_input:
			if(DictManager.getInstance().changeDict()){
				EditText et=(EditText)content_views_dic.findViewById(R.id.dic_input);
				
				if(!dic_list.isHanyuInput()){
					weiInputFragment.showInputBord(et);
				}else{
					weiInputFragment.removeInputBord();
					MyUtil.showSystemInputBord(et);
				}

				refreshDownloadDictBtn();
				
				dic_list.clearList();
				et.setText("");
			}else{
//				DictManager.getInstance(this).startDownloadWeiHanDict(this);
			}
			
			break;
		case R.id.dic_download_dict:
			DictManager.getInstance().downloadNowSelectedDict(this);
			break;
		}
	}
    private UsefulSentenceManager usefulstHelper;
	private void initUsefulSentenceView(){
		usefulstHelper=UsefulSentenceManager.getInstance(this);
		content_views_useful_sentence = (LinearLayout) View.inflate(this, R.layout.content_usefulsentence, null);
		final UsefulSentenceListAdapter dictListAdapter=new UsefulSentenceListAdapter(this);
		ListView dic_list=(ListView)content_views_useful_sentence.findViewById(R.id.dic_listview);
		dic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Word word=dictListAdapter.getItem(position);
				MyUtil.openWordView(StudyActivity.this,word);
			}
		});
		dic_list.setAdapter(dictListAdapter);
		dictListAdapter.setWords(usefulstHelper.searchList(""));
		
		final MyEditText dic_input=(MyEditText)content_views_useful_sentence.findViewById(R.id.dic_input);
		dic_input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				if(usefulstHelper==null) return;
				dictListAdapter.setWords(usefulstHelper.searchList(dic_input.getString()));
			}
		});
	}
	private void refreshUsefulSentence(){
		((EditText)content_views_useful_sentence.findViewById(R.id.dic_input)).setText("");
	}
	public void click_in_usefulsentence_view(View view){
		switch(view.getId()){
		case R.id.dic_search:
			if(usefulstHelper==null) return;
			final String wordtitle=((EditText)content_views_useful_sentence.findViewById(R.id.dic_input)).getText().toString();
			Word getWord=usefulstHelper.getWordContent(wordtitle);
			if(getWord!=null) MyUtil.openWordView(this,getWord);
			break;
		case R.id.dic_change_input:
			EditText et=(EditText)content_views_useful_sentence.findViewById(R.id.dic_input);
			et.setText("");
			if(!weiInputFragment.isShowingInputBord()){
//				((ImageButton)view).setImageResource(R.drawable.icon_weihan);
				weiInputFragment.showInputBord(et);
			}
			else{
//				((ImageButton)view).setImageResource(R.drawable.icon_hanwei);
				weiInputFragment.removeInputBord();
				MyUtil.showSystemInputBord(et);
			}
			break;
		}
	}
    
    static public WordsBookDbHelper wordsBookDbAdapter;
	private void initWordsBookView(){
    	initWordsBookDbAdapter();
    	content_views_wordsbook=View.inflate(this, R.layout.content_wordsbook, null);
    }
    private void initWordsBookDbAdapter(){
		wordsBookDbAdapter = new WordsBookDbHelper(this);
		wordsBookDbAdapter.open();
		
    }
	public void click_in_wordsbook_view(View view){
		switch(view.getId()){
		case R.id.wordsbook_addcustomword:
			startActivity(new Intent(this, AddCustomWordActivity.class));
			break;
		case R.id.wordsbook_import:
			final File exportFile=new File(MyUtil.getAppPath(), "wordsbook.export");
			if(exportFile.exists()){
				new AlertDialog.Builder(this).setTitle("提示").setMessage("将从储存卡导入数据，合并到已有单词本")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Word> importWords=new Gson().fromJson(MyUtil.readStrFromFile(exportFile), new TypeToken<ArrayList<Word>>(){}.getType());
						for(Word word:importWords){
							wordsBookDbAdapter.updateWordsBook(word.getWord(), word.getContent());
							refreshWordsBookView();
							Toast.makeText(StudyActivity.this, "导入完成", Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("取消", null).create().show();
			}else{
				Toast.makeText(StudyActivity.this, "没有找到单词本导出文件", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.wordsbook_export:
			new AlertDialog.Builder(this).setTitle("提示").setMessage("将导入单词本到储存卡")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final File exportFile = new File(MyUtil.getAppPath(), "wordsbook.export");
					final Runnable exportRunnable=new Runnable() {
						public void run() {
							try {
								Cursor cursor = wordsBookDbAdapter.fetchWordsBook();
								ArrayList<Word> exportWords = new ArrayList<Word>();
								while (cursor.moveToNext()) {
									exportWords.add(new Word(cursor.getString(1), cursor.getString(2)));
								}
								MyUtil.writeStrToFile(new Gson().toJson(exportWords), exportFile);
								Toast.makeText(StudyActivity.this, "导出完成", Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(StudyActivity.this, "导出失败", Toast.LENGTH_SHORT).show();
							}
						}
					};
					if(exportFile.exists()){
						new AlertDialog.Builder(StudyActivity.this).setTitle("提示").setMessage("检测到储存卡已存在导出记录,是否覆盖")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									exportRunnable.run();
								}
							}).setNegativeButton("取消", null).create().show();
					}else exportRunnable.run();
				}
			}).setNegativeButton("取消", null)
			.create().show();
			break;
		}
	}
    public void click_in_bookreader_view(View view){
    	switch(view.getId()){
    	case R.id.bookreader_showcloudbooks:
    		startActivity(new Intent(this, BooksInCloudListActivity.class));
    		break;
    	}
    }
    
    private void refreshWordsBookView(){
    	((WordsBookListView)content_views_wordsbook.findViewById(R.id.wordsbook_listview)).refresh();
    }
    
    private void initBookReaderView(){
    	content_views_bookreader=View.inflate(this, R.layout.content_bookreader, null);
    	refreshBookReaderView();
    }
    private void refreshBookReaderView(){
    	BookReaderListView bookReaderListView=(BookReaderListView) content_views_bookreader.findViewById(R.id.bookreader_listview);
    	bookReaderListView.refreshLocalAdapter();
    	BookReaderAdapter bookReaderAdapter=(BookReaderAdapter) bookReaderListView.getAdapter();
    	if(bookReaderAdapter.getCount()==0){
    		content_views_bookreader.findViewById(R.id.bookreader_nobook_tv).setVisibility(View.VISIBLE);
    		bookReaderListView.setVisibility(View.INVISIBLE);
    	}else{
    		content_views_bookreader.findViewById(R.id.bookreader_nobook_tv).setVisibility(View.GONE);
    		bookReaderListView.setVisibility(View.VISIBLE);
    	}
    }
    
    private void initGameView(){
    	content_views_game=View.inflate(this, R.layout.content_game, null);
    }
    public void click_in_game_view(View view){
    	switch(view.getId()){
    	case R.id.content_game_start_weiinput:
    		InputGameActivity.isWeiInput=true;
    		startActivity(new Intent(this, InputGameActivity.class));
    		break;
    	case R.id.content_game_start_haninput:
    		InputGameActivity.isWeiInput=false;
    		startActivity(new Intent(this, InputGameActivity.class));
    		break;
    	}
    }
    
    
}
