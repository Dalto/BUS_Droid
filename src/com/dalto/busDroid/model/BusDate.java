package com.dalto.busDroid.model;

import java.util.Calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class that represent a type of schedule.  This class is mostly used 
 * to get the id of the day in the database and place it in other query
 * to get the exact time from a schedule.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class BusDate {
	
	private int currentDayValue = -1;
	private boolean isHoliday = false;
	
	/**
	 * Empty contructor
	 */
	public BusDate() {	
	}
	
	/**
	 * The method set the shedule object to another date and change
	 * the schedule id.
	 * 
	 * @param _nbToAddFromCurrentValue The number of day after today we want to set (Ex: Tomorrow = 1)
	 */
	public void setCurrentDayValue(int _nbToAddFromCurrentValue) {
		
		//Set to today
		Calendar date = Calendar.getInstance();
		//Setting to the correct date
		date.add(Calendar.DAY_OF_MONTH, _nbToAddFromCurrentValue);
		
		this.setSchedule(date);
	}
	
	/**
	 * This is setting the schedule to today.
	 */
	public void setCurrentDayValueToToday() {
		this.setSchedule(Calendar.getInstance());
	}
	
	/**
	 * Return the shedule id
	 * @return
	 */
	public int getCurrentDayValue() {
		if (this.currentDayValue == -1) {
			this.setCurrentDayValueToToday();
		}
		
		return this.currentDayValue;
	}
	
	/**
	 * Return true if the object is an holiday
	 * @return
	 */
	public boolean isHoliday() {
		return this.isHoliday;
	}
	
	/**
	 * This method set the shedule id.  This method use the value of the Java Calendar,
	 * so there should be an equivalent in the database.  In the table DAY we need to have
	 * the 7 days of the week.
	 * 
	 * @param _date
	 */
	private void setSchedule(Calendar _date) {
		
		//Is it an holiday
		this.currentDayValue = this.getHolidayDayValue(_date);
		if (this.currentDayValue == -1) {
			//If not
			Database db = DataManager.GetInstance().getDatabase();
			db.openDataBase();
			
			SQLiteDatabase sqlite = db.getReadableDatabase();
			
			Cursor c = sqlite.rawQuery("" +
					" SELECT" +
					"	DAY_VALUE" +
					" FROM" +
					"	DAY" +
					" WHERE" +
					"	DAY_CALENDAR = " + _date.get(Calendar.DAY_OF_WEEK) + ";", null);
			
			c.moveToFirst();
			if (c.isAfterLast() == false) {
				this.currentDayValue = c.getInt(0);			
			}
			else {
				//This should never happen
				this.currentDayValue = 1;
			}
			
			c.close();
			db.close();
		}

	}
	
	/**
	 * Return the Day Value of the holiday date passed by argument.  If it is not an
	 * holiday, it return -1.
	 * @param _date
	 * @return
	 * @see setSchedule
	 */
	private int getHolidayDayValue(Calendar _date) {
		//Open the database  maybe Try catch ?
		Database db = DataManager.GetInstance().getDatabase();
		db.openDataBase();
		int returnVal = -1;
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		Cursor c = sqlite.rawQuery("" +
				" SELECT" +
				"	DAY_VALUE" +
				" FROM" +
				"	HOLIDAY" +
				" WHERE" +
				"	HOLIDAY_DATE = " + _date.get(Calendar.YEAR) + "-" + _date.get(Calendar.MONTH) + "-" + _date.get(Calendar.DAY_OF_MONTH) + 
				";", null);
		
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			//This is an holiday :)
			returnVal = c.getInt(0);
			this.isHoliday = true;
		}
		else {
			//This is not an holiday :(
			this.isHoliday = false;
		}
		
		c.close();
		db.close();
		
		return returnVal;
	}
}
