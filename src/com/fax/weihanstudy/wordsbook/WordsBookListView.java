package com.fax.weihanstudy.wordsbook;

import com.fax.weihanstudy.R;
import com.fax.weihanstudy.StudyActivity;
import com.fax.weihanstudy.Word;
import com.fax.weihanstudy.utils.MyUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class WordsBookListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	WordsBookDbHelper wordsBookDbAdapter=StudyActivity.wordsBookDbAdapter;
	public WordsBookListView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		String title=((TextView)view.findViewById(R.id.list_word_item_title)).getText().toString();
		String content=((TextView)view.findViewById(R.id.list_word_item_content)).getText().toString();
		Word word=new Word(title, content);
		MyUtil.openWordView(getContext(),word);
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		final String title=((TextView)view.findViewById(R.id.list_word_item_title)).getText().toString();
		final String content=((TextView)view.findViewById(R.id.list_word_item_content)).getText().toString();
		new AlertDialog.Builder(getContext()).setItems(new String[]{"查看单词","从单词本删除","删除全部"}, 
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:
							Word word=new Word(title, content);
							MyUtil.openWordView(getContext(),word);
							break;
						case 1:
							wordsBookDbAdapter.deleteFromWordsBook(title);
							refresh();
							break;
						case 2:
							new AlertDialog.Builder(getContext()).setTitle("提示")
								.setMessage("确定删除全部单词吗?")
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										wordsBookDbAdapter.clearWordsBook();
										refresh();
									}
								}).setNegativeButton("取消", null).create().show();
							break;
						}
					}
				}).create().show();
		return false;
	}
	public void refresh(){
		Cursor cursor=wordsBookDbAdapter.fetchWordsBook();
		int wordsCount=cursor.getCount();
		WordsBookCursorAdapter wordsBookListAdapter=new WordsBookCursorAdapter(getContext(), cursor, false);
		setAdapter(wordsBookListAdapter);
		
		((TextView)((ViewGroup)getParent()).findViewById(R.id.wordsbook_wordscount_tv)).setText(wordsCount+"个");
	}
	@Override
	public void onGlobalLayout() {
		super.onGlobalLayout();
		refresh();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}
	
}
