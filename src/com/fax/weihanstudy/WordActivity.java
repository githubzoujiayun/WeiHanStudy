package com.fax.weihanstudy;

import com.baidu.mobstat.StatActivity;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;
import com.fax.weihanstudy.wordsbook.WordsBookDbHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//չʾһ�����ʵĽ���
public class WordActivity extends StatActivity {
	public static final String IntentExtraWordTitle="title";
	public static final String IntentExtraWordContent="content";
	private String wordTitle;
	private String wordContent;
	WordsBookDbHelper dbAdapter=StudyActivity.wordsBookDbAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word);
		Intent intent=getIntent();
		wordTitle=intent.getStringExtra(IntentExtraWordTitle);
		wordContent=intent.getStringExtra(IntentExtraWordContent);
		((TextView)findViewById(R.id.dic_word_title)).setText(MyUtil.convertStr(wordTitle));
		((TextView)findViewById(R.id.dic_word_content)).setText(MyUtil.convertStr(wordContent));
		
		checkIfWordInWordsBook();
	}
	
	
	public void onclick(View view){
		switch(view.getId()){
		case R.id.dic_word_addto_wordsbook:
			updateWordsBook();
			view.setVisibility(View.GONE);
			findViewById(R.id.dic_word_removefrom_wordsbook).setVisibility(View.VISIBLE);
			Toast.makeText(this, "����ӵ����ʱ�", Toast.LENGTH_SHORT).show();
			break;
		case R.id.dic_word_removefrom_wordsbook:
			removeWordsBook();
			view.setVisibility(View.GONE);
			findViewById(R.id.dic_word_addto_wordsbook).setVisibility(View.VISIBLE);
			Toast.makeText(this, "�Ѵӵ��ʱ�ɾ��", Toast.LENGTH_SHORT).show();
			break;
		}
		StudyActivity.handler.obtainMessage(StudyActivity.Handler_Refresh_WordsBookView).sendToTarget();
	}
	//����Ƿ�����ڵ��ʱ�����������ʾ
	private void checkIfWordInWordsBook(){
		boolean isInWordBook=dbAdapter.isWordExists(wordTitle);
		
		if(isInWordBook){
			findViewById(R.id.dic_word_addto_wordsbook).setVisibility(View.GONE);
			findViewById(R.id.dic_word_removefrom_wordsbook).setVisibility(View.VISIBLE);
		}
	}
	private void updateWordsBook(){
		dbAdapter.updateWordsBook(wordTitle, wordContent);
	}
	private void removeWordsBook(){
		dbAdapter.deleteFromWordsBook(wordTitle);
	}
}
