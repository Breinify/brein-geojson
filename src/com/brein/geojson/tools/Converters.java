package com.brein.geojson.tools;

public class Converters {
    private static final double EARTH_RADIUS_KM = 6371;
    private static final double KM_AT_EQUATOR = 111.32;

    /**
     * Converts the length of an arc to kilometers on Earth
     */
    public static double distanceToKm(final double lat1, final double lon1, final double lat2, final double lon2) {

        final double lat1rad = Math.toRadians(lat1);
        final double lat2rad = Math.toRadians(lat2);

        final double deltaLat = Math.toRadians(lat2 - lat1);
        final double deltaLon = Math.toRadians(lon2 - lon1);

        final double haversine = Math.pow(Math.sin(deltaLat / 2), 2.0) + Math.cos(lat1rad) * Math.cos(lat2rad)
                * Math.pow(Math.sin(deltaLon / 2), 2.0);

        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    public static double distanceToMiles(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double distance = distanceToKm(lat1, lon1, lat2, lon2);
        return kmToMiles(distance);
    }

    /**
     * Determines the length (in degrees) of an east/west facing arc of a given length (in km) for a given latitude
     */
    public static double kmToDegrees(final double km, final double latitude) {
        return km / KM_AT_EQUATOR / Math.cos(Math.toRadians(latitude));
    }

    public static double milesToDegrees(final double miles, final double latitude) {
        return milesToKm(miles) / KM_AT_EQUATOR / Math.cos(Math.toRadians(latitude));
    }

    public static double kmToMiles(final double km) {
        return km * 0.621371;
    }

    public static double milesToKm(final double mi) {
        return mi / 0.621371;
    }
}
