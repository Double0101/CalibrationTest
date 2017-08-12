package com.zjgsu.ai.calibrationtest;

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
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Double on 10/06/2017.
 */

public class DrawSurface extends SurfaceView {
    private static final String TAG = "Calibration-Click";
    private ArrayList<MyRectF> myRects;

    private MyRectF currentRect;
    private MyRectF copyRect;


    private Paint[] mPaints;

    private int magnifierRadius;

    private ImageView imageView;

    private boolean isInited = false;
    private boolean isTouched = false;

    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix;

    private float multiple;

    private float currentX;
    private float currentY;

    private SurfaceHolder holder;

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setZOrderOnTop(true);
        myRects = new ArrayList<MyRectF>();
        holder = getHolder();

        currentRect = null;
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initPaint();
        multiple = 2f;
        magnifierRadius = 250;
        matrix = new Matrix();
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private void initPaint() {
        mPaints  = new Paint[6];
        mPaints[0] = new Paint();
        mPaints[0].setColor(Color.GREEN);
        mPaints[0].setStyle(Paint.Style.STROKE);
        mPaints[0].setStrokeWidth(5f);
        mPaints[0].setAlpha(200);
        mPaints[1] = new Paint();
        mPaints[1].setColor(Color.YELLOW);
        mPaints[1].setStyle(Paint.Style.STROKE);
        mPaints[1].setStrokeWidth(8f);
        mPaints[1].setAlpha(200);
        mPaints[2] = new Paint();
        mPaints[3] = new Paint();
        mPaints[3].setColor(Color.RED);
        mPaints[3].setStrokeWidth(15f);
        mPaints[4] = new Paint();
        mPaints[4].setColor(Color.BLUE);
        mPaints[4].setStrokeWidth(15f);
        mPaints[5] = new Paint();
        mPaints[5].setColor(Color.BLACK);
        mPaints[5].setStyle(Paint.Style.STROKE);
        mPaints[5].setStrokeWidth(4f);
    }

    public MyRectF[] getRects() {
        MyRectF[] giveRect = new MyRectF[myRects.size()];
        for (int i = 0; i < myRects.size(); ++i)
            giveRect[i] = new MyRectF(
                    myRects.get(i).left,
                    myRects.get(i).top,
                    myRects.get(i).right,
                    myRects.get(i).bottom);
        return giveRect;
    }

    /**
     * 改变放大镜的放大倍数
     *
     * @param multiple
     */
    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (myRects != null) {
            for (int i = 0; i < myRects.size(); ++i)
                canvas.drawRect(myRects.get(i), mPaints[0]);
            if (currentRect != null) {
                canvas.drawRect(currentRect, mPaints[1]);
                canvas.drawPoint(currentRect.left, currentRect.top, mPaints[4]);
                canvas.drawPoint(currentRect.right, currentRect.bottom, mPaints[4]);
                drawMagnifier(currentX, currentY, canvas);
            }
        }
    }

    private void drawMagnifier(float x, float y, Canvas canvas) {
        if (!isInited) {
            isInited = true;
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            // 为Paint添加shader
            mPaints[2].setShader(shader);
        }
        if (isTouched) {
            matrix.reset();
            if (multiple == 1)
                matrix.postTranslate(0, -magnifierRadius);
            else
                matrix.postScale(multiple, multiple, x, y + magnifierRadius / (multiple - 1));
            mPaints[2].getShader().setLocalMatrix(matrix);
            canvas.drawCircle(x, y - magnifierRadius, magnifierRadius, mPaints[2]);
            canvas.drawLine(x - magnifierRadius, y - magnifierRadius, x + magnifierRadius, y - magnifierRadius, mPaints[5]);
            canvas.drawLine(x, y - 2 * magnifierRadius, x, y, mPaints[5]);
            canvas.drawPoint(x, y - magnifierRadius, mPaints[3]);
        }
    }

    public void drawRect(int[] rectInfo, float l, float t, float r, float b) {
        if (rectInfo[0] < myRects.size()) {
            currentRect = myRects.get(rectInfo[0]);
            currentX = r;
            currentY = b;
            isTouched = true;
            if (rectInfo[1] == 0) {
                currentRect.left = r;
                currentRect.top = b;
            } else {
                currentRect.right = r;
                currentRect.bottom = b;
            }
        } else if (rectInfo[0] == myRects.size()) {
            addRect(new MyRectF(l, t, r, b));
        }
        invalidate();
    }

    public void moveRect(int index, float x1, float y1, float x2, float y2) {
        currentRect = myRects.get(index);
        float distanceX = x2 - x1, distanceY = y2 - y1;
        if (copyRect == null) {
            copyRect = new MyRectF(currentRect.left, currentRect.top, currentRect.right, currentRect.bottom);
        }
        currentRect.right = copyRect.right + distanceX;
        currentRect.left = copyRect.left + distanceX;
        currentRect.top = copyRect.top + distanceY;
        currentRect.bottom = copyRect.bottom + distanceY;
        invalidate();
    }

    public void removeRect(int which) {
        myRects.remove(which);
        invalidate();
    }

    public void addRect(MyRectF rectF) {
        myRects.add(rectF);
        invalidate();
    }

    public void addRects(ArrayList<MyRectF> rects) {
        myRects = rects;
        invalidate();
    }

    public void clear() {
        currentRect = null;
        copyRect = null;
        isTouched = false;
        currentX = -1;
        currentY = -1;
        invalidate();
    }

    public int getSize() {
        return myRects.size();
    }

    public int getRect(float x, float y) {
        for (int i = 0; i < myRects.size(); ++i)
            if (myRects.get(i).isCenter(x, y))
                return i;
        return -1;
    }

    public int[] getAjust(float x, float y) {
        int[] result = {-1, -1};
        for (MyRectF rect : myRects) {
            result[1] = rect.isPointMove(x, y);
            if (result[1] != -1) {
                result[0] = myRects.indexOf(rect);
                break;
            }
        }
        return result;
    }


}
