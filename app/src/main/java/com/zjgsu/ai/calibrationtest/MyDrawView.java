package com.zjgsu.ai.calibrationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
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

    public MyDrawView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_draw_view, this);

        mContext = context;
        imageView = (ImageView) findViewById(R.id.myImageView);
        drawSurface = (DrawSurface) findViewById(R.id.myDrawImage);
        drawSurface.setImageView(imageView);
    }

    public void setIndex(int index) {
        mCalibration = CalibrationLab.get(mContext).getCalibrations().get(index);
    }

    private void initImage() {
        String path = mCalibration.getPhotoPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        imageView.setAdjustViewBounds(true);

    }

    private void setPoints() {
        if (mCalibration.getAreaArray() != null) {
            drawSurface.setPoints(mCalibration.getAreaArray());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        initImage();
        // 获取宽高
        getWH();
        setPoints();
    }

    private void getWH() {
        // 获得imageview的宽高
        int viewW = imageView.getWidth();
        int viewH = imageView.getHeight();

        int dw = imageView.getDrawable().getBounds().width();
        int dh = imageView.getDrawable().getBounds().height();

        Matrix m = imageView.getImageMatrix();
        float[] values = new float[10];

        m.getValues(values);

        float sx = values[0];
        float sy = values[4];

        float cw = dw * sx;
        float ch = dh * sy;

        float bdW = (viewW - cw) / 2;
        float bdH = (viewH - ch) / 2;

        float lW = viewW - bdW;
        float lH = viewH - bdH;

        drawSurface.setWH(bdW, bdH, lW, lH);
        Log.i("MyView", "w=" + viewW + "h=" + viewH);
        Log.i("MyScale", "w=" + cw + "h=" + ch);
    }

    public float[] getPoints() {
        return drawSurface.getPoints();
    }
}
