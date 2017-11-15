/*
 * Copyright 2014 Hannes Janetzek
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
package org.oscim.tiling.source.geojson;

import com.google.gwt.core.client.JavaScriptObject;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.source.ITileDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import static org.oscim.core.MercatorProjection.latitudeToY;
import static org.oscim.core.MercatorProjection.longitudeToX;

public class GeoJsonTileDecoder implements ITileDecoder {
    static final Logger log = LoggerFactory.getLogger(GeoJsonTileDecoder.class);

    private final MapElement mapElement;
    private final GeoJsonTileSource mTileSource;

    private ITileDataSink mTileDataSink;

    public GeoJsonTileDecoder(GeoJsonTileSource tileSource) {
        mTileSource = tileSource;
        mapElement = new MapElement();
        mapElement.layer = 5;
    }

    final static LinkedHashMap<String, Object> mProperties = new LinkedHashMap<String, Object>(10);

    double mTileY, mTileX, mTileScale;

    public boolean decode(Tile tile, ITileDataSink sink, JavaScriptObject jso) {
        mTileDataSink = sink;

        mTileScale = 1 << tile.zoomLevel;
        mTileX = tile.tileX / mTileScale;
        mTileY = tile.tileY / mTileScale;
        mTileScale *= Tile.SIZE;

        FeatureCollection c = (FeatureCollection) jso;

        for (Feature f : c.getFeatures()) {
            mapElement.clear();
            mapElement.tags.clear();

            /* add tag information */
            mTileSource.decodeTags(mapElement, f.getProperties(mProperties));
            if (mapElement.tags.size() == 0)
                continue;

            /* add geometry information */
            decodeGeometry(f.getGeometry());

            if (mapElement.type == GeometryType.NONE)
                continue;

            mTileDataSink.process(mapElement);
        }

        return true;
    }

    private void decodeGeometry(Geometry<?> geometry) {
        String type = geometry.type();

        if ("Polygon".equals(type)) {
            Polygon p = (Polygon) geometry.getCoordinates();
            decodePolygon(p);
        } else if ("MultiPolygon".equals(type)) {
            MultiPolygon mp = (MultiPolygon) geometry.getCoordinates();
            for (int k = 0, l = mp.getNumGeometries(); k < l; k++)
                decodePolygon(mp.getGeometryN(k));

        } else if ("LineString".equals(type)) {
            LineString ls = (LineString) geometry.getCoordinates();
            decodeLineString(ls);

        } else if ("MultiLineString".equals(type)) {
            MultiLineString ml = (MultiLineString) geometry.getCoordinates();
            for (int k = 0, n = ml.getNumGeometries(); k < n; k++)
                decodeLineString(ml.getGeometryN(k));
        }
    }

    private void decodeLineString(LineString l) {
        mapElement.startLine();
        for (int j = 0, m = l.length(); j < m; j++) {
            decodePoint(l.get(j));
        }
    }

    private void decodePolygon(Polygon p) {
        for (int i = 0, n = p.getNumRings(); i < n; i++) {
            if (i > 0)
                mapElement.startHole();
            else
                mapElement.startPolygon();

            LineString ls = p.getRing(i);
            for (int j = 0, m = ls.length() - 1; j < m; j++)
                decodePoint(ls.get(j));
        }
    }

    private void decodePoint(LngLat point) {

        float x = (float) ((longitudeToX(point.getLongitude()) - mTileX) * mTileScale);
        float y = (float) ((latitudeToY(point.getLatitude()) - mTileY) * mTileScale);

        mapElement.addPoint(x, y);
    }

    @Override
    public boolean decode(Tile tile, ITileDataSink sink, InputStream is) throws IOException {
        return false;
    }
}
