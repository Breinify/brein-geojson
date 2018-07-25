package com.brein.geojson.docs;

import com.brein.geojson.geometry.IGeometryObject;
import com.brein.geojson.geometry.Point;
import com.brein.geojson.io.GeoJsonLoader;
import org.junit.Test;

import java.io.IOException;

public class TestDemo {
    @Test
    public void sample() throws IOException {
        final IGeometryObject square = GeoJsonLoader.loadFromResource("/data/samplePolygon.json");

        System.out.println("Loaded: " + square);
        System.out.println("A map representing the object's json is " + square.toMap());
        System.out.println();
        System.out.println("The center of the object is " + square.centroid());
        System.out.println("Surface Area is " + square.surfaceArea());
        System.out.println("The object's bounding box is " + square.boundingBox());

        System.out.println();
        final Point pointInSquare = new Point(1, 6);
        System.out.println(pointInSquare + " is " + (pointInSquare.within(square) ? "" : " not ") + "within the " +
                "square");

        final Point pointOutsideSquare = new Point(12, -2);
        System.out.println(pointOutsideSquare + " is " + (pointOutsideSquare.within(square) ? "" : "not ") + "within" +
                " the square");
    }
}
