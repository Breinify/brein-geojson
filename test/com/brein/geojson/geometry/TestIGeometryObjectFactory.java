package com.brein.geojson.geometry;

import com.brein.geojson.io.GeoJsonLoader;
import com.brein.geojson.tools.Constants;
import com.brein.geojson.tools.GeoJsonException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestIGeometryObjectFactory {

    @Test(expected = GeoJsonException.class)
    public void testBadType() {
        IGeometryObjectFactory.fromGeoJsonMap(Collections.singletonMap(Constants.GEOJSON_TYPE, "some bad type"));
    }

    @Test
    public void testLoadPoint() throws IOException {
        final Point point = (Point) GeoJsonLoader.loadFromResource("/data/samplePoint.json");

        Assert.assertEquals(new Point(100, 50.5), point);
    }

    @Test
    public void testLoadLine() throws IOException {
        final LineStrings lines = (LineStrings) GeoJsonLoader.loadFromResource("/data/sampleLineStrings.json");

        final List<Point> points = lines.getPoints();

        Assert.assertEquals(Arrays.asList(new Point(0.0, 0.0), new Point(1.0, 1.0), new Point(0.0, 1.0)), points);
    }

    @Test
    public void testLoadSimplePolygon() throws IOException {
        final Polygon poly = (Polygon) GeoJsonLoader.loadFromResource("/data/samplePolygon.json");

        Assert.assertEquals(0, poly.getHoles().size());

        Assert.assertEquals(Arrays.asList(
                new Line(0.0, 0.0, 10.0, 0.0),
                new Line(10, 0, 10, 10),
                new Line(10, 10, 0, 10),
                new Line(0, 10, 0, 0)),
                poly.getRing());
    }

    @Test
    public void testLoadPolygonWithHole() throws IOException {
        final Polygon poly = (Polygon) GeoJsonLoader.loadFromResource("/data/samplePolygonHole.json");

        Assert.assertEquals(1, poly.getHoles().size());

        Assert.assertEquals(Arrays.asList(
                new Line(0.0, 0.0, 10.0, 0.0),
                new Line(10, 0, 10, 10),
                new Line(10, 10, 0, 10),
                new Line(0, 10, 0, 0)),
                poly.getRing());

        Assert.assertEquals(Arrays.asList(
                new Line(4.0, 4.0, 6.0, 4.0),
                new Line(6, 4, 6, 6),
                new Line(6, 6, 4, 6),
                new Line(4, 6, 4, 4)),
                poly.getHoles().get(0));
    }

    @Test
    public void testFeatureCollection() throws IOException {
        final GeometryCollection coll =
                (GeometryCollection) GeoJsonLoader.loadFromResource("/data/sampleFeatureCollection.json");
        Assert.assertEquals(1, coll.size());

        final IGeometryObject shape = coll.getShapes().get(0);
        Assert.assertTrue(Polygon.class.isInstance(shape));

        final Polygon polygon = Polygon.class.cast(shape);
        Assert.assertEquals(Arrays.asList(
                new Line(-122.4010678561855, 37.97908330861924, -122.384588363998, 37.85449295424837),
                new Line(-122.384588363998, 37.85449295424837, -122.23489964329488, 37.95418212244576),
                new Line(-122.23489964329488, 37.95418212244576, -122.4010678561855, 37.97908330861924)),
                polygon.getRing());
    }
}