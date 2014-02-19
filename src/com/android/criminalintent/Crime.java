package com.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

    private Date date;
    private UUID id;
    private boolean solved;
    private String title;
    private Photo photo;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public Crime(JSONObject json) throws JSONException {

        id = UUID.fromString((String) json.get(JSON_ID));
        if (json.has(JSON_TITLE)) {
            title = (String) json.getString(JSON_TITLE);
        }
        if (json.has(JSON_DATE)) {
            date = new Date(json.getLong(JSON_DATE));
        }
        if (json.has(JSON_SOLVED)) {
            solved = json.getBoolean(JSON_SOLVED);
        }
        if (json.has(JSON_PHOTO)) {
            photo = new Photo(json.getJSONObject(JSON_PHOTO));
        }

    }

    public Date getDate() {
        return date;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;

    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, id);
        json.put(JSON_TITLE, title);
        json.put(JSON_SOLVED, solved);
        json.put(JSON_DATE, date.getTime());
        if (photo != null) {
            json.put(JSON_PHOTO, photo.toJSON());
        }
        return json;
    }

}
