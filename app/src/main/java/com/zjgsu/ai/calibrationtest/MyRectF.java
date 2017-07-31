package com.zjgsu.ai.calibrationtest;

import android.graphics.RectF;

/**
 * Created by Double on 31/07/2017.
 */

public class MyRectF extends RectF {
    public MyRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }
    public boolean isPointDelete(float x, float y) {
        return Math.abs(x - this.left) < 20 && Math.abs(y - this.top) < 20;
    }
    public boolean isPointMove(float x, float y) {
        return Math.abs(x - this.right) < 20 && Math.abs(y - this.bottom) < 20;
    }
}
