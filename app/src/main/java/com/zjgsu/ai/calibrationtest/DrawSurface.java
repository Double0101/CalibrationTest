package com.zjgsu.ai.calibrationtest;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Double on 10/06/2017.
 */

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

//    private static boolean isLongTouched;

    private static boolean RUNNING = false;

    private static final String TAG = "Calibration";

//    private static final int MIN_CLICK_DURATION = 2000;

//    private static Lock lock = new ReentrantLock();

    private ArrayList<float[]> rects;

    private float[] points;

    private float aW;

    private float aH;

    private float bW;

    private float bH;

    private Paint mPaint;

    private Paint cPaint;

    private Paint magnifierPaint;

    private int magnifierRadius;

    private ImageView imageView;

    private int index = -1;

//    private int selectInt = -1;

//    private long startClickTime = 0;

//    private float selectX;

//    private float selectY;

    private int pass = 0;


//    private int isFirst = 0;

    private boolean isCached = false;

    private Bitmap bitmap;

    private int t = 0;

    private BitmapShader shader;

    private Matrix matrix;

    private float multiple;

    private float pX;

    private float pY;

//    private int isFirst = 0;

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
        multiple = 2f;
        magnifierRadius = 300;
        matrix = new Matrix();

    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private void initPaint() {
        magnifierPaint = new Paint();
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
        pX = event.getX();
        pY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (/*!isLongTouched &&*/ isInArea(event.getX(), event.getY())) {
                    rects.add(new float[]{
                            event.getX(), event.getY(), 0, 0
                    });
                    pX = event.getX();
                    pY = event.getY();
//                    selectX = event.getX();
//                    selectY = event.getY();
                    isCached = false;
                    pass = 1;
//                    isFirst = 0;
//                    isLongTouched = false;
//                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    index = rects.size() - 1;
                    Log.i("SIZE", "" + index);
                    Log.i("Down", String.valueOf(rects.size()));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pass == 1) {
                    if (Math.abs(rects.get(rects.size() - 1)[0] - event.getX()) > 10
                            || Math.abs(rects.get(rects.size() - 1)[1] - event.getY()) > 10) {
//                        isLongTouched = false;
                        pX = event.getX();
                        pY = event.getY();
                        isCached = true;
                        Log.i("Move", "X" + event.getX() + "bX" + bW);
                        Log.i("Move", "Y" + event.getY() + "bY" + bH);
                        Log.i("MMMM1", index + " " + (rects.size() - 1));
                        if (event.getX() > bW) {
                            rects.get(index)[2] = bW;
                            Log.i("Move", 1 + "");
                        }
                        else {
                            Log.i("MMMM1", index + " " + (rects.size() - 1));
                            (rects.get(index))[2] = event.getX();
                        }
                        if (event.getY() > bH) {
                            rects.get(index)[3] = bH;
                            Log.i("Move", 2 + "");
                        }
                        else
                            (rects.get(index))[3] = event.getY();
                    } else {
//                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
//                        if (clickDuration > MIN_CLICK_DURATION) {
//                            isLongTouched = true;
//                        } else {
//                            isLongTouched = false;
//                        }
                    }

//                    if (isLongTouched) {
//                        lock.lock();
//                        if (isFirst == 0) {
//                            isFirst = 1;
//                            Log.d("ahsjdahskjdhaksjd", "执行了这个语句第nnn次");
//                            Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
//                            vib.vibrate(200);
//                            for (int i = 0; i < rects.size(); i++) {
//                                if (rects.get(i)[0] < selectX
//                                        && rects.get(i)[2] > selectX
//                                        && rects.get(i)[1] < selectY
//                                        && rects.get(i)[3] > selectY) {
//                                    selectInt = i;
//                                }
//                            }
//
//                            if (selectInt != -1) {
//                                new AlertDialog.Builder(getContext())
//                                        .setTitle("删除该标定区域")
//                                        .setMessage("您确定要删除该标定吗？")
//                                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                rects.remove(selectInt);
//                                                selectInt = -1;
//                                                dialog.cancel();
//                                                invalidate();
//                                                isLongTouched = false;
//                                            }
//                                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                        isLongTouched = false;
//                                    }
//                                }).show();
//                            } else {
//                                Toast.makeText(getContext(), "您未选任何标注区域", Toast.LENGTH_SHORT).show();
//                                isLongTouched = false;
//                            }
//                        }
//                        lock.unlock();
//                    }
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (pass == 1) {
                    if (rects.get(index)[2] == 0
                            || rects.get(index)[3] == 0
                            || rects.get(index)[2] - rects.get(index)[0] < 10
                            || rects.get(index)[3] - rects.get(index)[1] < 10) {
                        rects.remove(index);
                    }
                }
                isCached = false;
                pass = 0;
                index = -1;
//                isLongTouched = false;
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                isCached = false;
                invalidate();
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
    public void surfaceCreated(final SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

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

    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMyView(canvas);
    }

    public void drawMyView(Canvas canvas) {
        if (rects != null) {
            Log.i("oiwhgoiwg", rects.size()+"");

                for (int i = 0; i < rects.size(); i++) {
                    float[] po = rects.get(i);
                    canvas.drawRect(po[0], po[1], po[2], po[3], mPaint);

                    Log.i(TAG, po[0] + " " + po[1] + " " + po[2] + " " + po[3]);
                }
                if (index != -1) {
                    float[] p = rects.get(index);
                    canvas.drawRect(p[0], p[1], p[2], p[3], cPaint);
                }
                if (rects.size() > 0) {
                drawMagnifier(pX, pY, canvas);
//                canvas.save();
//                canvas.restore();
            }
        }
    }

    private void drawMagnifier(float x, float y, Canvas canvas) {
        if (t == 0) {
            t = 1;
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            Log.i("lfhwoighroiw", bitmap.getWidth() + " " + bitmap.getHeight());
        }
        if (isCached) {
            Log.i("lfhwoighroiw", "isexecute");
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            magnifierPaint.setShader(shader);
            matrix.reset();
            matrix.postScale(multiple, multiple, x, y + magnifierRadius / (multiple - 1));
            magnifierPaint.getShader().setLocalMatrix(matrix);
            canvas.drawCircle(x, y - magnifierRadius, magnifierRadius, magnifierPaint);
        }
    }


    public void setWH(float bdW, float bdH, float lW, float lH) {
        aW = bdW;
        aH = bdH;
        bW = lW;
        bH = lH;
    }
}
