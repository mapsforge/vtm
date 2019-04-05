package org.oscim.utils;

import org.junit.Assert;
import org.junit.Test;
import org.oscim.utils.geom.GeometryUtils;

public class GeometryTest {

    @Test
    public void testClosestPointOnLine2D() {
        float[] pP = {4, 1};
        float[] pL = {1, 2};
        float[] vL = {1, -1};

        float[] point = GeometryUtils.closestPointOnLine2D(pP, pL, vL);
        Assert.assertEquals(point[0], 3, 0.00001);
        Assert.assertEquals(point[1], 0, 0.00001);
    }

    @Test
    public void testDistancePointLine2D() {
        float[] pP = {1, 0};
        float[] pL = {0, 0};
        float[] vL = {2, 2};

        float distance = GeometryUtils.distancePointLine2D(pP, pL, vL);
        Assert.assertEquals(distance, Math.sqrt(2) / 2, 0.00001);
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

    @Test
    public void testIsClockwise() {
        // Coordinate system is LHS
        float[] points = new float[]{0, 0, 1, 0, 1, 1};

        float area = GeometryUtils.isClockwise(points, points.length);
        Assert.assertTrue(area > 0);

        points = new float[]{0, 0, 1, 1, 1, 0};
        area = GeometryUtils.isClockwise(points, points.length);
        Assert.assertTrue(area < 0);
    }

    @Test
    public void testIsTrisClockwise() {
        // Coordinate system is LHS
        float[] pA = new float[]{0, 0};
        float[] pB = new float[]{1, 0};
        float[] pC = new float[]{1, 1};

        float area = GeometryUtils.isTrisClockwise(pA, pB, pC);
        Assert.assertTrue(area > 0);

        area = GeometryUtils.isTrisClockwise(pA, pC, pB);
        Assert.assertTrue(area < 0);
    }
}
