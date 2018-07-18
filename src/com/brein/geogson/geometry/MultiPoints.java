package com.brein.geogson.geometry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiPoints extends GeometryCollection {
    public MultiPoints(final List<Point> points) {
        super(points.stream().map(p -> (IGeometryObject) p).collect(Collectors.toList()));
    }

    public MultiPoints(final Point... points) {
        this(Arrays.asList(points));
    }
}
