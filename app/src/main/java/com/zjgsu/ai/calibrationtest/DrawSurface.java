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
import android.graphics.RectF;
import android.graphics.Shader;
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

/**
 * Created by Double on 10/06/2017.
 */

public class DrawSurface extends SurfaceView {

    private static boolean isLongTouched;

    private static final String TAG = "Calibration-Click";

    private static final int MIN_CLICK_DURATION = 2000;

    private ArrayList<float[]> rects;

    private RectF newRect;

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

    private int drawTime = -1;

    private int selectInt = -1;

    private long startClickTime = 0;

    private float selectX;

    private float selectY;

    private boolean isTouched = false;

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
        initPaint();
        multiple = 2f;
        magnifierRadius = 250;
        matrix = new Matrix();
        newRect = new RectF();
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
                if (!isLongTouched && isInArea(event.getX(), event.getY())) {
                    pX = event.getX();
                    pY = event.getY();
                    selectX = event.getX();
                    selectY = event.getY();
                    isTouched = false;
                    isLongTouched = false;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(rects.get(rects.size() - 1)[0] - event.getX()) > 10
                        || Math.abs(rects.get(rects.size() - 1)[1] - event.getY()) > 10) {
                    isLongTouched = false;
                } else {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration >= MIN_CLICK_DURATION) {
                        isLongTouched = true;
                        Log.i(TAG, "long");
                    } else {
                        isLongTouched = false;
                        Log.i(TAG, "short");
                    }
                }

                if (isLongTouched) {
                    if (selectInt == -1) {
                        Log.i("remove1", rects.size() + " " + selectInt);
                        Log.d("fsferwgvf", "执行了这个语句第nnn次");
                        Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                        vib.vibrate(200);
                        Log.i("size", rects.size() + " ");
                        for (int i = 0; i < rects.size(); i++) {
                            if (rects.get(i)[0] < selectX
                                    && rects.get(i)[2] > selectX
                                    && rects.get(i)[1] < selectY
                                    && rects.get(i)[3] > selectY) {
                                selectInt = i;
                            }
                        }
                        Log.i("remove2", rects.size() + " " + selectInt);


                        if (selectInt != -1) {
                            Log.i(TAG, "jioejf");
                            new AlertDialog.Builder(getContext())
                                    .setTitle("删除该标定区域")
                                    .setMessage("您确定要删除该标定吗？")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("remove3", rects.size() + " " + selectInt);
                                            rects.remove(selectInt);
                                            Log.i("remove4", rects.size() + " " + selectInt);
                                            selectInt = -1;
                                            dialog.cancel();
                                            invalidate();
                                            isLongTouched = false;
                                        }
                                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    selectInt = -1;
                                    invalidate();
                                    isLongTouched = false;
                                }
                            }).show();
                        } else {
                            Toast.makeText(getContext(), "您未选任何标注区域", Toast.LENGTH_SHORT).show();
                            isLongTouched = false;
                        }
                    }
                } else {
                    if (!isLongTouched) {
                        pX = event.getX();
                        pY = event.getY();
                        isTouched = true;
                        Log.i(TAG, "rects.size() = " + rects.size());

                        newRect.set(selectX, selectY, pX, pY);

                        if (event.getX() > bW)
                            newRect.right = bW;
                        if (event.getY() > bH)
                            newRect.left = bH;
                    }
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isLongTouched) {
                    if (newRect.right - newRect.left < 10
                            || newRect.bottom - newRect.top < 10) {
                        newRect = new RectF();
                        Log.i("remove6", rects.size() + " " + selectInt);
                    }
                }
                selectX = -1;
                selectY = -1;
                isTouched = false;
                drawTime = -1;
                isLongTouched = false;
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                invalidate();
                break;
        }
        return true;
    }


    private boolean isInArea(float x, float y) {
        return (x > aW && x < bW && y > aH && y < bH);
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
            canvas.drawRect(newRect, cPaint);
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
        if (isTouched) {
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
