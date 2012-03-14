package com.dalto.busDroid.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * This class manage the database.  It takes the database in the asset and
 * copy it to the android data.  When the apps is created, it check if the
 * database is correctly install.  If not it will re-copy it on anroid database.
 * 
 * You need to specified the name and the path to your data
 * 
 * This code source is not from me:
 * From: http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 */


public class Database extends SQLiteOpenHelper {
	 //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.dalto.busDroid/databases/";
 
    private static String DB_NAME = "BUS_DROID.db";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public Database(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
  
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}
    	catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    public void copyDataBase(InputStream _input) throws IOException{
 
		//By calling this method and empty database will be created into the default system path
        //of your application so we are going to be able to overwrite that database with our database.
    	this.getReadableDatabase();
    	
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream output = new FileOutputStream(outFileName);
 
    	//transfer bytes from the input file to the output file
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = _input.read(buffer))>0){
    		output.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	output.flush();
    	output.close();
    	_input.close();
    }
 
    public void openDataBase() throws SQLException {
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
}
