package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Area {

    private static final String AREA = "area";

    private RectF[] rectFs;

    private Gson gson = new Gson();

    public Area(RectF[] points) {
        this.rectFs = points;
    }

    public RectF[] getRects() {
        return rectFs;
    }

    public Area(JSONObject json) throws JSONException {
        String str = json.getString(AREA);
        rectFs = gson.fromJson(str, RectF[].class);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        String str = gson.toJson(rectFs);
        json.put(AREA, str);

        return json;
    }

}
