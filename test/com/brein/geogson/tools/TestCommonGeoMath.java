package com.brein.geogson.tools;

import com.brein.geogson.geometry.BoundingBox;
import com.brein.geogson.geometry.Line;
import com.brein.geogson.geometry.Point;
import com.brein.geogson.geometry.Polygon;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestCommonGeoMath {

    @Test
    public void pointInRing() {
        final List<Line> ring = Polygon.pointsToLine(Arrays.asList(
                new Point(4, 13),
                new Point(-8, 7),
                new Point(2, 9),
                new Point(-9, -5),
                new Point(12, 6),
                new Point(4, 6),
                new Point(4, 13)
        ));

        final BoundingBox bbox = new BoundingBox(new Point(12, 13), new Point(-9, -5));

        Assert.assertTrue(CommonGeoMath.pointInRing(new Point(2, 11), ring, bbox));
        Assert.assertFalse(CommonGeoMath.pointInRing(new Point(6, 8), ring, bbox));
        Assert.assertFalse(CommonGeoMath.pointInRing(new Point(14, 19), ring, bbox));
        Assert.assertFalse(CommonGeoMath.pointInRing(new Point(-2, 7), ring, bbox));
        Assert.assertTrue(CommonGeoMath.pointInRing(new Point(-6, -2), ring, bbox));
        Assert.assertFalse(CommonGeoMath.pointInRing(new Point(3, -2), ring, bbox));
        Assert.assertTrue(CommonGeoMath.pointInRing(new Point(0, 0), ring, bbox));

    }

    @Test
    public void testRingArea() {
        final List<Line> square = Polygon.pointsToLine(Arrays.asList(
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, 1)
        ));

        Assert.assertEquals(1.0, CommonGeoMath.ringArea(square), 0.0001);

        final List<Line> squareLessTriangle = Polygon.pointsToLine(Arrays.asList(
                new Point(0, 0),
                new Point(1, 0),
                new Point(0.5, 0.5),
                new Point(1, 1),
                new Point(0, 1)
        ));

        Assert.assertEquals(0.75, CommonGeoMath.ringArea(squareLessTriangle), 0.0001);
    }

    @Test
    public void testCentroid() {
        final List<Line> square = Polygon.pointsToLine(Arrays.asList(
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, 1)
        ));

        Assert.assertEquals(new Point(0.5, 0.5), CommonGeoMath.ringCentroid(square));

        final List<Line> squareLessTriangle = Polygon.pointsToLine(Arrays.asList(
                new Point(0, 0),
                new Point(1, 0),
                new Point(0.5, 0.5),
                new Point(1, 1),
                new Point(0, 1)
        ));

        // object is mirrored, so only care about one side of it, upper triangle is centered at .5/3 + .5, lower
        // square is at .25, reweight both so upper is half the weight of lower square
        Assert.assertEquals(new Point((.5 * (.5 / 3 + .5) + .25) / 1.5, 0.5),
                CommonGeoMath.ringCentroid(squareLessTriangle));
    }
}