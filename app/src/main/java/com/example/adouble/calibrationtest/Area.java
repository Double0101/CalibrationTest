package com.example.adouble.calibrationtest;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Area {

    private static final String AREA = "area";

    private float[] points;

    private Gson gson = new Gson();

    public Area(float[] points) {
        this.points = points;
    }

    public float[] getArray() {
        return points;
    }

    public Area(JSONObject json) throws JSONException {
        String str = json.getString(AREA);
        points = gson.fromJson(str, float[].class);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        String str = gson.toJson(points);
        json.put(AREA, str);

        return json;
    }

}
