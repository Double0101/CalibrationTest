package com.zjgsu.ai.calibrationtest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Double on 2017/4/24.
 */

public class AnnotatedImage {

    private static final String JSON_CATEGORY = "category";
    private static final String JSON_ANNOTATION = "annotation";
    private static final String JSON_PHOTO = "path";

    private String category;
    private Annotation mAnnotation;
    private Photo src;

    public String getCategory() {
        return category;
    }

    public AnnotatedImage(String category, String path) {
        this.category = category;
        this.src = new Photo(path);
    }

    public AnnotatedImage(JSONObject json) throws JSONException {
        category = json.getString(JSON_CATEGORY);
        src = new Photo(json.getJSONObject(JSON_PHOTO));
        if (json.has(JSON_ANNOTATION))
            mAnnotation = new Annotation(json.getJSONObject(JSON_ANNOTATION));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_CATEGORY, category);
        json.put(JSON_PHOTO, src.toJSON());
        if (mAnnotation != null)
            json.put(JSON_ANNOTATION, mAnnotation.toJSON());
        return json;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSrc(Photo src) {
        this.src = src;
    }

    public void setAnnotation(Annotation annotation) {
        this.mAnnotation = annotation;
    }

    public Annotation getAnnotation() {
        return mAnnotation;
    }

    public MyRectF[] getAnnotationRects() {
        if (mAnnotation != null) {
            return mAnnotation.getRects();
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
