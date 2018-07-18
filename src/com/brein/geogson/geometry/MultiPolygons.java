package com.brein.geogson.geometry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiPolygons extends GeometryCollection {
    public MultiPolygons(final List<Polygon> polygons) {
        super(polygons.stream().map(p -> (IGeometryObject) p).collect(Collectors.toList()));
    }

    public MultiPolygons(final Polygon... polygons) {
        this(Arrays.asList(polygons));
    }
}
