package com.fax.weihanstudy.useful_sentence;

import java.util.ArrayList;

import com.fax.weihanstudy.R;
import com.fax.weihanstudy.Word;
import com.fax.weihanstudy.R.id;
import com.fax.weihanstudy.R.layout;
import com.fax.weihanstudy.utils.MyUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UsefulSentenceListAdapter extends BaseAdapter {
	private ArrayList<Word> words=new ArrayList<Word>();
	private Context context;
	public UsefulSentenceListAdapter(Context context) {
		this.context=context;
	}
	public void setWords(ArrayList<Word> words){
		this.words=words;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return words.size();
	}

	@Override
	public Word getItem(int position) {
		return words.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Word word=getItem(position);
		if(word==null) return new View(context);
		if(convertView==null) convertView=View.inflate(context, R.layout.list_word_item, null);
		((TextView) convertView.findViewById(R.id.list_word_item_title)).setText(MyUtil.convertStr(word.getWord()));
		((TextView) convertView.findViewById(R.id.list_word_item_content)).setText(MyUtil.convertStr(word.getContent()));
		return convertView;
	}

}
