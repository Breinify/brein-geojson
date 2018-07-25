package com.brein.geojson.geometry;

import java.util.Arrays;
import java.util.List;

public class BoundingBox {
    private Point upRight;
    private Point downLeft;

    public BoundingBox(final Point initialPoint) {
        upRight = initialPoint;
        downLeft = initialPoint;
    }

    public BoundingBox(final Point... points) {
        this(Arrays.asList(points));
    }

    public BoundingBox(final List<Point> points) {
        this(points.get(0));
        for (final Point p : points) {
            addPoint(p);
        }
    }

    public void addPoint(final Point newPoint) {
        if (newPoint.getLon() > upRight.getLon() || newPoint.getLat() > upRight.getLon()) {
            upRight = new Point(Math.max(newPoint.getLat(), upRight.getLat()),
                    Math.max(newPoint.getLon(), upRight.getLon()));
        }

        if (newPoint.getLon() < downLeft.getLon() || newPoint.getLat() < downLeft.getLon()) {
            downLeft = new Point(Math.min(newPoint.getLat(), downLeft.getLat()),
                    Math.min(newPoint.getLon(), downLeft.getLon()));
        }
    }

    public void addBBox(final BoundingBox other) {
        addPoint(other.downLeft);
        addPoint(other.upRight);
    }

    public BoundingBox copy() {
        return new BoundingBox(downLeft, upRight);
    }

    public Point getUpRight() {
        return upRight;
    }

    public Point getDownLeft() {
        return downLeft;
    }

    @Override
    public String toString() {
        return "[" + getDownLeft() + "," + getUpRight() + "]";
    }
}
