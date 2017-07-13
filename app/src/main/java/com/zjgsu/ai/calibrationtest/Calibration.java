package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Calibration {

    private static final String JSON_CATEGORY = "category";
    private static final String JSON_AREA = "area";
    private static final String JSON_PHOTO = "path";

    private static int count = 0;

    private String category;
    private Area mArea;
    private Photo src;

    public String getCategory() {
        return category;
    }

    public Calibration(String category, String path) {
        this.category = category;
        this.src = new Photo(path);
    }

    public Calibration(JSONObject json) throws JSONException {
        category = json.getString(JSON_CATEGORY);
        src = new Photo(json.getJSONObject(JSON_PHOTO));
        if (json.has(JSON_AREA))
            mArea = new Area(json.getJSONObject(JSON_AREA));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_CATEGORY, category);
        json.put(JSON_PHOTO, src.toJSON());
        if (mArea != null)
            json.put(JSON_AREA, mArea.toJSON());
        return json;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSrc(Photo src) {
        this.src = src;
    }

    public void setArea(Area area) {
        this.mArea = area;
    }

    public Area getArea() {
        return mArea;
    }

    public RectF[] getAreaRects() {
        if (mArea != null) {
            return mArea.getRects();
        } else
            return null;
    }

    public String getPhotoPath() {
        return src.getPath();
    }

    @Override
    public String toString() {
        return category;
    }
}
