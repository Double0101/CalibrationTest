package com.example.adouble.calibrationtest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Photo {

    private static final String JSON_FILENAME = "path";

    private String path;

    public Photo(String name) {
        path = name;
    }

    public String getPath() {
        return path;
    }

    public Photo(JSONObject json) throws JSONException {
        path = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, path);
        return json;
    }
}
