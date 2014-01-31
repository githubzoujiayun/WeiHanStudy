package com.fax.weihanstudy;

import com.fax.weihanstudy.utils.MyUtil;
import com.fax.weihanstudy.utils.UyghurConvert;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button  implements TextWatcher{
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode()) return;
		if(android.os.Build.VERSION.SDK_INT<11){
			setTypeface(UyghurConvert.GetUyghurFont(context));
			addTextChangedListener(this);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if(UyghurConvert.isNeedConvert(s.toString())){
			s.replace(0, s.length(), UyghurConvert.convert(s.toString()));
		}
	}

	
}
