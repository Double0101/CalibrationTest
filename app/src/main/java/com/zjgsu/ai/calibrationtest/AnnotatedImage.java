package com.zjgsu.ai.calibrationtest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Double on 2017/4/24.
 */

public class AnnotatedImage {

    private static final String JSON_ANNOTATION = "annotations";
    private static final String JSON_PHOTO = "path";

    private ArrayList<Annotation> mAnnotations;
    private Photo src;

    public AnnotatedImage(String path) {
        this.src = new Photo(path);
        mAnnotations = new ArrayList<>();
    }

    public AnnotatedImage(JSONObject json) throws JSONException {
        src = new Photo(json.getJSONObject(JSON_PHOTO));
        if (json.has(JSON_ANNOTATION)) {
            Gson gson = new Gson();
            mAnnotations = gson.fromJson(json.getString(JSON_ANNOTATION), new TypeToken<ArrayList<Annotation>>(){}.getType());
        } else {
            mAnnotations = new ArrayList<>();
        }
    }



    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_PHOTO, src.toJSON());
        if (mAnnotations != null) {
            Gson gson = new Gson();
            String str = gson.toJson(mAnnotations);
            json.put(JSON_ANNOTATION, str);
        }
        return json;
    }

    public void setSrc(Photo src) {
        this.src = src;
    }

    public void update(AnnotatedImage annotatedImage) {
        mAnnotations = annotatedImage.getAnnotations();
        src = annotatedImage.getSrc();
    }

    public void add(MyRectF rectF) {
        mAnnotations.add(new Annotation(rectF));
    }

    public Annotation get(int index) {
        return mAnnotations.get(index);
    }

    public int indexOf(Annotation ann) {
        return mAnnotations.indexOf(ann);
    }

    public void remove(int index) {
        mAnnotations.remove(index);
    }

    public void remove(Annotation anno) {
        mAnnotations.remove(anno);
    }

    public ArrayList<Annotation> getAnnotations() {
        return mAnnotations;
    }

    public boolean hasAnn() {
        if (mAnnotations == null || mAnnotations.size() == 0) return false;
        return true;
    }
    public Photo getSrc() {
        return src;
    }

    public int getSize() {
        return mAnnotations.size();
    }

    public String getPhotoPath() {
        return src.getPath();
    }

    @Override
    public String toString() {
        return src.toString();
    }
}
