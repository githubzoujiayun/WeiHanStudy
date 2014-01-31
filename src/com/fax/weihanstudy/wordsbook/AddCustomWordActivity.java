package com.fax.weihanstudy.wordsbook;

import com.baidu.mobstat.StatService;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.StudyActivity;
import com.fax.weihanstudy.WeiInputFragment;
import com.fax.weihanstudy.utils.MyUtil;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddCustomWordActivity extends FragmentActivity {
	EditText titleEt;
	EditText contentEt;
	ImageButton switchBtn;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordsbook_addcustomword);
		titleEt=(EditText) findViewById(R.id.wordsbook_addcustom_title_et);
		contentEt=(EditText) findViewById(R.id.wordsbook_addcustom_content_et);
		switchBtn=(ImageButton) findViewById(R.id.wordsbook_addcustom_change_input);
        weiInputFragment=new WeiInputFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_inputbord_contain, weiInputFragment).commit();
	}
	private int switchBtnRes;
	private void setSwitchBtnRes(int res){
		this.switchBtnRes=res;
		switchBtn.setImageResource(res);
	}
	private boolean isWeiInput(){
		return switchBtnRes==R.drawable.icon_weihan;
	}
	public void onclick(View view){
		switch(view.getId()){
		case R.id.wordsbook_addcustom_change_input:
			if(isWeiInput()){
				EditText nowInputEt=getNowInputEt();
				weiInputFragment.removeInputBord();
				titleEt.setFocusable(true);
				titleEt.setFocusableInTouchMode(true);
				contentEt.setFocusable(true);
				contentEt.setFocusableInTouchMode(true);
				titleEt.setEnabled(true);
				titleEt.setOnTouchListener(null);
				contentEt.setEnabled(true);
				contentEt.setOnTouchListener(null);
				titleEt.requestFocus();
				titleEt.setBackgroundResource(android.R.drawable.edit_text);
				contentEt.setBackgroundResource(android.R.drawable.edit_text);
				MyUtil.showSystemInputBord(nowInputEt);
				setSwitchBtnRes(R.drawable.icon_hanwei);
			}else{
				weiInputFragment.showInputBord(getNowInputEt());
				titleEt.setFocusable(false);
				contentEt.setFocusable(false);
				setTitleEtOnTouchListener();
				setContentEtOnTouchListener();
				titleEt.setBackgroundResource(R.drawable.my_edit_text);
				contentEt.setBackgroundResource(R.drawable.my_edit_text);
				setSwitchBtnRes(R.drawable.icon_weihan);
			}
			break;
		case R.id.wordsbook_addcustom_ok:
			String wordTitle=titleEt.getText().toString();
			String wordContext=contentEt.getText().toString();
			if(wordTitle.length()==0||wordContext.length()==0){
				Toast.makeText(this, "单词和词义不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			StudyActivity.wordsBookDbAdapter.updateWordsBook(wordTitle, wordContext);
			Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
			StudyActivity.handler.obtainMessage(StudyActivity.Handler_Refresh_WordsBookView).sendToTarget();
			this.finish();
			break;
		}
	}
	public EditText getNowInputEt(){
		if(!titleEt.isEnabled()) return titleEt;
		if(!contentEt.isEnabled()) return contentEt;
		if(titleEt.isFocused()) return titleEt;
		if(contentEt.isFocused()) return contentEt;
		else return titleEt;
	}
	public void setContentEtOnTouchListener(){
		contentEt.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					weiInputFragment.showInputBord(contentEt);
					titleEt.setEnabled(true);
					setTitleEtOnTouchListener();
				}
				return true;
			}
		});
	}
	public void setTitleEtOnTouchListener(){
		titleEt.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					weiInputFragment.showInputBord(titleEt);
					contentEt.setEnabled(true);
					setContentEtOnTouchListener();
				}
				return true;
			}
		});
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if(weiInputFragment!=null&&weiInputFragment.isShowingInputBord()){
    			weiInputFragment.hideInputBord();
				titleEt.setEnabled(true);
				contentEt.setEnabled(true);
    			return true;
    		}
		}
		return super.onKeyDown(keyCode, event);
	}
}
