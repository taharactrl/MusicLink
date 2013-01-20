package com.taharactrl.android.musiclink;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class CreatePlaylistView extends LinearLayout {

	private LinearLayout createPlaylistView = this; 
	
	private TextView playlistDialogTitle;
	private EditText playlistName;
	private Button playlistDialogCancel;
	private Button playlistDialogCreate;

	private PlaylistDBHelper musicPlaylistDBHelper;
	private Vector<MusicObject> musicObjects;
	
	public CreatePlaylistView(Context context, PlaylistDBHelper mpldbh, Vector<MusicObject> mos) {
		super(context);
		this.musicPlaylistDBHelper = mpldbh;
		musicObjects = mos;
		// TODO 自動生成されたコンストラクター・スタブ
		
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setOrientation(LinearLayout.VERTICAL);
				
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		addView(ll);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(50, 150, 50, 0);
		ll.setLayoutParams(lp);
		ll.setPadding(20, 20, 20, 20);
		ll.setBackgroundColor(Color.GRAY);

		

//		setLayoutParams(new Lay);
		
		playlistDialogTitle = new TextView(context);
		playlistDialogTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		playlistDialogTitle.setGravity(Gravity.CENTER);
		playlistDialogTitle.setText("create playlist");
		playlistDialogTitle.setTextColor(Color.BLACK);
		ll.addView(playlistDialogTitle);
		
		playlistName = new EditText(context);
		playlistName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		ll.addView(playlistName);
		
		
		LinearLayout buttonLayout = new LinearLayout(context);
		ll.addView(buttonLayout);
		
//		LayoutParams lp2 = new LayoutParams(width, height, weight)
		playlistDialogCancel = new Button(context);
		playlistDialogCancel.setText("cancel");
		playlistDialogCancel.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
		buttonLayout.addView(playlistDialogCancel);
		playlistDialogCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				createPlaylistView.setVisibility(View.GONE);
				
			}
		});
		
		playlistDialogCreate = new Button(context);
		playlistDialogCreate.setText("create");
		playlistDialogCreate.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
		buttonLayout.addView(playlistDialogCreate);
		playlistDialogCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				
				if(musicPlaylistDBHelper.createTable(playlistName.getText().toString())){
					 for(MusicObject mo : musicObjects){
						 if(mo.isSelected()){
							 musicPlaylistDBHelper.addMusic(playlistName.getText().toString(), mo);
						 }
					 }
				}
				
				createPlaylistView.setVisibility(View.GONE);
			}
		});
		
		
	}

}
