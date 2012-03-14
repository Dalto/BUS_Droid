package com.dalto.busDroid.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * This class represent the direction of a route.
 * 
 * @author Dalto (Simon Perreault)
 *
 */
public class Direction implements Parcelable{

	private int id;
	private String name;
	
	/**
	 * Constructor who take the name of the direction and
	 * the id of the bus who have this direction. 
	 * @param _name
	 * @param _idBus
	 */
	public Direction(String _name, int _idBus) {
		this.setId(_idBus);
		this.setName(_name);
	}
	
	/**
	 * Contructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public Direction(Parcel _in) {
		this.readFromParcel(_in);
	}
	
 
	/**
	 * Change the name of the direction
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the name of the direction
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the is of the bus.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the id of the bus
	 * @return
	 */
	public int getId() {
		return id;
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
		_out.writeInt(this.id);
		_out.writeString(this.name);
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Direction createFromParcel(Parcel in) {
            return new Direction(in);
        }

		@Override
		public Object[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Direction[size];
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
		this.id = _in.readInt();
		this.name = _in.readString();
	}
}
