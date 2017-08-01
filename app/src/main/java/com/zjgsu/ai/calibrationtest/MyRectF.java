package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

/**
 * Created by Double on 31/07/2017.
 */

public class MyRectF extends RectF {

    public MyRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }
    public MyRectF() {}
    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    public boolean isCenter(float x, float y) {
        return Math.abs((right + left) / 2 - x) < width() / 3
                && Math.abs((bottom - top) / 2 - y) < height() / 3;
    }
    public int isPointMove(float x, float y) {
        return (Math.abs(x - this.right) < 20 && Math.abs(y - this.bottom) < 20) ? 1 :
                (Math.abs(x - this.left) < 20 && Math.abs(y - this.top) < 20) ? 0 : -1;
    }
    public void move(float x1, float y1, float x2, float y2) {
        float x = x2 - x1, y = y2 - y1;
        right += x;
        left += x;
        top += y;
        bottom += y;
    }
}
