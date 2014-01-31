package com.fax.weihanstudy.games;

import java.io.IOException;
import java.util.Random;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import com.fax.weihanstudy.MyTextView;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;
import com.fax.weihanstudy.utils.UyghurConvert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AnimView extends LinearLayout implements AnimationListener, OnGlobalLayoutListener {
	private MyTextView showingTv;
	InputGameActivity inputGameActivity;
	boolean isWeiInput;

	public AnimView(Context context, AttributeSet attrs) {
		this((InputGameActivity)context, attrs);
	}
	public AnimView(InputGameActivity context, AttributeSet attrs) {
		super(context, attrs);
		inputGameActivity=context;
		isWeiInput=context.isWeiInput;
		
		getViewTreeObserver().addOnGlobalLayoutListener(this);//监听来开始动画
	}
	
	private int level=0;
	void setANewWordToTv(){
		String text = null;
		if (isWeiInput) {
			StringBuilder sb = new StringBuilder();
			String all = "abcdefghijklmnopqrstuvwxyz,";
			int length = new Random().nextInt(3) + 2 + level;
			for (int i = 0; i < length; i++) {
				int index = new Random().nextInt(all.length());
				sb.append(MyUtil.allWeiWordMap.get(all.substring(index, index + 1)));
			}
			text = sb.toString();
		}else{
			String hanzi=getHanzi();
			int length=hanzi.length();
			text="";
			for(int i=0;i<=level;i++){
				int index=new Random().nextInt(length);
				text+=hanzi.charAt(index)+"";
			}
		}
		
		//防止重复
		if (text!=null&&!text.equals(getText().toString())) {
			setText(text);
			inputGameActivity.remindTv.setText("");
		} else {
			setANewWordToTv();
		}
	}
	String hanzi;
	private String getHanzi(){
		if(hanzi==null){
			try {
				hanzi=MyUtil.readStrFromInputStream(inputGameActivity.getAssets().open("game_han.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hanzi;
	}
	
	private void setText(String string) {
		showingTv.setText(string);
	}
	protected CharSequence getText() {
		return showingTv.getText();
	}


	private void startMyAnim(){
		setMargin(0);
		setANewWordToTv();
		
		if(translateAsycn!=null) translateAsycn.cancel(true);
		translateAsycn=new TranslateAsycn();
		translateAsycn.execute();
		
		inputGameActivity.handler.obtainMessage(InputGameActivity.Msg_ClearTv).sendToTarget();
	}
	
	private TranslateAsycn translateAsycn;
	private boolean isPause=false;
	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}
	class TranslateAsycn extends AsyncTask<Void, Integer, Void> {
		int progress = 0;

		@SuppressLint("NewApi")
		public void execute(){
			if(android.os.Build.VERSION.SDK_INT<11) super.execute();
			else executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		@Override
		protected Void doInBackground(Void... params) {
			while (progress<500) {
				try {
					Thread.sleep(40-level*3);
				} catch (InterruptedException e) {
					return null;
				}
				if (!isPause) {
					progress++;
					publishProgress(progress);
				}
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startMyAnim();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			setMargin(progress);
			
			if(progress==score*10+50){//显示提示
				new Thread(){
					public void run(){
						String notice;
						if(isWeiInput){
							StringBuilder sb=new StringBuilder();
							String text=getText().toString();
							if(android.os.Build.VERSION.SDK_INT<11){
								text=UyghurConvert.reverseConvert(text);
							}
							sb.append("     ");
							for(int i=0;i<text.length();i++){
								sb.append(text.charAt(i));
								if(i!=text.length()-1) sb.append(" + ");
							}
							notice=sb.toString();
						}else{
							StringBuilder sb=new StringBuilder();
							for(char c:getText().toString().toCharArray()){
								sb.append(" ").append(MyUtil.getPinyinFromChar(c));
							}
							notice=sb.toString();
						}
						
						final String finalnotice=notice;
						inputGameActivity.handler.post(new Runnable() {
							public void run() {
								inputGameActivity.remindTv.setText("提示:"+finalnotice);
							}
						});
					}
				}.start();
			}
		}
	}
	private void setMargin(int progress){
		int marginLeft = ((ViewGroup) getParent()).getWidth() * progress / 500;
		((FrameLayout.LayoutParams) getLayoutParams()).setMargins(marginLeft, 0, -marginLeft, 0);
		requestLayout();
	}
	private AlphaAnimation getAlphaAnimation(){
		AlphaAnimation anim=new AlphaAnimation(1, 0);
		anim.setDuration(1000);
		anim.setAnimationListener(this);
		return anim;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		startMyAnim();
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	@Override
	public void onAnimationStart(Animation animation) {
		cancelTranslateAnim();
	}
	public void cancelTranslateAnim(){
		if(translateAsycn!=null) translateAsycn.cancel(true);
	}
	@Override
	public void onGlobalLayout() {
		showingTv=((MyTextView)findViewById(android.R.id.text1));
		startMyAnim();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}
	private int score=0;
	public void dismiss(int score){
		this.score=score;
		level=score/10;//长度随分数越来越长
		
		startAnimation(getAlphaAnimation());
	}


}
