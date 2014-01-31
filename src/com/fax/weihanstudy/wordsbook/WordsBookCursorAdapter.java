package com.fax.weihanstudy.wordsbook;

import com.fax.weihanstudy.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WordsBookCursorAdapter extends CursorAdapter {

	public WordsBookCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view.findViewById(R.id.list_word_item_title)).setText(cursor.getString(1));
		((TextView) view.findViewById(R.id.list_word_item_content)).setText(cursor.getString(2));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view=View.inflate(context, R.layout.list_word_item, null);
		((TextView) view.findViewById(R.id.list_word_item_title)).setText(cursor.getString(1));
		((TextView) view.findViewById(R.id.list_word_item_content)).setText(cursor.getString(2));
		return view;
	}

}
