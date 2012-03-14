package com.dalto.busDroid.model.adapter;

import java.util.LinkedList;

import com.dalto.busDroid.R;
import com.dalto.busDroid.model.BusObject;
import com.dalto.busDroid.model.BusTime;
import com.dalto.busDroid.model.Stop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StopsThreeNextAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private LinkedList< BusObject > list;
	
	public StopsThreeNextAdapter(Context _context, LinkedList< BusObject > _list) {
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
			_convertView = this.inflater.inflate(R.layout.row_stops_three_next, null);
		}
		
		BusObject obj = list.get(_position);
		if (obj != null && obj instanceof Stop) {
			Stop stop = (Stop) obj;
			
			TextView content = (TextView) _convertView.findViewById(R.id.row_stops_three_next_content);
			if (content != null) {
				content.setText(stop.getContent());
			}

			//Getting the time to now
			BusTime now = new BusTime();
			now.setToNow();
			
			//Get the next bus
			BusTime next[] = stop.getThreeNextBusTime();
			
			//Indicate the time when the next bus will pass from now
			TextView time1 = (TextView) _convertView.findViewById(R.id.row_stops_three_next_Time1);
			if (time1 != null && next[0] != null) {
				time1.setText(next[0].toString());
			}
			
			//Indicate the time when the second bus will pass from now
			TextView time2 = (TextView) _convertView.findViewById(R.id.row_stops_three_next_Time2);
			if (time2 != null && next[1] != null) {
				time2.setText(next[1].toString());
			}
			
			//Indicate the time when the third bus will pass from now
			TextView time3 = (TextView) _convertView.findViewById(R.id.row_stops_three_next_Time3);
			if (time3 != null && next[2] != null) {
				time3.setText(next[2].toString());
			}
			
			
			//Indicate the time left before the next bus (in minute)
			TextView restTime = (TextView) _convertView.findViewById(R.id.row_stops_three_next_RestTime);
			if (restTime != null && next[0] != null) {
				//Getting the difference between now and the next
				BusTime diff = BusTime.getTimeBetween(now, next[0]);
				
				if (!diff.isNegative()) {
					restTime.setText(diff.toMinute().toString()+ "m");	
				}
			}
 		}
		
		return _convertView;
	}

}
