package com.dalto.busDroid.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * This class represent a route.  This class contain a list of stop.
 * This class have 2 possible direction, but the user can chose one of them.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class Route extends BusObject{
	
	//The 2 possible direction
	private Direction directions[] = new Direction[2];
	//The direction chosen by the user
	private Direction direction = null;
	//The stopId when we get this route with from a stop 
	private int stopId;
	
	/**
	 * Constructor of Route.
	 * @param _routeName
	 * @param _noRoute
	 * @param _type Normally it is Route
	 */
	public Route(String _routeName, int _noRoute, BusObject.Type _type) {
		super(_routeName, _noRoute, _type);
	}
	
	/**
	 * Constructor of Route used when we create this route from a stop. (the user already chose the stop)
	 * @param _routeName
	 * @param _id
	 * @param _stopId
	 * @param _direction
	 * @param _type Normally it is RouteWithStop
	 */
	public Route(String _routeName, int _id, int _stopId, Direction _direction, BusObject.Type _type) {
		super(_routeName, _id, _type);
		this.direction = _direction;
		this.stopId = _stopId;
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public Route(Parcel _in) {
		super(_in);
		this.readFromParcel(_in);
	}

	/**
	 * This method will fill the list.
	 * First, if the list of direction has not been filled, it will fill it.
	 * Second, if the list of Stop / StopTime has not been filled, it will fill it
	 */
	@Override
	public void FillItems() {
		
		//Open the database  maybe Try catch ?
		Database db = DataManager.GetInstance().getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		
		//User did not chose a direction yet
		if (this.direction == null) {
			
			Cursor c = sqlite.rawQuery("" +
					" SELECT " +
					"	_ID, " +
					"	DIRECTION " +
					" FROM " +
					"	ROUTE " +
					" WHERE " +
					"	NO_ROUTE = " + this.id + " " +
					" ORDER BY " +
					"	_ID; ", null);
			
			c.moveToFirst();
			if (c.isAfterLast() == false) {
				this.directions[0] = new Direction(c.getString(1), c.getInt(0));
				c.moveToNext();
				
				if (c.isAfterLast() == false) {
					this.directions[1] = new Direction(c.getString(1), c.getInt(0));
				}
			}
			c.close();
		}
		//User did chose a direction and the route has not been filled
		else if (this.isFill != true && this.getType() == BusObject.Type.Route) {
			this.isFill = true;
			
			Cursor c = sqlite.rawQuery("" +
					" SELECT" +
					"	STO._ID," +
					"	STO.INTERSECTION," +
					"	IFNULL(STT.TIME, '-')" +
					" FROM" +
					"	STOP_TIME STT" +
					"		JOIN STOP STO" +
					"			ON STT.STOP_ID = STO._ID" +
					"		JOIN TRIP TRI" +
					"			ON STT.TRIP_ID = TRI._ID" +
					"				JOIN ROUTE ROU" +
					"					ON TRI.ROUTE_ID = ROU._ID" +
					" WHERE" +
					"	ROU._ID = " + this.direction.getId() +  " " + " AND " +
					" 	TRI.DAY_VALUE = " + DataManager.GetInstance().getBusDate().getCurrentDayValue() + 
					" ORDER BY" +
					"	STT.SEGMENT," +
					"	STT.TIME;", null);	
			
			c.moveToFirst();
			

			BusTime now = new BusTime();
			now.setToNow();
			
			while (c.isAfterLast() == false) {
				//We are getting the time by programmtion for performance issue
				int id = c.getInt(0);
				String stopName = c.getString(1);

				BusTime[] time = new BusTime[3];
				
				int i = 0;
				while (c.isAfterLast() == false && id == c.getInt(0)) {
					//Check if we have our 3 next time
					if (i < 3) {
						String tmp = c.getString(2);
						if (tmp != "-" &&
								Integer.parseInt(tmp.substring(0, 2)) >= Integer.parseInt(now.toString().substring(0, 2)) &&
								Integer.parseInt(tmp.substring(3, 5)) >= Integer.parseInt(now.toString().substring(3, 5))) {
							time[i] = new BusTime(tmp);
							++i;
						}
					}
					c.moveToNext();
				}
				
				//Adding the stop to the list
				Stop stop = new Stop(stopName,
						id,
						time,
						this.getDirection(),
						BusObject.Type.Stop);
				this.list.add(stop);
				
				c.moveToNext();
			}
			c.close();
		}
		else if (this.isFill != true && this.getType() == BusObject.Type.RouteWithStop) {
			//Query
			Cursor c = sqlite.rawQuery("" +
					" SELECT" +
					"	STT.TIME," +
					"	TRI._ID," + 
					" 	ROU.NO_ROUTE " +
					" FROM" +
					"	STOP_TIME STT" +
					"		JOIN STOP STO" +
					"			ON STT.STOP_ID = STO._ID " +
					"		JOIN TRIP TRI" +
					"			ON STT.TRIP_ID = TRI._ID" +
					"				JOIN ROUTE ROU" +
					"					ON TRI.ROUTE_ID = ROU._ID" +
					" WHERE" +
					"	STO._ID = " + this.stopId + " AND " +
					"	ROU.NO_ROUTE = " + this.getContent() + " AND " +
					" 	TRI.DAY_VALUE = " + DataManager.GetInstance().getBusDate().getCurrentDayValue() + 	
					" ORDER BY" +
					"	STT.TIME;", null);
		
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				//Adding to the list
				StopTime stopTime = new StopTime(c.getInt(2), c.getInt(1), new BusTime(c.getString(0)), this.getDirection());
				this.list.add(stopTime);
				
				c.moveToNext();
			}
			c.close();
			
			
		}
		
		
		//Close the database
		db.close();
	}

	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * Method to allow passing this object between activites.
	 */
	@Override
	public void writeToParcel(Parcel _out, int _flags) {
		super.writeToParcel(_out, _flags);
		_out.writeParcelable(this.directions[0],_flags);
		_out.writeParcelable(this.directions[1],_flags);
		_out.writeParcelable(this.direction, _flags);
		_out.writeInt(this.stopId);
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Route[size];
		}
	};
	
	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel _in) {
		int count = _in.readInt();
		for (int i = 0; i < count; ++i) {
			list.add((Stop) _in.readParcelable(Stop.class.getClassLoader()));
		}
		this.directions[0] = _in.readParcelable(Direction.class.getClassLoader());
		this.directions[1] = _in.readParcelable(Direction.class.getClassLoader());
		this.direction = _in.readParcelable(Direction.class.getClassLoader());
		this.stopId = _in.readInt();
	}
	
	/**
	 * Set the 2 directions
	 * @param _dir
	 */
	public void setDirections(Direction _dir[]) {
		this.directions = _dir;
	}
	
	/**
	 * Get the 2 directions
	 * @return
	 */
	public Direction[] getDirections() {
		return this.directions;
	}
	
	/**
	 * Set the chosen direction by the user
	 * @param _dir
	 */
	public void setDirection(Direction _dir) {
		this.direction = _dir;
	}
	
	/**
	 * Get the direction chosen by the user
	 * @return
	 */
	public Direction getDirection() {
		return this.direction;
	}
}
