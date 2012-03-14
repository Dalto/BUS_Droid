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
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class FavoritesAdapter extends BaseExpandableListAdapter{

	private LayoutInflater inflater;
	private Context context;
	private String sorting;
	private LinkedList<String> groups;
	//Need stop to work
	private LinkedList<LinkedList<BusObject>> children;
	
	public FavoritesAdapter(Context _context, LinkedList<String> _groups, LinkedList<LinkedList<BusObject>> _children, String _sorting) {
		this.context = _context;
		this.inflater = LayoutInflater.from(_context);
		this.groups = _groups;
		this.children = _children;
		this.sorting = _sorting;
	}
	
	public BusObject getChild(int _groupPosition, int _childPosition) {
		return this.children.get(_groupPosition).get(_childPosition);
	}

	public long getChildId(int _groupPosition, int _childPosition) {
		return _childPosition;
	}

	public View getChildView(int _groupPosition, int _childPosition,
			boolean _isLastChild, View _convertView, ViewGroup _parent) {
		if (_convertView == null) {
			_convertView = this.inflater.inflate(R.layout.row_stops_three_next, null);
		}
		
		BusObject obj = this.children.get(_groupPosition).get(_childPosition);
		if (obj != null && obj instanceof Stop) {
			Stop stop = (Stop) obj;
			
			TextView content = (TextView) _convertView.findViewById(R.id.row_stops_three_next_content);
			if (content != null) {
				if (Integer.parseInt(this.sorting) == 0) {
					content.setText(stop.getContent());
				}
				else {
					//Write the correct route
					content.setText(this.context.getString(R.string.route, stop.getContent()));	
				}
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

	public int getChildrenCount(int _groupPosition) {
		return children.get(_groupPosition).size();
	}

	public Object getGroup(int _groupPosition) {
		return groups.get(_groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int _groupPosition) {
		return _groupPosition;
	}

	public View getGroupView(int _groupPosition, boolean _isExpanded,
			View _convertView, ViewGroup _parent) {
		
		if (_convertView == null) {
			_convertView = this.inflater.inflate(R.layout.favorites_group_row, null);
		}
		
        String name = this.groups.get(_groupPosition);
        TextView content = (TextView) _convertView.findViewById(R.id.favoritesRowGroupText);
		if (content != null) {
			if (Integer.parseInt(this.sorting) == 0) {
				content.setText(this.context.getString(R.string.route, name));
			}
			else {
				content.setText(name);
			}
		}
		
        return _convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int _groupPosition, int _childPosition) {
		return true;
	}


}
