package com.dalto.busDroid.form;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.DataManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class AppStops extends Activity implements OnClickListener {
	private static final int NB_CHAR_STOP = 4;
	
	private ImageButton one;
	private ImageButton two;
	private ImageButton three;
	private ImageButton four;
	private ImageButton five;
	private ImageButton six;
	private ImageButton seven;
	private ImageButton eight;
	private ImageButton nine;
	private ImageButton zero;
	private ImageButton erase;
	
	private EditText dialerField;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stops);
        
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fixed to portrait
        
        this.dialerField = (EditText) findViewById(R.id.stop_DialerField);
        
        this.one = (ImageButton) findViewById(R.id.stop_one);
        this.one.setOnClickListener(this);
        
        this.two = (ImageButton) findViewById(R.id.stop_two);
        this.two.setOnClickListener(this);
        
        this.three = (ImageButton) findViewById(R.id.stop_three);
        this.three.setOnClickListener(this);
        
        this.four = (ImageButton) findViewById(R.id.stop_four);
        this.four.setOnClickListener(this);
        
        this.five = (ImageButton) findViewById(R.id.stop_five);
        this.five.setOnClickListener(this);
        
        this.six = (ImageButton) findViewById(R.id.stop_six);
        this.six.setOnClickListener(this);
        
        this.seven = (ImageButton) findViewById(R.id.stop_seven);
        this.seven.setOnClickListener(this);
        
        this.eight = (ImageButton) findViewById(R.id.stop_eight);
        this.eight.setOnClickListener(this);
        
        this.nine = (ImageButton) findViewById(R.id.stop_nine);
        this.nine.setOnClickListener(this);
        
        this.zero = (ImageButton) findViewById(R.id.stop_zero);
        this.zero.setOnClickListener(this);
        
        this.erase = (ImageButton) findViewById(R.id.stop_erase);
        this.erase.setOnClickListener(this);
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == this.one) {
			this.dialerField.append("1");
		}
		else if (v == this.two) {
			this.dialerField.append("2");
		}
		else if (v == this.three) {
			this.dialerField.append("3");
		}
		else if (v == this.four) {
			this.dialerField.append("4");
		}
		else if (v == this.five) {
			this.dialerField.append("5");
		}
		else if (v == this.six) {
			this.dialerField.append("6");
		}
		else if (v == this.seven) {
			this.dialerField.append("7");
		}
		else if (v == this.eight) {
			this.dialerField.append("8");
		}
		else if (v == this.nine) {
			this.dialerField.append("9");
		}
		else if (v == this.zero) {
			this.dialerField.append("0");
		}
		else if (v == this.erase) {
			if (this.dialerField.getText().length() > 0) {
				this.dialerField.getText().delete(this.dialerField.getText().length() - 1,
						this.dialerField.getText().length());
		    }
		}
		
		
		//If the number is a stop number
		if (this.dialerField.length() >= AppStops.NB_CHAR_STOP) {
			//Setting the schedule to today
			DataManager.GetInstance().getBusDate().setCurrentDayValueToToday();
			
			//Get the appropriate information
			BusObject serviceCustom = DataManager.GetInstance().getStopFromId(this.dialerField.getText().toString());
			
			if (serviceCustom != null) {
				//Changing activity
				Intent appview = new Intent(this, AppView.class);
				appview.putExtra("Object", serviceCustom);
				startActivity(appview);
			}
			else {
				//Erase it
				this.dialerField.setText("");
				openMessageBoxNotFound();
			}
		}
	}
	
	private void openMessageBoxNotFound() {
		AlertDialog notFound = new AlertDialog.Builder(this).create();
		notFound.setTitle(this.getString(R.string.stop_not_found_title));
		notFound.setMessage(this.getString(R.string.stop_not_found_message));
		
		notFound.show();
		
	}
}
