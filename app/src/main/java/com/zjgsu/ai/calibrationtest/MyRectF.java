package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

/**
 * Created by Double on 31/07/2017.
 */

public class MyRectF extends RectF {

    public static final int NO_POINT = -1;
    public static final int POINT_TOP_LEFT = 0;
    public static final int POINT_TOP_RIGHT = 1;
    public static final int POINT_BOTTOM_LEFT = 2;
    public static final int POINT_BOTTOM_RIGHT = 3;

    private MyPoint centerPoint;

    public MyRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
        centerPoint = new MyPoint((left + right) / 2, (top + bottom) / 2);
    }
    public MyRectF(MyPoint p1, MyPoint p2) {
        super(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        centerPoint = new MyPoint((left + right) / 2, (top + bottom) / 2);
    }

    public static MyRectF copyRect(MyRectF rect) {
        return new MyRectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void setTopAndLeft(MyPoint point) {
        top = point.getY();
        left = point.getX();
    }

    public void setTopAndRight(MyPoint point) {
        top = point.getY();
        right = point.getX();
    }

    public void setBottomAndLeft(MyPoint point) {
        bottom = point.getY();
        left = point.getX();
    }

    public void setBottomAndRight(MyPoint point) {
        bottom = point.getY();
        right = point.getX();
    }

    public void move(MyRectF copy, float distanceX, float distanceY) {
        right = copy.right + distanceX;
        left = copy.left + distanceX;
        top = copy.top + distanceY;
        bottom = copy.bottom + distanceY;
        setCenter();
    }

    public void modified(int which, MyPoint point) {
        if (which == NO_POINT) setBottomAndRight(point);
        else if (which == POINT_TOP_LEFT) setTopAndLeft(point);
        else if (which == POINT_TOP_RIGHT) setTopAndRight(point);
        else if (which == POINT_BOTTOM_LEFT) setBottomAndLeft(point);
        else if (which == POINT_BOTTOM_RIGHT) setBottomAndRight(point);
        setCenter();
    }

    public MyPoint getCenter() {
        return centerPoint;
    }

    private void setCenter() {
        centerPoint.setX((right + left) / 2);
        centerPoint.setY((top + bottom) / 2);
    }

    public boolean isCenter(MyPoint point) {
        return Math.abs(MyPoint.distanceX(centerPoint, point)) < (width() / 3)
                && Math.abs(MyPoint.distanceY(centerPoint, point)) < (height() / 3);
    }

    public int isPointMove(MyPoint point) {
        if (point.getX() < centerPoint.getX()) {
            if (point.getY() < centerPoint.getY() && Math.abs(point.getX() - this.left) < 25 && Math.abs(point.getY() - this.top) < 25)
                return POINT_TOP_LEFT;
            if (point.getY() >= centerPoint.getY() && Math.abs(point.getX() - this.left) < 25 && Math.abs(point.getY() - this.bottom) < 25)
                return POINT_BOTTOM_LEFT;
        }
        if (point.getX() >= centerPoint.getX()) {
            if (point.getY() < centerPoint.getY() && Math.abs(point.getX() - this.right) < 25 && Math.abs(point.getY() - this.top) < 25)
                return POINT_TOP_RIGHT;
            if (point.getY() >= centerPoint.getY() && Math.abs(point.getX() - this.right) < 25 && Math.abs(point.getY() - this.bottom) < 25)
                return POINT_BOTTOM_RIGHT;
        }
        return NO_POINT;
    }

}
