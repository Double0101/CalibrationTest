package com.zjgsu.ai.calibrationtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


/**
 * Created by Double on 2017/4/26.
 */

public class CalibrationActivity extends AppCompatActivity {

    private MyDrawView myDrawView;

    int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calibration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.calibrationTool);
        setSupportActionBar(toolbar);

        myDrawView = (MyDrawView) findViewById(R.id.calibrationView);
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        myDrawView.setIndex(index);
    }

    public void touchFinish(View view) {
        CalibrationLab.get(getApplication()).getCalibrations()
                .get(index).setArea(new Area(myDrawView.getRects()));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calibration_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_magnifier_x1:
                myDrawView.setMultiple(1);
                break;
            case R.id.menu_magnifier_x2:
                myDrawView.setMultiple(2);
                break;
            case R.id.menu_magnifier_x3:
                myDrawView.setMultiple(3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
