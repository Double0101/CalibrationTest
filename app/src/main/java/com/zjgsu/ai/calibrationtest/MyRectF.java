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

    private float centerX;
    private float centerY;

    public MyRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
        centerX = (left + right) / 2;
        centerY = (top + bottom) / 2;
    }

    public void setTopAndLeft(float x, float y) {
        top = y;
        left = x;
    }

    public void setTopAndRight(float x, float y) {
        top = y;
        right = x;
    }

    public void setBottomAndLeft(float x, float y) {
        bottom = y;
        left = x;
    }

    public void setBottomAndRight(float x, float y) {
        bottom = y;
        right = x;
    }

    public void move(MyRectF copy, float distanceX, float distanceY) {
        right = copy.right + distanceX;
        left = copy.left + distanceX;
        top = copy.top + distanceY;
        bottom = copy.bottom + distanceY;
        setCenter();
    }

    public void modified(int point, float x, float y) {
        if (point == NO_POINT) setBottomAndRight(x, y);
        else if (point == POINT_TOP_LEFT) setTopAndLeft(x, y);
        else if (point == POINT_TOP_RIGHT) setTopAndRight(x, y);
        else if (point == POINT_BOTTOM_LEFT) setBottomAndLeft(x, y);
        else if (point == POINT_BOTTOM_RIGHT) setBottomAndRight(x, y);
        setCenter();
    }

    private void setCenter() {
        centerX = (right + left) / 2;
        centerY = (top + bottom) / 2;
    }

    public boolean isCenter(float x, float y) {
        return Math.abs(centerX - x) < (width() / 3)
                && Math.abs(centerY - y) < (height() / 3);
    }

    public int isPointMove(float x, float y) {
        if (x < centerX) {
            if (y < centerY && Math.abs(x - this.left) < 50 && Math.abs(y - this.top) < 50)
                return POINT_TOP_LEFT;
            if (y >= centerY && Math.abs(x - this.left) < 50 && Math.abs(y - this.bottom) < 50)
                return POINT_BOTTOM_LEFT;
        }
        if (x >= centerX) {
            if (y < centerY && Math.abs(x - this.right) < 50 && Math.abs(y - this.top) < 50)
                return POINT_TOP_RIGHT;
            if (y >= centerY && Math.abs(x - this.right) < 50 && Math.abs(y - this.bottom) < 50)
                return POINT_BOTTOM_RIGHT;
        }
        return NO_POINT;
    }

}
