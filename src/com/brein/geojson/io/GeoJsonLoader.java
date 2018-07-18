package com.brein.geojson.io;

import com.brein.geojson.geometry.IGeometryObject;
import com.brein.geojson.geometry.IGeometryObjectFactory;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class GeoJsonLoader {
    public static IGeometryObject loadFromFile(final String fileName) throws IOException {
        return loadFromFile(new File(fileName));
    }

    public static IGeometryObject loadFromFile(final File jsonFile) throws IOException {
        return loadFromString(String.join("\n", Files.readAllLines(jsonFile.toPath())));
    }

    public static IGeometryObject loadFromString(final String jsonString) {
        //noinspection unchecked
        return IGeometryObjectFactory.fromGeoJsonMap(new Gson().fromJson(jsonString, Map.class));
    }

}
