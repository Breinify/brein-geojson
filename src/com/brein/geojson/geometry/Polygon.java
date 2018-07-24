package com.brein.geojson.geometry;

import com.brein.geojson.tools.CommonGeoMath;
import com.brein.geojson.tools.CommonGeoMath.IntersectionType;
import com.brein.geojson.tools.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Polygon implements IGeometryObject {
    private final List<Line> ring;
    private final List<List<Line>> holes;
    private final BoundingBox bbox;

    public Polygon(final Point... ring) {
        this(Arrays.asList(ring), Collections.emptyList());
    }

    public Polygon(final List<Point> ring, final List<List<Point>> holes) {
        this(pointsToLine(ring),
                holes.stream().map(Polygon::pointsToLine).collect(Collectors.toList()),
                new BoundingBox(ring));
    }

    public Polygon(final List<Line> ring, final List<List<Line>> holes, final BoundingBox bbox) {
        this.ring = ring;
        this.holes = holes;
        this.bbox = bbox;
    }

    @Override
    public boolean within(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            //if this polygon is just a point, then it may be within the other point
            if (CommonGeoMath.approxEquals(bbox.getDownLeft().distance(bbox.getUpRight()), 0)) {
                return bbox.getDownLeft().equals(other);
            }
            return false;
        } else if (Polygon.class.isAssignableFrom(other.getClass())) {
            final Polygon otherPoly = (Polygon) other;
            final CommonGeoMath.IntersectionType outerRingIntersection =
                    CommonGeoMath.ringPolygonIntersection(getRing(), otherPoly);
            if (outerRingIntersection.equals(IntersectionType.OUTSIDE) ||
                    outerRingIntersection.equals(IntersectionType.PARTIAL)) {
                return false;
            }

            for (final List<Line> hole : ((Polygon) other).getHoles()) {
                final IntersectionType holeIntersection = CommonGeoMath.ringPolygonIntersection(hole, this);
                //if the hole other object's hole is atleast partially within this polygon
                if (holeIntersection != IntersectionType.OUTSIDE) {
                    //then that hole is a place where this polygon isn't in it
                    return false;
                }
            }
            return true;
        } else if (Line.class.isAssignableFrom(other.getClass())) {
            //a polygon can only be within a line if the polygon is just a flat line
            for (final Line l : ring) {
                if (!l.within(other)) {
                    return false;
                }
            }
            return true;
        } else if (GeometryCollection.class.isAssignableFrom(other.getClass())) {
            return other.encases(this);
        }
        return false;
    }

    @Override
    public boolean encases(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            final Point otherPoint = (Point) other;
            if (!CommonGeoMath.pointInRing(otherPoint, ring, boundingBox())) {
                return false;
            }

            for (final List<Line> hole : holes) {
                if (CommonGeoMath.pointInRing(otherPoint, hole, boundingBox())) {
                    return false;
                }
            }
            return true;

        } else {
            return other.within(this);
        }
    }

    @Override
    public double distance(final IGeometryObject other) {
        if (other.within(this)) {
            return 0.0;
        } else if (Point.class.isAssignableFrom(other.getClass()) || Line.class.isAssignableFrom(other.getClass())) {
            double min = Double.MAX_VALUE;
            for (final Line edge : ring) {
                min = Math.min(min, edge.distance(other));
                if (CommonGeoMath.approxEquals(min, 0)) {
                    return 0;
                }
            }

            for (final List<Line> holeEdges : holes) {
                for (final Line edge : holeEdges) {
                    min = Math.min(min, edge.distance(other));
                    if (CommonGeoMath.approxEquals(min, 0)) {
                        return 0;
                    }
                }
            }
            return min;
        } else if (Polygon.class.isAssignableFrom(other.getClass())) {
            final Polygon p = (Polygon) other;

            double min = Double.MAX_VALUE;
            for (final Line edge : p.ring) {
                min = Math.min(min, edge.distance(this));
                if (CommonGeoMath.approxEquals(min, 0)) {
                    return 0;
                }
            }

            for (final List<Line> holeEdges : p.holes) {
                for (final Line edge : holeEdges) {
                    min = Math.min(min, edge.distance(this));
                    if (CommonGeoMath.approxEquals(min, 0)) {
                        return 0;
                    }
                }
            }

            for (final Line edge : ring) {
                min = Math.min(min, edge.distance(other));
                if (CommonGeoMath.approxEquals(min, 0)) {
                    return 0;
                }
            }

            for (final List<Line> holeEdges : holes) {
                for (final Line edge : holeEdges) {
                    min = Math.min(min, edge.distance(other));
                    if (CommonGeoMath.approxEquals(min, 0)) {
                        return 0;
                    }
                }
            }

            return min;

        } else {
            return other.distance(this);
        }
    }

    @Override
    public double surfaceArea() {
        return CommonGeoMath.ringArea(getRing()) - getHoles().stream()
                .mapToDouble(CommonGeoMath::ringArea)
                .sum();
    }

    @Override
    public BoundingBox boundingBox() {
        return bbox;
    }

    @Override
    public Point centroid() {
        double xWeighted = 0;
        double yWeighted = 0;
        double totalWeight = 0;

        final Point encasingCenter = CommonGeoMath.ringCentroid(getRing());
        final double encasingArea = CommonGeoMath.ringArea(getRing());

        xWeighted += encasingCenter.getLat() * encasingArea;
        yWeighted += encasingCenter.getLon() * encasingArea;
        totalWeight += encasingArea;

        for (final List<Line> hole : getHoles()) {
            final Point holdCenter = CommonGeoMath.ringCentroid(hole);
            final double holeArea = -CommonGeoMath.ringArea(hole);

            xWeighted += holdCenter.getLat() * holeArea;
            yWeighted += holdCenter.getLon() * holeArea;
            totalWeight += holeArea;
        }

        return new Point(xWeighted / totalWeight, yWeighted / totalWeight);
    }

    @Override
    public Map<String, Object> toMap() {
        final Map<String, Object> res = new HashMap<>();
        res.put(Constants.GEOJSON_TYPE, Constants.GEOJSON_TYPE_POLYGON);

        final List<List<List<Double>>> coords = new ArrayList<>();

        coords.add(linesToPoints(ring).stream().map(Point::getCoordinates).collect(Collectors.toList()));

        for (final List<Line> hole : holes) {
            coords.add(linesToPoints(hole).stream().map(Point::getCoordinates).collect(Collectors.toList()));
        }

        res.put(Constants.GEOJSON_COORDINATES, coords);

        return res;
    }

    public static List<Line> pointsToLine(final List<Point> points) {
        final List<Line> res = new ArrayList<>();

        for (int ct = 0; ct < points.size() - 1; ct++) {
            res.add(new Line(Arrays.asList(points.get(ct), points.get(ct + 1))));
        }

        return res;
    }

    public static List<Point> linesToPoints(final List<Line> lines) {
        final List<Point> res = new ArrayList<>();
        res.add(lines.get(0).getEndPoints().get(0));
        lines.forEach(l -> res.add(l.getEndPoints().get(1)));
        return res;
    }

    public static List<Line> pointsToLine(final Point... points) {
        return pointsToLine(Arrays.asList(points));
    }

    public List<Line> getRing() {
        return ring;
    }

    public List<List<Line>> getHoles() {
        return holes;
    }
}
