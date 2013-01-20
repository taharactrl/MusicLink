package com.taharactrl.android.musiclink;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BoxTextView extends RelativeLayout {

	private TextView body;
	private ImageView frame;
	private int width = LayoutParams.WRAP_CONTENT;
	private int height = LayoutParams.WRAP_CONTENT;
	private CharSequence text;
	private int fontSize = 12;
	private int border = 1;
	private int borderColor = Color.BLACK;
	private int backgroundColor = Color.WHITE;
	private int textColor = Color.BLACK;
	private int gravity = Gravity.CENTER;
	
	
	public int getGravity() {
		return gravity;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		body.setBackgroundColor(backgroundColor);
	}

	public int getBoxWidth() {
		return width;
	}

	public void setBoxWidth(int width) {
		this.width = width;
		frame.setLayoutParams(new LayoutParams(width,height));
		body.setLayoutParams(new LayoutParams(width-border*2, height-border*2));
	}

	public int getBoxHeight() {
		return height;
	}

	public void setBoxHeight(int height) {
		this.height = height;
		frame.setLayoutParams(new LayoutParams(width,height));
		body.setLayoutParams(new LayoutParams(width-border*2, height-border*2));
	}
	
	public void setBoxFontSize(int fontSize){
		this.fontSize = fontSize;
	}

	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
		body.setLayoutParams(new LayoutParams(width-border*2, height-border*2));
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
		frame.setBackgroundColor(borderColor);
	}
	
	public void setText(CharSequence text){
		this.text = text;
		body.setText(text);
	}
	
	public CharSequence getText(){
		return text;
	}
	
	public void setTextColor(int textColor){
		this.textColor = textColor;
		body.setTextColor(textColor);
	}

	public void setTextSize(int fontSize){
		this.fontSize = fontSize;
		body.setTextSize(fontSize);
	}
	
	public void setGravity(int gravity){
		this.gravity = gravity;
		body.setGravity(gravity);
	}
	
	public BoxTextView(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
		
		frame = new ImageView(context);
		frame.setLayoutParams(new LayoutParams(width,height));
		frame.setX(0);
		frame.setY(0);
		frame.setBackgroundColor(borderColor);
		addView(frame);
		
		body = new TextView(context);
		body.setLayoutParams(new LayoutParams(width-border*2, height-border*2));
		body.setX(border);
		body.setY(border);
		body.setBackgroundColor(backgroundColor);
		body.setText(text);
		body.setTextSize(fontSize);
		body.setTextColor(textColor);
		addView(body);
		
		
	}

}
