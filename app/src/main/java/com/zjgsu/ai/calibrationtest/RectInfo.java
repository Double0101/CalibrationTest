package com.zjgsu.ai.calibrationtest;

/**
 * Created by Double on 16/08/2017.
 */

public class RectInfo {
    private int rectNum;
    private int pointNum;

    public RectInfo() {
        rectNum = -1;
        pointNum = -1;
    }

    public void reset() {
        rectNum = -1;
        pointNum = -1;
    }

    public int getPointNum() {
        return pointNum;
    }

    public int getRectNum() {
        return rectNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public void setRectNum(int rectNum) {
        this.rectNum = rectNum;
    }
}
