package com.taharactrl.android.musiclink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicObject extends LinearLayout {

	private TextView musicTitleView;
	private CheckBox musicCheckBox;
	public TextView getMusicTitleView() {
		return musicTitleView;
	}

	public CheckBox getMusicCheckBox() {
		return musicCheckBox;
	}

	public String getMusicId() {
		return musicId;
	}
	
	public String getTitle(){
		return title;
	}

	public String getMusicFilename() {
		return filename;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getRate() {
		return rate;
	}

	public String getTrack() {
		return track;
	}

	public String getSelected() {
		return selected;
	}
	
	public String getMusicHash(){
		return hash;
	}

	private String musicId;
	private String filename;
	private String title;
	private String artist;
	private String album;
	private String rate;
	private String track;
	private String hash;
	private String selected = "0";
	
	private SQLiteDatabase musicDB;
	
	public MusicObject(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 50));
		
		musicCheckBox = new CheckBox(context);
		musicTitleView = new TextView(context);
		
		musicCheckBox.setWidth(20);
		musicCheckBox.setHeight(20);
		musicCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO 自動生成されたメソッド・スタブ
				if(isChecked){
					setSelected(true);
				}else{
					setSelected(false);
				}
			}
		});
		
		musicTitleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		musicTitleView.setTextColor(Color.BLACK);
		musicTitleView.setGravity(Gravity.CENTER_VERTICAL| Gravity.LEFT);
		//musicTitleView.setTextSize(size)
		
		addView(musicCheckBox);
		addView(musicTitleView);
		
	}

	public void setMusicId(String musicId, SQLiteDatabase musicDB){
		this.musicId = musicId;
		this.musicDB= musicDB; 
		
		Cursor c = musicDB.rawQuery("select "
									+MusicDBHelper.COL_FILENAME+","
									+MusicDBHelper.COL_TITLE+","
									+MusicDBHelper.COL_ALBUM+","
									+MusicDBHelper.COL_ARTIST+","
									+MusicDBHelper.COL_RATE+","
									+MusicDBHelper.COL_SELECTED+","
									+MusicDBHelper.COL_TRACK+","
									+MusicDBHelper.COL_HASH+" "+
									"from "+MusicDBHelper.TABLE_NAME+" where "+MusicDBHelper.COL_ID+"="+musicId+";", null);
		if(c.moveToFirst()){
			filename = c.getString(0);
			title = c.getString(1);
			album = c.getString(2);
			artist = c.getString(3);
			rate = c.getString(4);
			selected = c.getString(5);
			track = c.getString(6);
			hash = c.getString(7);
		}
		
		c.close();
		musicTitleView.setText(title);
		if(isSelected()){
			musicCheckBox.setChecked(true);
		}else{
			musicCheckBox.setChecked(false);
		}
		
	}

	public void setMusicHash(String hash, SQLiteDatabase musicDB){
		this.hash = hash;
		this.musicDB= musicDB; 
		
		Cursor c = musicDB.rawQuery("select "
									+MusicDBHelper.COL_FILENAME+","
									+MusicDBHelper.COL_TITLE+","
									+MusicDBHelper.COL_ALBUM+","
									+MusicDBHelper.COL_ARTIST+","
									+MusicDBHelper.COL_RATE+","
									+MusicDBHelper.COL_SELECTED+","
									+MusicDBHelper.COL_TRACK+","
									+MusicDBHelper.COL_ID+" "+
									"from "+MusicDBHelper.TABLE_NAME+" where "+MusicDBHelper.COL_HASH+"="+hash+";", null);
		if(c.moveToFirst()){
			filename = c.getString(0);
			title = c.getString(1);
			album = c.getString(2);
			artist = c.getString(3);
			rate = c.getString(4);
			selected = c.getString(5);
			track = c.getString(6);
			musicId = c.getString(7);
		}
		
		c.close();
		musicTitleView.setText(title);
		if(isSelected()){
			musicCheckBox.setChecked(true);
		}else{
			musicCheckBox.setChecked(false);
		}
		
	}
	public boolean isSelected(){
		if(selected.equals("1")){
			return true;
		}
		
		return false;
	}
	
	public void setSelected(boolean select){
//		Log.d("id",musicId);
		if(select){
			this.selected = "1";
			ContentValues valueTrue = new ContentValues();
			valueTrue.put(MusicDBHelper.COL_SELECTED, "1");
			musicDB.update(MusicDBHelper.TABLE_NAME, valueTrue, MusicDBHelper.COL_ID+"="+musicId, null);
//			musicDB.rawQuery("update "+MusicDBHelper.TABLE_NAME+" set "+MusicDBHelper.COL_SELECTED+"=1 " +
//							 "where "+MusicDBHelper.COL_ID+"="+musicId+";", null);
		}else{
			this.selected = "0";
			ContentValues valueFalse = new ContentValues();
			valueFalse.put(MusicDBHelper.COL_SELECTED, "0");
			musicDB.update(MusicDBHelper.TABLE_NAME, valueFalse, MusicDBHelper.COL_ID+"="+musicId, null);
//			musicDB.rawQuery("update "+MusicDBHelper.TABLE_NAME+" set "+MusicDBHelper.COL_SELECTED+"=0 " +
//							 "where "+MusicDBHelper.COL_ID+"="+musicId+";", null);
		}
	}
	
}
