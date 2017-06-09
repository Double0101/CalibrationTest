package com.example.adouble.calibrationtest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Shape {
    private static final String JSON_SHAPE = "shape";

    public int name;

    public Shape(int n) {
        name = n;
    }

    public Shape(JSONObject json) throws JSONException {
        name = json.getInt(JSON_SHAPE);
    }
}
