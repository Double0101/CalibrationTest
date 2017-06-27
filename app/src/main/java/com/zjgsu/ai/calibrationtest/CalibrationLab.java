package com.zjgsu.ai.calibrationtest;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Double on 2017/4/24.
 */

public class CalibrationLab {
    private static final String TAG = "CailbrationLab";
    private ArrayList<Calibration> calibrations;
    private static CalibrationLab sCalibrationLab;
    private static final String FILENAME = "calibrations.json";
    private CalibrationSerializer mSerializer;

    private Context mAppContext;

    public static CalibrationLab get(Context c) {
        if (sCalibrationLab == null) {
            sCalibrationLab = new CalibrationLab(c.getApplicationContext());
        }

        return sCalibrationLab;
    }

    public ArrayList<Calibration> getCalibrations() {
        return calibrations;
    }

    private CalibrationLab(Context appContext) {
        mAppContext = appContext;
        calibrations = new ArrayList<Calibration>();
        mSerializer = new CalibrationSerializer(mAppContext, FILENAME);

//        for (int i = 0; i < 10; i++) {
//            Calibration c = new Calibration("kdsahf", "hrueiogh");
//            calibrations.add(c);
//        }
        try {
            calibrations = mSerializer.loadCalibration();
        } catch (Exception e) {
            calibrations = new ArrayList<Calibration>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public void addCalibration(Calibration c) {
        calibrations.add(c);
    }

    public boolean saveCalibrations() {
        try {
            mSerializer.saveCalibrations(calibrations);
            Log.d(TAG, "calibrations saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving calibrations: ", e);
            return false;
        }
    }
}
