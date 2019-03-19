package org.oscim.utils;

import org.junit.Assert;
import org.junit.Test;
import org.oscim.utils.geom.GeometryUtils;

public class GeometryTest {

    @Test
    public void testIsTrisClockwise() {
        // Coordinate system is lhs
        float[] pA = new float[]{0, 0};
        float[] pB = new float[]{1, 0};
        float[] pC = new float[]{1, 1};

        float area = GeometryUtils.isTrisClockwise(pA, pB, pC);
        Assert.assertTrue(area > 0);

        area = GeometryUtils.isTrisClockwise(pA, pC, pB);
        Assert.assertTrue(area < 0);
    }

    @Test
    public void testIsClockwise() {
        // Coordinate system is lhs
        float[] points = new float[]{0, 0, 1, 0, 1, 1};

        float area = GeometryUtils.isClockwise(points, points.length);
        Assert.assertTrue(area > 0);

        points = new float[]{0, 0, 1, 1, 1, 0};
        area = GeometryUtils.isClockwise(points, points.length);
        Assert.assertTrue(area < 0);
    }

    @Test
    public void testDotProduct() {
        float[] p = {-1, 0, 0, 0, 0, 0};

        for (int i = 0; i < 9; i++) {
            p[4] = (float) Math.cos(Math.toRadians(i * 45));
            p[5] = (float) Math.sin(Math.toRadians(i * 45));
            System.out.println("\n> " + (i * 45) + " " + p[3] + ":" + p[4] + "\n="
                    + GeometryUtils.dotProduct(p, 0, 2, 4));
        }
    }
}
