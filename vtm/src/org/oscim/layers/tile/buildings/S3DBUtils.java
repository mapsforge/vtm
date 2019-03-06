/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017-2019 Gustl22
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.layers.tile.buildings;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.Tag;
import org.oscim.utils.ColorsCSS;
import org.oscim.utils.Tessellator;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides utils for S3DB layers.
 */
public final class S3DBUtils {
    private static final Logger log = LoggerFactory.getLogger(S3DBUtils.class);

    // Toggle this to debug and improve ridge calculation, you can see the faults in map then.
    private static final boolean IMPROVE_RIDGE_CALCULATION = false;
    private static final int SNAP_THRESHOLD = 70; // Threshold for ridge snap calculation (maybe should depend on map scale)

    /**
     * Adds point to ridgePoints and snaps it to a point which is in radius of SNAP_THRESHOLD.
     */
    private static void addSnapRidgePoint(int id, float[] point, TreeMap<Integer, float[]> ridgePoints) {
        // Simplify ridgePoints
        if (point == null) return;
        for (float[] ridPoint : ridgePoints.values()) {
            if (ridPoint == null) {
                log.debug("Ridge point not found!");
                continue;
            }
            if (GeometryUtils.distance2D(ridPoint, point) < SNAP_THRESHOLD) {
                ridgePoints.put(id, ridPoint);
                return;
            }
        }
        ridgePoints.put(id, point);
    }

    /**
     * Calculates a circle mesh of a Poly-GeometryBuffer.
     *
     * @param element the GeometryBuffer which is used to write the 3D mesh
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcCircleMesh(GeometryBuffer element, float minHeight, float maxHeight, String type) {
        float[] points = element.points;
        int[] index = element.index;

        boolean outerProcessed = false;
        for (int i = 0, pointPos = 0; i < index.length && !outerProcessed; i++) {
            if (index[i] < 0) {
                break;
            }

            int numSections = index[i] / 2;
            if (numSections < 0) continue;

            outerProcessed = true;

            // Init mesh
            GeometryBuffer mesh;
            switch (type) {
                case Tag.VALUE_ONION:
                    float[][] onionShape = new float[][]{
                            {1, 0, 0},
                            {0.2f, 0, 0.01f},
                            {0.875f, 0, 0.1875f},
                            {1, 0, 0.375f},
                            {0.875f, 0, 0.5625f},
                            {0.5f, 0, 0.75f},
                            {0.2f, 0, 0.8125f},
                            {0, 0, 1}};
                    mesh = initCircleMesh(onionShape, numSections);
                    break;
                case Tag.VALUE_DOME:
                default:
                    float[][] domeShape = new float[][]{
                            {1, 0, 0},
                            {0.825f, 0, 0.5f},
                            {0.5f, 0, 0.825f},
                            {0, 0, 1}};
                    mesh = initCircleMesh(domeShape, numSections);
                    break;
            }


            // Calculate center and load points
            float centerX = 0;
            float centerY = 0;
            float radius = 0;

            List<float[]> point3Fs = new ArrayList<>();

            for (int j = 0; j < (numSections * 2); j += 2, pointPos += 2) {
                float x = points[pointPos];
                float y = points[pointPos + 1];

                point3Fs.add(new float[]{x, y, minHeight});

                centerX += x;
                centerY += y;
            }

            centerX = centerX / numSections;
            centerY = centerY / numSections;

            // Calc max radius
            for (float[] point3F : point3Fs) {
                float difX = point3F[0] - centerX;
                float difY = point3F[1] - centerY;
                float tmpR = (float) Math.sqrt(difX * difX + difY * difY);
                if (tmpR > radius) {
                    radius = tmpR;
                }
            }

            element.points = mesh.points;

            // Calc radius and adjust angle
            int numPointsPerSection = (element.points.length / (3 * numSections));
            float heightRange = maxHeight - minHeight;
            for (int k = 0, j = 0; k < numSections; k++) {
                float px = point3Fs.get(k)[0] - centerX;
                float py = point3Fs.get(k)[1] - centerY;

                float phi = (float) Math.atan2(py, px);
                int sectionLimit = (numPointsPerSection + numPointsPerSection * k) * 3;

                for (boolean first = true; j < sectionLimit; j = j + 3) {
                    float r = element.points[j + 0] * radius; // Set radius to initial x (always positive) stretched with individual element radius
                    px = (float) (r * Math.cos(phi));
                    py = (float) (r * Math.sin(phi));

                    if (!first) {
                        element.points[j + 0] = centerX + px;
                        element.points[j + 1] = centerY + py;
                        element.points[j + 2] = minHeight + element.points[j + 2] * heightRange;
                    } else {
                        // Set lowest points to outline points.
                        first = false;
                        element.points[j + 0] = point3Fs.get(k)[0];
                        element.points[j + 1] = point3Fs.get(k)[1];
                        element.points[j + 2] = minHeight;
                    }
                }
            }

            element.index = mesh.index;
            element.pointNextPos = element.points.length;
        }

        element.type = GeometryBuffer.GeometryType.TRIS;
        return true;
    }

    /**
     * Calculates a flat mesh of a Poly-GeometryBuffer.
     *
     * @param element the GeometryBuffer which is used to write the 3D mesh
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcFlatMesh(GeometryBuffer element, float maxHeight) {

        if (Tessellator.tessellate(element, element) == 0) return false;

        float[] points = element.points;
        List<float[]> point3Fs = new ArrayList<>();

        // Calculate center and load points
        for (int pointPos = 0; pointPos < points.length; pointPos += 2) {
            float x = points[pointPos];
            float y = points[pointPos + 1];
            point3Fs.add(new float[]{x, y, maxHeight});
        }

        points = new float[3 * point3Fs.size()];
        for (int i = 0; i < point3Fs.size(); i++) {
            int pPos = 3 * i;
            float[] point3D = point3Fs.get(i);
            points[pPos + 0] = point3D[0];
            points[pPos + 1] = point3D[1];
            points[pPos + 2] = point3D[2];
        }

        element.points = points;
        element.pointNextPos = element.points.length;
        element.type = GeometryBuffer.GeometryType.TRIS;
        return true;
    }

    /**
     * Calculates a mesh for the outlines of a Poly-GeometryBuffer.
     *
     * @param element the GeometryBuffer which is used to write the 3D mesh
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcOutlines(GeometryBuffer element, float minHeight, float maxHeight) {
        float[] points = element.points;
        int[] index = element.index;

        element.points = null;
        element.index = null;

        for (int i = 0, pointPos = 0; i < index.length; i++) {
            if (index[i] < 0) {
                break;
            }

            int numPoints = index[i] / 2;
            if (numPoints < 0) continue;

            List<float[]> point3Fs = new ArrayList<>();

            // load points
            for (int j = 0; j < numPoints; j++, pointPos += 2) {
                float x = points[pointPos];
                float y = points[pointPos + 1];

                point3Fs.add(new float[]{x, y, minHeight});
                point3Fs.add(new float[]{x, y, maxHeight});
            }

            // Write index: index gives the first point of triangle mesh (divided 3)
            int[] meshIndex = new int[numPoints * 6]; // 3 vertices and each side needs 2 triangles
            for (int j = 0; j < point3Fs.size(); j = j + 2) {
                int pos = 3 * j; // triangle mesh
                meshIndex[pos + 2] = j;
                meshIndex[pos + 1] = (j + 1) % point3Fs.size();
                meshIndex[pos + 0] = (j + 3) % point3Fs.size();

                meshIndex[pos + 5] = (j + 3) % point3Fs.size();
                meshIndex[pos + 4] = (j + 2) % point3Fs.size();
                meshIndex[pos + 3] = (j);
            }

            // Write points
            float[] meshPoints = new float[point3Fs.size() * 3];
            for (int j = 0; j < point3Fs.size(); j++) {
                int pos = 3 * j;
                meshPoints[pos + 0] = point3Fs.get(j)[0];
                meshPoints[pos + 1] = point3Fs.get(j)[1];
                meshPoints[pos + 2] = point3Fs.get(j)[2];
            }

            // Init points and indices or add more polygons (e.g. inner rings)
            if (element.points == null) {
                element.points = meshPoints;
            } else {
                float[] tmpPoints = element.points;
                element.points = new float[tmpPoints.length + meshPoints.length];
                System.arraycopy(tmpPoints, 0, element.points, 0, tmpPoints.length);
                System.arraycopy(meshPoints, 0, element.points, tmpPoints.length, meshPoints.length);
            }

            if (element.index == null) {
                element.index = meshIndex;
            } else {
                int[] tmpIndex = element.index;
                element.index = new int[tmpIndex.length + meshIndex.length];
                System.arraycopy(tmpIndex, 0, element.index, 0, tmpIndex.length);
                // Shift all indices with previous element.points.length
                for (int k = 0; k < meshIndex.length; k++) {
                    element.index[k + tmpIndex.length] = meshIndex[k] + (element.pointNextPos / 3);
                }
            }
            element.pointNextPos = element.points.length;
        }

        if (element.points == null) {
            return false;
        }

        //element.indexCurrentPos = 0;
        element.type = GeometryBuffer.GeometryType.TRIS;
        return true;
    }

    /**
     * Calculates a pyramidal mesh of a Poly-GeometryBuffer.
     *
     * @param element the GeometryBuffer which is used to write the 3D mesh
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcPyramidalMesh(GeometryBuffer element, float minHeight, float maxHeight) {
        float[] points = element.points;
        int[] index = element.index;
        float[] topPoint = new float[3];
        topPoint[2] = maxHeight;

        for (int i = 0, pointPos = 0; i < index.length; i++) {
            if (index[i] < 0) {
                break;
            }
            if (i > 0) break; // May add inner rings

            int numPoints = index[i] / 2;
            if (numPoints < 0) continue;

            // Init top of roof (attention with pointPos)
            GeometryUtils.center(points, pointPos, numPoints << 1, topPoint);

            List<float[]> point3Fs = new ArrayList<>();
            // Load points
            for (int j = 0; j < (numPoints * 2); j += 2, pointPos += 2)
                point3Fs.add(new float[]{points[pointPos], points[pointPos + 1], minHeight});

            // Write index: index gives the first point of triangle mesh (divided 3)
            int[] meshIndex = new int[numPoints * 3];
            for (int j = 0; j < point3Fs.size(); j++) {
                int pos = 3 * j; // triangle mesh
                meshIndex[pos + 0] = j;
                meshIndex[pos + 1] = (j + 1) % point3Fs.size();
                meshIndex[pos + 2] = point3Fs.size();
            }

            // Write points
            point3Fs.add(topPoint);
            float[] meshPoints = new float[point3Fs.size() * 3];
            for (int j = 0; j < point3Fs.size(); j++) {
                int pos = 3 * j;
                float[] point3D = point3Fs.get(j);
                meshPoints[pos + 0] = point3D[0];
                meshPoints[pos + 1] = point3D[1];
                meshPoints[pos + 2] = point3D[2];
            }

            element.points = meshPoints;
            element.index = meshIndex;
            //element.indexCurrentPos = 0;
            element.pointNextPos = meshPoints.length;
        }

        element.type = GeometryBuffer.GeometryType.TRIS;
        return true;
    }

    /**
     * Calculates a ridge mesh of a Poly-GeometryBuffer.
     *
     * @param element           the GeometryBuffer which is used to write the 3D mesh
     * @param minHeight         the minimum height
     * @param maxHeight         the maximum height
     * @param orientationAcross indicates if ridge is parallel to short side
     * @param isGabled          indicates if should calculate a gable
     * @param specialParts      element to add missing parts of underlying element
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcRidgeMesh(GeometryBuffer element, float minHeight, float maxHeight, boolean orientationAcross, boolean isGabled, GeometryBuffer specialParts) {
        float[] points = element.points;
        int[] index = element.index;

        for (int i = 0, pointPos = 0; i < index.length; i++) {
            if (index[i] < 0) {
                break;
            }
            if (i > 0) break; // Handle only first polygon

            int numPoints = index[i] / 2;
            if (numPoints < 0) continue;

            if (numPoints < 4 || (!isGabled && orientationAcross)) {
                calcPyramidalMesh(element, minHeight, maxHeight);
                return true;
            }

            List<float[]> point3Fs = new ArrayList<>();

            // Calculate center and load points
            for (int j = 0; j < (numPoints * 2); j += 2, pointPos += 2) {
                float x = points[pointPos];
                float y = points[pointPos + 1];

                point3Fs.add(new float[]{x, y, minHeight});
            }

            // Calc vectors
            int groundSize = point3Fs.size();

            List<Float> lengths = new ArrayList<>();
            List<float[]> normVectors = GeometryUtils.normalizedVectors2D(point3Fs, lengths);

            List<Byte> simpleAngles = getSimpleAngles(normVectors);

            Integer indexStart = getIndexStart(simpleAngles, lengths, orientationAcross);

            int countConcavAngles = 0;
            for (Byte simpleAngle : simpleAngles) {
                if (simpleAngle > 1)
                    countConcavAngles++;
            }

            // Calc different mesh, if roof has no nearly right angle
            if (indexStart == null) {
                if (isGabled)
                    return calcSimpleGabledMesh(element, minHeight, maxHeight, orientationAcross, specialParts);
                else
                    return calcPyramidalMesh(element, minHeight, maxHeight);
            }

            List<float[]> bisections = getBisections(normVectors);
            List<float[]> intersections = new ArrayList<>();

            // Calc intersection of bisection
            for (int k = 0; k < groundSize; k++) {
                int nextTurn = getIndexNextTurn(k, simpleAngles);
                float[] pA = point3Fs.get(nextTurn);
                float[] pB = point3Fs.get(k);
                intersections.add(GeometryUtils.intersectionLines2D(pA, bisections.get(nextTurn), pB, bisections.get(k)));
            }

            // Calc ridge points
            TreeMap<Integer, float[]> ridgePoints = new TreeMap<>();
            TreeMap<Integer, float[]> ridgeLines = new TreeMap<>();
            HashSet<Integer> gablePoints = new HashSet<>(); // Only used if gabled
            Integer currentRidgeInd = null;
            boolean isOdd = false;
            for (int k = 0; k < groundSize; k++) {
                int shift = (k + indexStart) % groundSize;
                byte direction = simpleAngles.get(shift);
                if (direction == 0) {
                    continue; // direction is similar to last one
                } else if (direction > 0) {
                    // If shape turns right
                    float[] positionRidgeA = null;
                    float[] positionRidgeB = null;

                    // Check two previous corners
                    Integer indexPrevious = getIndexPreviousLeftTurn(shift, simpleAngles);
                    Integer indexPrevious2 = getIndexPreviousLeftTurn(indexPrevious == null ? shift - 1 : indexPrevious, simpleAngles);

                    if (indexPrevious != null && indexPrevious2 != null) {
                        // Write two previous
                        if (!ridgeLines.containsKey(indexPrevious2)) {
                            ridgeLines.put(indexPrevious2, normVectors.get(indexPrevious));
                        }

                        positionRidgeA = intersections.get(indexPrevious2);
                        currentRidgeInd = indexPrevious2;
                        if (isGabled) {
                            positionRidgeA = GeometryUtils.intersectionLines2D(positionRidgeA, ridgeLines.get(indexPrevious2), point3Fs.get(indexPrevious2), normVectors.get(indexPrevious2));
                            gablePoints.add(indexPrevious2);
                        }
                        ridgePoints.put(indexPrevious2, positionRidgeA);

                        // Remove previous ridge, if exists
                        gablePoints.remove(indexPrevious);
                        ridgePoints.remove(indexPrevious);
                        ridgeLines.remove(indexPrevious);
                    }

                    // Check two next corners
                    Integer indexNext = getIndexNextLeftTurn(shift, simpleAngles);
                    Integer indexNext2 = getIndexNextLeftTurn(indexNext == null ? shift + 1 : indexNext, simpleAngles);

                    if (indexNext != null && indexNext2 != null) {
                        if (ridgePoints.get(indexNext) == null) {
                            // Write both next
                            if (!ridgeLines.containsKey(indexNext)) {
                                ridgeLines.put(indexNext, normVectors.get(indexNext2));
                            }
                            positionRidgeB = intersections.get(indexNext);

                            if (isGabled) {
                                positionRidgeB = GeometryUtils.intersectionLines2D(positionRidgeB, ridgeLines.get(indexNext), point3Fs.get(indexNext), normVectors.get(indexNext));
                                gablePoints.add(indexNext);
                            }
                            ridgePoints.put(indexNext, positionRidgeB);
                        } else {
                            positionRidgeB = ridgePoints.get(indexNext);
                        }
                    }

                    // Handle multiple concaves
                    if (positionRidgeA == null || positionRidgeB == null) {
                        if (positionRidgeA == null && positionRidgeB == null && currentRidgeInd != null) {
                            positionRidgeA = ridgePoints.get(currentRidgeInd);
                        }
                        if (positionRidgeA != null && positionRidgeB == null) { // Next index is concave
                            positionRidgeA = GeometryUtils.intersectionLines2D(positionRidgeA, ridgeLines.get(currentRidgeInd), point3Fs.get(shift), bisections.get(shift));
                            currentRidgeInd = shift;
                            addSnapRidgePoint(shift, positionRidgeA, ridgePoints);
                            ridgeLines.put(shift, normVectors.get(shift)); // Add ridgeLine, if concave
                            isOdd = false;
                            continue;
                        } else if (positionRidgeA == null && positionRidgeB != null) { // Previous index is concave
                            positionRidgeA = GeometryUtils.intersectionLines2D(positionRidgeB, ridgeLines.get(indexNext), point3Fs.get(shift), bisections.get(shift));
                            addSnapRidgePoint(shift, positionRidgeA, ridgePoints);
                            currentRidgeInd = null;
                            isOdd = false;
                            continue;
                        } else {
                            log.debug("Should never happen, because positionRidge wouldn't be null then");
                            currentRidgeInd = null;
                            continue;
                        }
                    }

                    // Calc actual concave
                    if (currentRidgeInd == null || indexNext == null || ridgeLines.get(currentRidgeInd) == null || ridgeLines.get(indexNext) == null) {
                        log.debug("Concave shape not calculated correctly: " + element.toString());
                        currentRidgeInd = null;
                        continue;
                    }

                    float[] intersection = GeometryUtils.intersectionLines2D(positionRidgeA, ridgeLines.get(currentRidgeInd), positionRidgeB, ridgeLines.get(indexNext));
                    addSnapRidgePoint(shift, intersection, ridgePoints);

                    // Set opposite ridge, if only one concave corner
                    if (countConcavAngles == 1) {
                        Integer opposite = getIndexNextLeftTurn(indexNext2, simpleAngles);
                        if (opposite != null) {
                            if (isGabled)
                                gablePoints.remove(opposite);
                            ridgePoints.put(opposite, intersection);
                        }
                    }

                    // Reset ridges
                    currentRidgeInd = null;
                    isOdd = false;
                    continue;
                }
                // Regular right angle (left turn)
                if (isOdd) {
                    isOdd = false;
                    continue;
                }
                if (simpleAngles.get(shift) < -1) {
                    isOdd = true;
                }
                if (ridgePoints.containsKey(shift) && ridgeLines.containsKey(shift)) {
                    currentRidgeInd = shift;
                    continue;
                }
                if (currentRidgeInd != null) {
                    float[] intersection;
                    // If is gabled, then use the normal line as intersection instead of bisection, but if the angle is not right, this is usually not a gable point
                    if (isGabled && direction < -1) {
                        if (ridgePoints.get(currentRidgeInd) == null) {
                            log.debug("Gabled intersection calc failed");
                            currentRidgeInd = null;
                            continue;
                        }
                        intersection = GeometryUtils.intersectionLines2D(ridgePoints.get(currentRidgeInd), ridgeLines.get(currentRidgeInd), point3Fs.get(shift), normVectors.get(shift));
                        if (intersection == null) {
                            log.debug("Gabled intersection calc failed");
                            currentRidgeInd = null;
                            continue;
                        }
                        gablePoints.add(shift);
                        ridgePoints.put(shift, intersection);
                    } else {
                        intersection = GeometryUtils.intersectionLines2D(ridgePoints.get(currentRidgeInd), ridgeLines.get(currentRidgeInd), point3Fs.get(shift), bisections.get(shift));
                        addSnapRidgePoint(shift, intersection, ridgePoints);
                    }
                    if (isOdd) {
                        currentRidgeInd = null;
                    } else {
                        ridgeLines.put(shift, normVectors.get(shift));
                        currentRidgeInd = shift;
                    }
                } else {
                    Integer indexNext = getIndexNextLeftTurn(shift, simpleAngles);
                    if (indexNext == null) continue;
                    if (!ridgeLines.containsKey(shift)) {
                        ridgeLines.put(shift, normVectors.get(indexNext));
                    }
                    currentRidgeInd = shift;

                    float[] ridgePos = intersections.get(shift);
                    if (isGabled) {
                        ridgePos = GeometryUtils.intersectionLines2D(ridgePos, ridgeLines.get(currentRidgeInd), point3Fs.get(shift), normVectors.get(shift));
                        gablePoints.add(shift);
                    }
                    addSnapRidgePoint(shift, ridgePos, ridgePoints);
                }
            }

            if (ridgePoints.isEmpty()) {
                calcPyramidalMesh(element, minHeight, maxHeight);
                return true;
            }

            Iterator<Map.Entry<Integer, float[]>> ridgeIt = ridgePoints.entrySet().iterator();
            while (ridgeIt.hasNext()) {
                Map.Entry<Integer, float[]> ridgeEntry = ridgeIt.next();
                Integer key = ridgeEntry.getKey();
                if (ridgeEntry.getValue() == null) {
                    log.debug("Ridge calculation failed at point " + key);
                    ridgeIt.remove();
                    continue;
                }

                // Only remove ridgePoint at concave corners
                if (!isGabled || simpleAngles.get(key) > 0) {
                    boolean isIn = GeometryUtils.pointInPoly(ridgeEntry.getValue()[0], ridgeEntry.getValue()[1], points, points.length, 0);
                    if (!isIn) {
                        // FIXME can improve shapes with concaves that intersect each other and remove shapes which have ridgepoints outside the outline
                        if (!IMPROVE_RIDGE_CALCULATION) {
                            if (isGabled) {
                                return calcSimpleGabledMesh(element, minHeight, maxHeight, orientationAcross, specialParts);
                            } else return calcFlatMesh(element, minHeight);
                        }
                    }
                }
            }

            // Allocate the indices to the points
            int ridgePointSize = ridgePoints.size();
            float[] meshPoints = new float[(groundSize + ridgePointSize) * 3]; //(ridgePoints * 3 = 6)
            List<Integer> meshVarIndex = new ArrayList<>();

            // Add special building parts
            List<Integer> meshPartVarIndex = null;
            if (isGabled && specialParts != null) {
                meshPartVarIndex = new ArrayList<>();
            }

            for (int k = 0; k < groundSize; k++) {
                // Add first face
                float[] p = point3Fs.get(k);
                int ridgePointIndex1 = k;
                while (!ridgePoints.containsKey(ridgePointIndex1)) {
                    ridgePointIndex1 = (ridgePointIndex1 + groundSize - 1) % groundSize; // Decrease ridgePointIndex
                }
                int ridgeIndex1 = ridgePoints.headMap(ridgePointIndex1).size(); // set ridgeIndex to shift in ridgePoints
                if (meshPartVarIndex != null && gablePoints.contains(ridgePointIndex1) && getIndexNextTurn(ridgePointIndex1, simpleAngles).equals(getIndexNextTurn(k, simpleAngles))) {
                    meshPartVarIndex.add(k);
                    meshPartVarIndex.add((k + 1) % groundSize);
                    meshPartVarIndex.add(ridgeIndex1 + groundSize);
                } else {
                    meshVarIndex.add(k);
                    meshVarIndex.add((k + 1) % groundSize);
                    meshVarIndex.add(ridgeIndex1 + groundSize);
                }

                // Add second face, if necessary
                int ridgePointIndex2 = (k + 1) % groundSize;
                while (!ridgePoints.containsKey(ridgePointIndex2)) {
                    ridgePointIndex2 = (ridgePointIndex2 + groundSize - 1) % groundSize; // Decrease ridgePointIndex
                }

                if (ridgePointIndex2 != ridgePointIndex1) {
                    int ridgeIndex2 = ridgePoints.headMap(ridgePointIndex2).size(); // Set ridgeIndex to position in ridgePoints
                    meshVarIndex.add(ridgeIndex1 + groundSize);
                    meshVarIndex.add((k + 1) % groundSize);
                    meshVarIndex.add(ridgeIndex2 + groundSize);
                }

                // Write points
                meshPoints[3 * k + 0] = p[0];
                meshPoints[3 * k + 1] = p[1];
                meshPoints[3 * k + 2] = p[2];
            }

            // Tessellate top, if necessary (can be used to improve wrong rendered roofs)
            if (ridgePointSize > 2) {
                HashSet<Integer> ridgeSkipFaceIndex = new HashSet<>();
                boolean isTessellateAble = true;
                for (int k = 0; k < groundSize; k++) {
                    if (!isTessellateAble || ridgePoints.get(k) == null) continue;
                    Integer middle = null;
                    for (int m = k + 1; m <= k + groundSize; m++) {
                        int secIndex = m % groundSize;
                        if (ridgePoints.get(secIndex) == null) continue;
                        if (middle == null) {
                            middle = secIndex;
                        } else {
                            float isClockwise = GeometryUtils.isTrisClockwise(ridgePoints.get(k), ridgePoints.get(middle), ridgePoints.get(secIndex));
                            if (Math.abs(isClockwise) < 0.001) {
                                ridgeSkipFaceIndex.add(middle);
                                if (Arrays.equals(ridgePoints.get(k), ridgePoints.get(secIndex)))
                                    ridgeSkipFaceIndex.add(k);
                            }
                            if (isClockwise < 0 && IMPROVE_RIDGE_CALCULATION) {
                                // TODO Improve handling of counter clockwise faces and support multiple faces
                                isTessellateAble = false;
                                break;
                                //return calcSimpleGabledMesh(element, minHeight, maxHeight, orientationAcross, specialParts);
                            }
                            break;
                        }
                    }
                }
                int faceLength = ridgePointSize - ridgeSkipFaceIndex.size();
                if (isTessellateAble && faceLength > 0) {
                    float[] gbPoints = new float[2 * faceLength];
                    int k = 0;
                    List<Integer> faceIndex = new ArrayList<>(); // Store used indices
                    for (int m = 0; m < groundSize; m++) {
                        float[] point = ridgePoints.get(m);
                        if (ridgeSkipFaceIndex.contains(m) || point == null) {
                            continue;
                        }
                        faceIndex.add(m);
                        gbPoints[2 * k] = point[0];
                        gbPoints[2 * k + 1] = point[1];
                        k++;
                    }
                    GeometryBuffer buffer = new GeometryBuffer(gbPoints, new int[]{2 * faceLength});
                    if (Tessellator.tessellate(buffer, buffer) != 0) {
                        for (int ind : buffer.index) {
                            // Get position in ridgePoints, considering skipped points
                            meshVarIndex.add(ridgePoints.headMap(faceIndex.get(ind)).size() + groundSize);
                        }
                    } else {
                        // TODO Improve wrong or not tessellated faces
                        if (!IMPROVE_RIDGE_CALCULATION) {
                            if (isGabled) {
                                return calcSimpleGabledMesh(element, minHeight, maxHeight, orientationAcross, specialParts);
                            } else return calcFlatMesh(element, minHeight);
                        }
                    }
                }
            }


            // Replace in Java 8 / min API 24
            int[] meshIndex = new int[meshVarIndex.size()]; // new int[(groundSize + ridgePointSize) * 3]; // 3 vertices per point + 6 vertices for left and right roof
            for (int k = 0; k < meshIndex.length; k++)
                meshIndex[k] = meshVarIndex.get(k);

            for (int k = 0, l = 0; k < groundSize; k++) {
                // Add ridge points
                float[] tmp = ridgePoints.get(k);
                if (tmp != null) {
                    float[] p = new float[]{tmp[0], tmp[1], maxHeight};
                    int ppos = 3 * (l + groundSize);
                    meshPoints[ppos + 0] = p[0];
                    meshPoints[ppos + 1] = p[1];
                    meshPoints[ppos + 2] = p[2];
                    l++;
                }
            }

            // Add special parts e.g. for gabled roofs
            if (specialParts != null && meshPartVarIndex != null) {
                // Replace in Java 8 / min API 24
                int[] meshPartsIndex = new int[meshPartVarIndex.size()];
                for (int k = 0; k < meshPartsIndex.length; k++)
                    meshPartsIndex[k] = meshPartVarIndex.get(k);

                specialParts.points = meshPoints;
                specialParts.index = meshPartsIndex;
                specialParts.pointNextPos = meshPoints.length;
                specialParts.type = GeometryBuffer.GeometryType.TRIS;
            }

            element.points = meshPoints;
            element.index = meshIndex;
            element.pointNextPos = meshPoints.length;
            element.type = GeometryBuffer.GeometryType.TRIS;
        }

        return element.isTris();
    }

    /**
     * Calculates a simple gabled mesh of a Poly-GeometryBuffer.
     *
     * @param element the GeometryBuffer which is used to write the 3D mesh
     * @return true if calculation succeeded, false otherwise
     */
    private static boolean calcSimpleGabledMesh(GeometryBuffer element, float minHeight, float maxHeight, boolean orientationAcross, GeometryBuffer specialParts) {
        float[] points = element.points;
        int[] index = element.index;

        for (int i = 0, pointPos = 0; i < index.length; i++) {
            if (index[i] < 0) {
                break;
            }
            if (i > 0) break; // Handle only first polygon

            int numPoints = index[i] / 2;
            if (numPoints < 0) continue;

            if (numPoints < 4) {
                calcPyramidalMesh(element, minHeight, maxHeight);
                return true;
            }

            List<float[]> point3Fs = new ArrayList<>();

            // Calculate center and load points
            for (int j = 0; j < (numPoints * 2); j += 2, pointPos += 2) {
                float x = points[pointPos];
                float y = points[pointPos + 1];

                point3Fs.add(new float[]{x, y, minHeight});
            }

            // Calc vectors
            int groundSize = point3Fs.size();

            List<Float> lengths = new ArrayList<>();
            List<float[]> normVectors = GeometryUtils.normalizedVectors2D(point3Fs, lengths);

            List<Byte> simpleAngles = getSimpleAngles(normVectors);

            int indexStart = getIndicesLongestSide(simpleAngles, lengths, null)[0];
            if (orientationAcross) {
                Integer tmp = getIndexPreviousLeftTurn(indexStart, simpleAngles);
                if (tmp == null) {
                    tmp = getIndexNextTurn(indexStart, simpleAngles);
                }
                indexStart = tmp;
            }
            float[] vL = normVectors.get(indexStart);
            float[] pL = point3Fs.get(indexStart);
            float[] splitLinePoint = null;
            float maxDist = 0;
            for (float[] point : point3Fs) {
                float curDist = GeometryUtils.distancePointLine2D(point, pL, vL);
                if (curDist > maxDist) {
                    maxDist = curDist;
                    splitLinePoint = point; // Farthest point from line
                }
            }
            // Scale of normal vec
            maxDist = -Math.signum(GeometryUtils.isTrisClockwise(
                    pL,
                    GeometryUtils.sumVec(pL, vL),
                    splitLinePoint)) * (maxDist / 2);

            float[] normL = new float[]{-vL[1], vL[0]}; // Normal vec to line
            normL = GeometryUtils.scale(normL, (float) (maxDist / Math.sqrt(GeometryUtils.dotProduct(normL, normL)))); // normalize vec
            splitLinePoint = GeometryUtils.sumVec(pL, normL);
            float degreeNormL = (float) Math.atan2(normL[0], -normL[1]) * MathUtils.radiansToDegrees;

            // Split polygon
            int sideChange = 0;
            List<float[]> elementPoints1 = new ArrayList<>();
            List<float[]> elementPoints2 = new ArrayList<>();
            float[] secSplitPoint = GeometryUtils.sumVec(splitLinePoint, vL);
            float sideLastPoint = Math.signum(GeometryUtils.isTrisClockwise(splitLinePoint, secSplitPoint, point3Fs.get(groundSize - 1)));
            degreeNormL = sideLastPoint < 0 ? degreeNormL : (degreeNormL + 180f) % 360; // Correct angle
            List<Integer> intersection1 = new ArrayList<>(), intersection2 = new ArrayList<>();
            for (int k = 0; k < groundSize; k++) {
                // If point is not on the same side as the previous point, the split line intersect and can calc split point
                float sideCurPoint = Math.signum(GeometryUtils.isTrisClockwise(splitLinePoint, secSplitPoint, point3Fs.get(k)));
                if (sideCurPoint != sideLastPoint) {
                    if (sideChange > 2 && !IMPROVE_RIDGE_CALCULATION)
                        return calcFlatMesh(element, minHeight); // TODO Improve multiple side changes
                    int indexPrev = (k + groundSize - 1) % groundSize;
                    float[] intersection = GeometryUtils.intersectionLines2D(splitLinePoint, vL, point3Fs.get(indexPrev), normVectors.get(indexPrev));
                    elementPoints1.add(intersection);
                    elementPoints2.add(intersection);
                    intersection1.add(elementPoints1.size() - 1);
                    intersection2.add(elementPoints2.size() - 1);
                    sideChange++;
                }
                if (sideChange % 2 == 0) {
                    elementPoints1.add(point3Fs.get(k));
                } else {
                    elementPoints2.add(point3Fs.get(k));
                }
                sideLastPoint = sideCurPoint;
            }

            GeometryBuffer geoEle1 = new GeometryBuffer(elementPoints1.size(), 1);
            for (int k = 0; k < elementPoints1.size(); k++) {
                geoEle1.points[2 * k] = elementPoints1.get(k)[0];
                geoEle1.points[2 * k + 1] = elementPoints1.get(k)[1];
            }
            geoEle1.index[0] = geoEle1.points.length;
            geoEle1.pointNextPos = geoEle1.points.length;

            GeometryBuffer geoEle2 = new GeometryBuffer(elementPoints2.size(), 1);
            for (int k = 0; k < elementPoints2.size(); k++) {
                geoEle2.points[2 * k] = elementPoints2.get(k)[0];
                geoEle2.points[2 * k + 1] = elementPoints2.get(k)[1];
            }
            geoEle2.index[0] = geoEle2.points.length;
            geoEle2.pointNextPos = geoEle2.points.length;

            GeometryBuffer specialParts1 = new GeometryBuffer(geoEle1);
            GeometryBuffer specialParts2 = new GeometryBuffer(geoEle2);
            if (!(calcSkillionMesh(geoEle1, minHeight, maxHeight, degreeNormL, specialParts1)
                    && calcSkillionMesh(geoEle2, minHeight, maxHeight,
                    degreeNormL + 180, specialParts2))) {
                return false;
            }

            // Adapt gable intersections to max height
            for (Integer integer : intersection1) {
                geoEle1.points[integer * 3 + 2] = maxHeight;
                specialParts1.points[6 * integer + 5] = maxHeight;
            }
            for (Integer integer : intersection2) {
                geoEle2.points[integer * 3 + 2] = maxHeight;
                specialParts2.points[6 * integer + 5] = maxHeight;
            }

            // Merge buffers
            mergeMeshGeometryBuffer(geoEle1, geoEle2, element);
            mergeMeshGeometryBuffer(specialParts1, specialParts2, specialParts);
            return true;
        }
        return false;
    }

    /**
     * Calculates a skillion mesh of a Poly-GeometryBuffer.
     *
     * @param element      the GeometryBuffer which is used to write the 3D mesh
     * @param roofDegree   the direction of slope
     * @param specialParts the GeometryBuffer which is used to write the additional building parts
     * @return true if calculation succeeded, false otherwise
     */
    public static boolean calcSkillionMesh(GeometryBuffer element, float minHeight, float maxHeight, float roofDegree, GeometryBuffer specialParts) {
        float[] points = element.points;
        int[] index = element.index;

        for (int i = 0, pointPos = 0; i < index.length; i++) {
            if (index[i] < 0) {
                break;
            }
            if (i > 0) break; // May add inner rings

            int numPoints = index[i] / 2;
            if (numPoints < 0) continue;

            List<float[]> point3Fs = new ArrayList<>();
            for (int j = 0; j < (numPoints * 2); j += 2, pointPos += 2) {
                float x = points[pointPos];
                float y = points[pointPos + 1];

                point3Fs.add(new float[]{x, y, minHeight});
            }

            boolean hasOutlines = calcOutlines(specialParts, minHeight, maxHeight);

            // Use 3 points that match the angle the best and use as plane
            float[] min1 = null, min2 = null, max1 = null, max2 = null;
            float minDif1, minDif2, maxDif1, maxDif2;
            minDif1 = minDif2 = Float.MAX_VALUE;
            maxDif1 = maxDif2 = 0;

            if (calcFlatMesh(element, maxHeight)) {
                // To positive radian
                roofDegree = ((MathUtils.degreesToRadians * roofDegree) + MathUtils.PI2) % MathUtils.PI2;
                float[] vRidge = new float[2];
                vRidge[0] = (float) Math.sin(roofDegree);
                vRidge[1] = (float) -Math.cos(roofDegree);
                vRidge = GeometryUtils.scale(vRidge, 100000000); // Use very large value, so the distances are nearly parallel

                for (int k = 0; k < point3Fs.size(); k++) {
                    float[] point = point3Fs.get(k);
                    float vx = vRidge[0] - point[0];
                    float vy = vRidge[1] - point[1];
                    float currentDiff = (float) Math.sqrt(vx * vx + vy * vy);
                    if (max1 == null || currentDiff > maxDif1) {
                        if (max1 != null) {
                            max2 = max1;
                            maxDif2 = maxDif1;
                        }
                        max1 = point;
                        maxDif1 = currentDiff;
                    } else if (max2 == null || currentDiff > maxDif2) {
                        max2 = point;
                        maxDif2 = currentDiff;
                    }
                    if (min1 == null || currentDiff < minDif1) {
                        if (min1 != null) {
                            min2 = min1;
                            minDif2 = minDif1;
                        }
                        min1 = point;
                        minDif1 = currentDiff;
                    } else if (min2 == null || currentDiff < minDif2) {
                        min2 = point;
                        minDif2 = currentDiff;
                    }
                }
                if (min1 == max1) return false;

                float[] normal, zVector = new float[]{0, 0, 1}; // height intersection
                min1[2] = minHeight;
                max1[2] = maxHeight;

                // Use lower two points if they promise better results (e.g. at triangles)
                if (Math.abs(minDif2 - minDif1) < Math.abs(maxDif2 - maxDif1)) {
                    min2[2] = minHeight; // Note: min2 == max2 is possible
                    normal = GeometryUtils.normalOfPlane(min1, max1, min2);
                } else {
                    max2[2] = maxHeight;
                    normal = GeometryUtils.normalOfPlane(min1, max1, max2);
                }

                // Calc intersection points of ground points with plane
                for (int k = 0; k < point3Fs.size(); k++) {
                    float[] intersection = GeometryUtils.intersectionLinePlane(point3Fs.get(k), zVector, min1, normal);
                    if (intersection == null) return false;
                    intersection[2] = intersection[2] > (2 * maxHeight) ? maxHeight : (intersection[2] < minHeight ? minHeight : intersection[2]);
                    element.points[3 * k + 2] = intersection[2];
                    if (hasOutlines) {
                        specialParts.points[6 * k + 5] = intersection[2]; // Every sixth point is height of k
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * @return the bisections of vectors
     */
    private static List<float[]> getBisections(List<float[]> normVectors) {
        int size = normVectors.size();
        List<float[]> bisections = new ArrayList<>();
        // Calc bisections
        for (int k = 0; k < size; k++) {
            float[] vBC = normVectors.get((k + size - 1) % size);
            float[] vBA = normVectors.get(k);

            // Change direction to get correct angle
            vBC = Arrays.copyOf(vBC, vBC.length);
            vBC[0] = -vBC[0];
            vBC[1] = -vBC[1];

            // Calc bisection
            bisections.add(GeometryUtils.bisectionNorm2D(vBC, vBA));
        }
        return bisections;
    }

    /**
     * @param color    the color as string (see http://wiki.openstreetmap.org/wiki/Key:colour)
     * @param hsv      the hsv color to modify given color
     * @param relative declare if colors are modified relative to their values
     * @return the color as integer (8 bit each a, r, g, b)
     */
    public static int getColor(String color, Color.HSV hsv, boolean relative) {

        int c;
        if (color.charAt(0) == '#') {
            c = Color.parseColor(color, Color.CYAN);
        } else if ("transparent".equals(color)) {
            return Color.get(0, 1, 1, 1);
        } else {
            Integer css = ColorsCSS.get(color);
            if (css == null) {
                log.debug("unknown color:{}", color);
                c = Color.CYAN;
            } else {
                c = css;
            }
        }

        return hsv.mod(c, relative);
    }

    /**
     * @return the index of next left turn after specified index
     */
    private static Integer getIndexNextLeftTurn(int index, List<Byte> simpleAngles) {
        for (int i = index + 1; i < simpleAngles.size() + index; i++) {
            int iMod = i % simpleAngles.size();
            if (simpleAngles.get(iMod) < 0) {
                return iMod;
            } else if (simpleAngles.get(iMod) > 0) {
                return null;
            }
        }
        return (index + 1) % simpleAngles.size();
    }

    /**
     * @return the index of next turn after specified index
     */
    private static Integer getIndexNextTurn(int index, List<Byte> simpleAngles) {
        for (int i = index + 1; i < simpleAngles.size() + index; i++) {
            int iMod = i % simpleAngles.size();
            if (simpleAngles.get(iMod) != 0) {
                return iMod;
            }
        }
        return (index + 1) % simpleAngles.size();
    }

    /**
     * @return the index of previous left turn at specified index
     */
    private static Integer getIndexPreviousLeftTurn(int index, List<Byte> simpleAngles) {
        for (int i = simpleAngles.size() + index - 1; i >= 0; i--) {
            int iMod = i % simpleAngles.size();
            if (simpleAngles.get(iMod) < 0) {
                return iMod;
            } else if (simpleAngles.get(iMod) > 0) {
                return null;
            }
        }
        return (simpleAngles.size() + index - 1) % simpleAngles.size();
    }

    /**
     * @return the best index to begin a calculation
     */
    private static Integer getIndexStart(List<Byte> simpleAngles, List<Float> lengths, boolean directionAcross) {
        int size = simpleAngles.size();
        Integer indexStart = null;
        Integer concaveStart = null;
        for (int i = 0; i < size; i++) {
            if (indexStart != null && concaveStart != null) break;
            if (indexStart == null && simpleAngles.get(i) < -1) {
                // Use first angle as start index;
                indexStart = i;
            } else if (concaveStart == null && simpleAngles.get(i) > 1) {
                // A real concave corner
                concaveStart = i;
            }
        }

        if (indexStart == null) {
            return null;
        }

        if (concaveStart != null) {
            // look for next convex shape (point)
            for (int i = concaveStart; i < size + indexStart; i++) {
                if (simpleAngles.get(i % size) < 0) {
                    return i % size;
                }
            }
        }

        // Calculate longest side with right angle next to it.
        int[] iLongSide = getIndicesLongestSide(simpleAngles, lengths, indexStart);
        if (simpleAngles.get(iLongSide[1]) > -2) {
            // If angle is not good to start a ridge use previous
            indexStart = getIndexPreviousLeftTurn(iLongSide[0], simpleAngles);
        } else {
            indexStart = iLongSide[1]; // Get side next to longest one
        }

        // Direction across only possible if no concave shapes are present
        if (directionAcross) {
            return iLongSide[0];
        }

        return indexStart;
    }

    /**
     * @param indexStart the start index, if already calculated (can be null)
     * @return int[0] = start index, int[1] = end index
     */
    private static int[] getIndicesLongestSide(List<Byte> simpleAngles, List<Float> lengths, Integer indexStart) {
        int[] iLongSide = new int[2];
        int size = simpleAngles.size();
        if (indexStart == null) {
            for (int i = 0; i < size; i++) {
                if (simpleAngles.get(i) < 0) {
                    // Use first angle as start index;
                    indexStart = i;
                    break;
                }
            }
        }

        float longestSideLength = 0, currentLength = 0;
        int indexCurrentSide = indexStart;
        int loopSize = size + indexStart;
        for (int i = indexStart; i < loopSize; i++) {
            if (i >= size) {
                i -= size;
                loopSize -= size;
            }

            if (simpleAngles.get(i) != 0) {
                // Right angle
                currentLength = lengths.get(i);
                indexCurrentSide = i;
            } else {
                currentLength += lengths.get(i);
            }

            if (currentLength > longestSideLength) {
                longestSideLength = currentLength;
                iLongSide[0] = indexCurrentSide;
                iLongSide[1] = (i + 1) % size;
            }
        }
        return iLongSide;
    }

    /**
     * @param material the material as string (see http://wiki.openstreetmap.org/wiki/Key:material and following pages)
     * @param hsv      the hsv color to modify given material
     * @return the color as integer (8 bit each a, r, g, b)
     */
    public static int getMaterialColor(String material, Color.HSV hsv, boolean relative) {

        int c;
        if (material.charAt(0) == '#') {
            c = Color.parseColor(material, Color.CYAN);
        } else {
            switch (material) {
                case "roof_tiles":
                    c = Color.get(216, 167, 111);
                    break;
                case "tile":
                    c = Color.get(216, 167, 111);
                    break;
                case "concrete":
                case "cement_block":
                    c = Color.get(210, 212, 212);
                    break;
                case "metal":
                    c = 0xFFC0C0C0;
                    break;
                case "tar_paper":
                    c = 0xFF969998;
                    break;
                case "eternit":
                    c = Color.get(216, 167, 111);
                    break;
                case "tin":
                    c = 0xFFC0C0C0;
                    break;
                case "asbestos":
                    c = Color.get(160, 152, 141);
                    break;
                case "glass":
                    c = Color.fade(Color.get(130, 224, 255), 0.6f);
                    break;
                case "slate":
                    c = 0xFF605960;
                    break;
                case "zink":
                    c = Color.get(180, 180, 180);
                    break;
                case "gravel":
                    c = Color.get(170, 130, 80);
                    break;
                case "copper":
                    // same as roof color:green
                    c = Color.get(150, 200, 130);
                    break;
                case "wood":
                    c = Color.get(170, 130, 80);
                    break;
                case "grass":
                    c = 0xFF50AA50;
                    break;
                case "stone":
                    c = Color.get(206, 207, 181);
                    break;
                case "plaster":
                    c = Color.get(236, 237, 181);
                    break;
                case "brick":
                    c = Color.get(255, 217, 191);
                    break;
                case "stainless_steel":
                    c = Color.get(153, 157, 160);
                    break;
                case "gold":
                    c = 0xFFFFD700;
                    break;
                default:
                    c = Color.CYAN;
                    log.debug("unknown material:{}", material);
                    break;
            }
        }

        return hsv.mod(c, relative);
    }

    /**
     * @param normVectors the normalized vectors
     * @return a list of simple angles:
     * 0           straight
     * (+/-) 1     (right/left) obtuse angle
     * (+/-) 2     (right/left) right angle (or acute angle)
     */
    private static List<Byte> getSimpleAngles(List<float[]> normVectors) {
        int size = normVectors.size();
        // List<Float> angles = new ArrayList<>();
        List<Byte> simpAngls = new ArrayList<>();
        float tmpAnlgeSum = 0;
        float threshold = MathUtils.PI / 12;
        for (int k = 0; k < size; k++) {
            // Check angle between next and this vector
            float[] v2 = normVectors.get(k);
            float[] v1 = normVectors.get((k - 1 + size) % size);
            float val = v1[0] * v2[0] + v1[1] * v2[1];
            float angle = (float) Math.acos(Math.abs(val) > 1 ? Math.signum(val) : val);
            // angles.add(angle);

            // Positive is turns right, negative turns left
            byte simpAngle = (byte) Math.signum(v1[0] * (-v2[1]) + v1[1] * v2[0]);
            if (angle > (MathUtils.PI / 2) - threshold) {
                // Right angle
                simpAngle *= 2;
                tmpAnlgeSum = 0;
            } else if (angle < threshold) {
                tmpAnlgeSum += simpAngle * angle; // Many small angles, indicate a corner
                if (Math.abs(tmpAnlgeSum) > threshold) {
                    // Can improve sum of concave/convex shapes
                    simpAngle = (byte) Math.signum(tmpAnlgeSum);
                    tmpAnlgeSum = 0;
                } else
                    simpAngle = 0;
            } else {
                // Angle which is not right, and not straight
                tmpAnlgeSum = 0;
            }

            simpAngls.add(simpAngle);
        }

        return simpAngls;
    }

    private static GeometryBuffer initCircleMesh(float[][] circleShape, int numSections) {
        int indexSize = numSections * (circleShape.length - 1) * 2 * 3; // * 2 faces * 3 vertices
        int[] meshIndex = new int[indexSize];

        int meshSize = numSections * circleShape.length;
        float[] meshPoints = new float[meshSize * 3];
        for (int i = 0; i < numSections; i++) {
            for (int j = 0; j < circleShape.length; j++) {
                // Write point mesh
                int pPos = 3 * (i * circleShape.length + j);
                meshPoints[pPos + 0] = circleShape[j][0];
                meshPoints[pPos + 1] = circleShape[j][1];
                meshPoints[pPos + 2] = circleShape[j][2];

                // Write point indices
                if (j != circleShape.length - 1) {
                    int iPos = 6 * (i * (circleShape.length - 1) + j); // 6 = 2 * Mesh * 3PointsPerMesh
                    pPos = pPos / 3;
                    meshIndex[iPos + 2] = pPos + 0;
                    meshIndex[iPos + 1] = pPos + 1;
                    meshIndex[iPos + 0] = (pPos + circleShape.length) % meshSize;

                    // FIXME if is last point, only one tris is needed, if top shape is closed
                    meshIndex[iPos + 5] = pPos + 1;
                    meshIndex[iPos + 4] = (pPos + circleShape.length + 1) % meshSize;
                    meshIndex[iPos + 3] = (pPos + circleShape.length) % meshSize;
                }
            }

        }

        return new GeometryBuffer(meshPoints, meshIndex);
    }

    private static void mergeMeshGeometryBuffer(GeometryBuffer gb1, GeometryBuffer gb2, GeometryBuffer out) {
        if (!(gb1.isTris() && gb2.isTris())) return;
        int gb1PointSize = gb1.points.length;
        float[] mergedPoints = new float[gb1PointSize + gb2.points.length];
        System.arraycopy(gb1.points, 0, mergedPoints, 0, gb1PointSize);
        System.arraycopy(gb2.points, 0, mergedPoints, gb1PointSize, gb2.points.length);
        out.points = mergedPoints;
        out.pointNextPos = mergedPoints.length;

        int gb1IndexSize = gb1.index.length;
        int[] mergedIndices = new int[gb1IndexSize + gb2.index.length];
        System.arraycopy(gb1.index, 0, mergedIndices, 0, gb1IndexSize);
        gb1PointSize /= 3; // (x,y,z)
        for (int k = 0; k < gb2.index.length; k++) {
            mergedIndices[gb1IndexSize + k] = gb2.index[k] + gb1PointSize;
        }
        out.index = mergedIndices;
        out.type = gb1.type;
    }

    private S3DBUtils() {
    }
}
