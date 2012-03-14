package com.dalto.busDroid.form;

import com.dalto.busDroid.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAbout extends Dialog {

	private ImageView image;
	private TextView title;
	private TextView content;
	private Button button;
	
	
	public AppAbout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        this.image = (ImageView) findViewById(R.id.about_logo);
        this.image.setImageResource(R.drawable.icon);
        
        this.title = (TextView) findViewById(R.id.about_title);
        this.title.setText(this.getContext().getString(R.string.about_title));
        
        this.content = (TextView) findViewById(R.id.about_content);
        this.content.setText(this.getContext().getString(R.string.about_content));
        
        this.button = (Button) findViewById(R.id.about_button);
        this.button.setText(this.getContext().getString(R.string.about_button_text));
	}

}
