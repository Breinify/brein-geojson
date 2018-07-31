package com.brein.geojson.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TestPolygon {
    final Function<Double, List<Point>> makeRings = radius ->
            Arrays.asList(new Point(-radius, -radius),
                    new Point(radius, -radius),
                    new Point(radius, radius),
                    new Point(-radius, radius),
                    new Point(-radius, -radius));

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

        Assert.assertTrue(new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(2, 0),
                new Point(2, 1),
                new Point(0, 1),
                new Point(0, 0)),
                Collections.emptyList())
                .encases(new Point(0.1, 0.9)));

        Assert.assertFalse(shape.encases(new Point(0.8, 0.8)));
        Assert.assertTrue(shape.encases(new Point(0.1, 0.9)));
        Assert.assertFalse(shape.encases(new Point(-0.1, 0.9)));

        //this line is within the outer ring, but overlaps the inner hole
        Assert.assertFalse(shape.encases(new Line(.1, .5, 1.2, .5)));
        Assert.assertTrue(shape.encases(new Line(.1, .1, .1, .5)));

        Assert.assertFalse(shape.encases(new LineStrings(new Point(.1, .1), new Point(.1, .5), new Point(1.2, .5))));
        Assert.assertTrue(shape.encases(new LineStrings(new Point(.1, .1), new Point(.1, .5), new Point(.1, .75))));

        Assert.assertTrue(shape.encases(new Point(.1, .5)));
        Assert.assertFalse(shape.encases(new Point(.5, .5)));

        Assert.assertFalse(shape.within(new Point(.1, .5)));
        Assert.assertFalse(shape.within(new Point(.5, .5)));
        Assert.assertFalse(shape.within(new LineStrings(new Point(.1, .1), new Point(.1, .5), new Point(1.2, .5))));

    }

    /**
     * Look at cases where two polygons with holes interact with each other
     */
    @Test
    public void testDoughnuts() {

        final Polygon bigNoHole = new Polygon(makeRings.apply(1.5), Collections.emptyList());
        final Polygon bigWithHole = new Polygon(makeRings.apply(1.5),
                Collections.singletonList(makeRings.apply(0.5)));

        final Polygon smallNoHole = new Polygon(makeRings.apply(1.0), Collections.emptyList());

        final Polygon smallWithBigHole = new Polygon(makeRings.apply(1.0),
                Collections.singletonList(makeRings.apply(0.75)));
        final Polygon smallWithSmallHole = new Polygon(makeRings.apply(1.0),
                Collections.singletonList(makeRings.apply(0.25)));

        Assert.assertTrue(bigNoHole.encases(bigWithHole));
        Assert.assertFalse(bigNoHole.within(bigWithHole));
        Assert.assertFalse(bigWithHole.encases(bigNoHole));
        Assert.assertTrue(bigWithHole.within(bigNoHole));

        Assert.assertTrue(bigWithHole.within(bigWithHole));
        Assert.assertTrue(bigWithHole.encases(bigWithHole));

        Assert.assertTrue(bigNoHole.encases(smallNoHole));
        Assert.assertTrue(bigNoHole.encases(smallWithSmallHole));
        Assert.assertTrue(bigNoHole.encases(smallWithBigHole));

        Assert.assertFalse(bigWithHole.encases(smallWithSmallHole));
        Assert.assertTrue(bigWithHole.encases(smallWithBigHole));
    }

    @Test
    public void testPointInPoly() {
        final Polygon square = new Polygon(new Point(-1.5, -1.5), new Point(1.5, -1.5), new Point(1.5, 1.5), new
                Point(-1.5, 1.5), new Point(-1.5, -1.5));

        Assert.assertTrue(new Point(-1, -1).within(square));
    }

    @Test
    public void testPointPolyDistance() {
        final Polygon bigWithHole = new Polygon(makeRings.apply(1.5),
                Collections.singletonList(makeRings.apply(0.5)));

        Assert.assertEquals(0, bigWithHole.distance(new Point(1, 1)), 0.0001);
        Assert.assertEquals(0.5, bigWithHole.distance(new Point(0, 0)), 0.0001);
        Assert.assertEquals(1, bigWithHole.distance(new Point(2.5, 0)), 0.0001);
    }

    @Test
    public void testPolyLineDistance() {

        final Polygon bigWithHole = new Polygon(makeRings.apply(1.5),
                Collections.singletonList(makeRings.apply(0.5)));

        final Line line = new Line(Arrays.asList(new Point(2, 1), new Point(2, 2)));

        Assert.assertEquals(0.5, bigWithHole.distance(line), 0.0001);

        final Line within = new Line(Arrays.asList(new Point(1, 1), new Point(-1, 1)));
        Assert.assertEquals(0, bigWithHole.distance(within), 0.0001);
        Assert.assertEquals(0, within.distance(bigWithHole), 0.0001);
    }

    @Test
    public void testPolyPolyDistance() {

        final Polygon bigWithHole = new Polygon(makeRings.apply(1.5),
                Collections.singletonList(makeRings.apply(0.5)));

        final Polygon triangle = new Polygon(new Point(2.0, 1.0), new Point(2.0, 2.0), new Point(5.0, 1.5), new Point
                (2.0, 1.0));

        Assert.assertEquals(0.5, bigWithHole.distance(triangle), 0.0001);

        final Polygon smallWithBigHole = new Polygon(makeRings.apply(1.0),
                Collections.singletonList(makeRings.apply(0.75)));

        Assert.assertEquals(0.0, smallWithBigHole.distance(bigWithHole), 0.0001);

    }
}