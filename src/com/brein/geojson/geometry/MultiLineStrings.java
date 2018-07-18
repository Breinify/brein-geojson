package com.brein.geojson.geometry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiLineStrings extends GeometryCollection {
    public MultiLineStrings(final List<Line> lines) {
        super(lines.stream().map(l -> (IGeometryObject) l).collect(Collectors.toList()));
    }

    public MultiLineStrings(final Line... lines) {
        this(Arrays.asList(lines));
    }
}
