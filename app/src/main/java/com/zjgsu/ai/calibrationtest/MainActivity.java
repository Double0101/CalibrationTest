package com.zjgsu.ai.calibrationtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView listView;

    private ArrayList<Calibration> mCalibrations;

    private ArrayAdapter<Calibration> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calibration);
        mCalibrations = CalibrationLab.get(this).getCalibrations();
        Log.i(TAG, Integer.toString(mCalibrations.size()));

        listView = (ListView) findViewById(R.id.calibrationlist);

        adapter = new ArrayAdapter<Calibration>(this,
                android.R.layout.simple_list_item_1, mCalibrations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, CalibrationActivity.class);
                intent.putExtra("index", position);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calibration_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CalibrationLab.get(this).saveCalibrations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_button:
                Intent intent = new Intent(MainActivity.this, CaliPreActivity.class);
                startActivityForResult(intent, 1);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle bundle = data.getBundleExtra("bundle1");
                Log.i(TAG, bundle.getString("category"));
                Log.i(TAG, bundle.getString("path"));
                Calibration calibration = new Calibration(bundle.getString("category"), bundle.getString("path"));
                Log.i(TAG, calibration.toString());
                CalibrationLab.get(this).addCalibration(calibration);
                Log.i(TAG, Integer.toString(mCalibrations.size()));
            }
            else if (requestCode == 2) {
                Bundle bundle = data.getBundleExtra("bundle2");
                mCalibrations.get(bundle.getInt("index")).setArea(new Area(
                        bundle.getFloatArray("points")));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
