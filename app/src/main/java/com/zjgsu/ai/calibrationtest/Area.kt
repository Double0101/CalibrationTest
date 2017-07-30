package com.zjgsu.ai.calibrationtest

import android.graphics.RectF
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Created by Double on 28/07/2017.
 */

class Area private constructor() {
    companion object {
        private val AREA: String = "area"
    }
    private var rectFs: Array<RectF>? = null
    private var gson: Gson? = null
    fun getRects() = rectFs!!

    constructor(json: JSONObject) : this() {
        var str: String = json.getString(AREA)
        gson = Gson()
        rectFs = gson!!.fromJson(str, Array<RectF>::class.java)
    }
    constructor(rects: Array<RectF>) : this() {
        gson = Gson()
        rectFs = rects
    }
    fun toJSON() : JSONObject {
        var json : JSONObject =  JSONObject()
        var str : String = gson!!.toJson(rectFs)
        json.put(AREA, str)
        return json
    }
}
