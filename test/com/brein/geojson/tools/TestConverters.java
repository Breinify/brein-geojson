package com.brein.geojson.tools;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestConverters {

    @Test
    public void degreesToKm() {
        Assert.assertEquals(8117, Converters.distanceToKm(20, 15, 25, 95), 1.0);
        Assert.assertEquals(7553, Converters.distanceToKm(85, 0, 18, 35), 1.0);
    }

    @Test
    public void kmToDegrees() {
        Assert.assertEquals(1.0, Converters.kmToDegrees(111.32, 0), 0.01);
        Assert.assertEquals(2.0, Converters.kmToDegrees(111.32 * 2, 0), 0.01);

        Assert.assertEquals(1.0, Converters.kmToDegrees(85.39, 40), 0.01);
        Assert.assertEquals(2.0, Converters.kmToDegrees(85.39 * 2, 40), 0.01);
        Assert.assertEquals(1.0, Converters.kmToDegrees(85.39, -40), 0.01);
        Assert.assertEquals(2.0, Converters.kmToDegrees(85.39 * 2, -40), 0.01);

        Assert.assertEquals(1.0, Converters.kmToDegrees(19.39, 80), 0.01);
        Assert.assertEquals(2.0, Converters.kmToDegrees(19.39 * 2, 80), 0.01);
        Assert.assertEquals(1.0, Converters.kmToDegrees(19.39, -80), 0.01);
        Assert.assertEquals(2.0, Converters.kmToDegrees(19.39 * 2, -80), 0.01);

    }

    @Test
    public void testUnitConversion() {
        Assert.assertEquals(3.11, Converters.kmToMiles(5.0), 0.01);
        Assert.assertEquals(32.19, Converters.milesToKm(20), 0.01);
    }
}