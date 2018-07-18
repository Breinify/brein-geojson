package com.brein.geojson.io;

import com.brein.geojson.geometry.IGeometryObject;
import com.brein.geojson.geometry.Point;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestGeoJsonLoader {

    @Test
    public void loadFromResource() throws IOException {
        final IGeometryObject pt = GeoJsonLoader.loadFromResource("/data/samplePoint.json");

        Assert.assertTrue(pt.getClass().isAssignableFrom(Point.class));

        final Point point = (Point) pt;
        Assert.assertEquals(100, ((Point) pt).getLat(), 0.0001);
        Assert.assertEquals(50.5, ((Point) pt).getLon(), 0.0001);
    }
}