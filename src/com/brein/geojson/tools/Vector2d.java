package com.brein.geojson.tools;

import com.brein.geojson.geometry.Point;

public class Vector2d {
    private final double x;
    private final double y;

    public Vector2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(final Point p) {
        this(p.getLat(), p.getLon());
    }

    public Vector2d add(final Vector2d other) {
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d subtract(final Vector2d other) {
        return new Vector2d(x - other.x, y - other.y);
    }

    public double cross(final Vector2d other) {
        return this.x * other.y - this.y * other.x;
    }

    public Vector2d scale(final double t) {
        return new Vector2d(this.x * t, this.y * t);
    }

    public double dot(final Vector2d other) {
        return x * other.x + y * other.y;
    }
}
