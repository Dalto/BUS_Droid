package com.dalto.busDroid.form;

/*--------
 * TODO --
 * -------
 * Menu
 * 		Place date in title bar
 * 		
 * Do about (Cuz about is there)
 * Do Gmap (Can wait)
 * 
 * Documentation ?! Wait What !! 
 * 
 * Place it on git
 * Design (Bored stuff)
 * 
 */


/*-------
 * @BUG---
 * ------
 * Quand on est le matin, sa bug
 * 
 */

import java.io.IOException;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.DataManager;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class Appmain extends TabActivity {
	
	private TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        displayScreen();
        
    }
    
    private void displayScreen() {
        TabSpec tabSpec;
        Intent intent;
        tabHost = getTabHost();
        
        //Adding Favorites tabs
        intent = new Intent(this, AppFavorites.class);
        
        tabSpec = tabHost.newTabSpec("Favorites").setIndicator(getString(R.string.favorites)).setContent(intent);
        tabHost.addTab(tabSpec);
        
        //Adding Routes tabs
        intent = new Intent(this, AppRoutes.class);
        tabSpec = tabHost.newTabSpec("Routes").setIndicator(getString(R.string.routes)).setContent(intent);
        tabHost.addTab(tabSpec);
        
        //Adding Stops tabs
        intent = new Intent(this, AppStops.class);
        tabSpec = tabHost.newTabSpec("Stops").setIndicator(getString(R.string.stops)).setContent(intent);
        tabHost.addTab(tabSpec);
        
        //Adding Map tabs
        //intent = new Intent(this, AppMap.class);
        //tabSpec = tabHost.newTabSpec("Map").setIndicator(getString(R.string.map)).setContent(intent);
        //tabHost.addTab(tabSpec);
        
        //Adding About tabs
        //intent = new Intent(this, AppAbout.class);
        //tabSpec = tabHost.newTabSpec("About").setIndicator("About").setContent(intent);
        //tabHost.addTab(tabSpec);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        String defaultTab = preferences.getString("opening_tab", "0");
        
        this.tabHost.setCurrentTab(Integer.parseInt(defaultTab)); 
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {

    	// Create the inflater menu
    	MenuInflater inflater = getMenuInflater();

    	// Parse the XML file
    	inflater.inflate(R.layout.menu_home, menu);

    	return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
	
    	
	    //Getting the id and compare it
	    switch (item.getItemId()) {
		    case R.id.menuHomeAbout:
		    	AppAbout about = new AppAbout(this);
		    	about.show();
		    	break;
	    	case R.id.menuHomeSetting:
	    		startActivityForResult(new Intent(this, Setting.class), 0);
	    		break;
	    }
	
	    return super.onOptionsItemSelected(item);

    }
}