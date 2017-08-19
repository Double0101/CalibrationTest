package com.zjgsu.ai.calibrationtest;

import android.view.MotionEvent;

/**
 * Created by Double on 17/08/2017.
 */

public class MyPoint {
    private float x;
    private float y;
    public MyPoint(MotionEvent e) {
        x = e.getX();
        y = e.getY();
    }
    public void reset() {
        x = -1;
        y = -1;
    }
    public MyPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public MyPoint(MyPoint p) {
        this.x = p.getX();
        this.y = p.getY();
    }
    public static float distanceX(MyPoint p1, MyPoint p2) {
        return p2.getX() - p1.getX();
    }
    public static float distanceY(MyPoint p1, MyPoint p2) {
        return p2.getY() - p1.getY();
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public MyPoint getMinusBound(MyPoint bound) {
        return new MyPoint(x - bound.getX(), y - bound.getY());
    }
    public MyPoint getPlusBound(MyPoint bound) {
        return new MyPoint(x + bound.getX(), y + bound.getY());
    }
}
