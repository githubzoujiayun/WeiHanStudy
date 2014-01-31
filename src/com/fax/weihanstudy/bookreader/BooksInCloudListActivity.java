package com.fax.weihanstudy.bookreader;

import com.baidu.mobstat.StatActivity;
import com.fax.weihanstudy.R;

import android.app.Activity;
import android.os.Bundle;

public class BooksInCloudListActivity extends StatActivity {
	BookReaderListView bookReaderListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookreader_cloud_list);
		bookReaderListView=(BookReaderListView) findViewById(android.R.id.list);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BookDownloader.removeDownloadListener(bookReaderListView);
	}
	
}
