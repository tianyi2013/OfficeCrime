package com.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
	
	private static final String JSON_FILENAME = "filename";
	private static final String JSON_ROTATION_DEGREE = "orientation";
	
	private String fileName;
	
	private int orientation;
	
	public Photo(String fileName, int orientation){
		this.fileName = fileName;
		this.orientation = orientation;
	}
	
	public Photo(JSONObject json) throws JSONException {
		fileName = json.getString(JSON_FILENAME);
		orientation = json.getInt(JSON_ROTATION_DEGREE);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, fileName);
		json.put(JSON_ROTATION_DEGREE, orientation);
		return json;
	}
	
	public String getFileName(){
		return fileName;
	}

	public int getOrientation() {
		return orientation;
	}

}
