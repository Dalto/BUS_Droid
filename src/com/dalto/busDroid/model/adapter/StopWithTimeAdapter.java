package com.dalto.busDroid.model.adapter;

import java.util.LinkedList;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.Stop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StopWithTimeAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private LinkedList< BusObject > list;
	
	public StopWithTimeAdapter(Context _context, LinkedList< BusObject > _list) {
		this.inflater = LayoutInflater.from(_context);
		this.list = _list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int _item) {
		return list.get(_item);
	}

	@Override
	public long getItemId(int _position) {
		return _position;
	}
	
	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		
		if (_convertView == null) {
			_convertView = this.inflater.inflate(R.layout.row_stopwithtime, null);
		}
		
		BusObject obj = list.get(_position);
		if (obj != null && obj instanceof Stop) {
			Stop stop = (Stop) obj;
			
			
			TextView content = (TextView) _convertView.findViewById(R.id.row_routeswithtime_Content);
			if (content != null) {
				content.setText(stop.getContent());
			}
			
			TextView time = (TextView) _convertView.findViewById(R.id.row_routeswithtime_Time);
			if (content != null) {
				time.setText(stop.getTime().toString());
			}
 		}

		return _convertView;
	}

}
