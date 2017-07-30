package com.zjgsu.ai.calibrationtest

import android.content.Context
import android.util.Log

/**
 * Created by Double on 30/07/2017.
 */

class CalibrationLab private constructor(private var mAppContext: Context) {

    companion object {
        private val TAG = "CalibrationLab"
        private val FILENAME = "calibration.json"
        private lateinit var sCalibrationLab: CalibrationLab
        fun get(c: Context): CalibrationLab {
            if (sCalibrationLab == null)
                sCalibrationLab = CalibrationLab(c.applicationContext)
            return sCalibrationLab
        }
    }

    private var calbrations: ArrayList<Calibration>

    fun getCalibrations() = calbrations

    private var mSerializer: CalibrationSerializer

    init {
        mSerializer = CalibrationSerializer(mAppContext, FILENAME)
        try {
            calbrations = mSerializer.loadCalibration()
        } catch (e: Exception) {
            calbrations = ArrayList()
            Log.e(TAG, "Error loadig crimes: ", e)
        }
    }
    fun addCalibration(c: Calibration) { calbrations.add(c)}

    fun saveCalibrations(): Boolean {
        try {
            mSerializer.saveCalibrations(calbrations)
            Log.d(TAG, "calibrations saved to file")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving calibrations: ", e)
            return false
        }
    }
}