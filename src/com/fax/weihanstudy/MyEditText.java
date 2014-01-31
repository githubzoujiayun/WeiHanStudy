package com.fax.weihanstudy;

import com.fax.weihanstudy.utils.MyUtil;
import com.fax.weihanstudy.utils.UyghurConvert;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class MyEditText extends EditText implements TextWatcher {
	public MyEditText(Context context, AttributeSet attrs) {
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
		try {
			if (UyghurConvert.isNeedConvert(s.toString())) {
				s.replace(0, s.length(), UyghurConvert.convert(UyghurConvert.reverseConvert(s.toString())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * ÇëÊ¹ÓÃ {@code getString()}.
     * @deprecated use {@link #getString()} instead.
     */
    @Deprecated
	public Editable getText() {
		return super.getText();
	}

	public String getString() {
		Editable s= getText();
		try {
			if(android.os.Build.VERSION.SDK_INT<11){
				return UyghurConvert.reverseConvert(s.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s.toString();
	}

}
