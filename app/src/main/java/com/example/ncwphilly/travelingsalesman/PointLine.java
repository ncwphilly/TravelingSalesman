package com.example.ncwphilly.travelingsalesman;

import android.graphics.Point;

/**
 * Created by ncwphilly on 2/4/17.
 */

public class PointLine {


    private Point p1;
    private Point p2;

    public PointLine(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }


    public Point getP2() {
        return p2;
    }
}
