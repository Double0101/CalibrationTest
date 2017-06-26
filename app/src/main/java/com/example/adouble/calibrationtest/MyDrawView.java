package com.example.adouble.calibrationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Double on 2017/5/13.
 */

public class MyDrawView extends RelativeLayout {

    private ImageView imageView;

    private DrawSurface drawSurface;

    private Calibration mCalibration;

    private Context mContext;

    private int index;

    public MyDrawView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_draw_view, this);

        mContext = context;
        imageView = (ImageView) findViewById(R.id.myImageView);
        drawSurface = (DrawSurface) findViewById(R.id.myDrawImage);
    }

    public void initView() {
        mCalibration = CalibrationLab.get(mContext).getCalibrations().get(index);

        String path = mCalibration.getPhotoPath();
        setImageBitmap(path);
        if (mCalibration.getAreaArray() != null) {
            drawSurface.setPoints(mCalibration.getAreaArray());
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void setImageBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        imageView.setAdjustViewBounds(true);
    }

    public float[] getPoints() {
        return drawSurface.getPoints();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // 获取宽高

        initView();
    }
}
