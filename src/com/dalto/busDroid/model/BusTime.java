package com.dalto.busDroid.model;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class represent the time.
 * This class make the time management easier.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class BusTime implements Parcelable{
	
	//Android Time
	private String time;
	private boolean negative = false;
	
	/**
	 * Empty constructor to create an empty BusTime
	 */
	public BusTime() {
	}
	
	/**
	 * Construct a Bustime with a string 
	 * @param _time
	 */
	public BusTime(String _time) {
		this.time = _time;
	}
	
	/**
	 * Construct a Bustime with a string 
	 * @param _time
	 * @param _negative If the time is negative
	 */
	public BusTime(String _time, boolean _negative) {
		this.negative = _negative;
		this.time = _time;
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public BusTime(Parcel _in) {
		this.readFromParcel(_in);
	}


	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Method to allow passing this object between activites.
	 */
	public void writeToParcel(Parcel _out, int _flags) {
		_out.writeString(this.time);
		if (this.negative == true) {
			_out.writeInt(1);
		}
		else {
			_out.writeInt(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BusTime createFromParcel(Parcel in) {
            return new BusTime(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new BusTime[size];
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
		this.time = _in.readString();
		int i = _in.readInt();
		if (i == 1) {
			this.negative = true;
		}
		else {
			this.negative = false;
		}
	}
	
	/**
	 * Return the time between two time
	 * @param _firstTime
	 * @param _secondTime2
	 * @return
	 */
	static public BusTime getTimeBetween(BusTime _firstTime, BusTime _secondTime2) {		

		Integer minute = _secondTime2.getMinute() - _firstTime.getMinute();
		Integer hour = _secondTime2.getHour() - _firstTime.getHour();
		
		//If minutes were bellow 0
		if (minute <= 0) {
			--hour;
			minute += 60;
		}
		
		boolean neg = false;
		//Negativep
		if (hour < 0) {
			neg = true;
		}
		return new BusTime(BusTime.format(hour, minute), neg);
	}
	
	/**
	 * Set the BusTime value to now
	 */
	public void setToNow() {
		Calendar c = Calendar.getInstance(); 
		int minute = c.get(Calendar.MINUTE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		
		
		this.time = BusTime.format(hour, minute);
	}
	
	
	/**
	 * Return the string value of the time.
	 * Format (HH:MM)
	 */
	public String toString() {
		return this.time;
	}
	
	/**
	 * Return the number of minute contained by this class
	 * @return
	 */
	public Integer toMinute() {
	
		Integer i = this.getHour() * 60 +
					this.getMinute();
		
		return i;
	}
	
	/**
	 * Return if the time is negative or not
	 * @return
	 */
	public boolean isNegative() {
		return this.negative;
	}
	
	/**
	 * Return the number of minute
	 * @return
	 */
	private Integer getMinute() {
		//If the time is negative we need to add one to the substring
		if (this.time.startsWith("-")) {
			return Integer.parseInt(this.time.substring(4), this.time.length());
		}
		else {	
			return Integer.parseInt(this.time.substring(3, this.time.length()));
		}
		
	}
	
	/**
	 * Return the number of hour
	 * @return
	 */
	private Integer getHour() {
		//If the time is negative we need to add one to the substring
		if (this.time.startsWith("-")) {
			return -(Integer.parseInt(this.time.substring(1, 3)));
		}
		else {
			return Integer.parseInt(this.time.substring(0, 2));
		}
		
	}
	
	/**
	 * This is formating to the right time format
	 * @param hour
	 * @param min
	 * @return
	 */
	private static String format(Integer hour, Integer min) {
		return String.format("%02d:%02d", hour, min);
	}

}
