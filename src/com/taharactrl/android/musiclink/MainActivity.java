package com.taharactrl.android.musiclink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Vector;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class MainActivity extends Activity {

	Activity mainActivity = this;
	
	MusicDBHelper musicDBHelper;
	SQLiteDatabase musicDB;
	
	PlaylistDBHelper playlistDBHelper;
	SQLiteDatabase playlistDB;

	ArrayList<ArrayList<String>> musicList;
	
	Vector<MusicObject> musicObjects = new Vector<MusicObject>();
	Vector<TextView> musicAlbumViews = new Vector<TextView>();
	
	
	Vector<MusicObject> musicObjectsAlbum = new Vector<MusicObject>();
	ArrayList<String> artistList;
	Vector<MusicObject> musicObjectsArtist = new Vector<MusicObject>();
//	Vector<MusicObject> musicObjectsArtist = new Vector<MusicObject>();
	
	private LinearLayout tabAll;
	private LinearLayout tabAlbum;
	private LinearLayout tabArtist;
	private LinearLayout tabPlaylist;
	private LinearLayout tabCurrent;
	
	private SeekBar seekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		TabHost tabhost = (TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		
		TabSpec ts1 = tabhost.newTabSpec("tabAll");
		ts1.setIndicator("All");
		ts1.setContent(R.id.tab_list_all);
		tabhost.addTab(ts1);
		
		TabSpec ts2 = tabhost.newTabSpec("tabAlbum");		
		ts2.setIndicator("Album");
		ts2.setContent(R.id.tab_list_album);
		tabhost.addTab(ts2);

		TabSpec ts3 = tabhost.newTabSpec("tabArtist");		
		ts3.setIndicator("Artist");
		ts3.setContent(R.id.tab_list_artist);
		tabhost.addTab(ts3);

		TabSpec ts4 = tabhost.newTabSpec("tabPlaylist");		
		ts4.setIndicator("PlayList");
		ts4.setContent(R.id.tab_playlist);
		tabhost.addTab(ts4);

		TabSpec ts5 = tabhost.newTabSpec("tabCurrent");		
		ts5.setIndicator("Current");
		ts5.setContent(R.id.tab_list_current);
		tabhost.addTab(ts5);
		
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO 自動生成されたメソッド・スタブ
				Log.d("tabId",tabId);
				if(tabId.equals("tabAll")){
					tabAll.removeAllViews();
					tabCurrent.removeAllViews();
					for(MusicObject mo : musicObjects){
						mo.setMusicId(mo.getMusicId(), musicDB);
						tabAll.addView(mo);
					}					
				}else if(tabId.equals("tabAlbum")){
					for(MusicObject mo : musicObjectsAlbum){
						mo.setMusicId(mo.getMusicId(), musicDB);
					}
					
				}else if(tabId.equals("tabArtist")){
					for(MusicObject mo : musicObjectsArtist){
						mo.setMusicId(mo.getMusicId(), musicDB);
					}
					
				}else if(tabId.equals("tabPlaylist")){
					createPlaylistView();					
				}else if(tabId.equals("tabCurrent")){
					tabAll.removeAllViews();
					tabCurrent.removeAllViews();
					for(MusicObject mo : musicObjects){
						mo.setMusicId(mo.getMusicId(), musicDB);
						if(mo.isSelected()){
							tabCurrent.addView(mo);
						}
					}					
					
				}
			}
		});

		Log.d("time0", String.valueOf(System.currentTimeMillis()));
		
//		musicDBHelper = new MusicDBHelper(getApplicationContext());
		musicDBHelper = new MusicDBHelper(this);
		musicDB = musicDBHelper.getWritableDatabase();
		musicList = musicDBHelper.getMusicList(musicDB);

		Log.d("time db", String.valueOf(System.currentTimeMillis()));
		
		for(int i = 0; i < musicList.size(); i++){
			MusicObject mo = new MusicObject(getApplicationContext());
//			mo.setMusicTitle(musicList.get(i).get(MusicDBHelper.TITLE));
			mo.setMusicId(musicList.get(i).get(MusicDBHelper.ID), musicDB);
			musicObjects.add(mo);
		}
		
		Log.d("time io", String.valueOf(System.currentTimeMillis()));
		// ALL
		tabAll = (LinearLayout)findViewById(R.id.linearlayout_all);
		tabAll.setOrientation(LinearLayout.VERTICAL);
		for(MusicObject mo : musicObjects){
			tabAll.addView(mo);

		}
		
		Log.d("time all", String.valueOf(System.currentTimeMillis()));

		// ALBUM
		tabAlbum = (LinearLayout)findViewById(R.id.linearlayout_ablum);
		tabAlbum.setOrientation(LinearLayout.VERTICAL);
//		tabAlbum.
		for(int i = 0; i<musicObjects.size();i++){
			MusicObject mo = musicObjects.get(i);
			TextView tv = new TextView(getApplicationContext());
			tv.setText(mo.getAlbum());
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			boolean isAlbumExist = false;
			if(musicAlbumViews.size()>0){
				for(int j = 0; j < musicAlbumViews.size(); j++){
					if(musicAlbumViews.get(j).getText().toString().equals(tv.getText())){
						isAlbumExist = true;
						break;
					}
				}
				
				if(!isAlbumExist){
					musicAlbumViews.add(tv);
				}
			}else{
				musicAlbumViews.add(tv);
			}
		}
		
		for(TextView tv : musicAlbumViews){
			AccordionView av = new AccordionView(getApplicationContext());
			av.setParentView(tv);
//			int num = 0;
			for(int j = 0; j < musicList.size(); j++){
//				Log.d("t1",musicList.get(j).get(MusicDBHelper.TITLE));
//				Log.d("t2",tv.getText().toString());
				if(musicList.get(j).get(MusicDBHelper.ALBUM).equals(tv.getText().toString())){
					MusicObject mo = new MusicObject(getApplicationContext());
//					mo.setMusicTitle(musicList.get(i).get(MusicDBHelper.TITLE));
					mo.setMusicId(musicList.get(j).get(MusicDBHelper.ID), musicDB);
					av.addChildView(mo);
					
					musicObjectsAlbum.add(mo);
//					num++;
				}
			}
//			Log.d("num",String.valueOf(num));
			tabAlbum.addView(av);
		}
		
		Log.d("time album", String.valueOf(System.currentTimeMillis()));
		
		// ARTIST
		tabArtist = (LinearLayout)findViewById(R.id.linearlayout_artist);
		tabArtist.setOrientation(LinearLayout.VERTICAL);
		
		artistList = new ArrayList<String>();
		for(int i = 0; i<musicList.size();i++){
			if(artistList.indexOf(musicList.get(i).get(MusicDBHelper.ARTIST))<0){
				artistList.add(musicList.get(i).get(MusicDBHelper.ARTIST));
			}
		}
		Collections.sort(artistList);

		for(String artist : artistList){
			TextView tv = new TextView(getApplicationContext());
			tv.setText(artist);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			AccordionView av = new AccordionView(getApplicationContext());
			av.setParentView(tv);
			for(int j = 0; j < musicList.size(); j++){
				if(musicList.get(j).get(MusicDBHelper.ARTIST).equals(artist)){
					MusicObject mo = new MusicObject(getApplicationContext());
					mo.setMusicId(musicList.get(j).get(MusicDBHelper.ID), musicDB);
					av.addChildView(mo);
					
					musicObjectsArtist.add(mo);
				}
			}
			tabArtist.addView(av);
			
		}
		
		Log.d("time artist", String.valueOf(System.currentTimeMillis()));
		
		// CURRENT
		tabCurrent = (LinearLayout)findViewById(R.id.linearlayout_current);
		tabCurrent.setOrientation(LinearLayout.VERTICAL);
		
		// PLAYLIST
		tabPlaylist = (LinearLayout)findViewById(R.id.linearlayout_playlist);
		tabPlaylist.setOrientation(LinearLayout.VERTICAL);
		
		playlistDBHelper = new PlaylistDBHelper(getApplicationContext());
		playlistDB = playlistDBHelper.getWritableDatabase();
		
		
		
		
		
		
		
		
		LinearLayout controllerLayout = (LinearLayout)findViewById(R.id.linearlayout_controller);
		int grayRatio = 0xBB;
		controllerLayout.setBackgroundColor(Color.rgb(grayRatio, grayRatio, grayRatio));
		
		
		
		
		ImageView playImageView = (ImageView)findViewById(R.id.imageView_play);
		playImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PLAY);
				startService(intent);
			}
		});

		
		ImageView nextImageView = (ImageView)findViewById(R.id.imageView_next);
		nextImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.NEXT);
				startService(intent);				
			}
		});
		
		ImageView prevImageView = (ImageView)findViewById(R.id.imageView_prev);
		prevImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PREV);
				startService(intent);				
			}
		});
		
		ImageView pauseImageView = (ImageView)findViewById(R.id.imageView_pause);
		pauseImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PAUSE);
				startService(intent);				
			}
		});

		ImageView repeatImageView = (ImageView)findViewById(R.id.imageView_repeat);
		repeatImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.REPEAT_CHANGE);
				startService(intent);
			}
		});

		ImageView shuffleImageView = (ImageView)findViewById(R.id.imageView_shuffle);
		shuffleImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.SHUFFLE_CHANGE);
				startService(intent);				
			}
		});
		
		
		TextView title = (TextView)findViewById(R.id.textView_musicinfo);
		title.setText("Not Playing");
		
		IntentFilter filter = new IntentFilter(MusicPlayerService.SERVICE_NAME);
		registerReceiver(musiclinkReceiver, filter);
		
		seekBar = (SeekBar)findViewById(R.id.seekBar_music_time);
		seekBar.setMax(SEEKBAR_MAX);
		seekBar.setProgress(0);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO 自動生成されたメソッド・スタブ
				int progress = seekBar.getProgress();
				float ratio = progress/(float)SEEKBAR_MAX;
				int targetTime = (int) (nowPlayingMusicLength*ratio);
				
				Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
				intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.SEEK);
				intent.putExtra(MusicPlayerService.SEEK, targetTime);
				startService(intent);
				
				setSeekBarTouching(false);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO 自動生成されたメソッド・スタブ
				setSeekBarTouching(true);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
		});
		
		
		ImageView createPlaylistImage = (ImageView)findViewById(R.id.button_create_playlist);
		createPlaylistImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				CreatePlaylistView cpv = new CreatePlaylistView(getApplicationContext(), playlistDBHelper, musicObjects);
				RelativeLayout rl = (RelativeLayout)findViewById(R.id.main_activity_base_layout);
				rl.addView(cpv);
			}
		});
		
		Log.d("time control", String.valueOf(System.currentTimeMillis()));
		
//		startService(new Intent(getApplicationContext(), MusicPlayerService.class)
//							.putExtra(MusicPlayerService.ACTION, MusicPlayerService.INITIALIZE));
		
		Log.d("time service", String.valueOf(System.currentTimeMillis()));
		
	}

	private final int SEEKBAR_MAX = 100000;
	private boolean seekBarTouching = false;

	private int nowPlayingMusicCurrentTime;
	private int nowPlayingMusicLength;
	private float nowPlayingMusicSeekbarRatio;
	
	
	private MusicLinkReceiver musiclinkReceiver = new MusicLinkReceiver();
	private class MusicLinkReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
//			Log.d("recieve","recieved");
			
			TextView title = (TextView)findViewById(R.id.textView_musicinfo);
			int currentTime = intent.getIntExtra(MusicPlayerService.NowPlayingMusicCurrentPosition,0);
			int length = intent.getIntExtra(MusicPlayerService.NowPlayingMusicLength,0);
			
			title.setText(intent.getStringExtra(MusicPlayerService.NowPlayingMusicTitle)
					   	  +"   ("
						  +(currentTime/1000)/60 +":"+(currentTime/1000)%60
						  +"/"
						  +(length/1000)/60 +":"+(length/1000)%60
						  +")");
			nowPlayingMusicCurrentTime = currentTime;
			nowPlayingMusicLength = length;
			float seekBarRatio = currentTime/(float)length;
			nowPlayingMusicSeekbarRatio = seekBarRatio;
			if(!isSeekBarTouching()){
				seekBar.setProgress((int)(seekBarRatio*SEEKBAR_MAX));
			}
			
			int repeatMode = intent.getIntExtra(MusicPlayerService.REPEAT_MODE, MusicPlayerService.REPEAT_NONE);
			boolean isShuffle = intent.getBooleanExtra(MusicPlayerService.SHUFFLE_MODE, false);
			
			ImageView repeatImage = (ImageView)findViewById(R.id.imageView_repeat);
			if(repeatMode == MusicPlayerService.REPEAT_NONE){
				repeatImage.setImageResource(R.drawable.repeat_all);
				repeatImage.setColorFilter(new LightingColorFilter(0x0000ff, 0xffff00));
			}else if(repeatMode == MusicPlayerService.REPEAT_ALL){
				repeatImage.setImageResource(R.drawable.repeat_all);
				repeatImage.setColorFilter(null);				
			}else if(repeatMode == MusicPlayerService.REPEAT_ONE){
				repeatImage.setImageResource(R.drawable.repeat_one);
				repeatImage.setColorFilter(null);								
			}

			ImageView shuffleImage = (ImageView)findViewById(R.id.imageView_shuffle);
			if(isShuffle){
				shuffleImage.setColorFilter(null);
			}else{
				shuffleImage.setColorFilter(new LightingColorFilter(0x0000ff, 0xffff00));
			}
			
//			Log.d("RepeatMode", String.valueOf(repeatMode));
//			Log.d("IsShuffle", String.valueOf(isShuffle));

			
		}
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	Handler handler = new Handler();
	AlertDialog alertDialog;
	ProgressDialog progressDialog;
	public boolean onOptionsItemSelected(MenuItem item){
	
		switch(item.getItemId()){
		case R.id.update_db:
			progressDialog = ProgressDialog.show(mainActivity, "Please wait", "Searching local storage.\nCreating music database...");
			
			Thread updateDBThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
//					final MusicDBHelper musicDBHelper = new MusicDBHelper(mainActivity.getApplicationContext());
//					final SQLiteDatabase db = musicDBHelper.getWritableDatabase();
					musicDBHelper.deleteDB(musicDB);
					musicDBHelper.createMusicTable(musicDB);
					musicDBHelper.createMusicDB(musicDB);					
			
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							progressDialog.dismiss();
							
							AlertDialog.Builder alertDialogBuiler = new AlertDialog.Builder(mainActivity);
							alertDialogBuiler.setMessage("Database updated completely");
							
							alertDialog = alertDialogBuiler.create();
							alertDialog.show();
							Handler handler2 = new Handler();
							handler2.postDelayed(new Runnable() {
								
								@Override
								public void run() {
//									ml.updateList(musicDB.getMusicList(db));
									
									alertDialog.dismiss();
								}
							}, 1000);
							
						}
					});
				
				}
				
			});
			updateDBThread.start();
			
			return true;
		}
		
		return false;
	}
	public boolean isSeekBarTouching() {
		return seekBarTouching;
	}
	public void setSeekBarTouching(boolean seekBarTouching) {
		this.seekBarTouching = seekBarTouching;
	}

	
	public void createPlaylistView(){
		tabPlaylist.removeAllViews();
		
		Cursor cursorPlaylist = playlistDB.rawQuery("select name from sqlite_master where type='table';", null); 
		boolean isEof = cursorPlaylist.moveToFirst();
		while(isEof){
			
			String playlistName = cursorPlaylist.getString(cursorPlaylist.getInt(0));
			if(!playlistName.equals("android_metadata") && !playlistName.equals("sqlite_sequence")){
			
				TextView playlistItem = new TextView(getApplicationContext());
				playlistItem.setText(playlistName);
				playlistItem.setTextColor(Color.BLACK);
				playlistItem.setTextSize(20);
				playlistItem.setGravity(Gravity.CENTER_VERTICAL);
				
				AccordionView av = new AccordionView(getApplicationContext());
				av.setParentView(playlistItem);
				tabPlaylist.addView(av);
				
				Cursor cursorPlaylistContents = playlistDB.rawQuery("select hash from "+playlistName+";", null);
				boolean isEOF2 = cursorPlaylistContents.moveToFirst();
				while(isEOF2){
					
					MusicObject mo = new MusicObject(getApplicationContext());
					mo.setMusicHash(cursorPlaylistContents.getString(0), musicDB);
					av.addChildView(mo);
					
					isEOF2 = cursorPlaylistContents.moveToNext();
				}
				
			}
			
			isEof = cursorPlaylist.moveToNext();
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
		
		switch(keyCode){
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.NEXT);
			startService(intent);
			return true;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PREV);
			startService(intent);
			return true;
		case KeyEvent.KEYCODE_MEDIA_PLAY:
			intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PLAY);
			startService(intent);
			return true;
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
			intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PAUSE);
			startService(intent);
			return true;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.PLAY_PAUSE);
			startService(intent);
			return true;
//		case KeyEvent.KEYCODE_BACK:
//			
//			AlertDialog.Builder adb = new AlertDialog.Builder(this);
//			adb.setTitle("Quit this app?");
//			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO 自動生成されたメソッド・スタブ
//					Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
//					intent.putExtra(MusicPlayerService.ACTION, MusicPlayerService.QUIT);
////					startService(intent);
//					stopService(intent);
//					finish();
//				}
//			});
//			adb.setNegativeButton("Only close window", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO 自動生成されたメソッド・スタブ
//					finish();
//				}
//			});
//			
//			AlertDialog ad = adb.create();
//			ad.show();
//			
//			return true;
//	    		
		}
	    	
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDestroy(){
		
		musicDB.close();
		musicDBHelper.close();
		unregisterReceiver(musiclinkReceiver);
		
		super.onDestroy();
	}

}
