package com.dalto.busDroid.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * This class manage the general data of the application.  This class is used
 * to get the reference to the database.  It is also used to get the favorites or
 * the service.  This class manage the BusDate too.  I used it to keep everything
 * related to data without relation.
 * 
 * This class is a singleton
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class DataManager {

	private static final String BASE_URL = "http://kar0t.hostzi.com/";
	private static final String ACTION_DOWNLOAD = "?action=download";
	private static final String ACTION_UPDATE = "?action=update";
	private static final String ACTION_VERSION = "?action=checkVersion&version=";
	
	//The instace of this class
	static private DataManager instance;
	//The reference to the database
	private Database database = null;
	//The reference to the database
	private BusDate busDate = new BusDate();
	
	/**
	 * Empty constructor, private because this class use the singleton pattern
	 */
	private DataManager() {
		
	}
	
	/**
	 * Return the instance of this class and create it if
	 * it has not been.
	 * @return
	 */
	static public DataManager GetInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	/**
	 * This method return a list of all the service from
	 * the database
	 * @return
	 */
	public LinkedList<BusObject> getAllService() {
		//Open the database  maybe Try catch ?
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		LinkedList< BusObject > list = new LinkedList< BusObject >();
		
		Cursor c = sqlite.rawQuery(
				" SELECT " +
				"	_ID," +
				"	TYPE_SERVICE," +
				"	PATH_IMAGE" +
				" FROM" +
				"	SERVICE;", null);
		
		
		
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			//Adding to the list
			Service service = new Service(c.getString(1),
					c.getInt(0),
					c.getString(2));
			list.add(service);
			c.moveToNext();
		}
		
		//Close the database
		c.close();
		db.close();

		return list;
	}
	
	/**
	 * This method return a stop from the given stopId.  It return null if the id 
	 * is not in the database
	 * 
	 * @param _stopId
	 * @return null if the object has not been found
	 */
	public BusObject getStopFromId(String _stopId) {
		//Open the database  maybe Try catch ?
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		Stop stop = null;
		
		Cursor c = sqlite.rawQuery(
				" SELECT DISTINCT" +
				"	STO._ID," +
				"	STO.INTERSECTION" +
				" FROM" +
				"	STOP STO" +
				" WHERE" +
				"	STO._ID = " + _stopId + ";", null);
		
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			//We are creating the stop
			stop = new Stop(c.getString(1),
					c.getInt(0),
					BusObject.Type.StopSearch);
		}
		
		//Close the database
		c.close();
		db.close();

		return stop;
	}
	
	/**
	 * Return an HashMap of all the favorites of the user.
	 * The key is the title of the group and the value is a list
	 * of all BusObject in the group.
	 * 
	 * @param _sorting		The way the HashMap is generated (By Stop, By routes)
	 * @return
	 */
	public HashMap< String, LinkedList< BusObject > > getFavorites(String _sorting) {
		HashMap< String, LinkedList< BusObject > > hashMap = new HashMap< String, LinkedList< BusObject > >();
		
		//The query
		String query = "" +
				"SELECT" +
				"	ROU.NO_ROUTE," +
				"	STO._ID," +
				"	STO.INTERSECTION," +
				"	ROU.DIRECTION," +
				"	STT.TIME" +
				" FROM" +
				"	STOP_TIME STT " + 
				"		JOIN STOP STO " +
				"			ON STT.STOP_ID = STO._ID " +
				"		JOIN TRIP TRI " +
				"			ON STT.TRIP_ID = TRI._ID " +
				"				JOIN ROUTE ROU " +
				"					ON TRI.ROUTE_ID = ROU._ID " +
				"		JOIN FAVORITE FAV " +
				"			ON  STO._ID = FAV.STOP_ID AND ROU._ID = FAV.ROUTE_ID " +
				" WHERE " +
				" 	TRI.DAY_VALUE =  " + getBusDate().getCurrentDayValue();
				
		
		Log.i("BusDroid", _sorting);
		int indexToCheck = Integer.parseInt(_sorting);
		if (indexToCheck == 0) {
			query += " ORDER BY ROU.NO_ROUTE;";
		}
		else {
			query += " ORDER BY STO._ID;";
		}
		
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		//Doing the query
		Cursor c = sqlite.rawQuery(query, null);
		
		Log.i("BusDroid", String.valueOf(c.getCount()));
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			//Getting the sorting index
			Integer index = c.getInt(indexToCheck);
			String stopName = c.getString(2);
			LinkedList< BusObject > list = new LinkedList< BusObject >();
			
			//For each object in the same index
			while (c.isAfterLast() == false && index == c.getInt(indexToCheck)) {
				BusTime[] time = new BusTime[3];
				
				int noBus = c.getInt(0);
				int noStop = c.getInt(1);
				stopName = c.getString(2);
				String busDirection = c.getString(3);
				
				//For each same elements (Getting the time)
				int i = 0;
				while (c.isAfterLast() == false && noBus == c.getInt(0) && noStop == c.getInt(1)) {
					//Check if we have our 3 next time
					if (i < 3) {
						String tmp = c.getString(4);
						if (tmp != "-" &&
								Integer.parseInt(tmp.substring(0, 2)) >= 6 &&
								Integer.parseInt(tmp.substring(3, 5)) >= 30) {
							time[i] = new BusTime(tmp);
							++i;
						}
					}
					c.moveToNext();
				}
				
				//Adding it to the list
				if (indexToCheck == 0) {
					Stop stop = new Stop(stopName,
							noStop,
							time,
							new Direction(busDirection, noBus),
							BusObject.Type.Stop);
					list.add(stop);
				}
				else {
					Stop stop = new Stop(noBus + "",
							noStop,
							time,
							new Direction(busDirection, noBus),
							BusObject.Type.Stop);
					list.add(stop);
				}
				
				c.moveToNext();
			}
			
			//We put it in the hashmap
			if (indexToCheck == 0) {
				hashMap.put(index.toString(), list);
			}
			else {
				hashMap.put(stopName, list);
			}
			c.moveToNext();
		}
		
		c.close();
		db.close();

		return hashMap;
	}
	
	public void createDatabaseObject(Context _context) {
		this.database = new Database(_context);
	}
	
	/**
	 * This method try to download and create the database from the internet.  When this method is called, all the data will be erased.
	 * 
	 * this will call the webserver with these parameter ?action=download
	 * 
	 * @throws IOException
	 */
	public void downloadDatabase() throws IOException {
		Log.i("BusDroid", "1");
		
		URL urlDb = new URL(DataManager.BASE_URL + DataManager.ACTION_DOWNLOAD);
		Log.i("BusDroid", "2");
		HttpURLConnection connection = (HttpURLConnection) urlDb.openConnection();
		Log.i("BusDroid", "3");
		InputStream inputStream = connection.getInputStream();
		
		Log.i("BusDroid", "Got connection");
		this.getDatabase().copyDataBase(inputStream);
		Log.i("BusDroid", "end");
		connection.disconnect();
	}
	
	/**
	 * This method will update the database from the Internet.  All the data stored in the table
	 * FAVORITES will be keep.
	 * 
	 * this will call the webserver with these parameter ?action=update
	 * 
	 */
	public void updateDatabase() throws IOException {
		
		URL urlDb = new URL(DataManager.BASE_URL + DataManager.ACTION_UPDATE);
		HttpURLConnection connection = (HttpURLConnection) urlDb.openConnection();
		InputStream inputStream = connection.getInputStream();
		
		this.getDatabase().copyDataBase(inputStream);
		
		connection.disconnect();
	}
	
	
	
	/**
	 * Method that check if the database is up to date.  In this case we supposed that,
	 * the database already exist. If the database is updated, it will return true, false otherwise.
	 * 
	 * This will call the webserver with these parameter ?action=checkVersion&version=X.XX
	 * 
	 * @return
	 */
	public boolean checkIfDatabaseIsUpToDate() throws IOException {
		
		URL urlDb = new URL(DataManager.BASE_URL + DataManager.ACTION_VERSION + this.getDatabaseVersion());
		HttpURLConnection connection = (HttpURLConnection) urlDb.openConnection();
		InputStream inputStream = connection.getInputStream();
		boolean retval = false; 
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer stringBuffer = new StringBuffer();
		String line;

		while((line = bufferedReader.readLine()) != null) {

			stringBuffer.append(line);
	
			// Android 2.3 and more
			if(!bufferedReader.ready()) {
				break;
			}

		}
		
		Log.i("BusDroid", "StringBuffer : " + stringBuffer.toString());
		
		//Check the server answer
		if (stringBuffer.toString().startsWith("1")) {
			retval = true;
		};
		
		connection.disconnect();
		
		Log.i("BusDroid", "Update :" + retval);
		
		return retval;
		//return false; // Uncomment to make the apps always update
	}
	
	/**
	 * Return the value of the database contain in the sqlite database.  At this point, we supposed that the database has been fully created.
	 * @return The version number in String format
	 */
	private String getDatabaseVersion() {
		
		String retval = "0.001";
		String query = "" +
				" SELECT" +
				"	VERSION" +
				" FROM" +
				"	DB_CONFIG;";
		
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		//Doing the query
		Cursor c = sqlite.rawQuery(query, null);
		
		c.moveToFirst();
		
		if (!c.isAfterLast()) {
			retval = c.getString(0);
		}
		
		c.close();
		db.close();
		
		return retval;
	}
	
	/**
	 * This method will check if the database exist in the android system.  It will return
	 * true if it exist, false otherwise.  This method is useful to know if we need to 
	 * download a new database from the Internet.
	 *  
	 * @return
	 */
	public boolean checkIfDatabaseExist() {
		return this.getDatabase().checkDataBase();
	}
	
	/**
	 * return an instance of the database
	 * @return
	 */
	public Database getDatabase() {
		return this.database;		
	}
	
	/**
	 * Return true if the particular stop is already in the favorites of the user.
	 * Mostly use to display correctly the star when viewing a stop.
	 * 
	 * @param _stopId
	 * @param _busId
	 * @return
	 */
	public boolean isInFavorites(int _stopId, int _busId) {
		//Open the database  maybe Try catch ?
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getReadableDatabase();
		
		Cursor c = sqlite.rawQuery("" +
				"SELECT" +
				"	*" +
				" FROM" +
				"	FAVORITE" +
				" WHERE" +
				"	ROUTE_ID = " + _busId + " AND " +
				"	STOP_ID = " + _stopId + ";", null);
		
		c.moveToFirst();
		int count = c.getCount();
		c.close();
		db.close();
		
		//If nothing has been return by the query
		if (count == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * This is adding or removing a stop from the favourites.  It is mostly used
	 * with the checked value of the favourites star.
	 * 
	 * @param _add
	 * @param _stopId
	 * @param _busId
	 */
	public void addOrRemoveFavorites(boolean _add, int _stopId, int _busId) {
		
		boolean isInFavorite = this.isInFavorites(_stopId, _busId);
		
		//Open the database  maybe Try catch ?
		Database db = this.getDatabase();
		db.openDataBase();
		
		SQLiteDatabase sqlite = db.getWritableDatabase();

		String query = "";
		
		//We are sure to not add it 2 times
		if (_add == true && isInFavorite == false) {
			query = "INSERT INTO" +
					"	FAVORITE(_ID, ROUTE_ID, STOP_ID)" +
					" VALUES(NULL, " + _busId +", " + _stopId + ");";
			sqlite.execSQL(query);
		}
		else if (_add == false && isInFavorite == true) {
			query = "DELETE FROM" +
					"	FAVORITE" +
					" WHERE" +
					"	ROUTE_ID = " + _busId + " AND " +
					"	STOP_ID = " + _stopId + ";";
			sqlite.execSQL(query);
		}
		
		db.close();
	}
	
	/**
	 * return an instance of the BusDate
	 * @return
	 */
	public BusDate getBusDate() {
		return this.busDate;
	}
	
}
