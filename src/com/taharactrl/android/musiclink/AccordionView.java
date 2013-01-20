package com.taharactrl.android.musiclink;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class AccordionView extends LinearLayout {

	private boolean isOpened = false;
	
	LinearLayout parentLayout;
	LinearLayout childLayout;
	
	public AccordionView(Context context) {
		super(context);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		
		parentLayout = new LinearLayout(context);
		childLayout = new LinearLayout(context);
		
		setOrientation(LinearLayout.VERTICAL);
		parentLayout.setOrientation(LinearLayout.VERTICAL);
		childLayout.setOrientation(LinearLayout.VERTICAL);
		addView(parentLayout);
		
		parentLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				if(!isOpened()){
					setOpened(true);
					addView(childLayout);
				}else{
					setOpened(false);
					removeView(childLayout);
				}
			}
		});
		
	}

	public boolean isOpened() {
		return isOpened;
	}

	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}
	
	public void setParentView(View v){
		parentLayout.addView(v);
	}
	
	public void addChildView(View v){
		childLayout.addView(v);
	}

}
