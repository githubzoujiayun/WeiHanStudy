package com.fax.weihanstudy.dict;

import com.fax.weihanstudy.R;
import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**弃用，现在用dictListAdapter显示页面*/
@Deprecated
public class DictCursorAdapter extends CursorAdapter {
	public DictCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view.findViewById(R.id.list_word_item_title)).setText(MyUtil.convertStr(cursor.getString(1)));
		((TextView) view.findViewById(R.id.list_word_item_content)).setText(MyUtil.convertStr(cursor.getString(2)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view=View.inflate(context, R.layout.list_word_item, null);
		((TextView) view.findViewById(R.id.list_word_item_title)).setText(MyUtil.convertStr(cursor.getString(1)));
		((TextView) view.findViewById(R.id.list_word_item_content)).setText(MyUtil.convertStr(cursor.getString(2)));
		return view;
	}

	
}
