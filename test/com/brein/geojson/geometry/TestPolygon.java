package com.brein.geojson.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class TestPolygon {

    @Test
    public void testPolyWithHole() {
        final Polygon shape = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(2, 0),
                new Point(2, 1),
                new Point(0, 1),
                new Point(0, 0)),
                Collections.singletonList(Arrays.asList(
                        new Point(.2, .3),
                        new Point(1, .3),
                        new Point(1, .9),
                        new Point(.2, .9),
                        new Point(.2, .3)
                )));

        Assert.assertEquals(1.52, shape.surfaceArea(), 0.0001);

        Assert.assertTrue(new Point(1.1263157, 0.4684210526).distance(shape.centroid()) < 0.0001);

        Assert.assertFalse(shape.encases(new Point(0.8, 0.8)));
        Assert.assertTrue(shape.encases(new Point(0.1, 0.9)));
        Assert.assertFalse(shape.encases(new Point(-0.1, 0.9)));
    }
}