package com.brein.geojson.geometry;

import com.brein.geojson.tools.CommonGeoMath;
import com.brein.geojson.tools.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Helper class for a single line, not to be confused with a LineString
 */
public class Line implements IGeometryObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(Line.class);
    private final BoundingBox bbox;
    private final List<Point> endPoints;

    public Line(final List<Point> endPoints) {
        this.endPoints = endPoints;
        this.bbox = new BoundingBox(endPoints);
    }

    public Line(final double lat1, final double lon1, final double lat2, final double lon2) {
        this(Arrays.asList(new Point(lat1, lon1), new Point(lat2, lon2)));
    }

    @Override
    public boolean within(final IGeometryObject other) {
        if (Polygon.class.isAssignableFrom(other.getClass())) {
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
        } else if (Point.class.isAssignableFrom(other.getClass())) {
            final Point otherPoint = (Point) other;
            return getEndPoints().get(0).equals(otherPoint) && getEndPoints().get(1).equals(otherPoint);
        } else if (Line.class.isAssignableFrom(other.getClass())) {
            return other.encases(this);
        } else if (GeometryCollection.class.isAssignableFrom(other.getClass())) {
            return other.encases(this);
        }
        LOGGER.warn("Unsure of class type " + other.getClass());
        return false;
    }

    @Override
    public boolean encases(final IGeometryObject other) {
        if (Polygon.class.isAssignableFrom(other.getClass())) {
            for (final Line line : ((Polygon) other).getRing()) {
                if (!this.encases(line)) {
                    return false;
                }
            }
            return true;
        } else if (Line.class.isAssignableFrom(other.getClass())) {
            final Line otherLine = (Line) other;
            final Vector2d p = new Vector2d(endPoints.get(0));
            final Vector2d q = new Vector2d(otherLine.endPoints.get(0));

            final Vector2d r = new Vector2d(endPoints.get(1)).subtract(p);
            final Vector2d s = new Vector2d(otherLine.endPoints.get(1)).subtract(q);

            final double qpCrossR = q.subtract(p).cross(r);
            final double rCrossS = r.cross(s);

            if (CommonGeoMath.approxEquals(qpCrossR, 0.0) && CommonGeoMath.approxEquals(rCrossS, 0)) {
                final double rDotR = r.dot(r);
                if (CommonGeoMath.approxEquals(rDotR, 0.0)) {
                    return true;
                }
                final double t0 = (q.subtract(p)).dot(r) / (r.dot(r));
                final double t1 = t0 + s.dot(r) / (r.dot(r));

                return t1 >= 0 && t1 <= 1 && t0 >= 0 && t0 <= 1;
            } else {
                return false;
            }
        } else if (Point.class.isAssignableFrom(other.getClass())) {
            final Point p = (Point) other;

            final double pointDist = p.distance(endPoints.get(0)) + p.distance(endPoints.get(1));
            final double closestDist = this.length();
            return CommonGeoMath.approxEquals(pointDist, closestDist);
        } else {
            return other.within(this);
        }
    }

    @Override
    public boolean intersects(final IGeometryObject other) {
        if (Line.class.isAssignableFrom(other.getClass())) {
            return intersectsOtherLine((Line) other);
        } else {
            return CommonGeoMath.approxEquals(0, distance(other));
        }
    }

    @Override
    public double distance(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            //see https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
            final Point o = (Point) other;
            final double length = length();
            if (CommonGeoMath.approxEquals(length, 0)) {
                return getEndPoints().get(0).distance(o);
            }
            final Vector2d p = new Vector2d(o);
            final Vector2d end1 = new Vector2d(getEndPoints().get(0));
            final Vector2d end2 = new Vector2d(getEndPoints().get(1));
            final double t = Math.max(0, Math.min(1, (p.subtract(end1)).dot(end2.subtract(end1)) / length / length));

            final Vector2d projection = end1.add((end2.subtract(end1).scale(t)));

            return o.distance(projection.toPoint());
        } else if (Line.class.isAssignableFrom(other.getClass())) {
            final Line o = (Line) other;
            if (o.intersects(this)) {
                return 0;
            }
            double min = Double.MAX_VALUE;

            for (int ct = 0; ct < 2; ct++) {
                min = Math.min(min, o.distance(getEndPoints().get(ct)));
                min = Math.min(min, this.distance(o.getEndPoints().get(ct)));
                if (CommonGeoMath.approxEquals(min, 0)) {
                    return 0;
                }
            }
            return min;
        } else {
            return other.distance(this);
        }
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

    @Override
    public boolean equals(final Object other) {
        if (other == null || !other.getClass().equals(this.getClass())) {
            return false;
        }
        final Line o = (Line) other;
        if (o.endPoints.get(0).equals(endPoints.get(0)) && o.endPoints.get(1).equals(endPoints.get(1))) {
            return true;
        } else if (o.endPoints.get(0).equals(endPoints.get(1)) && o.endPoints.get(1).equals(endPoints.get(0))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return endPoints.get(0).hashCode() ^ endPoints.get(1).hashCode();
    }

    @Override
    public String toString() {
        return endPoints.toString();
    }

    public boolean intersectsOtherLine(final Line other) {
        //see https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect

        final Vector2d p = new Vector2d(endPoints.get(0));
        final Vector2d q = new Vector2d(other.endPoints.get(0));

        final Vector2d r = new Vector2d(endPoints.get(1)).subtract(p);
        final Vector2d s = new Vector2d(other.endPoints.get(1)).subtract(q);

        final double qpCrossR = q.subtract(p).cross(r);
        final double rCrossS = r.cross(s);

        if (CommonGeoMath.approxEquals(qpCrossR, 0.0) && CommonGeoMath.approxEquals(rCrossS, 0)) {
            double t0 = (q.subtract(p)).dot(r) / (r.dot(r));
            double t1 = t0 + s.dot(r) / (r.dot(r));

            if (t1 < t0) {
                final double temp = t1;
                t1 = t0;
                t0 = temp;
            }

            return (t1 <= 1 || t0 >= 0) && (!((t1 > 1 && t0 > 1) || (t1 < 0 && t0 < 0)));
        } else if (CommonGeoMath.approxEquals(rCrossS, 0)) {
            return false;
        } else {
            final double u = qpCrossR / rCrossS;
            final double t = (q.subtract(p).cross(s)) / rCrossS;
            return (0 <= t && t <= 1 && 0 <= u && u <= 1);
        }
    }
}
