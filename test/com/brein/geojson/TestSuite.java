package com.brein.geojson;

import com.brein.geojson.geometry.TestGeometryCollection;
import com.brein.geojson.geometry.TestIGeometryObjectFactory;
import com.brein.geojson.geometry.TestLine;
import com.brein.geojson.geometry.TestPolygon;
import com.brein.geojson.io.TestGeoJsonLoader;
import com.brein.geojson.tools.TestCommonGeoMath;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // math
        TestLine.class,
        TestPolygon.class,
        TestCommonGeoMath.class,
        TestGeometryCollection.class,

        // loading
        TestGeoJsonLoader.class,
        TestIGeometryObjectFactory.class,

        // tools
        TestCommonGeoMath.class
})
public class TestSuite {
}
