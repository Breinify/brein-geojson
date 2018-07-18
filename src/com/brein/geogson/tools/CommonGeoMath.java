package com.brein.geogson.tools;

import com.brein.geogson.geometry.BoundingBox;
import com.brein.geogson.geometry.Line;
import com.brein.geogson.geometry.Point;
import com.brein.geogson.geometry.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonGeoMath {
    public static int pointRingIntersection(final Point point, final List<Line> ring, final BoundingBox ringBBox) {
        //find point outside of ring
        final Point target = new Point(ringBBox.getUpRight().getLat() + 1, ringBBox.getUpRight().getLon() + 1);
        final Line ray = new Line(Arrays.asList(point, target));

        int intersections = 0;
        for (final Line line : ring) {
            if (line.intersects(ray)) {
                intersections++;
            }
        }

        return intersections;
    }

    public static boolean pointInRing(final Point point, final List<Line> ring, final BoundingBox ringBBox) {
        return pointRingIntersection(point, ring, ringBBox) % 2 == 1;
    }

    public static double ringArea(final List<Line> ring) {
        return Math.abs(0.5 * ring.stream()
                .mapToDouble(l -> l.getEndPoints().get(0).getLat() * l.getEndPoints().get(1).getLon()
                        - l.getEndPoints().get(1).getLat() * l.getEndPoints().get(0).getLon())
                .sum());
    }

    public static Point ringCentroid(final List<Line> ring) {
        final double area = ringArea(ring);

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

        return new Point(lat, lon);
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
                if (polyLine.intersects(ringLine)) {
                    return IntersectionType.PARTIAL;
                }
            }
        }

        if (hasInsidePoints) {
            //todo: handle case where ring encases a hole
            return IntersectionType.INSIDE;
        } else {
            return IntersectionType.OUTSIDE;
        }
    }

    public enum IntersectionType {
        INSIDE,
        PARTIAL,
        OUTSIDE
    }
}
