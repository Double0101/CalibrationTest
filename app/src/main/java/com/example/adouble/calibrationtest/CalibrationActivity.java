package com.example.adouble.calibrationtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


/**
 * Created by Double on 2017/4/26.
 */

public class CalibrationActivity extends AppCompatActivity {

    private MyDrawView myDrawView;

    private Bitmap bitmap;

    int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calibration);
        myDrawView = (MyDrawView) findViewById(R.id.calibrationView);
        Intent intent = getIntent();
        final String path = intent.getStringExtra("photo_path");
        index = intent.getIntExtra("index", 0);
        bitmap = BitmapFactory.decodeFile(path);
        myDrawView.setImageBitmap(bitmap);
        if (intent.getFloatArrayExtra("points") != null) {
            myDrawView.setPoints(intent.getFloatArrayExtra("points"));
        }
    }

    public void touchFinish(View view) {
        float[] points = myDrawView.getPoints();
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putFloatArray("points", points);
        data.putExtra("bundle2", bundle);
        setResult(RESULT_OK, data);

        finish();
    }

}
