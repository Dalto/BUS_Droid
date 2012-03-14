package com.dalto.busDroid.form;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusDate;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.DataManager;
import com.dalto.busDroid.model.Route;
import com.dalto.busDroid.model.Service;
import com.dalto.busDroid.model.Stop;
import com.dalto.busDroid.model.StopTime;
import com.dalto.busDroid.model.adapter.ListAdapter;
import com.dalto.busDroid.model.adapter.StopWithTimeAdapter;
import com.dalto.busDroid.model.adapter.StopsAdapter;
import com.dalto.busDroid.model.adapter.StopsThreeNextAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class AppView extends Activity implements OnItemClickListener, OnCheckedChangeListener, OnClickListener {
	public static final int RESUT_CODE = 0;
	public static final int LAST_HOUR_DAY = 4;
	
	private BusObject obj;
	
	private ImageView viewIcon;
	private TextView title;
	private TextView direction;
	private ListView listView;
	private CheckBox addFavorites;
	private LinkedList<BusObject> list;
	private Handler handler = new Handler();
	
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        
        //Get busObject from intent
        this.obj  = this.getIntent().getParcelableExtra("Object");
        //Fill the item's list
        this.obj.FillItems();
        
        //Get view property
        this.viewIcon = (ImageView) findViewById(R.id.view_icon);
        this.title = (TextView) findViewById(R.id.view_title);
        this.direction = (TextView) findViewById(R.id.view_direction);
        this.listView = (ListView) findViewById(R.id.view_listview);
        this.addFavorites = (CheckBox) findViewById(R.id.view_add_favorite);
        
        //Here we set everything related to the layout
        this.displayScreen();
    }
    
    private void displayScreen() {
    	if (this.obj.getType() == BusObject.Type.Stop) {
    		//Displaying stopTimes of that stop
    		if (this.obj instanceof Stop) {
    			Stop stop = (Stop) this.obj;
    			this.list = stop.getList();
	    		//TODO Add icon for Stop
    			viewIcon.setImageResource(R.drawable.icon);
	    		//viewIcon.set(service.getImage());
    			this.direction.setText(stop.getDirection().getName());
    			this.title.setText(stop.getContent());
    			
    			//Set the title to show the current schedule date
    	        BusDate date = DataManager.GetInstance().getBusDate();
    	        if (date.isHoliday()) {
    	        	//Say that it is not a mistake, but the schedule has changed
    	        	this.setTitle(this.getString(R.string.app_name) + " - " +
    	        			this.getResources().getStringArray(R.array.schedule)[date.getCurrentDayValue()] + " " +
    	        			this.getString(R.string.holiday_message));
    	        }
    	        else {
    	        	this.setTitle(this.getString(R.string.app_name) + " - " + this.getResources().getStringArray(R.array.schedule)[date.getCurrentDayValue()]);
    	        }
    			
    			this.listView.setAdapter(new ListAdapter(this, list));
    			this.listView.setOnItemClickListener(this);
    			
    			boolean isFavorites = DataManager.GetInstance().isInFavorites(stop.getId(), stop.getDirection().getId());
    			this.addFavorites.setChecked(isFavorites);
    			this.addFavorites.setOnCheckedChangeListener(this);
    		}
    		else {
    			Log.e("BusDroid", "Object of type Stop wasn't instance of Stop ?!");
    		}
    	}
    	else if (this.obj.getType() == BusObject.Type.Route){
    		//Displaying stops of that routes
    		if (this.obj instanceof Route) {
    			Route route = (Route) this.obj;
	    		this.list = route.getList();
	    		//TODO Add icon for Route
	    		//viewIcon.set(service.getImage());
	    		viewIcon.setImageResource(R.drawable.icon);   
	    		this.title.setText(String.format(getString(R.string.route), route.getContent()));
	    		this.direction.setText(route.getDirection().getName());
	    		this.listView.setAdapter(new StopsAdapter(this, list));
	    		this.listView.setOnItemClickListener(this);
	    		this.addFavorites.setVisibility(8);
    		}
    		else {
    			Log.e("BusDroid", "Object of type Route wasn't instance of Route ?!");
    		}
    	}
    	else if (this.obj.getType() == BusObject.Type.StopTime) {
    		//Displaying stops of that StopTime
    		if (this.obj instanceof StopTime) {
    			StopTime stopTime = (StopTime) this.obj;
    			this.list = stopTime.getList();
	    		//TODO Add icon for Route
	    		//viewIcon.set(service.getImage());
    			viewIcon.setImageResource(R.drawable.icon);
    			this.title.setText(String.format(getString(R.string.route), stopTime.getBusNumber() + ""));
    			
    			//Set the title to show the current schedule date
    	        BusDate date = DataManager.GetInstance().getBusDate();
    	        if (date.isHoliday()) {
    	        	//Say that it is not a mistake, but the schedule has changed
    	        	this.setTitle(this.getString(R.string.app_name) + " - " +
    	        			this.getResources().getStringArray(R.array.schedule)[date.getCurrentDayValue()] + " " +
    	        			this.getString(R.string.holiday_message));
    	        }
    	        else {
    	        	this.setTitle(this.getString(R.string.app_name) + " - " + this.getResources().getStringArray(R.array.schedule)[date.getCurrentDayValue()]);
    	        }
    	        
    	        
    			this.direction.setText(stopTime.getDirection().getName());
    			this.listView.setAdapter(new StopWithTimeAdapter(this, list));
	    		//listView.setOnItemClickListener(this);  Connot be click
    			this.addFavorites.setVisibility(8);
    		}
    		else {
    			Log.e("BusDroid", "Object of type StopTime wasn't instance of StopTime ?!");
    		}
    	}
    	else if (this.obj.getType() == BusObject.Type.Service){
    		//Displaying routes of that service
    		if (this.obj instanceof Service) {
	    		Service service = (Service) this.obj;
	    		this.list = service.getList();
	    		//TODO Add icon for Service
	    		//viewIcon.set(service.getImage());
	    		viewIcon.setImageResource(R.drawable.icon);
	    		this.title.setText(service.getContent());
	    		this.listView.setAdapter(new ListAdapter(this, list));
	    		this.listView.setOnItemClickListener(this);
	    		this.addFavorites.setVisibility(8);
    		}
    		else {
    			Log.e("BusDroid", "Object of type Service wasn't instance of Service ?!");
    		}
    	}
    	else if (this.obj.getType() == BusObject.Type.RouteWithStop) {
    		//Displaying stopTimes of that stop
    		if (this.obj instanceof Route) {
    			Route route = (Route) this.obj;
    			this.list = route.getList();
	    		//TODO Add icon for Stop
	    		//viewIcon.set(service.getImage());
    			this.direction.setText(route.getDirection().getName());
    			this.title.setText(route.getContent());
    			this.listView.setAdapter(new ListAdapter(this, list));
    			this.listView.setOnItemClickListener(this);
    			this.addFavorites.setVisibility(8);
    		}
    		else {
    			Log.e("BusDroid", "Object of type RouteWithStop wasn't instance of Route ?!");
    		}
    	}
    	else if (this.obj.getType() == BusObject.Type.StopSearch) {
    		//Displaying routes of that stop
    		if (this.obj instanceof Stop) {
	    		Stop stop = (Stop) this.obj;
	    		this.list = stop.getList();
	    		//TODO Add icon for Service
	    		//viewIcon.set(service.getImage());
	    		viewIcon.setImageResource(R.drawable.icon);
	    		this.title.setText(stop.getContent());
	    		this.listView.setAdapter(new ListAdapter(this, list));
	    		this.listView.setOnItemClickListener(this);
	    		this.addFavorites.setVisibility(8);
    		}
    		else {
    			Log.e("BusDroid", "Object of type StopSearch wasn't instance of Stop ?!");
    		}
    		
    	}
    	else {
    		//We've got a problems
    		Log.e("BusDroid", "Object has no type ?!");
    	}
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    	Intent intent = null;
    	
		//Know that can be weird be obj contain different object
    	if (this.obj.getType() == BusObject.Type.Stop) {
    		intent = new Intent(this, AppView.class);
    		StopTime stopTime = (StopTime) listView.getItemAtPosition(position);
    		intent.putExtra("Object", stopTime);
    	}
    	else if (this.obj.getType() == BusObject.Type.Route){
    		intent = new Intent(this, AppView.class);
    		Stop stop = (Stop) listView.getItemAtPosition(position);
    		intent.putExtra("Object", stop);
    	}
    	else if (this.obj.getType() == BusObject.Type.StopTime) {
    		//intent = new Intent(this, AppView.class);
    		//Stop stop = (Stop) listView.getItemAtPosition(position);
    		//intent.putExtra("Object", stop);
    	}
    	else if (this.obj.getType() == BusObject.Type.Service){
    		
    		//Calling the Direction view
    		intent = new Intent(this, AppDirection.class);
    		Route route = (Route) listView.getItemAtPosition(position);
    		intent.putExtra("Object", route);
    	}
    	else if (this.obj.getType() == BusObject.Type.StopSearch) {
    		//Calling the Direction view
    		intent = new Intent(this, AppView.class);
    		Route route = (Route) listView.getItemAtPosition(position);
    		intent.putExtra("Object", route);
    	}
    	else if (this.obj.getType() == BusObject.Type.RouteWithStop) {
    		intent = new Intent(this, AppView.class);
    		StopTime stopTime = (StopTime) listView.getItemAtPosition(position);
    		intent.putExtra("Object", stopTime);
    	}
    	else {
    		//We've got a problems
    		Log.e("BusDroid", "Object has no type ?!");
    	}
		
    	this.startActivityForResult(intent, RESUT_CODE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (obj instanceof Stop) {
			Stop stop = (Stop) obj;
			DataManager.GetInstance().addOrRemoveFavorites(isChecked, stop.getId(), stop.getDirection().getId());
		}
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

    	// Create the inflater menu
    	MenuInflater inflater = getMenuInflater();

    	if (this.obj instanceof Stop) {
        	// Parse the XML file
        	inflater.inflate(R.layout.menu_view_stop, menu);
    	}
    	else {
        	// Parse the XML file
        	inflater.inflate(R.layout.menu_view, menu);
    	}

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
		
		builder.setItems(time, this);

		
		//AlertDialog alert = builder.show();
		builder.show();

    }

	@Override
	public void onClick(DialogInterface _dialog, int _item) {
		// TODO Auto-generated method stub
    	int old = DataManager.GetInstance().getBusDate().getCurrentDayValue();
    	DataManager.GetInstance().getBusDate().setCurrentDayValue(_item);
    	
		if (old != DataManager.GetInstance().getBusDate().getCurrentDayValue()) {
			Log.i("BusDroid", "Redisplay screen");

			this.obj.FillItems();
			this.displayScreen();

		}
	}
}
