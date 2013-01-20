package com.taharactrl.android.musiclink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaylistDBHelper extends SQLiteOpenHelper {

	private final static int DB_VERSION = 1;
	private final static String DB_NAME = "playlist.db";
	
//	private String playlistName;
	
	private boolean playlistExist = false;
	
	public PlaylistDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO 自動生成されたコンストラクター・スタブ
		
//		this.playlistName = playListName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ

	}
	
	public boolean createTable(String playlistName){
		setPlaylistExist(false);
		
		String createSQL = "create table '"+playlistName+
				   "'("+
				   "_id integer primary key autoincrement,"+
				   "hash text not null"+
				   ");";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'",null);
		boolean isEof =	cursor.moveToFirst();
		while(isEof){
			if(cursor.getString(cursor.getInt(0)).equals(playlistName)){
				setPlaylistExist(true);
			};
			isEof = cursor.moveToNext();
		}
		
		if(!isPlaylistExist()){
			db.execSQL(createSQL);
			return true;
		}
		
		return false;
	}
	
	public void deleteTable(String playlistName){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(playlistName, null, null);
	}

	public boolean isPlaylistExist() {
		return playlistExist;
	}

	public void setPlaylistExist(boolean playlistExist) {
		this.playlistExist = playlistExist;

	
	}

	public boolean addMusic(String table,MusicObject mo){
		
		ContentValues value = new ContentValues();
		value.put("hash", mo.getMusicHash());
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(table, null, value);
		
		
		return false;
	}
	
}
