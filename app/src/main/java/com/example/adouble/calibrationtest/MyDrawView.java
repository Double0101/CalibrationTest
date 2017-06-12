package com.example.adouble.calibrationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Double on 2017/5/13.
 */

public class MyDrawView extends FrameLayout {

    private int time = 0;

    private ImageView imageView;

    private DrawSurface drawImage;

    private float height;

    private float width;

    private float left;

    private float top;

    public MyDrawView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_draw_view, this);

        imageView = (ImageView) findViewById(R.id.myImageView);
        drawImage = (DrawSurface) findViewById(R.id.myDrawImage);
    }

    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);

    }

    public float[] getPoints() {
        return drawImage.getPoints();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (time == 0) {
            height = imageView.getHeight();
            width = imageView.getWidth();
            left = imageView.getLeft();
            top = imageView.getTop();
//            drawImage.setHWLT(height, width, left, top);
            Log.i("MyDrawView", String.valueOf(height));
            Log.i("Left&Top", String.valueOf(left) + " " + String.valueOf(top));
            time++;
        }
    }

    public void setPoints(float[] points) {
        drawImage.setPoints(points);
    }
}
