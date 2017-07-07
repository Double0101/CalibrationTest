package com.zjgsu.ai.calibrationtest;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
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

    private static final String TAG = "Calibration-Click";

    private ArrayList<float[]> rects;

    private float[] points;

    private float aW;

    private float aH;

    private float bW;

    private float bH;

    private Paint mPaint;

    private Paint cPaint;

    private Paint magnifierPaint;

    private Paint pPaint;

    private Paint linePaint;

    private int magnifierRadius;

    private ImageView imageView;

    private int index = -1;

    private boolean isCached = false;

    private Bitmap bitmap;

    private int t = 0;

    private BitmapShader shader;

    private Matrix matrix;

    private float multiple;

    private float pX;

    private float pY;

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
        magnifierRadius = 250;
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
        pPaint = new Paint();
        pPaint.setColor(Color.RED);
        pPaint.setStrokeWidth(10f);
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        PathEffect effect = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
        linePaint.setAntiAlias(true);
        linePaint.setPathEffect(effect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        pX = event.getX();
        pY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isInArea(event.getX(), event.getY())) {
                    rects.add(new float[]{
                            event.getX(), event.getY(), 0, 0
                    });
                    pX = event.getX();
                    pY = event.getY();
                    isCached = false;
                    index = rects.size() - 1;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                    if (Math.abs(rects.get(rects.size() - 1)[0] - event.getX()) > 10
                            || Math.abs(rects.get(rects.size() - 1)[1] - event.getY()) > 10) {
                        pX = event.getX();
                        pY = event.getY();
                        isCached = true;
                        if (event.getX() > bW) {
                            rects.get(index)[2] = bW;
                        } else {
                            (rects.get(index))[2] = event.getX();
                        }
                        if (event.getY() > bH) {
                            rects.get(index)[3] = bH;
                        } else
                            (rects.get(index))[3] = event.getY();
                        invalidate();
                    } else {

                    }
                break;

            case MotionEvent.ACTION_UP:
                    if (rects.get(index)[2] == 0
                            || rects.get(index)[3] == 0
                            || rects.get(index)[2] - rects.get(index)[0] < 10
                            || rects.get(index)[3] - rects.get(index)[1] < 10) {
                        rects.remove(index);
                    }
                isCached = false;
                index = -1;
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
    public void surfaceCreated(final SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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
            }
        }
    }

    private void drawMagnifier(float x, float y, Canvas canvas) {
        if (t == 0) {
            t = 1;
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
        }
        if (isCached) {
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            magnifierPaint.setShader(shader);
            matrix.reset();
            if (multiple == 1)
                matrix.postTranslate(0, -magnifierRadius);
            else {
                matrix.postScale(multiple, multiple, x, y + magnifierRadius / (multiple - 1));
            }
            magnifierPaint.getShader().setLocalMatrix(matrix);
            canvas.drawCircle(x, y - magnifierRadius, magnifierRadius, magnifierPaint);
            canvas.drawLine(x - magnifierRadius, y - magnifierRadius, x + magnifierRadius, y - magnifierRadius, linePaint);
            canvas.drawLine(x, y - 2 * magnifierRadius, x, y, linePaint);
            canvas.drawPoint(x, y - magnifierRadius, pPaint);
        }
    }


    public void setWH(float bdW, float bdH, float lW, float lH) {
        aW = bdW;
        aH = bdH;
        bW = lW;
        bH = lH;
    }
}
