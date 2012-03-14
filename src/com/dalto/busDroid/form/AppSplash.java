package com.dalto.busDroid.form;

import java.io.IOException;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.DataManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class AppSplash extends Activity {

	private TextView message;
	private Handler handler = new Handler();
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash_screen);
	
	    this.message = (TextView) findViewById(R.id.splash_message);
	    
	    startSplashScreen();
	}
	
	public void startSplashScreen() {
	    // thread for displaying the SplashScreen
		
		
		MyRunnableSplashScreen splash = new MyRunnableSplashScreen(this);
		Log.i("BusDroid", "inb4");
		splash.start();
	}
	
	public void setMessageText(int _id) {
		this.message.setText(_id);
	}
	
	private class MyRunnableSplashScreen extends Thread {
		
		private Context context;
		private int idDisplayMessage;
		
		public MyRunnableSplashScreen(Context _context) {
			this.context = _context;
		}
		
		@Override
        public void run() {
			Log.i("BusDroid", "on Tread");
			
			boolean dbExist = false;
			
			DataManager.GetInstance().createDatabaseObject(this.context);
			
			try {
				message.setText(getApplication().getResources().getString(R.string.splash_checking_db));
				dbExist = DataManager.GetInstance().checkIfDatabaseExist();
				
				if (dbExist == false) {
					//Need to download the database
					this.idDisplayMessage = R.string.splash_downloading;
					this.displayMessage();
					
					Log.i("BusDroid", "download");
					DataManager.GetInstance().downloadDatabase();
					
					dbExist = true;
				}
				else if (DataManager.GetInstance().checkIfDatabaseIsUpToDate() == false) {
					//Update the database;
					this.idDisplayMessage = R.string.splash_updating;
					this.displayMessage();
					
					Log.i("BusDroid", "update");
					DataManager.GetInstance().downloadDatabase();
				}
			} 
			catch (IOException e) {
				Log.i("BusDroid", e.getMessage());
				//Something went wrong
			}
			finally {
				Log.i("BusDroid", "Done");
			}
			
			if (dbExist == true) {
				//Maby the DB is not up to date, but we can access to the application
				
	        	//Close this activity
	            finish();
	            //Start the main activity
	            Intent intent = new Intent(this.context, Appmain.class);
	            startActivity(intent);
	            //Stop the tread
	            //stop();
			}
			else {
				//We cannot open the application
				this.idDisplayMessage = R.string.splash_sorry_db_dont_exist;
				this.displayMessage();
			}
        }
		
		private void displayMessage() {
			
			handler.post(new Runnable() {	
				public void run() {
					message.setText(getApplication().getString(idDisplayMessage));
				}
			});
			
		}
	}
}
