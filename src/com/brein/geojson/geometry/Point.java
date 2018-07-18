package com.brein.geojson.geometry;

import com.brein.geojson.tools.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Point implements IGeometryObject {
    private final double lat;
    private final double lon;

    public Point(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean within(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            final Point o = (Point) other;
            return this.lat == o.lat && this.lon == o.lon;
        } else {
            return other.encases(this);
        }
    }

    @Override
    public boolean encases(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            final Point o = (Point) other;
            return this.lat == o.lat && this.lon == o.lon;
        } else {
            final BoundingBox bbox = other.boundingBox();
            if (!bbox.getDownLeft().equals(bbox.getUpRight())) {
                return false;
            }
            return this.lat == bbox.getDownLeft().lat && this.lon == bbox.getDownLeft().lon;
        }
    }

    @Override
    public double distance(final IGeometryObject other) {
        if (Point.class.isAssignableFrom(other.getClass())) {
            final Point o = (Point) other;
            return Math.sqrt(Math.pow(this.lat - o.lat, 2) + Math.pow(this.lon - o.lon, 2));
        }
        return 0;
    }

    @Override
    public double surfaceArea() {
        return 0;
    }

    @Override
    public BoundingBox boundingBox() {
        return new BoundingBox(this);
    }

    @Override
    public Point centroid() {
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        final Map<String, Object> res = new HashMap<>();
        res.put(Constants.GEOJSON_TYPE, Constants.GEOJSON_TYPE_POINT);
        res.put(Constants.GEOJSON_COORDINATES, getCoordinates());
        return res;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        } else if (other.getClass().equals(this.getClass())) {
            final Point o = (Point) other;
            return this.lat == o.lat && this.lon == o.lon;
        } else {
            return false;
        }
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "(" + getLat() + "," + getLon() + ")";
    }

    public List<Double> getCoordinates() {
        return Arrays.asList(getLat(), getLon());
    }

}
