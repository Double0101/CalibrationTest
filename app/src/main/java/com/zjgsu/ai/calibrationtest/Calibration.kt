package com.zjgsu.ai.calibrationtest

/**
 * Created by Double on 30/07/2017.
 */

class Calibration {

    companion object {
        private val JSON_CATEGORY: String = "category"
        private val JSON_AREA: String = "area"
        private val JSON_PHOTO: String = "path"

        private var count: Int = 0
    }

    private var category: String? = null
    private var mArea: Area? = null
    private var src: Photo? = null



}
//public class Calibration {
//    public String getCategory() {
//        return category;
//    }
//
//    public Calibration(String category, String path) {
//        this.category = category;
//        this.src = new Photo(path);
//    }
//
//    public Calibration(JSONObject json) throws JSONException {
//        category = json.getString(JSON_CATEGORY);
//        src = new Photo(json.getJSONObject(JSON_PHOTO));
//        if (json.has(JSON_AREA))
//            mArea = new Area(json.getJSONObject(JSON_AREA));
//    }
//
//    public JSONObject toJSON() throws JSONException {
//        JSONObject json = new JSONObject();
//        json.put(JSON_CATEGORY, category);
//        json.put(JSON_PHOTO, src.toJSON());
//        if (mArea != null)
//            json.put(JSON_AREA, mArea.toJSON());
//        return json;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public void setSrc(Photo src) {
//        this.src = src;
//    }
//
//    public void setArea(Area area) {
//        this.mArea = area;
//    }
//
//    public Area getArea() {
//        return mArea;
//    }
//
//    public RectF[] getAreaRects() {
//        if (mArea != null) {
//            return mArea.getRects();
//        } else
//            return null;
//    }
//
//    public String getPhotoPath() {
//        return src.getPath();
//    }
//
//    @Override
//    public String toString() {
//        return category;
//    }
//}
