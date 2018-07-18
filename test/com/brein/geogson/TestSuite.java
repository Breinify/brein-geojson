package com.brein.geogson;

import com.brein.geogson.geometry.TestLine;
import com.brein.geogson.geometry.TestPolygon;
import com.brein.geogson.tools.TestCommonGeoMath;
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
