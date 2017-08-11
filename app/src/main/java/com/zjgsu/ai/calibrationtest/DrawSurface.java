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

    private Paint rectPaint;   //  框的paint
    private Paint curPaint;   //  现在画的框的paint
    private Paint magnifierPaint;
    private Paint aimPaint;   //  放大镜的准星
    private Paint movePaint;   //  矩形框的移动点
    private Paint linePaint;    //  放大镜的线

    private int magnifierRadius;

    private ImageView imageView;

    private boolean isTouched = false;

    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix;

    private int t = 0;

    private float multiple;

    private float qX;
    private float qY;

    private float pX;
    private float pY;

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
        magnifierPaint = new Paint();

        rectPaint = new Paint();
        rectPaint.setColor(Color.GREEN);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setAlpha(200);

        curPaint = new Paint();
        curPaint.setColor(Color.YELLOW);
        curPaint.setStyle(Paint.Style.STROKE);
        curPaint.setStrokeWidth(8f);
        curPaint.setAlpha(200);

        aimPaint = new Paint();
        aimPaint.setColor(Color.RED);
        aimPaint.setStrokeWidth(15f);

        movePaint = new Paint();
        movePaint.setColor(Color.BLUE);
        movePaint.setStrokeWidth(15f);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
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
                canvas.drawRect(myRects.get(i), rectPaint);
            if (currentRect != null) {
                canvas.drawRect(currentRect, curPaint);
                canvas.drawPoint(currentRect.left, currentRect.top, movePaint);
                canvas.drawPoint(currentRect.right, currentRect.bottom, movePaint);
                drawMagnifier(pX, pY, canvas);
            }
        }
    }

    private void drawMagnifier(float x, float y, Canvas canvas) {
        if (t == 0) {
            t = 1;
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            // 为Paint添加shader
            magnifierPaint.setShader(shader);
        }
        if (isTouched) {
            matrix.reset();
            if (multiple == 1)
                matrix.postTranslate(0, -magnifierRadius);
            else
                matrix.postScale(multiple, multiple, x, y + magnifierRadius / (multiple - 1));
            magnifierPaint.getShader().setLocalMatrix(matrix);
            canvas.drawCircle(x, y - magnifierRadius, magnifierRadius, magnifierPaint);
            canvas.drawLine(x - magnifierRadius, y - magnifierRadius, x + magnifierRadius, y - magnifierRadius, linePaint);
            canvas.drawLine(x, y - 2 * magnifierRadius, x, y, linePaint);
            canvas.drawPoint(x, y - magnifierRadius, aimPaint);
        }
    }

    public void drawRect(int[] rectInfo, float l, float t, float r, float b) {
        if (rectInfo[0] < myRects.size()) {
            MyRectF rect = myRects.get(rectInfo[0]);
            if (rectInfo[1] == 0) {
                rect.left = r;
                rect.top = b;
            } else {
                rect.right = r;
                rect.bottom = b;
            }
        } else if (rectInfo[0] == myRects.size()) {
            addRect(new MyRectF(l, t, r, b));
        }
        invalidate();
    }

    public void moveRect(int index, float x1, float y1, float x2, float y2) {
        MyRectF rect = myRects.get(index);
        float distanceX = x2 - x1, distanceY = y2 - y1;
        if (currentRect == null) {
            currentRect = new MyRectF(rect.left, rect.top, rect.right, rect.bottom);
        }
        rect.right += distanceX;
        rect.left += distanceX;
        rect.top += distanceY;
        rect.bottom += distanceY;
        invalidate();
    }

    public int getSize() {
        return myRects.size();
    }

    public int getRect(float x, float y) {
        for (int i = 0; i < myRects.size(); i++)
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
    }
}
