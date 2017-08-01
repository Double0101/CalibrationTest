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
import android.view.GestureDetector;
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
    private static final String TAG = "Calibration-Click";
    private ArrayList<MyRectF> rects;

    private MyRectF newRect;

    private float aW;
    private float aH;

    private float bW;
    private float bH;

    private Paint mPaint;   //  框的paint
    private Paint cPaint;   //  现在画的框的paint
    private Paint magnifierPaint;
    private Paint pPaint;   //  放大镜的准星&矩形框的删除点
    private Paint rPaint;   //  矩形框的移动点
    private Paint linePaint;    //  放大镜的线

    private int magnifierRadius;

    private ImageView imageView;

    private boolean isTouched = false;
    private boolean isAjust = false;
    private boolean isMove = false;

    private Bitmap bitmap;
    private BitmapShader shader;
    private Matrix matrix;

    private int t = 0;
    private int whichRect = -1;
    private int whichPoint = -1;

    private float multiple;

    private float qX;
    private float qY;

    private float pX;
    private float pY;

    private SurfaceHolder holder;
    private MyGestureDetector detector;

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setZOrderOnTop(true);
        rects = new ArrayList<MyRectF>();
        holder = getHolder();
        newRect = null;
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initPaint();
        detector = new MyGestureDetector();
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
        pPaint.setStrokeWidth(15f);

        rPaint = new Paint();
        rPaint.setColor(Color.BLUE);
        rPaint.setStrokeWidth(15f);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
    }

    /**
     * 判断点是否在屏幕中的图片区域
     * @param x
     * @param y
     * @return
     */
    private boolean isInArea(float x, float y) {
        return (x > aW && x < bW && y > aH && y < bH);
    }

    public MyRectF[] getRects() {
        MyRectF[] giveRect = new MyRectF[rects.size()];
        for (int i = 0; i < rects.size(); ++i)
            giveRect[i] = new MyRectF(
                    rects.get(i).left - aW,
                    rects.get(i).top - aH,
                    rects.get(i).right - aW,
                    rects.get(i).bottom - aH);

        return giveRect;
    }

    public void setRects(MyRectF[] rectList) {
        this.rects = new ArrayList<MyRectF>();
        for (int i = 0; i < rectList.length; ++i)
            rects.add(
                    new MyRectF(rectList[i].left + aW, rectList[i].top + aH,
                            rectList[i].right + aW, rectList[i].bottom + aH));
    }

    /**
     * 改变放大镜的放大倍数
     * @param multiple
     */
    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rects != null) {
            for (int i = 0; i < rects.size(); ++i)
                canvas.drawRect(rects.get(i), mPaint);
            if (newRect != null) {
                canvas.drawRect(newRect, cPaint);
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
            canvas.drawPoint(x, y - magnifierRadius, pPaint);
        }
    }

    /**
     * 通过父类容器调用
     * 得到图片的留白的点的位置
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
     * @param x
     * @param y
     * @return
     */
    public int whichAjsut(float x, float y) {
        for (int i = 0; i < rects.size(); i++) {
            whichPoint = rects.get(i).isPointMove(x, y);
            if (whichPoint != -1)
                return i;
        }
        return -1;
    }

    /**
     * 判断触点在哪个框的中央
     * @param x
     * @param y
     * @return
     */
    public int inWhichCenter(float x, float y) {
        for (int i = 0; i < rects.size(); i++)
            if (rects.get(i).isCenter(x, y))
                return i;
        return -1;
    }

    private void clean() {
        pX = pY = qX = qY = whichRect = whichPoint = -1;
        newRect = null;
        isMove = isAjust = isTouched = false;
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (whichRect != -1)
                rects.remove(whichRect);
            else {
                pX = e.getX();
                pY = e.getY();
                whichRect = whichAjsut(pX, pY);
                if (whichRect != -1)
                    new AlertDialog.Builder(getContext())
                            .setTitle("删除该标定区域")
                            .setMessage("您确定要删除该标定吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rects.remove(whichRect);
                                    dialog.cancel();

                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                else
                    Toast.makeText(getContext(), "您并没有选择任何标注区域", Toast.LENGTH_SHORT).show();
            }
            invalidate();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            clean();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            pX = e.getX();
            pY = e.getY();
            whichRect = whichAjsut(pX, pY);
            if (whichRect != -1)
                isAjust = true;
            else {
                whichRect = inWhichCenter(pX, pY);
                if (whichRect != -1)
                    isMove = true;
            }
            newRect = new MyRectF();
            return super.onDown(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            qX = e.getX();
            qY = e.getY();
            if (whichRect != -1) {
                newRect = null;
                newRect = rects.get(whichRect);
                if (isAjust) {
                    //  移动点
                    if (whichPoint == 0) {
                        newRect.left = e.getX();
                        newRect.top = e.getY();
                    } else if (whichPoint == 1){
                        newRect.right = e.getX();
                        newRect.bottom = e.getY();
                    }
                }
                else if (isMove) {
                    //  move
                    newRect.move(pX, pY, qX, qY);
                }
            } else {
                //  画框
                newRect.set(pX, pY, qX, qY);
            }
            invalidate();
            super.onShowPress(e);
        }
    }
}
