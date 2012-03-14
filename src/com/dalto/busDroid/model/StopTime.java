package com.dalto.busDroid.model;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * This class represent a Time from a stop.  It used to give all time a bus will
 * pass to this stop.  It contain a list of stop (The full traject if you are taking the bus
 * at that time).
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class StopTime extends BusObject {

	//The time
	private BusTime time;
	//The nubmer of the bus passing here (Used to get the full traject from a time)
	private int busNumber;
	//The direction of the bus
	private Direction direction;
	
	/**
	 * Constructor of StopTime
	 * @param _busNumber
	 * @param _idTraject
	 * @param _time
	 * @param _direction
	 */
	public StopTime(int _busNumber, int _idTraject, BusTime _time, Direction _direction) {
		super(_time.toString(), _idTraject, BusObject.Type.StopTime);
		this.time = _time;
		this.busNumber =  _busNumber;
		this.direction = _direction;
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public StopTime(Parcel _in) {
		super(_in);
		readFromParcel(_in);
	}
	
	/**
	 * Fill the list of Stop.  It give the full traject of the bus at this time.
	 */
	@Override
	public void FillItems() {
		
		if (this.isFill != true) {
			this.isFill = true;
			
			//Open the database maybe Try catch ?
			Database db = DataManager.GetInstance().getDatabase();
			db.openDataBase();
			
			SQLiteDatabase sqlite = db.getReadableDatabase();		
			
			//Query
			Cursor c = sqlite.rawQuery("" +
					" SELECT " +
					"	STO._ID, " +
					"	STO.INTERSECTION, " +
					"	STT.TIME " +
					" FROM " +
					"	STOP_TIME STT " +
					"		JOIN STOP STO " +
					"			ON STT.STOP_ID = STO._ID " +
					" WHERE " +
					"	STT.TRIP_ID = " + this.getId() +
					" ORDER BY " +
					"	STT.SEGMENT;", null);
		
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				//Adding to the list
				BusTime time[] = { new BusTime(c.getString(2)) };
				Stop stop = new Stop(c.getString(1),
						c.getInt(0),
						time,
						BusObject.Type.Stop);
				
				this.list.add(stop);
				
				c.moveToNext();
			}
			
			//Close the database	
			c.close();
			db.close();
		
		}
	}
	
	/**
	 * Return the bus number
	 * @return
	 */
	public int getBusNumber() {
		return this.busNumber;
	}
	
	/**
	 * Return the direction
	 * @return
	 */
	public Direction getDirection() {
		return this.direction;
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
		//StopTime
		_out.writeParcelable(this.time, _flags);
		_out.writeInt(this.busNumber);
		_out.writeParcelable(this.direction, _flags);

	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StopTime createFromParcel(Parcel in) {
            return new StopTime(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new StopTime[size];
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
		//Stops
		int count = _in.readInt();
		for (int i = 0; i < count; ++i) {
			this.list.add((Stop) _in.readParcelable(Stop.class.getClassLoader()));
		}
		//StopTime
		this.time = _in.readParcelable(BusTime.class.getClassLoader());
		this.busNumber = _in.readInt();
		this.direction = _in.readParcelable(Direction.class.getClassLoader());
	}
}
