package com.example.adouble.calibrationtest;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
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
import java.util.Calendar;

/**
 * Created by Double on 10/06/2017.
 */

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static boolean isLongTouched;

    private static boolean RUNNING = false;

    private static final String TAG = "Calibration";

    private static final int MIN_CLICK_DURATION = 2000;

    private ArrayList<float[]> rects;

    private float[] points;

    private float aW;

    private float aH;

    private float bW;

    private float bH;

    private Paint mPaint;

    private Paint cPaint;

    private int index = -1;

    private int selectInt = -1;

    private long startClickTime = 0;

    private float selectX;

    private float selectY;

    private int pass = 0;

    private Handler mHandle;

    private SurfaceHolder holder;

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setZOrderOnTop(true);
        points = new float[4];
        rects = new ArrayList<float[]>();
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
                if (!isLongTouched && isInArea(event.getX(), event.getY())) {
                    rects.add(new float[]{
                            event.getX(), event.getY(), 0, 0
                    });
                    selectX = event.getX();
                    selectY = event.getY();
                    pass = 1;
                    isLongTouched = false;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    index = rects.size() - 1;
                    Log.i("Down", String.valueOf(rects.size()));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pass == 1) {
                    if (Math.abs(rects.get(rects.size() - 1)[0] - event.getX()) > 10
                            || Math.abs(rects.get(rects.size() - 1)[1] - event.getY()) > 10) {
                        isLongTouched = false;
                        mHandle.removeCallbacksAndMessages(null);
                        Log.i("Move", "X" + event.getX() + "bX" + bW);
                        Log.i("Move", "Y" + event.getY() + "bY" + bH);
                        if (event.getX() > bW) {
                            rects.get(index)[2] = bW;
                            Log.i("Move", 1 + "");
                        }
                        else
                            (rects.get(index))[2] = event.getX();
                        if (event.getY() > bH) {
                            rects.get(index)[3] = bH;
                            Log.i("Move", 2 + "");
                        }
                        else
                            (rects.get(index))[3] = event.getY();
                    } else {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration > MIN_CLICK_DURATION) {
                            isLongTouched = true;
                        } else {
                            isLongTouched = false;
                        }
                    }

                    if (isLongTouched) {
                        Log.d("ahsjdahskjdhaksjd", "执行了这个语句第nnn次");
                        Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                        vib.vibrate(200);
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
                                            rects.remove(selectInt);
                                            selectInt = -1;
                                            dialog.cancel();
                                            invalidate();
                                            isLongTouched = false;
                                        }
                                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    isLongTouched = false;
                                }
                            }).show();
                        } else {
                            Toast.makeText(getContext(), "您未选任何标注区域", Toast.LENGTH_SHORT).show();
                            isLongTouched = false;
                        }
                    } 
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (pass == 1) {
                    if (rects.get(rects.size() - 1)[2] == 0
                            || rects.get(rects.size() - 1)[3] == 0
                            || rects.get(rects.size() - 1)[2] - rects.get(rects.size() - 1)[0] < 10
                            || rects.get(rects.size() - 1)[3] - rects.get(rects.size() - 1)[1] < 10) {
                        rects.remove(rects.size() - 1);
                    }
                }
                pass = 0;
                index = -1;
                isLongTouched = false;
                break;
        }
        return true;
    }

    private boolean isInArea(float x, float y) {
        if (x > aW && x < bW && y > aH && y < bH)
            return true;
        return false;
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        RUNNING = true;
//        new Thread(new DrawThread()).start();
    }

    class DrawThread implements Runnable {

        @Override
        public void run() {
            int SLEEP_TIME = 0;
            while (RUNNING) {
                try {
                    Canvas canvas = holder.lockCanvas();
                    drawMyView(canvas);
                    holder.unlockCanvasAndPost(canvas);
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        RUNNING = false;
    }

    public float[] getPoints() {
        points = new float[rects.size() * 4];
        for (int i = 0; i < rects.size(); i++) {
            points[4 * i] = (rects.get(i))[0] - aW;
            points[4 * i + 1] = (rects.get(i))[1] - aH;
            points[4 * i + 2] = (rects.get(i))[2] - aW;
            points[4 * i + 3] = (rects.get(i))[3] - aH;
        }

        return points;
    }

    public void setPoints(float[] points) {
        this.points = points;
        rects = new ArrayList<float[]>();
        for (int i = 0; i < (points.length + 1) / 4; i++) {
            rects.add(new float[]{points[4 * i] + aW, points[4 * i + 1] + aH,
                    points[4 * i + 2] + aW, points[4 * i + 3] + aH});
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMyView(canvas);
    }

    public void drawMyView(Canvas canvas) {
        if (rects != null) {
            if (rects.size() > 0) {
                for (int i = 0; i < rects.size(); i++) {
                    float[] po = rects.get(i);
                    canvas.drawRect(po[0], po[1], po[2], po[3], mPaint);

                    Log.i(TAG, po[0] + " " + po[1] + " " + po[2] + " " + po[3]);
                }
                if (index != -1) {
                    float[] p = rects.get(index);
                    canvas.drawRect(p[0], p[1], p[2], p[3], cPaint);
                }
                canvas.save();
                canvas.restore();
            }
        }
    }

    public void setWH(float bdW, float bdH, float lW, float lH) {
        aW = bdW;
        aH = bdH;
        bW = lW;
        bH = lH;
    }
}
