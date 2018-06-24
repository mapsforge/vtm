/*
 * Copyright 2018 Gustl22
 * Copyright 2018 devemux86
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
import org.oscim.core.Box;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.theme.styles.ExtrusionStyle;
import org.oscim.utils.ExtrusionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

/**
 * An experimental layer to display S3DB elements.
 */
public class S3DBLayer extends BuildingLayer {

    private static final Logger log = LoggerFactory.getLogger(S3DBLayer.class);

    private final float TILE_SCALE = (ExtrusionUtils.REF_TILE_SIZE / (Tile.SIZE * COORD_SCALE));

    private boolean mColored = true;
    private boolean mTransparent = true;

    public S3DBLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, MIN_ZOOM, map.viewport().getMaxZoomLevel());
    }

    /**
     * @param map       The map data to add
     * @param tileLayer The vector tile layer which contains the tiles and map elements
     * @param zoomMin   The minimum zoom at which the layer appears
     * @param zoomMax   The maximum zoom at which the layer appears
     */
    public S3DBLayer(Map map, VectorTileLayer tileLayer, int zoomMin, int zoomMax) {
        super(map, tileLayer, zoomMin, zoomMax, true);
    }

    public boolean isColored() {
        return mColored;
    }

    /**
     * @param colored true: use colour written in '*:colour' tag
     *                false: use top colour of extrusion style
     */
    public void setColored(boolean colored) {
        mColored = colored;
    }

    public boolean isTransparent() {
        return mTransparent;
    }

    /**
     * @param transparent if true coloured buildings blend transparency of extrusion style
     *                    (only in combination with isColored = true)
     */
    public void setTransparent(boolean transparent) {
        mTransparent = transparent;
    }

    @Override
    public void complete(MapTile tile, boolean success) {
        super.complete(tile, success);
        // Do stuff here
    }

    @Override
    public void processElement(MapElement element, ExtrusionStyle extrusion, MapTile tile) {
        float groundScale = tile.getGroundScale();

        int maxHeight = 0; // cm
        int minHeight = 0; // cm
        int roofHeight = 0;

        // Get roof height
        String v = element.tags.getValue(Tag.KEY_ROOF_HEIGHT);
        if (v != null) {
            roofHeight = (int) (Float.parseFloat(v) * 100);
        } else if ((v = element.tags.getValue(Tag.KEY_ROOF_LEVELS)) != null) {
            roofHeight = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        } else if ((v = element.tags.getValue(Tag.KEY_ROOF_ANGLE)) != null) {
            Box bb = null;
            for (int k = 0; k < element.index[0]; k += 2) {
                float p1 = element.points[k];
                float p2 = element.points[k + 1];
                if (bb == null)
                    bb = new Box(p1, p2, p1, p2);
                else {
                    bb.add(p1, p2);
                }
            }
            if (bb != null) {
                float minSize = (int) Math.min(bb.getHeight(), bb.getWidth()) * groundScale; // depends on lat
                // Angle is simplified, 40 is an estimated constant
                roofHeight = (int) ((Float.parseFloat(v) / 45.f) * (minSize * 40));
            }
        } else if ((v = element.tags.getValue(Tag.KEY_ROOF_SHAPE)) != null && !v.equals(Tag.VALUE_FLAT)) {
            roofHeight = (2 * BUILDING_LEVEL_HEIGHT);
        }

        // Get building height
        v = element.tags.getValue(Tag.KEY_HEIGHT);
        if (v != null) {
            maxHeight = (int) (Float.parseFloat(v) * 100);
        } else {
            // #TagFromTheme: generalize level/height tags
            if ((v = element.tags.getValue(Tag.KEY_BUILDING_LEVELS)) != null) {
                maxHeight = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
                maxHeight += roofHeight;
            }
        }
        if (maxHeight == 0)
            maxHeight = extrusion.defaultHeight * 100;

        v = element.tags.getValue(Tag.KEY_MIN_HEIGHT);
        if (v != null)
            minHeight = (int) (Float.parseFloat(v) * 100);
        else {
            // #TagFromTheme: level/height tags
            if ((v = element.tags.getValue(Tag.KEY_BUILDING_MIN_LEVEL)) != null)
                minHeight = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        }

        // Get building color
        Integer bColor = null;
        if (mColored) {
            if (element.tags.containsKey(Tag.KEY_BUILDING_COLOR)) {
                bColor = S3DBUtils.getColor(element.tags.getValue(Tag.KEY_BUILDING_COLOR), false, false);
            } else if (element.tags.containsKey(Tag.KEY_BUILDING_MATERIAL)) {
                bColor = S3DBUtils.getMaterialColor(element.tags.getValue(Tag.KEY_BUILDING_MATERIAL), false);
            }
        }

        if (bColor == null) {
            bColor = extrusion.colorTop;
        } else if (mTransparent) {
            // Multiply alpha channel of extrusion style
            bColor = ExtrusionStyle.blendAlpha(bColor, Color.aToFloat(extrusion.colorTop));
        }

        // Scale x, y and z axis
        ExtrusionUtils.mapPolyCoordScale(element);
        float minHeightS = ExtrusionUtils.mapGroundScale(minHeight, groundScale) * TILE_SCALE;
        float maxHeightS = ExtrusionUtils.mapGroundScale(maxHeight, groundScale) * TILE_SCALE;
        float minRoofHeightS = ExtrusionUtils.mapGroundScale(maxHeight - roofHeight, groundScale) * TILE_SCALE;

        // Process building and roof
        processRoof(element, tile, minRoofHeightS, maxHeightS, bColor);
        if (S3DBUtils.calcOutlines(element, minHeightS, minRoofHeightS)) {
            get(tile).addMeshElement(element, groundScale, bColor);
        }
    }

    @Override
    protected void processElements(MapTile tile) {
        if (!mBuildings.containsKey(tile.hashCode()))
            return;

        List<BuildingElement> tileBuildings = mBuildings.get(tile.hashCode());
        Set<BuildingElement> rootBuildings = new HashSet<>();
        for (BuildingElement partBuilding : tileBuildings) {
            if (!partBuilding.element.isBuildingPart())
                continue;

            TagSet partTags = partBuilding.element.tags;
            String refId = partTags.getValue(Tag.KEY_REF); // #TagFromTheme
            refId = refId == null ? partTags.getValue("root_id") : refId; // Mapzen
            if (refId == null)
                continue;

            // Search buildings which inherit parts
            for (BuildingElement rootBuilding : tileBuildings) {
                if (rootBuilding.element.isBuildingPart()
                        || !(refId.equals(rootBuilding.element.tags.getValue(Tag.KEY_ID))))
                    continue;

                if (mColored) {
                    TagSet rootTags = rootBuilding.element.tags;
                    for (int i = 0; i < rootTags.size(); i++) {
                        Tag rTag = rootTags.get(i);
                        if ((rTag.key.equals(Tag.KEY_COLOR) && !partTags.containsKey(Tag.KEY_MATERIAL)
                                || rTag.key.equals(Tag.KEY_ROOF_COLOR) && !partTags.containsKey(Tag.KEY_ROOF_MATERIAL)
                                || rTag.key.equals(Tag.KEY_ROOF_SHAPE))
                                && !partTags.containsKey(rTag.key)) {
                            partTags.add(rTag);
                        }
                    }
                }
                rootBuildings.add(rootBuilding);
                break;
            }
        }

        tileBuildings.removeAll(rootBuildings); // root buildings aren't rendered

        for (BuildingElement buildingElement : tileBuildings) {
            processElement(buildingElement.element, buildingElement.style, tile);
        }
        mBuildings.remove(tile.hashCode());
    }

    /**
     * Process the roof parts of building.
     *
     * @param element       the MapElement which needs a roof
     * @param tile          the tile which contains map element
     * @param minHeight     the height of the underlying building
     * @param maxHeight     the height of the roof + minHeight (whole building)
     * @param buildingColor the color of main building
     */
    private void processRoof(MapElement element, MapTile tile, float minHeight, float maxHeight, int buildingColor) {
        Integer roofColor = null;
        String v;

        if (mColored) {
            v = element.tags.getValue(Tag.KEY_ROOF_COLOR);
            if (v != null)
                roofColor = S3DBUtils.getColor(v, true, false);
            else if ((v = element.tags.getValue(Tag.KEY_ROOF_MATERIAL)) != null)
                roofColor = S3DBUtils.getMaterialColor(v, true);
        }

        boolean roofOrientationAcross = false;
        if ((v = element.tags.getValue(Tag.KEY_ROOF_ORIENTATION)) != null) {
            if (v.equals(Tag.VALUE_ACROSS)) {
                roofOrientationAcross = true;
            }
        }

        // Calc roof shape
        v = element.tags.getValue(Tag.KEY_ROOF_SHAPE);
        if (v == null) {
            v = Tag.VALUE_FLAT;
        }

        float groundScale = tile.getGroundScale();

        GeometryBuffer gElement = new GeometryBuffer(element);
        GeometryBuffer specialParts = null;
        if (roofColor == null)
            roofColor = buildingColor;
        else if (mTransparent) {
            // For simplicity use transparency of building, which is identical in nearly all cases
            roofColor = ExtrusionStyle.blendAlpha(roofColor, Color.aToFloat(buildingColor));
        }

        boolean success;
        switch (v) {
            case Tag.VALUE_DOME:
            case Tag.VALUE_ONION:
                success = S3DBUtils.calcCircleMesh(gElement, minHeight, maxHeight, v);
                break;
            case Tag.VALUE_ROUND:
            case Tag.VALUE_SALTBOX:
            case Tag.VALUE_GABLED:
            case Tag.VALUE_GAMBREL:
                specialParts = new GeometryBuffer(0, 0); // No data in GeometryBuffer needed
                success = S3DBUtils.calcRidgeMesh(gElement, minHeight, maxHeight, roofOrientationAcross, true, specialParts);
                break;
            case Tag.VALUE_MANSARD:
            case Tag.VALUE_HALF_HIPPED:
            case Tag.VALUE_HIPPED:
                success = S3DBUtils.calcRidgeMesh(gElement, minHeight, maxHeight, roofOrientationAcross, false, null);
                break;
            case Tag.VALUE_SKILLION:
                // ROOF_SLOPE_DIRECTION is not supported yet
                String roofDirection = element.tags.getValue(Tag.KEY_ROOF_DIRECTION);
                float roofDegree = 0;
                if (roofDirection != null) {
                    roofDegree = Float.parseFloat(roofDirection);
                }
                specialParts = new GeometryBuffer(element);
                success = S3DBUtils.calcSkillionMesh(gElement, minHeight, maxHeight, roofDegree, specialParts);
                break;
            case Tag.VALUE_PYRAMIDAL:
                success = S3DBUtils.calcPyramidalMesh(gElement, minHeight, maxHeight);
                break;
            case Tag.VALUE_FLAT:
            default:
                success = S3DBUtils.calcFlatMesh(gElement, minHeight);
                break;
        }

        if (success) {
            get(tile).addMeshElement(gElement, groundScale, roofColor);
            if (specialParts != null) {
                get(tile).addMeshElement(specialParts, groundScale, buildingColor);
            }
        } else {
            log.debug("Roof calculation failed: " + element.toString());
        }
    }
}
