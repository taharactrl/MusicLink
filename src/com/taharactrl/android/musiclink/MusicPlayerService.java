package com.taharactrl.android.musiclink;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;


public class MusicPlayerService extends Service {

	public static final String MUSIC_FILE_PATH = "com.taharactrl.android.musiclink.MUSIC_FILE_PATH";
	public static final String PLAY = "com.taharactrl.android.musiclink.PLAY";
	public static final String PAUSE = "com.taharactrl.android.musiclink.PAUSE";
	public static final String PLAY_PAUSE = "com.taharactrl.android.musiclink.PLAY_PAUSE";
	public static final String NEXT = "com.taharactrl.android.musiclink.NEXT";
	public static final String PREV = "com.taharactrl.android.musiclink.PREV";
	public static final String STOP = "com.taharactrl.android.musiclink.STOP";
	public static final String QUIT = "com.taharactrl.android.musiclink.QUIT";
	public static final String SEEK = "com.taharactrl.android.musiclink.SEEK";
	public static final String ACTION = "com.taharactrl.android.musiclink.ACTION";
	public static final String REPEAT_MODE = "com.taharactrl.android.musiclink.REPEAT_MODE";
	public static final String SHUFFLE_MODE = "com.taharactrl.android.musiclink.SHUFFLE_MODE";
	public static final String REPEAT_CHANGE = "com.taharactrl.android.musiclink.REPEAT_CHANGE";
	public static final String SHUFFLE_CHANGE = "com.taharactrl.android.musiclink.SHUFFLE_CHANGE";
	public static final String INITIALIZE = "com.taharactrl.android.musiclink.INITIALIZE";
	
	public static final String NowPlayingMusicTitle = "com.taharactrl.android.musiclink.MusicTitle";
	public static final String NowPlayingMusicLength = "com.taharactrl.android.musiclink.MusicLength";
	public static final String NowPlayingMusicCurrentPosition = "com.taharactrl.android.musiclink.MusicCurrentPosition";
	private String nowPlayingMusicTitle = "not playing";
	
//	public static enum RepeateMode {NONE, ALL, ONE};
	public static final int REPEAT_NONE = 0;
	public static final int REPEAT_ALL = 1;
	public static final int REPEAT_ONE = 2;
	private int repeatMode = REPEAT_NONE;
	private boolean isShuffle = false;
	
	private int sleepTime = 0;
	private Thread mSelfStopThread = new Thread() {
		public void run() {
			while (true) {
				// 停止後 10 分再生がなかったらサービスを止める
				boolean needSleep = false;
				if ((isPausing || !isPlaying) && (sleepTime > 10)) {
					needSleep = true;
				}
				if (needSleep) {
					break;
				}
				try {
					Thread.sleep(1 * 1000 * 60); // 停止中でない、または 10 分経過してない場合は 1 分休む
					sleepTime++;
				} catch (InterruptedException e) {
				}
			}
			if(!isNotStartService){
				MusicPlayerService.this.stopSelf();
			}
		}
	};

	private boolean isNotStartService = true;
	
	private MediaPlayer mediaPlayer = null;
	
	private MusicDBHelper musicDBHelper;
	private SQLiteDatabase musicDB;
	
	private MusicObject nowPlayingMusicObject = null;

	private Vector<MusicObject> musicObjectsOrderList = new Vector<MusicObject>();
	
	
	class MusicPlayerServiceBinder extends Binder{
		
		 MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}
	
	
	private Notification notification = null;
	private NotificationManager notificationManager;
	private final int notificationID = 1;
	
	private AudioManager audioManager;
	private ComponentName musiclinkGlobalReceiverComponent;
	private RemoteControlClient remoteControlClient;
	
	private PendingIntent pi;
	@Override
	public void onCreate(){
		super.onCreate();
		
		audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		musiclinkGlobalReceiverComponent = new ComponentName(getApplicationContext(), MusicLinkGlobalReciever.class);
		audioManager.registerMediaButtonEventReceiver(musiclinkGlobalReceiverComponent);
		
		Intent intentRemoteControlClient = new Intent(Intent.ACTION_MEDIA_BUTTON);
		intentRemoteControlClient.setComponent(musiclinkGlobalReceiverComponent);
		remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(getApplicationContext(), 0, intentRemoteControlClient, 0));
		audioManager.registerRemoteControlClient(remoteControlClient);
		
		remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY
				| RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
				| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
				| RemoteControlClient.FLAG_KEY_MEDIA_STOP);
		
		
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent intentNotification = new Intent(getApplicationContext(), MainActivity.class);
//		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
		pi = PendingIntent.getActivity(getApplicationContext(), 0, intentNotification, 0);
		
//		RemoteViews notificationViews = new RemoteViews(getPackageName(), R.layout.remote_control); 
		RemoteViews notificationViews = new RemoteViews(getPackageName(), R.layout.notification_layout); 
		
		
		notification = new Notification();
		notification.icon = R.drawable.music_icon_notification;
//		notification.set
//		notification.setLatestEventInfo(getApplicationContext(), "test", "testtest", pi);
//		RemoteViews rl = new RemoteViews(packageName, layoutId) 
//		notification.contentView = notificationViews;
//		notification.contentView = notificationViews;
//		notification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_NO_CLEAR;
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
//		notification.flags = Notification.
//		notification.contentIntent = pi;
//		notification.contentView.setOnClickPendingIntent(R.id.notification_prev, 
//				PendingIntent.getService(getApplicationContext(),0,
//						new Intent(getApplicationContext(), MusicPlayerService.class).putExtra(ACTION, MusicPlayerService.PREV),0));
//		notification.contentView.setOnClickPendingIntent(R.id.notification_play, 
//				PendingIntent.getService(getApplicationContext(),0,
//						new Intent(getApplicationContext(), MusicPlayerService.class).putExtra(ACTION, MusicPlayerService.PLAY),0));
//		notification.contentView.setOnClickPendingIntent(R.id.notification_pause, 
//				PendingIntent.getService(getApplicationContext(),0,
//						new Intent(getApplicationContext(), MusicPlayerService.class).putExtra(ACTION, MusicPlayerService.PAUSE),0));
//		notification.contentView.setOnClickPendingIntent(R.id.notification_next, 
//				PendingIntent.getService(getApplicationContext(),0,
//						new Intent(getApplicationContext(), MusicPlayerService.class).putExtra(ACTION, MusicPlayerService.NEXT),0));
		
		
//		notification = new Notification(R.drawable.music_icon, "get notification", System.currentTimeMillis());
//		notification = );
		
		
		musicDBHelper = new MusicDBHelper(getApplicationContext());

		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				sendInfo();
			}
		};
		timer.schedule(timerTask, 0, 100);
//		timer.
		
		mSelfStopThread.start();
	}
	
	Timer timer = new Timer();
	
	Vector<MusicObject> musicSelectedList;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isNotStartService = false;
		
		String action = intent.getStringExtra(ACTION);
		
		Log.d("ACTION", action);
		
		if(!action.equals(INITIALIZE)){
			musicSelectedList = new Vector<MusicObject>();
			musicDB = musicDBHelper.getWritableDatabase();
			Cursor cursor = musicDB.rawQuery("select "+MusicDBHelper.COL_ID+" from "+MusicDBHelper.TABLE_NAME
											 +" where "+MusicDBHelper.COL_SELECTED
											 + " order by "+MusicDBHelper.COL_ALBUM+","+MusicDBHelper.COL_TRACK+";", null);
			boolean isEOF = cursor.moveToFirst();
			
			MusicObject mo;
			while(isEOF){
				mo = new MusicObject(getApplicationContext());
				mo.setMusicId(cursor.getString(0), musicDB);
				musicSelectedList.add(mo);
				isEOF = cursor.moveToNext();
			}
		}
		
		if(action.equals(PLAY)){
			if(isPausing){
				mediaPlayer.start();
			}else{
				if(nowPlayingMusicObject == null){
					Play();
				}else{
					Play(nowPlayingMusicObject);
				}
			}
		}else if(action.equals(NEXT)){
			if(nowPlayingMusicObject == null){
				Play();
			}else{
				if(!Next(nowPlayingMusicObject)){
					Play();
				}
			}			
		}else if(action.equals(PREV)){
			if(nowPlayingMusicObject == null){
				Play();
			}else{
				Prev(nowPlayingMusicObject);
			}			
		}else if(action.equals(PAUSE)){
			Pause();
		}else if(action.equals(PLAY_PAUSE)){
			if(isPausing){
				mediaPlayer.start();
				isPausing = false;
			}else{
				Pause();
			}
		}else if(action.equals(SEEK)){
			int timeSeeked = intent.getIntExtra(SEEK, 0);
			mediaPlayer.seekTo(timeSeeked);
		}else if(action.equals(REPEAT_CHANGE)){
			if(getRepeatMode() == REPEAT_NONE){
				setRepeatMode(REPEAT_ALL);
			}else if(getRepeatMode() == REPEAT_ALL){
				setRepeatMode(REPEAT_ONE);
			}else if(getRepeatMode() == REPEAT_ONE){
				setRepeatMode(REPEAT_NONE);
			}
		}else if(action.equals(SHUFFLE_CHANGE)){
			setShuffle(!isShuffle());
		}else if(action.equals(QUIT)){
			quit();
		}
		
//		if(!is)
		sendInfo();
		sleepTime = 0;
		
		return START_NOT_STICKY;	// Means we started the service, but don't want it to
									// restart in case it's killed.
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		return new MusicPlayerServiceBinder();
	}

	private boolean isPlaying = false;
	public boolean Play(MusicObject mo){
		
		if(musicSelectedList.size()<=0){
			return false;
		}
		
		String filePath = mo.getMusicFilename();
		Uri musicUri = Uri.parse("file://"+filePath);
		if(mediaPlayer !=null){
			mediaPlayer.stop();
		}
		mediaPlayer = MediaPlayer.create(getApplicationContext(), musicUri);
		mediaPlayer.start();
		isPausing = false;
		setPlaying(true);
		nowPlayingMusicDuration = mediaPlayer.getDuration();
		nowPlayingMusicObject = mo;
		nowPlayingMusicTitle = nowPlayingMusicObject.getTitle();

		
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO 自動生成されたメソッド・スタブ
				setPlaying(false);
				if(repeatMode == REPEAT_NONE){
					Next(nowPlayingMusicObject);
				}else if(repeatMode == REPEAT_ALL){
					if(!Next(nowPlayingMusicObject)){
						Play();
					}
				}else if(repeatMode == REPEAT_ONE){
					Play(nowPlayingMusicObject);
				} 
			}
		});
		
		
		return true;
	}
	
	public boolean Play(){
		if(musicSelectedList.size()>0){
			Play(musicSelectedList.get(0));
			return true;
		}
		
		return false;
	}
	
	public boolean Next(MusicObject currentMusicObject){
		int i;
		if(!isShuffle()){
			for(i = 0; i<musicSelectedList.size(); i++){
				if(musicSelectedList.get(i).getMusicHash().equals(currentMusicObject.getMusicHash())){
					if(i+1<musicSelectedList.size()){
						return Play(musicSelectedList.get(i+1));
					}
					
					
					break;
				}
			}
		
		}else{
			int randNum = (int) Math.floor(Math.random()*musicSelectedList.size());
//			musicObjectsOrderList.
			return Play(musicSelectedList.get(randNum));
			
		}
		return false;
	}
	
	public boolean Prev(MusicObject currentMusicObject){
		int i;
		for(i = 0; i<musicSelectedList.size(); i++){
			if(musicSelectedList.get(i).getMusicHash().equals(currentMusicObject.getMusicHash())){
				if(i-1>=0){
					return Play(musicSelectedList.get(i-1));
				}
				
				break;
			}
		}
		
		return Play(musicSelectedList.get(musicSelectedList.size()-1));
	}
	

	
	private boolean isPausing = false;
	public void Pause(){
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}
		isPausing = true;
	}
	
	public static final String SERVICE_NAME = "MusicLink Player Service";
	
	private int nowPlayingMusicDuration = 999999;
	
	public void sendInfo(){
		Intent intent = new Intent(SERVICE_NAME);
		intent.putExtra(NowPlayingMusicTitle, nowPlayingMusicTitle);
		if(mediaPlayer != null){
			intent.putExtra(NowPlayingMusicLength, nowPlayingMusicDuration);
			intent.putExtra(NowPlayingMusicCurrentPosition, mediaPlayer.getCurrentPosition());

			notification.setLatestEventInfo(getApplicationContext(), nowPlayingMusicTitle,
					  (mediaPlayer.getCurrentPosition()/1000)/60 +":"+(mediaPlayer.getCurrentPosition()/1000)%60
					  +"/"
					  +(nowPlayingMusicDuration/1000)/60 +":"+(nowPlayingMusicDuration/1000)%60
					  , pi);
//			notification.contentView.setTextViewText(R.id.notification_title, nowPlayingMusicTitle);
//			notification.contentView.setTextViewText(R.id.notification_time, 
//					  (mediaPlayer.getCurrentPosition()/1000)/60 +":"+(mediaPlayer.getCurrentPosition()/1000)%60
//					  +"/"
//					  +(nowPlayingMusicDuration/1000)/60 +":"+(nowPlayingMusicDuration/1000)%60
//	);

		}

//		Log.d("send","send");
		
		intent.putExtra(REPEAT_MODE, getRepeatMode());
		intent.putExtra(SHUFFLE_MODE, isShuffle());
		sendBroadcast(intent);
		
		
//		notification.contentView.setChronometer(viewId, base, format, started)
		
		
		if(!isPausing && isPlaying){
			notificationManager.notify(notificationID, notification);
		}
		
	}

	
	public int getRepeatMode() {
		return repeatMode;
	}

	public void setRepeatMode(int repeatMode) {
		this.repeatMode = repeatMode;
	}

	public boolean isShuffle() {
		return isShuffle;
	}

	public void setShuffle(boolean isShuffle) {
		this.isShuffle = isShuffle;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public void quit(){
		Log.d("quit","quit call");
		notificationManager.cancelAll();
//		notificationManager.
		stopService(new Intent(getApplicationContext(), MusicPlayerService.class));
		timer.cancel();
		timer.purge();
		stopSelf();
	}
	
	@Override
	public void onDestroy(){
		
		Log.d("Destroy","Destroy");
		
		if(musicDB != null){
			musicDB.close();
		}
		if(musicDBHelper != null){
			musicDBHelper.close();
		}
		
		
		super.onDestroy();
	}
	
}
