package com.brein.geojson.geometry;

import com.brein.geojson.tools.Constants;
import com.brein.geojson.tools.GeoJsonException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IGeometryObjectFactory {
    @SuppressWarnings("unchecked")
    public static IGeometryObject fromGeoJsonMap(final Map<String, Object> in) {
        final String type = (String) in.get(Constants.GEOJSON_TYPE);
        final Object coordinates = in.get(Constants.GEOJSON_COORDINATES);

        // if we have no type check for GEOJSON_GEOMETRY
        if (type == null) {
            final Map<String, Object> geometry = Map.class.cast(in.get(Constants.GEOJSON_GEOMETRY));
            if (geometry == null) {
                throw new GeoJsonException("Can't understand type " + in);
            } else {
                return fromGeoJsonMap(geometry);
            }
        } else {

            switch (type) {
                case Constants.GEOJSON_TYPE_POINT:
                    return makePoint(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_LINE_STRING:
                    return makeLineStrings(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_POLYGON:
                    return makePolygon(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_MULTI_POINT:
                    return makeMultiPoint(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_MULTI_LINE_STRING:
                    return makeMultiLineString(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_MULTI_POLYGON:
                    return makeMultiPolygon(List.class.cast(coordinates));
                case Constants.GEOJSON_TYPE_GEOMETRY_COLLECTION:
                    return makeGeoCollection(List.class.cast(in.get(Constants.GEOJSON_GEOMETRIES)));
                case Constants.GEOJSON_TYPE_FEATURE_COLLECTION:

                    // ignore the features information
                    final List<Map<String, Object>> features = List.class.cast(in.get(Constants.GEOJSON_FEATURES));
                    return new GeometryCollection(features.stream()
                            .map(IGeometryObjectFactory::fromGeoJsonMap)
                            .collect(Collectors.toList()));
                case Constants.GEOJSON_TYPE_FEATURE:

                    // ignore the feature information
                    final Map<String, Object> geometry = Map.class.cast(in.get(Constants.GEOJSON_GEOMETRY));
                    return fromGeoJsonMap(geometry);

                default:
                    throw new GeoJsonException("Can't understand type " + type);
            }
        }
    }

    public static Point makePoint(final List<Double> coordinates) {
        //the geojson standard allows for a height parameter, but we ignore it for our case
        if (coordinates.size() < 2 || coordinates.size() > 3) {
            throw new GeoJsonException("Unexpected number of coordinates supplied, expected 2 or 3 got " +
                    coordinates.size());
        }
        return new Point(coordinates.get(0), coordinates.get(1));
    }

    public static LineStrings makeLineStrings(final List<List<Double>> coordinates) {
        return new LineStrings(coordinates.stream()
                .map(IGeometryObjectFactory::makePoint)
                .collect(Collectors.toList()));
    }

    public static Polygon makePolygon(final List<List<List<Double>>> coordinates) {
        final List<List<Point>> allLines = coordinates.stream()
                .map(IGeometryObjectFactory::makeLineStrings)
                .map(LineStrings::getPoints)
                .collect(Collectors.toList());

        return new Polygon(allLines.get(0),
                allLines.size() == 1 ? Collections.emptyList() : allLines.subList(1, allLines.size()));
    }

    public static MultiPoints makeMultiPoint(final List<List<Double>> coordinates) {
        return new MultiPoints(coordinates.stream()
                .map(IGeometryObjectFactory::makePoint)
                .collect(Collectors.toList()));
    }

    public static MultiLineStrings makeMultiLineString(final List<List<List<Double>>> coordinates) {
        return new MultiLineStrings(coordinates.stream()
                .map(IGeometryObjectFactory::makeLineStrings)
                .flatMap(lines -> lines.getShapes().stream())
                .map(shape -> (Line) shape)
                .collect(Collectors.toList()));
    }

    public static MultiPolygons makeMultiPolygon(final List<List<List<List<Double>>>> coordinates) {
        return new MultiPolygons(coordinates.stream()
                .map(IGeometryObjectFactory::makePolygon)
                .collect(Collectors.toList()));
    }

    public static GeometryCollection makeGeoCollection(final List<Map<String, Object>> geometries) {
        return new GeometryCollection(geometries.stream()
                .map(IGeometryObjectFactory::fromGeoJsonMap)
                .collect(Collectors.toList()));
    }

}
