package com.dalto.busDroid.model.adapter;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;

import java.util.LinkedList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StopsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private LinkedList< BusObject > list;
	
	public StopsAdapter(Context _context, LinkedList< BusObject > _list) {
		inflater = LayoutInflater.from(_context);
		list = _list;
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
			_convertView = this.inflater.inflate(R.layout.row_stop, null);
		}
		
		BusObject obj = list.get(_position);
		if (obj != null) {
			TextView content = (TextView) _convertView.findViewById(R.id.row_stops_content);
			TextView id = (TextView) _convertView.findViewById(R.id.row_stops_id);
			if (content != null) {
				content.setText(obj.getContent());
			}
			if (id != null) {
				id.setText(String.valueOf(obj.getId()));
			}
 		}
		
		return _convertView;
	}

}
