package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class Annotation {

    private static final String ANNOTATION_RECT = "annotation_rect";
    private static final String ANNOTATION_CATEGORY = "annotation_category";

    private String category;
    private MyRectF rectF;

    public Annotation(MyRectF rect) {
        this.rectF = rect;
    }

    public MyRectF getRect() {
        return rectF;
    }

    public void setCategory(int category) {
        this.category = AnnotatedImage.ANNOTATION_CATEGORY[category];
    }

    public String getCategory() {
        return category;
    }

    public Annotation(JSONObject json) throws JSONException {
        Gson gson = new Gson();
        rectF = gson.fromJson(json.getString(ANNOTATION_RECT), MyRectF.class);
        category = json.getString(ANNOTATION_CATEGORY);
    }

    public JSONObject toJSON() throws JSONException {
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        String str = gson.toJson(rectF);
        json.put(ANNOTATION_RECT, str);
        String c = gson.toJson(category);
        json.put(ANNOTATION_CATEGORY, c);

        return json;
    }

    public MyRectF getMinusBoundRect(MyPoint bound) {
        return new MyRectF(rectF.left + bound.getX(), rectF.top + bound.getY(), rectF.right + bound.getX(), rectF.bottom + bound.getY());
    }

    public void modifiedRect(int which, MyPoint point) {
        rectF.modified(which, point);
    }

    public void moveRect(MyRectF copy, float distanceX, float distanceY) {
        rectF.move(copy, distanceX, distanceY);
    }

    public boolean isCenter(MyPoint point) {
        return rectF.isCenter(point);
    }

    public int isPointMove(MyPoint point) {
        return rectF.isPointMove(point);
    }

}
