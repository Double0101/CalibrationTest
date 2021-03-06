package com.zjgsu.ai.calibrationtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Double on 2017/5/13.
 */

public class MyDrawView extends RelativeLayout implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private int currentMode = 0;
    private static final int MODE_DRAW = 1;
    private static final int MODE_AJUST = 2;
    private static final int MODE_MOVE = 3;

    private boolean isInited = false;

    private ImageView imageView;

    private DrawSurface drawSurface;

    private int category;

    private AnnotatedImage mAnnotatedImage;

    private GestureDetector detector;

    private Context mContext;

    private RectInfo rectInfo;

    private MyPoint boundA, boundB;

    public MyDrawView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_draw_view, this);
        detector = new GestureDetector(this);
        mContext = context;
        rectInfo = new RectInfo();
        imageView = (ImageView) findViewById(R.id.myImageView);
        drawSurface = (DrawSurface) findViewById(R.id.myDrawImage);
        drawSurface.setImageView(imageView);
    }

    public void setIndex(int index) {
        mAnnotatedImage = AnnotationLab.get(mContext).getAnnotatedImages().get(index);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!isInited) {
            String path = mAnnotatedImage.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            getBound();
            if (mAnnotatedImage.hasAnn()) {
                drawSurface.setAnnotatedList(mAnnotatedImage);
            } else {
                drawSurface.setAnnotatedList(new AnnotatedImage(path));
            }
            isInited = true;
        }
    }
    // 获得imageview的宽高
    private void getBound() {
        int viewW = imageView.getWidth();
        int viewH = imageView.getHeight();
        int dw = imageView.getDrawable().getBounds().width();
        int dh = imageView.getDrawable().getBounds().height();
        Matrix m = imageView.getImageMatrix();
        float[] values = new float[10];
        m.getValues(values);
        float sx = values[0];
        float sy = values[4];
        float cw = dw * sx;
        float ch = dh * sy;
        boundA = new MyPoint((viewW - cw) / 2, (viewH - ch) / 2);
        boundB = new MyPoint(viewW - boundA.getX(), viewH - boundA.getY());

        drawSurface.setBound(new MyPoint((viewW - cw) / 2, (viewH - ch) / 2),
                new MyPoint(viewW - boundA.getX(), viewH - boundA.getY()));
    }

    public void setMultiple(int i) {
        drawSurface.setMultiple(i);
    }

    public AnnotatedImage getAnnotatedImage() {
        return drawSurface.getAnnotationList();
    }

    private void clear() {
        currentMode = 0;
        rectInfo.reset();
        drawSurface.clear();
    }

    private int modeManage(MyPoint point) {
        rectInfo.setRectNum(drawSurface.getRect(point.getMinusBound(boundA)));
        if (!rectInfo.hasRect()) {
            rectInfo = drawSurface.getAjust(point.getMinusBound(boundA));
            if (rectInfo.hasRect()) {
                return MODE_AJUST;
            } else {
                rectInfo.setRectNum(drawSurface.getSize());
                return MODE_DRAW;
            }
        } else {
            return MODE_MOVE;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentMode == MODE_DRAW) {
                setAnnCategory();
            } else {
                clear();
            }
        }
        return true;
    }

    private void setAnnCategory() {
        final String[] items = AnnotatedImage.ANNOTATION_CATEGORY;
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        myDialog.setTitle("选择标定种类");
        myDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        category = which;
                    }
                });
        myDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawSurface.setAnnCategory(category);
                clear();
                dialog.cancel();
            }
        });
        myDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawSurface.removeWithoutCategory();
                clear();
            }
        });
        myDialog.show();
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        final int which = drawSurface.getRect(new MyPoint(e).getMinusBound(boundA));
        if (which == -1) {
            Toast.makeText(getContext(), "您并没有选择任何标注区域", Toast.LENGTH_SHORT).show();
            return false;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("删除该标定区域")
                .setMessage("您确定要删除该标定吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int w) {
                        drawSurface.removeRect(which);
                        dialog.cancel();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        MyPoint p1 = new MyPoint(e1);
        MyPoint p2 = new MyPoint(e2);
        if (p1.getX() > boundA.getX() && p1.getX() < boundB.getX() && p1.getY() > boundA.getY() && p1.getY() < boundB.getY()
                && p2.getX() > boundA.getX() && p2.getX() < boundB.getX() && p2.getY() > boundA.getY() && p2.getY() < boundB.getY()) {
            if (!rectInfo.hasRect()) {
                currentMode = modeManage(p1);
            }
            switch (currentMode) {
                case MODE_DRAW:
                case MODE_AJUST:
                    drawSurface.drawRect(rectInfo, p1, p2);
                    break;
                case MODE_MOVE:
                    drawSurface.moveRect(rectInfo, p1, p2);
                    break;
                default:
                    clear();
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
