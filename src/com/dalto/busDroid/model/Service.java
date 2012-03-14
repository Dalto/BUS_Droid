package com.dalto.busDroid.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represent a type of bus from your compagny. 
 * This class need a path to an image and contain a list of route.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class Service extends BusObject  {
	
	private String img_path;
	
	/**
	 * Constructor of Service
	 * @param _serviceName
	 * @param _id
	 * @param _img_path
	 */
	public Service(String _serviceName, int _id, String _img_path) {
		super(_serviceName, _id, BusObject.Type.Service);
		this.img_path = _img_path;
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public Service(Parcel _in) {
		super(_in);
		this.readFromParcel(_in);
	}

	/**
	 * This method fill the list with routes.
	 */
	@Override
	public void FillItems() {
		if (this.isFill != true) {
			this.isFill = true;
			
			//Open the database  maybe Try catch ?
			Database db = DataManager.GetInstance().getDatabase();
			db.openDataBase();
			
			SQLiteDatabase sqlite = db.getReadableDatabase();
			
			Cursor c = sqlite.rawQuery("" +
					" SELECT DISTINCT " +
					"	ROU.NO_ROUTE " +
					" FROM" +
					"	ROUTE ROU " +
					"		JOIN SERVICE SER " +
					"			ON ROU.SERVICE_ID = SER._ID " +
					" WHERE " +
					"	SER._ID = " + this.id + " " +
					" ORDER BY " +
					"	ROU.NO_ROUTE;", null);	
			
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				Route route = new Route(c.getInt(0) + "", c.getInt(0), BusObject.Type.Route);
				this.list.add(route);
				c.moveToNext();
			}
			
			//Close the database
			c.close();
			db.close();
		}
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
		//Image
		_out.writeString(this.img_path);
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Service[size];
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
		//Keep here to do the correct cast
		int count = _in.readInt();
		for (int i = 0; i < count; ++i) {
			this.list.add((Route) _in.readParcelable(Route.class.getClassLoader()));
		}
		//Image
		this.img_path = _in.readString();
		
	}
	
	/**
	 * Return the path to the image
	 * @return
	 */
	public String getImage() {
		return this.img_path;
	}
}
