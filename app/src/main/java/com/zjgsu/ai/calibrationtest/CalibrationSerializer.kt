package com.zjgsu.ai.calibrationtest

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*

/**
 * Created by Double on 30/07/2017.
 */

class CalibrationSerializer constructor(private var mContext: Context, private var mFilename: String){

    fun loadCalibration(): ArrayList<Calibration> {
        var calibrations: ArrayList<Calibration> = ArrayList()
        var reader: BufferedReader? = null
        try {
            var input = mContext.openFileInput(mFilename)
            reader = BufferedReader(InputStreamReader(input))
            var jsonString = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                jsonString.append(line)
                line = reader.readLine()
            }
            var array: JSONArray = JSONTokener(jsonString.toString()).nextValue() as JSONArray
            for (i in 0 until array.length())
                calibrations.add(Calibration(array.getJSONObject(i)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            reader!!.close()
        }
        return calibrations
    }

    fun saveCalibrations(calibrations: ArrayList<Calibration>) {
        var array: JSONArray = JSONArray()
        for (c in calibrations)
            array.put(c.toJSON())
        var writer: Writer? = null
        try {
            var out:OutputStream = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE)
            writer = OutputStreamWriter(out)
            writer.write(array.toString())
        } finally {
            writer!!.close()
        }
    }
}
