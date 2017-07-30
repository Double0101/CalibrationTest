package com.zjgsu.ai.calibrationtest

import org.json.JSONObject

/**
 * Created by Double on 30/07/2017.
 */

class Calibration private constructor() {

    companion object {
        private val JSON_CATEGORY: String = "category"
        private val JSON_AREA: String = "area"
        private val JSON_PHOTO: String = "path"

        private var count: Int = 0
    }

    private lateinit var category: String
    fun getCategory() = category
    fun setCategory(c: String) { category = c }

    private lateinit var mArea: Area
    fun getArea() = mArea
    fun setArea(area: Area) { mArea = area }

    private var src: Photo? = null
    fun setSrc(p: String) { src = Photo(p) }

    fun getPhotoPath() = src?.getPath()

    fun getAreaRects() = mArea?.getRects()

    constructor(category: String, path: String) : this() {
        this.category = category
        this.src = Photo(path)
    }

    constructor(json: JSONObject) : this() {
        category = json.getString(JSON_CATEGORY)
        src = Photo(json.getJSONObject(JSON_PHOTO))
        if (json.has(JSON_AREA))
            mArea = Area(json.getJSONObject(JSON_AREA))
    }

    fun toJSON(): JSONObject {
        var json: JSONObject = JSONObject()
        json.put(JSON_CATEGORY, category)
        json.put(JSON_PHOTO, src?.toJSON())
        json.put(JSON_AREA, mArea?.toJSON())

        return json
    }

    override fun toString() = category

}