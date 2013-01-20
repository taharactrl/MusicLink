package com.taharactrl.android.musiclink;

import java.io.File;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class MusicDBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "musiclink.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE_NAME = "list";
	public static final String COL_ID = "id";
	public static final String COL_FILENAME = "filename";
	public static final String COL_TITLE = "title";
	public static final String COL_ARTIST = "artist";
	public static final String COL_ALBUM = "album";
	public static final String COL_RATE = "rate";
	public static final String COL_HASH = "hash";
	public static final String COL_TRACK = "track";
	public static final String COL_SELECTED = "selected";
	
	private String[] ext = {"mp3", "m4a", "3gp", "mp4", "flac", "ogg", "wav", "aac"};
	
	
	//private final String musicPath = "/mnt/sdcard/external_sd/music";
//	private final String musicPath = "/mnt/sdcard/Music";
	private final String[] musicPathList = {
											Environment.getExternalStorageDirectory()+"/music",
											Environment.getExternalStorageDirectory()+"/external_sd/music"
											};
	private String musicPath;
	
	FileSearch serach = new FileSearch();

	private ArrayList<ArrayList<String>> musicList; 
	MediaMetadataRetriever mmr;
	ArrayList<String> musicInfo;

	private SQLiteDatabase musicDB;
	
//	private ProgressDialog progressDialog;
//	private Handler handlerProgressDialog;
	
	public MusicDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO 自動生成されたコンストラクター・スタブ
		
//		musicDB = getWritableDatabase();

//		handlerProgressDialog = new Handler();
//		progressDialog = new ProgressDialog(context);
//		progressDialog.setTitle("Please wait");
//		progressDialog.setMessage("Searching local storage\nCreating music database...");

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
	
		
		createMusicTable(db);
//		musicDB = getWritableDatabase();
		createMusicDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ

	}

	
	ContentValues values;
	
	public void createMusicTable(){
//		SQLiteDatabase db = getWritableDatabase();
//		musicDB = getWritableDatabase();
		createMusicTable(musicDB);
		
	}
	
	public boolean createMusicTable(SQLiteDatabase db){
		boolean isTableExist = false;
		Cursor c = db.rawQuery("select name from sqlite_master where type='table'",null);
		boolean isEOF = c.moveToFirst();
		while(isEOF){
			if(c.getString(c.getInt(0)).equals(TABLE_NAME)){
				isTableExist = true;
			};
			
			isEOF = c.moveToNext();
		}		
		
		c.close();
		
		String createSQL = "create table '"+TABLE_NAME+
				   "'("+
				   COL_ID+" integer primary key autoincrement,"+
				   COL_FILENAME+" text not null,"+
				   COL_TITLE+" text not null,"+
				   COL_ARTIST+" text not null,"+
				   COL_ALBUM+" text not null," +
				   COL_HASH+" text not null,"+
				   COL_TRACK+" real not null,"+
				   COL_RATE+" real not null,"+
				   COL_SELECTED+" boolean"+
				   ");";

		if(!isTableExist){
			db.execSQL(createSQL);
			return true;
		}
//		db.close();
		return false;
	}
	
	public void createMusicDB(){
		SQLiteDatabase db = getWritableDatabase();
		createMusicDB(db);
	}
	
	
	public void createMusicDB(SQLiteDatabase db){
		
		for(int j = 0; j<musicPathList.length;j++){
			musicPath = musicPathList[j];
			File dir = new File(musicPath);
//			 dir.getPath()
			if(dir.exists()){
		
				for(int k = 0; k<ext.length;k++){
		
					File[] musicFiles = serach.listFiles(musicPath, "*."+ext[k]);
			
					musicList = new ArrayList<ArrayList<String>>();
					mmr = new MediaMetadataRetriever();		
					
					for(int i = 0; i<musicFiles.length;i++){
						musicInfo = new ArrayList<String>();
						musicInfo.add(musicFiles[i].getAbsoluteFile().toString());
						if(musicFiles[i].length() > 1000){
							mmr.setDataSource(musicFiles[i].getAbsoluteFile().toString());
							if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!=null){
								musicInfo.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
							}else{
								musicInfo.add(musicFiles[i].getName());
							}
					
							if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!=null){
								musicInfo.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
							}else{
								musicInfo.add("unknown");
							}
					
							if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)!=null){
								musicInfo.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
							}else{
								musicInfo.add("unknown");
							}
					
							if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)!=null){
//								if(Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER))<10){
//									musicInfo.add("00"+mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
//								}else if(Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER))<100){
//									musicInfo.add("0"+mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
//								}else{
//									musicInfo.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
//								}
								String tmp = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
//								Log.d("tmp", tmp);
								if(tmp.contains("/")){
								String n = tmp.substring(0, tmp.indexOf("/"));
//								Log.d("n", n);						 		
								String d = tmp.substring(tmp.indexOf("/")+1);
//								Log.d("d", d);
								tmp = String.valueOf(Float.parseFloat(n) / Float.parseFloat(d));
								}
								musicInfo.add(tmp);
							}else{
								musicInfo.add("unknown");
							}

							musicList.add(musicInfo);
						}
	
					}
				}
			}

		}
		
		for(int i = 0; i<musicList.size();i++){
	
			values = new ContentValues();
			values.put(COL_FILENAME, musicList.get(i).get(0));
			values.put(COL_TITLE, musicList.get(i).get(1));
			values.put(COL_ARTIST, musicList.get(i).get(2));
			values.put(COL_ALBUM, musicList.get(i).get(3));
			values.put(COL_HASH, String.valueOf(musicList.get(i).get(0).hashCode()));
			values.put(COL_TRACK, musicList.get(i).get(4));
			values.put(COL_RATE, "0");
			values.put(COL_SELECTED, "false");

			if(!isDataExist(musicList.get(i).get(0), db)){
				db.insert(TABLE_NAME, null, values);
			}
				
		}

	}

	public static final int PATH=0;
	public static final int TITLE=1;
	public static final int ARTIST=2;
	public static final int ALBUM=3;
	public static final int ID=4;
	public ArrayList<ArrayList<String>> getMusicList(SQLiteDatabase db){
		ArrayList<ArrayList<String>> ml = new ArrayList<ArrayList<String>>(); 
		Cursor c = db.rawQuery("select "+COL_FILENAME+","+COL_TITLE+","+COL_ARTIST+","+COL_ALBUM+","+COL_ID+" " +
				               "from "+TABLE_NAME+" order by album,track;", null);
		String filePath;
		String title;
		String artist;
		String album;
		String id;
		
		boolean isEof = c.moveToFirst();
		while(isEof){
			
			filePath = c.getString(0);
			title = c.getString(1);
			artist = c.getString(2);
			album = c.getString(3);
			id = c.getString(4);
		
			ArrayList<String> subml = new ArrayList<String>();
			subml.add(filePath);
			subml.add(title);
			subml.add(artist);
			subml.add(album);
			subml.add(id);
			
			ml.add(subml);
			

			isEof = c.moveToNext();

		}
				
//		musicDB.close();
		Log.d("db","close");
		
		c.close();
		return ml;
	}
	
	public void deleteDB(SQLiteDatabase db){
//		getWritableDatabase().execSQL("drop table "+TABLE_NAME+";");
//		musicDB.execSQL("drop table "+TABLE_NAME+";");
		db.execSQL("drop table "+TABLE_NAME+";");
	}
	
	public boolean isDataExist(String target, SQLiteDatabase db){
		String sql = " SELECT "+COL_HASH+" FROM "+TABLE_NAME+" WHERE hash = '"+String.valueOf(target.hashCode())+"';";
//		Cursor c = getReadableDatabase().rawQuery(sql, null);
		Cursor c = db.rawQuery(sql, null);
		int count = c.getCount();
		c.close();
		if(count == 0){
			return false;
			
		} else {
			return true;
		}
	}
	
	public SQLiteDatabase getMusicDB(){
		return musicDB;
	}
	
}
