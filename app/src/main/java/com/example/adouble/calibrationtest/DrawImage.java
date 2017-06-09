package com.example.adouble.calibrationtest;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.LogRecord;

/**
 * Created by Double on 2017/5/4.
 */

public class DrawImage extends ImageView {

    private static final String TAG = "Calibration";

    private static boolean isLongTouched;

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

    @Override
    protected void onDraw(Canvas canvas) {
        if (rects != null) {
            if (rects.size() > 0) {
                for (int i = 0; i < rects.size(); i++) {
                    Log.i("MainActivity", String.valueOf(i));
                    Log.i("MainActivity", String.valueOf(rects.size()));
                    float[] po = rects.get(i);
                    canvas.drawRect(po[0], po[1], po[2], po[3], mPaint);
                }
                if (index != -1) {
                    float[] p = rects.get(index);
                    canvas.drawRect(p[0], p[1], p[2], p[3], cPaint);
                }
                canvas.restore();
            }
        }
        super.onDraw(canvas);
    }

    public DrawImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        points = new float[4];
        rects = new ArrayList<float[]>();
        isLongTouched = false;
        Log.i("MainActivity", "DrawImage");
        initPaint();
        mHandle = new Handler();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
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
                        Log.i("Handler", String.valueOf(event.getX()) + " " + String.valueOf(event.getY()));
//                        long[] pattern = {1000,2000};
                        //两个参数，一个是自定义震动模式，
                        //数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。。。时长的单位是毫秒
                        //第二个是“是否反复震动”,-1 不重复震动
                        //第二个参数必须小于pattern的长度，不然会抛ArrayIndexOutOfBoundsException
//                        vib.vibrate(pattern, 1);
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

    public float[] getPoints() {
        points = new float[rects.size() * 4];
        for (int i = 0; i < rects.size(); i++) {
            points[4 * i] = (rects.get(i))[0];
            points[4 * i + 1] = (rects.get(i))[1];
            points[4 * i + 2] = (rects.get(i))[2];
            points[4 * i + 3] = (rects.get(i))[3];
        }

        return points;
    }

    public void setPoints(float[] points) {
        this.points = points;
        rects = new ArrayList<float[]>();
        for (int i = 0; i < (points.length + 1) / 4; i++) {
            rects.add(new float[]{points[4 * i], points[4 * i + 1],
                    points[4 * i + 2], points[4 * i + 3]});
        }
    }

    public void setHWLT(float height, float width, float left, float top) {
        this.height = height;
        this.width = width;
        this.left = left;
        this.top = top;
    }
}
