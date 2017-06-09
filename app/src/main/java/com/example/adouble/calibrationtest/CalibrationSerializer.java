package com.example.adouble.calibrationtest;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Double on 2017/4/25.
 */

public class CalibrationSerializer {

    private Context mContext;
    private String mFilename;

    public CalibrationSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public ArrayList<Calibration> loadCalibration() throws IOException, JSONException {
        ArrayList<Calibration> calibrations = new ArrayList<Calibration>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                jsonString.append(line);
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                calibrations.add(new Calibration(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                reader.close();
        }

        return calibrations;
    }

    public void saveCalibrations(ArrayList<Calibration> calibrations) throws JSONException, IOException{
        JSONArray array = new JSONArray();
        for (Calibration c : calibrations)
            array.put(c.toJSON());

        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
