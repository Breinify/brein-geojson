package com.brein.geojson.geometry;

import com.brein.geojson.io.GeoJsonLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestGeometryCollection {
    @Test
    public void testReno() throws IOException {
        final IGeometryObject reno = GeoJsonLoader.loadFromResource("/data/reno.json");

        final IGeometryObject pointsInReno = GeoJsonLoader.loadFromResource("/data/renoTestPoints.json");

        Assert.assertTrue(reno.encases(new Point(-119.88899, 39.63715)));

        Assert.assertTrue(reno.encases(pointsInReno));
        Assert.assertTrue(pointsInReno.within(reno));

        final IGeometryObject pointsOutsideReno = GeoJsonLoader.loadFromResource("/data/renoTestPointsOutside.json");

        Assert.assertFalse(reno.encases(pointsOutsideReno));
        Assert.assertFalse(pointsOutsideReno.within(reno));

        final List<IGeometryObject> shapes = ((MultiPoints) pointsOutsideReno).getShapes();

        for (final IGeometryObject shape : shapes) {
            Assert.assertFalse(shape.within(reno));
            Assert.assertFalse(reno.encases(shape));
        }
    }
}
