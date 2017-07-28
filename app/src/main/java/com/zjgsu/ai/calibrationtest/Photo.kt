package com.zjgsu.ai.calibrationtest

import org.json.JSONObject

/**
 * Created by Double on 28/07/2017.
 */
class Photo constructor(private val path: String) {
    companion object {
        private val JSON_FILENAME: String = "path"
    }

    constructor(json: JSONObject) : this(json.getString(JSON_FILENAME))

    fun getPath(): String { return path }

    fun toJSON(): JSONObject {
        var json: JSONObject = JSONObject()
        json.put(JSON_FILENAME, path)
        return json
    }
}