/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017 devemux86
 * Copyright 2019 Gustl22
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
package org.oscim.tiling.source.overpass;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.core.Tile;
import org.oscim.core.osm.OsmData;
import org.oscim.core.osm.OsmElement;
import org.oscim.core.osm.OsmNode;
import org.oscim.core.osm.OsmRelation;
import org.oscim.core.osm.OsmWay;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.source.ITileDecoder;
import org.oscim.tiling.source.mapfile.OSMUtils;
import org.oscim.utils.overpass.OverpassAPIReader;

import java.io.IOException;
import java.io.InputStream;

import static org.oscim.core.MercatorProjection.latitudeToY;
import static org.oscim.core.MercatorProjection.longitudeToX;

public class TileDecoder implements ITileDecoder {

    private final MapElement mMapElement;
    private ITileDataSink mTileDataSink;

    private double mTileY, mTileX, mTileScale;

    public TileDecoder() {
        mMapElement = new MapElement();
        mMapElement.layer = 5;
    }

    public synchronized boolean decode(Tile tile, ITileDataSink sink, InputStream is) {
        mTileDataSink = sink;
        mTileScale = 1 << tile.zoomLevel;
        mTileX = tile.tileX / mTileScale;
        mTileY = tile.tileY / mTileScale;
        mTileScale *= Tile.SIZE;

        OsmData data;
        try {
            OverpassAPIReader reader = new OverpassAPIReader();
            reader.parse(is);
            data = reader.getData();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        for (OsmNode element : data.getNodes())
            parseFeature(element);
        for (OsmWay element : data.getWays())
            parseFeature(element);
        for (OsmRelation element : data.getRelations())
            parseFeature(element);

        return true;
    }

    private synchronized void parseFeature(OsmElement element) {
        if (element.tags == null || element.tags.size() == 0)
            return;

        synchronized (mMapElement) {
            mMapElement.clear();
            mMapElement.tags.clear();

            mMapElement.tags.set(element.tags);
            //add tag information
            decodeTags(mMapElement);

            parseGeometry(element);

            if (mMapElement.type == GeometryType.NONE)
                return;

            mTileDataSink.process(mMapElement);
        }
    }

    private void parseGeometry(OsmElement element) {
        //TODO mulipolygons
        if (element instanceof OsmWay) {
            boolean linearFeature = !OSMUtils.isArea(mMapElement);
            if (linearFeature) {
                mMapElement.type = GeometryType.LINE;
                parseLine((OsmWay) element);
            } else {
                mMapElement.type = GeometryType.POLY;
                parsePolygon((OsmWay) element);
            }
        } else if (element instanceof OsmNode) {
            mMapElement.type = GeometryType.POINT;
            mMapElement.startPoints();
            parseCoordinate((OsmNode) element);
        }
    }

    private void parsePolygon(OsmWay element) {
        //int ring = 0;

        //for (element.rings) {
        //if (ring == 0)
        mMapElement.startPolygon();
        //else
        //    mMapElement.startHole();

        //ring++;
        parseCoordSequence(element);
        mMapElement.removeLastPoint();
        //}
    }

    private void parseLine(OsmWay element) {
        mMapElement.startLine();
        parseCoordSequence(element);
    }

    private void parseCoordSequence(OsmWay element) {
        for (OsmNode node : element.nodes)
            parseCoordinate(node);
    }

    private void parseCoordinate(OsmNode element) {
        mMapElement.addPoint((float) ((longitudeToX(element.lon) - mTileX) * mTileScale),
                (float) ((latitudeToY(element.lat) - mTileY) * mTileScale));

    }

    private void decodeTags(MapElement mapElement) {
        TagSet tags = mapElement.tags;
        Tag tag = tags.get(Tag.KEY_ROOF_DIRECTION);
        if (tag != null) {
            if (!isNumeric(tag.value)) {
                switch (tag.value.toLowerCase()) {
                    case "n":
                    case "north":
                        tag.value = "0";
                        break;
                    case "e":
                    case "east":
                        tag.value = "90";
                        break;
                    case "s":
                    case "south":
                        tag.value = "180";
                        break;
                    case "w":
                    case "west":
                        tag.value = "270";
                        break;

                    case "ne":
                        tag.value = "45";
                        break;
                    case "se":
                        tag.value = "135";
                        break;
                    case "sw":
                        tag.value = "225";
                        break;
                    case "nw":
                        tag.value = "315";
                        break;

                    case "nne":
                        tag.value = "22";
                        break;
                    case "ene":
                        tag.value = "67";
                        break;
                    case "ese":
                        tag.value = "112";
                        break;
                    case "sse":
                        tag.value = "157";
                        break;
                    case "ssw":
                        tag.value = "202";
                        break;
                    case "wsw":
                        tag.value = "247";
                        break;
                    case "wnw":
                        tag.value = "292";
                        break;
                    case "nnw":
                        tag.value = "337";
                        break;
                    default:
                        tag.value = "0";
                        break;
                }
            }
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
