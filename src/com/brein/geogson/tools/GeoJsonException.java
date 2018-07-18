package com.brein.geogson.tools;

public class GeoJsonException extends RuntimeException {
    public GeoJsonException(final String message) {
        super(message);
    }

    public GeoJsonException(final String message, final Exception cause) {
        super(message, cause);
    }
}
