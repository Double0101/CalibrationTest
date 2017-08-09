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

public class DrawSurface extends SurfaceView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "Calibration-Click";
    public ArrayList<MyRectF> myRects;

    private MyRectF newRect;

    private float aW;
    private float aH;

    private float bW;
    private float bH;

    private Paint rectPaint;   //  框的paint
    private Paint curPaint;   //  现在画的框的paint
    private Paint magnifierPaint;
    private Paint aimPaint;   //  放大镜的准星
    private Paint movePaint;   //  矩形框的移动点
    private Paint linePaint;    //  放大镜的线

    private int magnifierRadius;

    private ImageView imageView;
    private GestureDetector detector;
    private MyDialog dialog;

    private boolean isTouched = false;
    private boolean isAjust = false;
    private boolean isMove = false;

    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix;

    private int t = 0;
    public int whichRect = -1;
    private int whichPoint = -1;

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
        detector = new GestureDetector(this);
        dialog = new MyDialog(getContext());
        newRect = null;
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

    /**
     * 判断点是否在屏幕中的图片区域
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInArea(float x, float y) {
        return (x > aW && x < bW && y > aH && y < bH);
    }

    public MyRectF[] getRects() {
        MyRectF[] giveRect = new MyRectF[myRects.size()];
        for (int i = 0; i < myRects.size(); ++i)
            giveRect[i] = new MyRectF(
                    myRects.get(i).left - aW,
                    myRects.get(i).top - aH,
                    myRects.get(i).right - aW,
                    myRects.get(i).bottom - aH);
        return giveRect;
    }

    public void setRects(MyRectF[] rectList) {
        this.myRects = new ArrayList<MyRectF>();
        for (int i = 0; i < rectList.length; ++i)
            myRects.add(
                    new MyRectF(rectList[i].left + aW, rectList[i].top + aH,
                            rectList[i].right + aW, rectList[i].bottom + aH));
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
            if (newRect != null) {
                canvas.drawRect(newRect, curPaint);
                canvas.drawPoint(newRect.left, newRect.top, movePaint);
                canvas.drawPoint(newRect.right, newRect.bottom, movePaint);
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

    /**
     * 通过父类容器调用
     * 得到图片的留白的点的位置
     *
     * @param bdW
     * @param bdH
     * @param lW
     * @param lH
     */
    public void setWH(float bdW, float bdH, float lW, float lH) {
        aW = bdW;
        aH = bdH;
        bW = lW;
        bH = lH;
    }

    /**
     * 判断触点在哪个框的两个点周围
     * 随后可以对这个框的大小进行调整
     *
     * @param x
     * @param y
     * @return
     */
    public int whichAjsut(float x, float y) {
        for (int i = 0; i < myRects.size(); i++) {
            whichPoint = myRects.get(i).isPointMove(x, y);
            if (whichPoint != -1)
                return i;
        }
        return -1;
    }

    /**
     * 判断触点在哪个框的中央
     *
     * @param x
     * @param y
     * @return
     */
    public int inWhichCenter(float x, float y) {
        for (int i = 0; i < myRects.size(); i++)
            if (myRects.get(i).isCenter(x, y))
                return i;
        return -1;
    }

    private void clean() {
        pX = pY = qX = qY = whichRect = whichPoint = -1;
        newRect = null;
        isMove = isTouched = false;
    }

    private MyRectF findRect(float x, float y) {
        if (inWhichCenter(x, y) != -1)
            return myRects.get(inWhichCenter(x, y));
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && newRect != null) {
            if (!isAjust) {
                myRects.add(newRect);
                clean();
            } else {
                clean();
            }
            invalidate();
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown");

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isInArea(e1.getX(), e1.getY()) && isInArea(e2.getX(), e2.getY())) {
            Log.i(TAG, "onScroll");
            if (newRect == null) {
                whichRect = whichAjsut(e1.getX(), e1.getY());
                if (whichRect == -1) {
                    newRect = new MyRectF();
                } else {
                    newRect = myRects.get(whichRect);
                    isAjust = true;
                    isTouched = true;
                }
            } else if (isAjust) {
                if (whichPoint == 0) {
                    pX = e2.getX();
                    pY = e2.getY();
                    newRect.left = pX;
                    newRect.top = pY;
                } else if (whichPoint == 1) {
                    pX = e2.getX();
                    pY = e2.getY();
                    newRect.right = pX;
                    newRect.bottom = pY;
                }
            } else {
                pX = e2.getX();
                pY = e2.getY();
                newRect.set(e1.getX(), e1.getY(), e2.getX(), e2.getY());
            }
            invalidate();
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i(TAG, "Single");
        if (!isAjust) {
            newRect = findRect(e.getX(), e.getY());
            if (newRect != null)
                isAjust = true;
        } else
            isAjust = false;
        invalidate();
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i(TAG, "Doubletap");
        if (whichRect != -1)
            whichRect = -1;
        else {
            pX = e.getX();
            pY = e.getY();
            whichRect = inWhichCenter(pX, pY);
            Log.i(TAG, myRects.size() + " " + whichRect);
            if (whichRect != -1) {
                Log.i(TAG, myRects.toString());
                dialog.setRemove(myRects, whichRect);
            }
            else
                Toast.makeText(getContext(), "您并没有选择任何标注区域", Toast.LENGTH_SHORT).show();
        }
        invalidate();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {

        return false;
    }
}
