package com.dalto.busDroid.model;

import java.util.LinkedList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Abstract class that represent an object for the application (Stop, Route, Service, ...)
 * The classe implements parcelable so it can be passe throught activities.
 * 
 * @author Simon Perreault
 *
 */
public abstract class BusObject implements Parcelable {
	
	/**
	 * Enum type for different object.
	 * Very useful when you want to know what is your object.
	 * 
	 * @author Simon Perreault
	 */
	public enum Type {
		Service,
		Route,
		RouteWithStop,
		Stop,
		StopTime,
		StopSearch
	}
	
	//Content to be displayed by the UI
	protected String mainContent;
	/*
	 * Id of the object in the database
	 * This is very useful for query
	 */
	protected int id;
	//Type of the object
	protected Type type;
	//To know if the object is fill or not (To not refill)
	protected boolean isFill = false;
	
	/*
	 * This list contain other object.
	 * Exemple : Stop contain StopTime
	 */
	protected LinkedList<BusObject> list = new LinkedList<BusObject>();
	
	/**
	 * Empty constructor for parcelable item
	 */
	public BusObject() {}
	
	/**
	 * Constructor of BusObject
	 * @param _mainContent 	The content to be displayed by the UI
	 * @param id			The id relative to the element in the database
	 * @param _type			The type of the object (Enum)
	 */
	public BusObject(String _mainContent, int id, Type _type) {
		this.mainContent = _mainContent;
		this.id = id;
		this.type = _type;
	}
	
	/**
	 * Constructor to build object from Parcelable
	 * @param _in	The parcelable object to read
	 */
	public BusObject(Parcel _in) {
		readFromParcel(_in);
	}
	
	/**
	 * Set the content (what is displayed by the UI)
	 * @param _mainContent 
	 */
	public void setContent(String _mainContent) {
		this.mainContent = _mainContent;
	}
	
	/**
	 * Return the main content of the object (what is displayed by the UI)
	 * @return
	 */
	public String getContent() {
		return mainContent;
	}
	
	/**
	 * Return the id of the object (Id of the object in the database)
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Return the type of the object
	 * @return
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Return the list that is contained by this object
	 * @return
	 */
	public LinkedList<BusObject> getList() {
		return this.list;
	}
	
	/**
	 * Set the list to the object
	 * @param _list
	 */
	public void setList(LinkedList< BusObject > _list) {
		this.list = _list;
		this.isFill = true;
	}
	
	/**
	 * Method to overwrite.
	 * This will fill the list of BusObject of this object by
	 * querying the database.
	 * Example : A Stop will be filled with StopTime
	 */
	public abstract void FillItems();
	
	/**
	 * Method to allow passing this object between activities.
	 */
	public void writeToParcel(Parcel _out, int _flags) {
		_out.writeInt(this.id);
		_out.writeString(this.mainContent);
		_out.writeString(this.type.name());
		_out.writeInt(this.list.size());
		for (BusObject obj: this.list) {
			_out.writeParcelable(obj, _flags);
		}
	}
	
	/**
	 * Method to read the parcel to transform it to an real BusObject
	 * @param _in 	The parcelable object to read
	 */
	private void readFromParcel(Parcel _in) {
		this.id = _in.readInt();
		this.mainContent = _in.readString();
		try {
			this.type = Type.valueOf(_in.readString());
		} catch (IllegalArgumentException x) {
			this.type = null;
		}
		
	}

}
