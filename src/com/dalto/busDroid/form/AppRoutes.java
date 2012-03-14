package com.dalto.busDroid.form;

import java.util.LinkedList;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.DataManager;
import com.dalto.busDroid.model.Service;
import com.dalto.busDroid.model.adapter.ServiceAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AppRoutes extends Activity implements OnItemClickListener{
	
	private ListView lstRoutes;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);
        
        LinkedList<BusObject> services = DataManager.GetInstance().getAllService();
        
        lstRoutes = (ListView) findViewById(R.id.lstRoutes);
        lstRoutes.setAdapter(new ServiceAdapter(this, services));
        lstRoutes.setOnItemClickListener(this); 
    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		//Setting the schedule to today
		DataManager.GetInstance().getBusDate().setCurrentDayValueToToday();
		
		
		Service service = (Service)lstRoutes.getItemAtPosition(position);
		
		Intent appview = new Intent(this, AppView.class);
		appview.putExtra("Object", service);
		startActivity(appview);
		
	}
}
