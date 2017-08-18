package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Annotation {

    private static final String ANNOTATION = "annotation";

    private MyRectF[] rectFs;

    private Gson gson = new Gson();

    public Annotation(MyRectF[] points) {
        this.rectFs = points;
    }

    public MyRectF[] getRects() {
        return rectFs;
    }

    public Annotation(JSONObject json) throws JSONException {
        String str = json.getString(ANNOTATION);
        rectFs = gson.fromJson(str, MyRectF[].class);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        String str = gson.toJson(rectFs);
        json.put(ANNOTATION, str);

        return json;
    }

}
