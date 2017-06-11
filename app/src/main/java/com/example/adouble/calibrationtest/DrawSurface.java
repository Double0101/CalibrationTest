package com.example.adouble.calibrationtest;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Double on 10/06/2017.
 */

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static boolean isLongTouched;

    private static final String TAG = "Calibration";

    private ArrayList<float[]> rects;

    private float[] points;

    private float height;

    private float width;

    private float left;

    private float top;

    private Paint mPaint;

    private Paint cPaint;

    private int index = -1;

    private int selectInt = -1;

    private float selectX;

    private float selectY;

    private Handler mHandle;

    private SurfaceHolder holder;

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
        initPaint();
        mHandle = new Handler();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setAlpha(200);
        cPaint = new Paint();
        cPaint.setColor(Color.YELLOW);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setStrokeWidth(8f);
        cPaint.setAlpha(200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG+"1", String.valueOf(event.getX()) + " " + String.valueOf(event.getY()));
                if (!isLongTouched) {
                    rects.add(new float[]{
                            event.getX(), event.getY(), 0, 0
                    });
                    selectX = event.getX();
                    selectY = event.getY();
                    index = rects.size() - 1;
                    Log.i("Down", String.valueOf(rects.size()));
                }
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLongTouched = true;
                        Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                        vib.vibrate(200);
                        Log.i("Handler", String.valueOf(rects.size()));
                        for (int i = 0; i < rects.size(); i++) {
                            if (rects.get(i)[0] < selectX
                                    && rects.get(i)[2] > selectX
                                    && rects.get(i)[1] < selectY
                                    && rects.get(i)[3] > selectY) {
                                selectInt = i;
                            }
                        }

                        if (selectInt != -1) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("删除该标定区域")
                                    .setMessage("您确定要删除该标定吗？")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("Left&Top", String.valueOf(left) + " " + String.valueOf(top));
                                            Log.i(TAG, "remove");
                                            Log.i(TAG, String.valueOf(selectInt));
                                            Log.i(TAG, String.valueOf(rects.size()));
                                            rects.remove(selectInt);
                                            selectInt = -1;
                                            dialog.cancel();
                                            invalidate();
                                            isLongTouched = false;
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Log.i("Left&Top", String.valueOf(left) + " " + String.valueOf(top));
                                            isLongTouched = false;
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(getContext(), "您未选任何标注区域", Toast.LENGTH_SHORT).show();
                            isLongTouched = false;
                        }

                    }
                }, 2000);


                break;

            case MotionEvent.ACTION_MOVE:
                if (!isLongTouched) {
                    Log.i(TAG, "MOVE");
                    Log.i(TAG, String.valueOf(event.getX()) + " " + String.valueOf(event.getY()));
                    if (Math.abs(rects.get(rects.size() - 1)[0] - event.getX()) > 10
                            || Math.abs(rects.get(rects.size() - 1)[1] - event.getY()) > 10) {
                        isLongTouched = false;
                        Log.i("Move", String.valueOf(rects.size()));
                        mHandle.removeCallbacksAndMessages(null);
                        if (event.getX() > width)
                            rects.get(index)[2] = width;
                        else
                            (rects.get(index))[2] = event.getX();
                        if (event.getY() > height)
                            rects.get(index)[3] = height;
                        else
                            (rects.get(index))[3] = event.getY();
                    }
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (rects.get(rects.size() - 1)[2] == 0
                        || rects.get(rects.size() - 1)[3] == 0
                        || Math.abs(rects.get(rects.size() - 1)[2] - rects.get(rects.size() - 1)[0]) < 10
                        || Math.abs(rects.get(rects.size() - 1)[3] - rects.get(rects.size() - 1)[1]) < 10
                        || rects.get(rects.size() - 1)[2] < rects.get(rects.size() - 1)[0]) {
                    Log.i(TAG, String.valueOf(isLongTouched));

                    rects.remove(rects.size() - 1);
                }
                Log.i("RECT", String.valueOf(rects.get(rects.size() - 1)[0]) +
                        " " + String.valueOf(rects.get(rects.size() - 1)[1]) +
                        " " + String.valueOf(rects.get(rects.size() - 1)[2]) +
                        " " + String.valueOf(rects.get(rects.size() - 1)[3]));
                index = -1;
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
