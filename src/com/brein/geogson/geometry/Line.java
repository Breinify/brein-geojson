package com.brein.geogson.geometry;

import com.brein.geogson.tools.Vector2d;
import com.sun.istack.internal.logging.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line implements IGeometryObject {
    private static final Logger LOGGER = Logger.getLogger(Line.class);
    private final BoundingBox bbox;
    private final List<Point> endPoints;

    public Line(final List<Point> endPoints) {
        this.endPoints = endPoints;
        this.bbox = new BoundingBox(endPoints);
    }

    @Override
    public boolean within(final IGeometryObject other) {
        if (other.getClass().isAssignableFrom(Polygon.class)) {
            final Polygon otherPoly = (Polygon) other;
            if (!getEndPoints().get(0).within(otherPoly) || !getEndPoints().get(1).within(otherPoly)) {
                return false;
            }

            for (final Line outerLine : otherPoly.getRing()) {
                if (this.intersects(outerLine)) {
                    return false;
                }
            }

            for (final List<Line> holes : otherPoly.getHoles()) {
                for (final Line holeLine : holes) {
                    if (this.intersects(holeLine)) {
                        return false;
                    }
                }
            }
            return true;
        } else if (other.getClass().isAssignableFrom(Point.class)) {
            final Point otherPoint = (Point) other;
            return getEndPoints().get(0).equals(otherPoint) && getEndPoints().get(1).equals(otherPoint);
        } else if (other.getClass().isAssignableFrom(Line.class)) {

            final Line otherLine = (Line) other;
            final Vector2d p = new Vector2d(endPoints.get(0));
            final Vector2d q = new Vector2d(otherLine.endPoints.get(0));

            final Vector2d r = new Vector2d(endPoints.get(1)).subtract(p);
            final Vector2d s = new Vector2d(otherLine.endPoints.get(1)).subtract(q);

            final double qpCrossR = q.subtract(p).cross(r);
            final double rCrossS = r.cross(s);

            if (qpCrossR == 0 && rCrossS == 0) {
                final double t0 = (q.subtract(p)).dot(r) / (r.dot(r));
                final double t1 = t0 + s.dot(r) / (r.dot(r));

                //todo: test that this is within and not encase
                return t1 >= 0 && t1 <= 1 && t0 >= 0 && t0 <= 1;
            } else {
                return false;
            }
        } else if (other.getClass().isAssignableFrom(GeometryCollection.class)) {
            return other.encases(this);
        }
        LOGGER.warning("Unsure of class type " + other.getClass());
        return false;
    }

    @Override
    public boolean encases(final IGeometryObject other) {
        if (other.getClass().isAssignableFrom(Polygon.class)) {
            for (final Line line : ((Polygon) other).getRing()) {
                if (!this.encases(line)) {
                    return false;
                }
            }
            return true;
        } else if (other.getClass().isAssignableFrom(Line.class)) {
            return other.within(this);
        } else if (other.getClass().isAssignableFrom(Point.class)) {
            return new Line(Arrays.asList(other.boundingBox().getDownLeft(), other.boundingBox()
                    .getUpRight())).within(this);
        } else if (other.getClass().isAssignableFrom(GeometryCollection.class)) {
            return other.within(this);
        }

        return false;//todo
    }

    @Override
    public double distance(final IGeometryObject other) {
        return 0;//todo
    }

    @Override
    public double surfaceArea() {
        return 0;
    }

    @Override
    public BoundingBox boundingBox() {
        return bbox;
    }

    @Override
    public Point centroid() {
        final Point p1 = endPoints.get(0);
        final Point p2 = endPoints.get(1);
        return new Point((p1.getLat() - p2.getLat()) / 2.0, (p1.getLon() - p2.getLon()) / 2.0);
    }

    @Override
    public Map<String, Object> toMap() {
        return new LineStrings(endPoints).toMap();
    }

    public double length() {
        return endPoints.get(0).distance(endPoints.get(1));
    }

    public List<Point> getEndPoints() {
        return endPoints;
    }

    public boolean intersects(final Line other) {
        //see https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect

        final Vector2d p = new Vector2d(endPoints.get(0));
        final Vector2d q = new Vector2d(other.endPoints.get(0));

        final Vector2d r = new Vector2d(endPoints.get(1)).subtract(p);
        final Vector2d s = new Vector2d(other.endPoints.get(1)).subtract(q);

        final double qpCrossR = q.subtract(p).cross(r);
        final double rCrossS = r.cross(s);

        if (qpCrossR == 0 && rCrossS == 0) {
            double t0 = (q.subtract(p)).dot(r) / (r.dot(r));
            double t1 = t0 + s.dot(r) / (r.dot(r));

            if (t1 < t0) {
                final double temp = t1;
                t1 = t0;
                t0 = temp;
            }

            return (t1 <= 1 || t0 >= 0) && (!((t1 > 1 && t0 > 1) || (t1 < 0 && t0 < 0)));
        } else if (rCrossS == 0) {
            return false;
        } else {
            final double u = qpCrossR / rCrossS;
            final double t = (q.subtract(p).cross(s)) / rCrossS;
            return (0 <= t && t <= 1 && 0 <= u && u <= 1);
        }
    }
}
