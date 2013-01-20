package com.taharactrl.android.musiclink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicLinkGlobalReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 自動生成されたメソッド・スタブ

		if(intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
			context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.PAUSE));
		}else if(intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)){
			KeyEvent keyEvent = (KeyEvent)intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			if(keyEvent.getAction() != KeyEvent.ACTION_DOWN){
				return;
			}
			
		    switch(keyEvent.getKeyCode()){
		    case KeyEvent.KEYCODE_MEDIA_NEXT:
		    	context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.NEXT));
		    	break;
		    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
		    	context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.PREV));
		    	break;
		    case KeyEvent.KEYCODE_MEDIA_PLAY:
		    	context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.PLAY));
		    	break;
		    case KeyEvent.KEYCODE_MEDIA_PAUSE:
		    	context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.PAUSE));
		    	break;
		    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		    	context.startService(new Intent(context, MusicPlayerService.class).putExtra(MusicPlayerService.ACTION, MusicPlayerService.PLAY_PAUSE));
		    	break;
		    }

			
			
		}
		
	}

}
