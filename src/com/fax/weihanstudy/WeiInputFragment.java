package com.fax.weihanstudy;

import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class WeiInputFragment extends Fragment implements View.OnClickListener{
	EditText inputingEditText;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.wei_input_view, container,false);
		setAllChildOnClickListener((ViewGroup) view);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		hideInputBord();
	}

	private void setAllChildOnClickListener(ViewGroup viewGroup){
		int count=viewGroup.getChildCount();
		for(int i=0;i<count;i++){
			View child=viewGroup.getChildAt(i);
			if(child instanceof ViewGroup){
				setAllChildOnClickListener((ViewGroup) child);
			}else{
				child.setOnClickListener(this);
			}
		}
	}
	private static String allEngWord="abcdefghijklmnopqrstuvwxyz,";
	@Override
	public void onClick(View view) {
		if(inputingEditText==null) return;
		String inputingText=inputingEditText.getText().toString();
		switch(view.getId()){
		case R.id.input_wei_bord:
			inputingEditText.setText(inputingText+((Button)view).getText());
			break;
		case R.id.input_wei_bord_back:
			if(inputingText.length()==0) return;
			inputingEditText.setText(inputingText.substring(0, inputingText.length()-1));
			break;
		case R.id.input_wei_bord_hide:
			hideInputBord();
			break;
		case R.id.input_wei_bord_fn://shift
			boolean isChecked=((CheckBox)view).isChecked();
			String addS="";
			if(isChecked) addS="2";
			for (int i = 0; i < allEngWord.length(); i++) {
				char c = allEngWord.charAt(i);
				String key = c + addS;
				String value = MyUtil.allWeiWordMap.get(key);
				if (value == null) {
					getView().findViewWithTag(c + "").setEnabled(false);
				} else {
					((MyButton) getView().findViewWithTag(c + "")).setText(value);
					getView().findViewWithTag(c + "").setEnabled(true);
				}
			}
			break;
		}
		inputingEditText.setSelection(0, inputingEditText.getText().length());
	}
	
//	ViewGroup inputBordContain;
//	View inputBord;
	public void showInputBord(EditText et){
		Log.d("fax", "showInputBord");
		if(isShowingInputBord()){
			hideInputBord();
		}
		et.setEnabled(false);
		et.clearFocus();

		InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
		inputingEditText=et;
		

		ImageButton switchBtn=(ImageButton) ((ViewGroup)inputingEditText.getParent()).findViewById(R.id.dic_change_input);
		if(switchBtn!=null) switchBtn.setImageResource(R.drawable.icon_weihan);
		
		if(getView()!=null) getView().setVisibility(View.VISIBLE);
		else Log.e("fax", "WeiInputFragment.getView()==null");
//		ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(-1, -2);
//		inputBordContain.addView(inputBord, params);
	}
	public boolean isShowingInputBord(){
//		if(inputBord==null) return false;
//		if(inputBord.getParent()!=null) return true;
		if(getView()!=null) return getView().getVisibility()==View.VISIBLE;
		return false;
	}
	public void hideInputBord(){
		if(isShowingInputBord()){
			Log.d("fax", "hideInputBord");
			if (inputingEditText!=null) {
				inputingEditText.setEnabled(true);
				inputingEditText.setOnTouchListener(new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction()==MotionEvent.ACTION_UP){
							showInputBord(inputingEditText);
						}
						return true;
					}
				});
//				inputingEditText.setOnClickListener(null);
//				inputingEditText.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						showInputBord(inputingEditText);
//					}
//				});
			}
			if(getView()!=null&&canHide) getView().setVisibility(View.GONE);
		}
	}
	public void removeInputBord(){
		Log.d("fax", "removeInputBord");
		if (inputingEditText!=null) {
			inputingEditText.setEnabled(true);
			inputingEditText.setOnTouchListener(null);
//			inputingEditText.setOnClickListener(null);
			ImageButton switchBtn=(ImageButton) ((ViewGroup)inputingEditText.getParent()).findViewById(R.id.dic_change_input);
			if(switchBtn!=null) switchBtn.setImageResource(R.drawable.icon_hanwei);
		}
		if(getView()!=null&&canHide) getView().setVisibility(View.GONE);
	}
	private boolean canHide=true;
	public void setCanHide(boolean canHide) {
		this.canHide = canHide;
	}
}
