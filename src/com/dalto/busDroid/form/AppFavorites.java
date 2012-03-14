package com.dalto.busDroid.form;

import java.util.HashMap;
import java.util.LinkedList;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.DataManager;
import com.dalto.busDroid.model.Stop;
import com.dalto.busDroid.model.adapter.FavoritesAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class AppFavorites extends Activity implements  OnChildClickListener{

	private ExpandableListView list;
	private String sortingPref = "";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        
        this.list = (ExpandableListView) this.findViewById(R.id.favoriteList);

        this.loadFavorites();
        
        this.list.setOnChildClickListener(this);
        //this.list.setOnGroupClickListener(this);

    }
    
    @Override
    protected void onRestart() {
    	//Reload from preference
    	Log.i("BusDroid", "Restart");
    	this.loadFavorites();
    	super.onRestart();
    }
    
    private void loadFavorites() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        this.sortingPref = preferences.getString("sort_favorite", "0");

        HashMap< String, LinkedList< BusObject > > favorites = DataManager.GetInstance().getFavorites(this.sortingPref);
        
        LinkedList< String > group = new LinkedList< String >();
        LinkedList< LinkedList < BusObject > > children = new LinkedList< LinkedList < BusObject >>();
        
        Log.i("BusDroid", favorites.toString());
        
        for (String key : favorites.keySet()) {
        	group.add(key);
        	children.add(favorites.get(key));
        }
        
        this.list.setAdapter(new FavoritesAdapter(this, group, children, this.sortingPref));
    }

	//@Override
	/*public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		//We just want to expand it
		return true;
	}*/
	
	@Override
	public boolean onChildClick(ExpandableListView _parent, View _v,
			int _groupPosition, int _childPosition, long _id) {
		
		//Setting the schedule to today
		DataManager.GetInstance().getBusDate().setCurrentDayValueToToday();
		
		//Getting the object
		Stop stop = (Stop) this.list.getExpandableListAdapter().getChild(_groupPosition, _childPosition);
		
		Intent appview = new Intent(this, AppView.class);
		appview.putExtra("Object", stop);
		startActivity(appview);
		
		return false;
	}
	



}
