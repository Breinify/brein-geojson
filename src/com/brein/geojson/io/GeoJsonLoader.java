package com.brein.geojson.io;

import com.brein.geojson.geometry.IGeometryObject;
import com.brein.geojson.geometry.IGeometryObjectFactory;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public class GeoJsonLoader {
    public static IGeometryObject loadFromResource(final String resourceName) throws IOException {
        try (final InputStream inStream = GeoJsonLoader.class.getResourceAsStream(resourceName)) {
            final BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

            return loadFromString(String.join("\n", in.lines().collect(Collectors.toList())));
        }
    }

    public static IGeometryObject loadFromFile(final String fileName) throws IOException {
        return loadFromFile(new File(fileName));
    }

    public static IGeometryObject loadFromFile(final File jsonFile) throws IOException {
        return loadFromString(String.join("\n", Files.readAllLines(jsonFile.toPath())));
    }

    public static IGeometryObject loadFromString(final String jsonString) {
        //noinspection unchecked
        return loadFromMap(new Gson().fromJson(jsonString, Map.class));
    }

    public static IGeometryObject loadFromMap(final Map<String, Object> json) {
        //noinspection unchecked
        return IGeometryObjectFactory.fromGeoJsonMap((Map<String, Object>) json.get("geometry"));
    }
}
