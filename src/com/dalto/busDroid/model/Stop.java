package com.dalto.busDroid.model;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * This class represent a stop.  It contain a list
 * of BusTime or a list of Route depending the type of stop.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class Stop extends BusObject {
	
	//The 3 next time the bus will pass from now
	private BusTime[] time = new BusTime[3];
	//The direction of the stop (or the bus who is passing to this stop)
	private Direction direction;
	
	/**
	 * Constructor of Stop.  Used to represent a Stop from a Routes
	 * @param _stopName
	 * @param _id
	 * @param _nextTime
	 * @param _direction
	 * @param _type
	 */
	public Stop(String _stopName, int _id, BusTime[] _nextTime, Direction _direction, BusObject.Type _type) {
		super(_stopName, _id, _type);
		this.time = _nextTime;
		this.direction = _direction;
	}
	
	/**
	 * Constructor of Stop.  Used to represent a Stop when fellowing a traject (See BusTime)
	 * @param _stopName
	 * @param _id
	 * @param _time
	 * @param _type
	 */
	public Stop(String _stopName, int _id, BusTime[] _time, BusObject.Type _type) {
		super(_stopName, _id, _type);
		this.time = _time;
	}
	
	/**
	 * Constructor of Stop.  Used to représent a list of route from a stop.
	 * @param _stopName
	 * @param _id
	 * @param _type
	 */
	public Stop(String _stopName, int _id, BusObject.Type _type) {
		super(_stopName, _id, _type);
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public Stop(Parcel _in) {
		super(_in);
		this.readFromParcel(_in);
	}


	/**
	 * Fill the list with BusTime if the type is Stop and
	 * with Route if the type is StopSearch.
	 */
	@Override
	public void FillItems() {
		
		this.list.clear();

		//Open the database maybe Try catch ?
		Database db = DataManager.GetInstance().getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		if (this.getType() == BusObject.Type.Stop) {
			this.isFill = true;
			
			//Query
			Cursor c = sqlite.rawQuery("" +
					" SELECT" +
					"	STT.TIME, " +
					"	TRI._ID," + 
					" 	ROU.NO_ROUTE " +
					" FROM" +
					"	STOP_TIME STT " +
					"		JOIN STOP STO " +
					"			ON STT.STOP_ID = STO._ID" +
					"		JOIN TRIP TRI" +
					"			ON STT.TRIP_ID = TRI._ID" +
					"				JOIN ROUTE ROU" +
					"					ON TRI.ROUTE_ID = ROU._ID" +
					" WHERE" +
					"	STO._ID = " + this.getId() + " AND " +
					"	ROU._ID = " + this.getDirection().getId() + " AND " +
					" 	TRI.DAY_VALUE = " + DataManager.GetInstance().getBusDate().getCurrentDayValue() + 	
					" ORDER BY" +
					"	STT.TIME;", null);
		
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				//Adding it to the list
				StopTime stopTime = new StopTime(c.getInt(2), c.getInt(1), new BusTime(c.getString(0)), this.getDirection());
				this.list.add(stopTime);
				
				c.moveToNext();
			}
			c.close();

		}
		else if (this.getType() == BusObject.Type.StopSearch){
			this.isFill = true;
			
			//Query
			Cursor c = sqlite.rawQuery("" +
					" SELECT DISTINCT" +
					"	ROU._ID," +
					"	ROU.DIRECTION," +
					"	ROU.NO_ROUTE," +
					" 	STO._ID	"	+
					" FROM" +
					"	STOP_TIME STT" +
					"		JOIN STOP STO" +
					"			ON STT.STOP_ID = STO._ID" +
					"		JOIN TRIP TRI" +
					"			ON STT.TRIP_ID = TRI._ID" +
					"				JOIN ROUTE ROU" +
					"					ON TRI.ROUTE_ID = ROU._ID" +
					" WHERE" +
					"	STO._ID = " + this.getId() +
					" ORDER BY" +
					"	ROU.NO_ROUTE;", null);
		
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				//Adding it to the list
				Route route = new Route(c.getInt(2) + "",
						c.getInt(2),
						c.getInt(3),
						new Direction(c.getString(1),c.getInt(0)),
						BusObject.Type.RouteWithStop);
				this.list.add(route);
				
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
		//Route
		_out.writeParcelable(this.direction, _flags);
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Stop[size];
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
		//Times
		int count = _in.readInt();
		for (int i = 0; i < count; ++i) {
			this.list.add((StopTime) _in.readParcelable(StopTime.class.getClassLoader()));
		}
		this.direction = _in.readParcelable(Direction.class.getClassLoader());
	}
	
	/**
	 * Return the three next time the bus will pass to this stop
	 * @return
	 */
	public BusTime[] getThreeNextBusTime() {
		return this.time;
	}
	
	/**
	 * Return the next time the bus will pass to this stop.  Used to contain a
	 * time when fellowing a traject.
	 * @return
	 */
	public BusTime getTime() {
		return this.time[0];
	}
	
	/**
	 * Return the direction of the bus passing to this stop.
	 * @return
	 */
	public Direction getDirection() {
		return this.direction;
	}


}
