package com.brein.geojson.geometry;

import com.brein.geojson.tools.CommonGeoMath;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public interface IGeometryObject {
    /**
     * Compares if this object is entirely within another object. The opposite of `encases(...)`.
     *
     * @param other the object to compare to
     */
    boolean within(final IGeometryObject other);

    /**
     * Compares if this object entirely surrounds another object. The opposite of `within(...)`.
     *
     * @param other the object to compare to
     */
    boolean encases(final IGeometryObject other);

    /**
     * Compares if two objects touch or overlap each other
     *
     * @param other the object to compare to
     */
    default boolean intersects(final IGeometryObject other) {
        return CommonGeoMath.approxEquals(distance(other), 0);
    }

    /**
     * Determines the distance between this and another object
     *
     * @param other the object to measure the distance from
     *
     * @return the distance between the two objects
     */
    double distance(final IGeometryObject other);

    /**
     * @return The surface area of the object, in square units
     */
    double surfaceArea();

    /**
     * @return The bounds of the object
     */
    BoundingBox boundingBox();

    /**
     * @return The center of the object
     */
    Point centroid();

    /**
     * @return A Map<String, Object> representation of the GeoGson of the object
     */
    Map<String, Object> toMap();

    /**
     * Gets a sorted list of nearby objects
     *
     * @param shapes      the shapes to compare distances
     * @param maxDistance the largest distance for a returned shape
     *
     * @return A sorted list of shapes and distances
     */
    default List<Entry<IGeometryObject, Double>> objectsNearby(final Collection<IGeometryObject> shapes,
                                                               final double maxDistance) {
        return shapes.stream().map(s -> new SimpleEntry<>(s, distance(s)))
                .filter(e -> e.getValue() <= maxDistance)
                .sorted(Comparator.comparing(SimpleEntry::getValue))
                .collect(Collectors.toList());
    }
}
