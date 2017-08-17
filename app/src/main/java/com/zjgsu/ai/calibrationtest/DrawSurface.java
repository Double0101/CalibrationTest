package com.zjgsu.ai.calibrationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.ImageView;

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

    private MyPoint currentPoint;

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
        magnifierRadius = 200;
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
                drawMagnifier(currentPoint, canvas);
            }
        }
    }

    private void drawMagnifier(MyPoint point, Canvas canvas) {
        if (!isInited) {
            isInited = true;
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaints[2].setShader(shader);
        }
        if (isTouched) {
            matrix.reset();
            if (multiple == 1) matrix.postTranslate(0, -magnifierRadius);
            else matrix.postScale(multiple, multiple, point.getX(), point.getY() + magnifierRadius / (multiple - 1));
            mPaints[2].getShader().setLocalMatrix(matrix);
            canvas.drawCircle(point.getX(), point.getY() - magnifierRadius, magnifierRadius, mPaints[2]);
            drawSight(point, canvas);
        }
    }

    private void drawSight(MyPoint point, Canvas canvas) {
        canvas.drawLine(point.getX() - magnifierRadius, point.getY() - magnifierRadius, point.getX() + magnifierRadius, point.getY() - magnifierRadius, mPaints[5]);
        canvas.drawLine(point.getX(), point.getY() - 2 * magnifierRadius, point.getX(), point.getY(), mPaints[5]);
        canvas.drawPoint(point.getX(), point.getY() - magnifierRadius, mPaints[3]);
    }

    public void drawRect(RectInfo rectInfo, MyPoint p1, MyPoint p2) {
        if (rectInfo.getRectNum() < myRects.size()) {
            currentRect = myRects.get(rectInfo.getRectNum());
            currentPoint = new MyPoint(p2);
            isTouched = true;
            currentRect.modified(rectInfo.getPointNum(), currentPoint);
        } else if (rectInfo.getRectNum() == myRects.size()) {
            addRect(new MyRectF(p1, p2));
        }
        invalidate();
    }

    public void moveRect(RectInfo info, MyPoint p1, MyPoint p2) {
        if (currentRect == null) currentRect = myRects.get(info.getRectNum());
        if (copyRect == null) copyRect = MyRectF.copyRect(currentRect);
        currentRect.move(copyRect, MyPoint.distanceX(p1, p2), MyPoint.distanceY(p1, p2));
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

    public void setRects(ArrayList<MyRectF> rects) {
        myRects = rects;
        invalidate();
    }

    public void clear() {
        if (currentRect != null && (currentRect.height() < 50 || currentRect.width() < 50)) {
            myRects.remove(currentRect);
        }
        if (currentPoint != null) {
            currentPoint.reset();
        }
        currentRect = null;
        copyRect = null;
        isTouched = false;
        invalidate();
    }

    public int getSize() {
        return myRects.size();
    }

    public int getRect(MyPoint point) {
        for (int i = 0; i < myRects.size(); ++i)
            if (myRects.get(i).isCenter(point))
                return i;
        return -1;
    }

    public RectInfo getAjust(MyPoint point) {
        RectInfo result = new RectInfo();
        for (MyRectF rect : myRects) {
            result.setPointNum(rect.isPointMove(point));
            if (result.hasPoint()) {
                result.setRectNum(myRects.indexOf(rect));
                break;
            }
        }
        return result;
    }


}
