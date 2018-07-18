package com.brein.geojson.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestLine {
    @Test
    public void testCollision() {
        final Line l1 = makeLine(-3, -1, 4, 1);
        final Line l2 = makeLine(2, 4, -1, -5);

        Assert.assertTrue(l1.intersects(l2));
        Assert.assertTrue(l2.intersects(l1));
        Assert.assertTrue(l1.intersects(l1));

        //l1 moved up
        Assert.assertFalse(makeLine(-2, -1, 5, 1).intersects(l1));

        //Rays intersect, segments do not
        Assert.assertFalse(makeLine(-3, -3, -1, -1).intersects(l1));

        //horizontal lines
        Assert.assertTrue(makeLine(1, 1, 0, 1).intersects(makeLine(.75, 1, 1.25, 1)));
        Assert.assertFalse(makeLine(1, 1, 0, 1).intersects(makeLine(-.75, 1, -1.25, 1)));

        //vertical lines
        Assert.assertTrue(makeLine(1, 0, 1, 1).intersects(makeLine(1, .75, 1, 1.25)));
        Assert.assertFalse(makeLine(1, 1, 1, 0).intersects(makeLine(1, -.75, 1, -1.25)));
    }

    @Test
    public void testPointOnLine() {
        Assert.assertTrue(makeLine(0, 0, 1, 1).encases(new Point(0.5, 0.5)));
        Assert.assertFalse(makeLine(0, 0, 1, 1).encases(new Point(0.75, 0.5)));
        Assert.assertTrue(makeLine(0, 0, 1, 1).encases(new Point(0.0, 0.0)));
    }

    public Line makeLine(final double x1, final double y1, final double x2, final double y2) {
        return new Line(Arrays.asList(new Point(x1, y1), new Point(x2, y2)));
    }
}
