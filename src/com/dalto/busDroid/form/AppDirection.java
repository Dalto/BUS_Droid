package com.dalto.busDroid.form;

import java.text.DateFormat;
import java.util.Calendar;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusDate;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.DataManager;
import com.dalto.busDroid.model.Direction;
import com.dalto.busDroid.model.Route;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

public class AppDirection extends Activity implements OnClickListener {

	private Route route;
	
	private ImageView viewIcon;
	private TextView title;
	private Button btnDirection[] = new Button[2];
	private Direction directions[];
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.direction);
        this.setTitle(this.getString(R.string.app_name));
        
        
        //Get busObject from intent
        BusObject obj = this.getIntent().getParcelableExtra("Object");
        obj.FillItems();
        
        //Get view property
        this.viewIcon = (ImageView) findViewById(R.id.direction_icon);
        this.title = (TextView) findViewById(R.id.direction_title);
        this.btnDirection[0] = (Button) findViewById(R.id.direction_direction1);
        this.btnDirection[1] = (Button) findViewById(R.id.direction_direction2);
        
        
        if (obj instanceof Route) {
            Log.i("BusDroid", "instance of route");
        	this.route = (Route) obj;
    		//TODO Add icon for Route
    		//viewIcon.set(service.getImage());
        	viewIcon.setImageResource(R.drawable.icon);
			this.directions = route.getDirections();
			Log.i("BusDroid", route.getContent());
    		this.title.setText(String.format(getString(R.string.route), route.getContent()));
    		this.btnDirection[0].setText(this.directions[0].getName());
        	this.btnDirection[0].setOnClickListener(this);
    		this.btnDirection[1].setText(this.directions[1].getName());
        	this.btnDirection[1].setOnClickListener(this);
        }
        else {
    		//We've got a problems
        	this.route = null;
    		Log.e("BusDroid", "Direction was called by a wrong object");
        }
	}

	@Override
	public void onClick(View v) {
		if (this.route != null) { 
			if (v == btnDirection[0]) {
				route.setDirection(this.directions[0]);
			}
			else if (v == btnDirection[1]) {
				route.setDirection(this.directions[1]);
			}
			Intent intent = new Intent(this, AppView.class);
			intent.putExtra("Object", this.route);
			this.startActivityForResult(intent, AppView.RESUT_CODE);
		}
		else {
    		Log.e("BusDroid", "Route was null");
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

    	// Create the inflater menu
    	MenuInflater inflater = getMenuInflater();

    	// Parse the XML file
    	inflater.inflate(R.layout.menu_view, menu);

    	return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
	

	    // On récupère l'id de l'item et on le compare
    	
	    //Getting the id and compare it
	    switch (item.getItemId()) {
		    case R.id.menuViewAbout:
		    	return true;
	    	case R.id.menuViewSetting:
	    		startActivityForResult(new Intent(this, Setting.class), 0);
	    		break;
	    	case R.id.menuViewChangeDate:
	    		this.openListDialogChangeShedule(this);
	    		break;
	    	case R.id.menuViewBackHome:
	    		this.setResult(RESULT_OK);
    			this.finish();
    			break;
	    }
	
	    return super.onOptionsItemSelected(item);

    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	if (requestCode == AppView.RESUT_CODE) {
    		
    		//If it return without the back button
    		if(resultCode == RESULT_OK) {
    			this.setResult(RESULT_OK);
    			this.finish();
    		}
    		else if (resultCode == RESULT_CANCELED) {
    			
    		}
    		
    	}
    	
    	//Call the parent
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void openListDialogChangeShedule(Context _context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setTitle(getString(R.string.menu_schedule_title));
		String time[] = new String[20];
		
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.HOUR_OF_DAY) < AppView.LAST_HOUR_DAY) {
			//yersterday schedule
			c.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		for (int i = 0; i < 20; ++i) {
			time[i] = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		builder.setItems(time, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	DataManager.GetInstance().getBusDate().setCurrentDayValue(item);
		    }
		});
		//AlertDialog alert = builder.show();
		builder.show();
    }

}
