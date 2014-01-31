package com.fax.weihanstudy.dict;

import com.baidu.mobstat.StatService;
import com.fax.weihanstudy.MyEditText;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.StudyActivity;
import com.fax.weihanstudy.WeiInputFragment;
import com.fax.weihanstudy.utils.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

//透明的单词查找窗口
public class FloatDictActivity extends FragmentActivity {

	LinearLayout content_views_dic;
	private com.fax.weihanstudy.dict.DictListView dic_list;
	WeiInputFragment weiInputFragment;

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		content_views_dic=(LinearLayout) View.inflate(this, R.layout.content_dic, null);
		setContentView(content_views_dic);
		dic_list=(com.fax.weihanstudy.dict.DictListView)content_views_dic.findViewById(R.id.dic_listview);
		
		final MyEditText dic_input=(MyEditText)content_views_dic.findViewById(R.id.dic_input);
		dic_input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				dic_list.clearList();
				if(s.length()==0) return;
				dic_list.searchWord(dic_input.getString());
			}
		});
		dic_list.clearList();
		
		FrameLayout fl=new FrameLayout(this);
		fl.setId(android.R.id.content);
		content_views_dic.addView(fl);
		weiInputFragment=new WeiInputFragment();
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, weiInputFragment).commit();
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
					((ImageButton)view).setImageResource(R.drawable.icon_weihan);
					weiInputFragment.showInputBord(et);
				}
				else{
					((ImageButton)view).setImageResource(R.drawable.icon_hanwei);
					weiInputFragment.removeInputBord();
					MyUtil.showSystemInputBord(et);
				}
				
				dic_list.clearList();
				et.setText("");
			}else{
//				DictManager.getInstance(this).startDownloadWeiHanDict(this);
			}
			
			break;
		}
	}
	
}
