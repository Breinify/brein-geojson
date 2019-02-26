package com.brein.geojson.tools;

import com.brein.geojson.geometry.BoundingBox;
import com.brein.geojson.geometry.Line;
import com.brein.geojson.geometry.Point;
import com.brein.geojson.geometry.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonGeoMath {
    public static int pointRingIntersection(final Point point, final List<Line> ring, final BoundingBox ringBBox) {
        for (final Line ringLine : ring) {
            if (point.within(ringLine)) {
                return 1;
            }
        }
        //find point outside of ring
        final Point target = new Point(ringBBox.getUpRight().getLon() + 1, ringBBox.getUpRight().getLat() + 1);
        final Line ray = new Line(Arrays.asList(point, target));

        //if we hit a corner of a ring, then we'll count two lines that it hit with
        boolean intersectingCorner = false;

        int intersections = 0;
        for (final Line line : ring) {
            if (line.intersects(ray)) {
                if (line.getEndPoints().get(0).within(ray) || line.getEndPoints().get(1).within(ray)) {
                    if (intersectingCorner) {
                        intersections++;
                    }
                    intersectingCorner = !intersectingCorner;
                } else {
                    intersections++;
                }
            }
        }

        return intersections;
    }

    public static boolean pointInRing(final Point point, final List<Line> ring, final BoundingBox ringBBox) {
        for(final Line line: ring){
            if(CommonGeoMath.approxEquals(0, point.distance(line))){
                return true;
            }
        }

        return pointRingIntersection(point, ring, ringBBox) % 2 == 1;
    }

    public static double signedRingArea(final List<Line> ring){
        return 0.5 * ring.stream()
                .mapToDouble(l -> l.getEndPoints().get(0).getLat() * l.getEndPoints().get(1).getLon()
                        - l.getEndPoints().get(1).getLat() * l.getEndPoints().get(0).getLon())
                .sum();
    }

    public static double ringArea(final List<Line> ring) {
        return Math.abs(signedRingArea(ring));
    }

    public static Point ringCentroid(final List<Line> ring) {
        final double area = signedRingArea(ring);

        final double lat = 1 / 6.0 / area * ring.stream().mapToDouble(l ->
                (l.getEndPoints().get(0).getLat() + l.getEndPoints().get(1).getLat()) *
                        (l.getEndPoints().get(0).getLat() * l.getEndPoints().get(1).getLon() -
                                l.getEndPoints().get(1).getLat() * l.getEndPoints().get(0).getLon()))
                .sum();

        final double lon = 1 / 6.0 / area * ring.stream().mapToDouble(l ->
                (l.getEndPoints().get(0).getLon() + l.getEndPoints().get(1).getLon()) *
                        (l.getEndPoints().get(0).getLat() * l.getEndPoints().get(1).getLon() -
                                l.getEndPoints().get(1).getLat() * l.getEndPoints().get(0).getLon()))
                .sum();

        return new Point(lon, lat);
    }

    public static IntersectionType ringPolygonIntersection(final List<Line> ring, final Polygon poly) {
        boolean hasInsidePoints = false;
        boolean hasOutsidePoints = false;

        for (final Line l : ring) {
            if (l.getEndPoints().get(0).within(poly)) {
                hasInsidePoints = true;
            } else {
                hasOutsidePoints = true;
            }

            // there must be a line that crosses the polygon, so there must be a partial overlap
            if (hasInsidePoints && hasOutsidePoints) {
                return IntersectionType.PARTIAL;
            }
        }

        final List<Line> allPolyLines = new ArrayList<>(poly.getRing());
        for (final List<Line> hole : poly.getHoles()) {
            allPolyLines.addAll(hole);
        }

        for (final Line ringLine : ring) {
            for (final Line polyLine : allPolyLines) {
                if (polyLine.intersects(ringLine) && !polyLine.equals(ringLine)) {
                    if (polyLine.getEndPoints().get(0).equals(ringLine.getEndPoints().get(0)) ||
                            polyLine.getEndPoints().get(1).equals(ringLine.getEndPoints().get(0)) ||
                            polyLine.getEndPoints().get(0).equals(ringLine.getEndPoints().get(1)) ||
                            polyLine.getEndPoints().get(1).equals(ringLine.getEndPoints().get(1))) {
                        //lines don't actually cross, just touch
                        continue;
                    }

                    return IntersectionType.PARTIAL;
                }
            }
        }

        if (hasInsidePoints) {
            return IntersectionType.INSIDE;
        } else {
            return IntersectionType.OUTSIDE;
        }
    }

    public static boolean approxEquals(final double d1, final double d2){
        return Math.abs(d1 - d2) < Constants.EPSILON;
    }

    public enum IntersectionType {
        INSIDE,
        PARTIAL,
        OUTSIDE
    }


}
