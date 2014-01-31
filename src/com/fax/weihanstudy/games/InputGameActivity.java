package com.fax.weihanstudy.games;

import java.util.Random;

import com.baidu.mobstat.StatService;
import com.fax.weihanstudy.MyTextView;
import com.fax.weihanstudy.R;
import com.fax.weihanstudy.WeiInputFragment;
import com.fax.weihanstudy.utils.MyUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InputGameActivity extends FragmentActivity{
	AnimView animView;
	ProgressBar timeline;
	EditText et;
	MyTextView remindTv;
	Handler handler;
	static final int Msg_ClearTv=0;
	static final int Msg_TimeLose=1;
	static final int Msg_TimeAdd=2;
	static final int TimeLoseDiv=600;//时间条每600毫秒掉1
	public static boolean isWeiInput=true;
	private int[] scoreLevelsWei=new int[]{3,10,20,50,100};
	private int[] scoreLevelsHan=new int[]{3,10,20,50,100};
	private String[] gameOverMsg=new String[]{
			"太差了，继续努力",
			"一般呢，再接再厉吧",
			"可以呀，再加把劲！",
			"厉害，佩服~",
			"这分数，你真神了~膜拜",
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_inputwords);
		animView=(AnimView) findViewById(R.id.game_inputwords_aimtv);
		remindTv=(MyTextView) findViewById(R.id.game_inputwords_remind_tv);

		
		et=(EditText)findViewById(R.id.game_inputwords_et);
		et.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				if(s.toString().equals(animView.getText().toString())){
					dismissAView();
				}
			}
		});
		
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case Msg_ClearTv:
					et.setText("");
					break;
				case Msg_TimeLose://timeline减一个百分点(每1秒)
					timeline.setProgress(timeline.getProgress()-1);
					
					if(timeline.getProgress()<=0){//游戏结束
						isPause=true;
						isStop=true;
						animView.cancelTranslateAnim();
						
						
						String gameOverMsgExtra="";
						int[] scoreLevels;
						if(isWeiInput) scoreLevels=scoreLevelsWei;
						else scoreLevels=scoreLevelsHan;
						for(int i=0;i<scoreLevels.length;i++){
							if(score<scoreLevels[i]){
								gameOverMsgExtra=gameOverMsg[i];
								break;
							}
						}
							
							
						new AlertDialog.Builder(InputGameActivity.this).setTitle(R.string.gameover)
							.setMessage(getString(R.string.game_score)+": "+score+"\n"+gameOverMsgExtra)
							.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							}).create().show();
					}
					break;
				case Msg_TimeAdd://timeline加十个百分点
					timeline.setProgress(timeline.getProgress()+10);
					break;
				}
			}
		};
		
		timeline=(ProgressBar) findViewById(R.id.game_inputwords_time_line);
		initGameInfo();
		

		if(!isWeiInput){
			et.setEnabled(true);
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			handler.postDelayed(new Runnable() {
				public void run() {
					MyUtil.showSystemInputBord(et);
				}
			},200);
		}else{
			WeiInputFragment weiInputFragment=new WeiInputFragment();
			getSupportFragmentManager().beginTransaction()
				.add(R.id.activity_inputbord_contain, weiInputFragment).commit();
			weiInputFragment.showInputBord(et);
			weiInputFragment.setCanHide(false);
			
		}
	}
	
	@Override
	protected void onDestroy() {
		isStop=true;
		super.onDestroy();
	}
	private boolean isPause=false;
	@Override
	protected void onPause() {
		super.onPause();
		isPause=true;
		StatService.onPause(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		isPause=false;
		StatService.onResume(this);
	}

	private boolean isStop=false;
//	@Override
//	protected void onStop() {
//		super.onStop();
//		isStop=true;
//	}

	private void initGameInfo(){
		score=0;
		refreshScore();
		
		new Thread(){
			public void run(){
				while(!isStop){
					try {
						Thread.sleep(TimeLoseDiv);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					animView.setPause(isPause);
					if(!isPause){
						handler.obtainMessage(Msg_TimeLose).sendToTarget();
					}
				}
				animView.cancelTranslateAnim();
			}
		}.start();
	}
	private int score=0;
	private void dismissAView(){
		animView.dismiss(score);
		score++;
		handler.obtainMessage(Msg_TimeAdd).sendToTarget();
		refreshScore();
	}
	private void refreshScore(){
		((TextView)findViewById(R.id.game_inputwords_score_tv)).setText(getString(R.string.game_score)+": "+score);
		
		SharedPreferences sp=getPreferences(MODE_PRIVATE);
		String key="highscore"+(isWeiInput?"_wei":"_han");
		int highscore=sp.getInt(key, 0);
		if(score>highscore){
			highscore=score;
			sp.edit().putInt(key, highscore).commit();
		}
		((TextView)findViewById(R.id.game_inputwords_highscore_tv)).setText(getString(R.string.game_high_score)+": "+highscore);
	}
}
