package com.brein.geojson;

import com.brein.geojson.geometry.TestLine;
import com.brein.geojson.geometry.TestPolygon;
import com.brein.geojson.tools.TestCommonGeoMath;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestLine.class,
        TestPolygon.class,
        TestCommonGeoMath.class
})
public class TestSuite {
}
