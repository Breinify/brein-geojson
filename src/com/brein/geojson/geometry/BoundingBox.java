package com.brein.geojson.geometry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BoundingBox implements IGeometryObject {
    private Point upRight;
    private Point downLeft;

    private Polygon polyRepresentation = null;

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

        polyRepresentation = null;
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

    public Polygon getPolyRepresentation(){
        if(polyRepresentation == null){
            buildPoly();
        }
        return polyRepresentation;
    }

    protected void buildPoly(){
        polyRepresentation = new Polygon(upRight, new Point(downLeft.getLon(), upRight.getLat()),
                downLeft, new Point(upRight.getLon(), downLeft.getLat()), upRight);
    }

    @Override
    public String toString() {
        return "[" + getDownLeft() + "," + getUpRight() + "]";
    }

    @Override
    public boolean within(final IGeometryObject other) {
        return getPolyRepresentation().within(other);
    }

    @Override
    public boolean encases(final IGeometryObject other) {
        return getPolyRepresentation().encases(other);
    }

    @Override
    public double distance(final IGeometryObject other) {
        return getPolyRepresentation().distance(other);
    }

    @Override
    public double surfaceArea() {
        return Math.abs((upRight.getLat() - downLeft.getLat()) * (upRight.getLon() - downLeft.getLon()));
    }

    @Override
    public BoundingBox boundingBox() {
        return this;
    }

    @Override
    public Point centroid() {
        return new Point((upRight.getLon() + downLeft.getLon()) / 2, (upRight.getLat() + downLeft.getLat()) / 2);
    }

    @Override
    public Map<String, Object> toMap() {
        return getPolyRepresentation().toMap();
    }
}
