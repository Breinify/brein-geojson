package com.brein.geojson.geometry;

import com.brein.geojson.tools.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineStrings extends GeometryCollection {
    private final List<Point> points;

    public LineStrings(final List<Point> points) {
        super(Polygon.pointsToLine(points).stream().map(l -> (IGeometryObject) l).collect(Collectors.toList()));
        this.points = points;
    }

    public LineStrings(final Point... points) {
        this(Arrays.asList(points));
    }

    @Override
    public Map<String, Object> toMap() {
        final Map<String, Object> res = new HashMap<>();
        res.put(Constants.GEOJSON_TYPE, Constants.GEOJSON_TYPE_LINE_STRING);

        final List<List<Double>> coordinates = points.stream().map(Point::getCoordinates).collect(Collectors.toList());
        res.put(Constants.GEOJSON_COORDINATES, coordinates);

        return res;
    }

    public List<Point> getPoints() {
        return points;
    }
}
