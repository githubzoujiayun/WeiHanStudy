/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.fax.weihanstudy.wordsbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Implementation of the database adapter.
 */
public class WordsBookDbHelper {
	
	private static final String TAG = "fax";

	private static final String DATABASE_NAME = "WeiHanStudy";
	private static final int DATABASE_VERSION = 5;
	
    public static final String WORDS_BOOK_ROWID = "_id";
	public static final String WORDS_BOOK_TITLE = "word_title";
	public static final String WORDS_BOOK_CONTENT = "word_content";
	
	private static final String WORDS_BOOK_DATABASE_TABLE = "WORDS_BOOK";
	
	private static final String HISTORY_DATABASE_CREATE = "CREATE TABLE " + WORDS_BOOK_DATABASE_TABLE + " (" +
		WORDS_BOOK_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		WORDS_BOOK_TITLE + " TEXT, " +
		WORDS_BOOK_CONTENT + " TEXT);";
	
	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mContext;
    
    /**
     * Constructor.
     * @param ctx The current context.
     */
    public WordsBookDbHelper(Context ctx) {
        this.mContext = ctx;
    }
    
    /**
     * Open the database helper.
     * @return The current database adapter.
     */
    public WordsBookDbHelper open() {
        mDbHelper = new DatabaseHelper(mContext, this);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    /**
     * Close the database helper.
     */
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Get a cursor to suggestions from words book..
     * @param pattern The pattern to match.
     * @return A Cursor to suggestions.
     */    
    public Cursor getSuggestionsFromWordsBook(String pattern) {
    	Cursor cursor;
    	
    	if ((pattern != null) &&
    			(pattern.length() > 0)) {	
    		pattern = "%" + pattern + "%";    	
    		cursor = mDb.query(WORDS_BOOK_DATABASE_TABLE, new String[] {WORDS_BOOK_ROWID, WORDS_BOOK_CONTENT}, WORDS_BOOK_CONTENT + " LIKE '" + pattern + "'", null, null, null, null);
    	} else {
    		cursor = mDb.query(WORDS_BOOK_DATABASE_TABLE, new String[] {WORDS_BOOK_ROWID, WORDS_BOOK_CONTENT}, null, null, null, null, null);
    	}
    	
    	return cursor;
    }
    
    /**
     * Get the history.
     * @return A list of lists of HistoryItem. Each top-level list represents a day of history.
     */
    public Cursor fetchWordsBook() {
    	
		return mDb.query(WORDS_BOOK_DATABASE_TABLE,
				new String[] {WORDS_BOOK_ROWID, WORDS_BOOK_TITLE, WORDS_BOOK_CONTENT},
				null,
				null,
				null,
				null,
				null);
    }
    
    /**
     * Get the id for an history record identified by its url.
     * @param title The url to look for.
     * @return The history record index, or -1 if not found.
     */
    private long getWordsBookItemIdByTitle(String title) {
    	long result = -1;
    	
		try {
			Cursor cursor = mDb.query(WORDS_BOOK_DATABASE_TABLE, new String[] { WORDS_BOOK_ROWID }, WORDS_BOOK_TITLE + " = \"" + title + "\"", null, null,
					null, null);
	    	
	    	if (cursor.moveToFirst()) {
	    		
	    		result = cursor.getLong(cursor.getColumnIndex(WORDS_BOOK_ROWID));    		    		    		
	    	}
	    	
	    	cursor.close();
		} catch (Exception e) {
		}
    	
    	return result;
    }
    
    public void updateWordsBook(String title, String url) {
    	
    	long existingId = getWordsBookItemIdByTitle(title);
    	
    	if (existingId != -1) {
    		ContentValues args = new ContentValues();
            args.put(WORDS_BOOK_CONTENT, url);            

            mDb.update(WORDS_BOOK_DATABASE_TABLE, args, WORDS_BOOK_ROWID + "=" + existingId, null);
    	} else {
    		ContentValues initialValues = new ContentValues();
        	initialValues.put(WORDS_BOOK_TITLE, title);
            initialValues.put(WORDS_BOOK_CONTENT, url);
            
            mDb.insert(WORDS_BOOK_DATABASE_TABLE, null, initialValues);
    	}
    	
    }
    public boolean isWordExists(String title){
    	return getWordsBookItemIdByTitle(title)!=-1;
    }
    
    public void deleteFromWordsBook(String title) {
    	mDb.execSQL("DELETE FROM " + WORDS_BOOK_DATABASE_TABLE + " WHERE " + WORDS_BOOK_ROWID + " = " + getWordsBookItemIdByTitle(title) + ";");
    }
    public void deleteFromWordsBook(long id) {
    	mDb.execSQL("DELETE FROM " + WORDS_BOOK_DATABASE_TABLE + " WHERE " + WORDS_BOOK_ROWID + " = " + id + ";");
    }
    
    /**
     * Delete all records from history.
     */
    public void clearWordsBook() {
    	mDb.execSQL("DELETE FROM " + WORDS_BOOK_DATABASE_TABLE + ";");
    }
    
    /**
     * DatabaseHelper.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

//    	private DbAdapter mParent;
    	
    	/**
    	 * Constructor.
    	 * @param context The current context.
    	 * @param parent The DbAdapter parent.
    	 */
		public DatabaseHelper(Context context, WordsBookDbHelper parent) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
//			mParent = parent;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(HISTORY_DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {			
			Log.d(TAG, "Upgrading database.");
			
		}
    	
    }
	
}
