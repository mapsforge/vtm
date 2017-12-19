/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017 Gustl22
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
package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.core.GeometryBuffer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.renderer.BufferObject;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

public class ExtrusionBuckets extends TileData {
    static final Logger log = LoggerFactory.getLogger(ExtrusionBuckets.class);

    public ExtrusionBucket buckets;

    public boolean compiled;
    public long animTime;

    public final int zoomLevel;

    public final double x;
    public final double y;

    public BufferObject ibo;
    public BufferObject vbo;

    public ExtrusionBuckets(MapTile tile) {
        zoomLevel = tile.zoomLevel;
        x = tile.x;
        y = tile.y;
    }

    /**
     * Add ExtrusionBucket at last position
     */
    private void add(ExtrusionBucket el) {
        if (buckets == null) {
            this.buckets = el;
        }
        RenderBucket b = buckets;
        while (b.next != null) b = b.next;

        b.next = el;
    }

    /**
     * Insert ExtrusionBucket at first position
     */
    private void push(ExtrusionBucket el) {
        this.buckets = Inlist.push(this.buckets, el);
    }

    /**
     * Add poly element to corresponding ExtrusionBucket
     *
     * @param element     the MapElement as polygon
     * @param groundScale the scale of ground
     * @param colors      the colors (top, side, side, line) of element
     * @param height      the height of extrusion
     * @param minHeight   the minimum height of extrusion
     */
    public void addPolyElement(GeometryBuffer element, float groundScale, float[] colors, int height, int minHeight) {
        // Add to bucket which has same color
        for (ExtrusionBucket eb = this.buckets(); eb != null; eb = eb.next()) {
            if (eb.getColors() == colors) {
                eb.addPoly(element, height, minHeight);
                return;
            }
        }

        // Add to new bucket with different color
        ExtrusionBucket eb = new ExtrusionBucket(0, groundScale, colors);
        eb.addPoly(element, height, minHeight);
        this.push(eb);
    }

    /**
     * Add mesh element to corresponding ExtrusionBucket
     *
     * @param element     the MapElement as mesh
     * @param groundScale the scale of ground
     * @param color       the color of element
     */
    public void addMeshElement(GeometryBuffer element, float groundScale, int color) {
        // Add to bucket which has same color
        for (ExtrusionBucket eb = this.buckets(); eb != null; eb = eb.next()) {
            if (eb.getColor() == color) {
                eb.addMesh(element);
                return;
            }
        }

        // Add to new bucket with different color
        ExtrusionBucket eb = new ExtrusionBucket(0, groundScale, color);
        eb.addMesh(element);
        this.push(eb);
    }

    /**
     * Set new ExtrusionBuckets and clear previous.
     */
    public void resetBuckets(ExtrusionBucket el) {
        for (RenderBucket b = buckets; b != null; b = b.next)
            b.clear();

        buckets = el;
    }

    /**
     * Set ExtrusionBuckets.
     */
    public void setBuckets(ExtrusionBucket el) {
        buckets = el;
    }

    /**
     * Get root bucket
     */
    public ExtrusionBucket buckets() {
        return buckets;
    }

    @Override
    protected void dispose() {
        resetBuckets(null);

        if (compiled) {
            ibo = BufferObject.release(ibo);
            vbo = BufferObject.release(vbo);
        }

    }

    public void prepare() {
        for (RenderBucket b = buckets; b != null; b = b.next)
            b.prepare();
    }

    public boolean compile() {

        if (buckets == null)
            return false;

        int sumIndices = 0;
        int sumVertices = 0;

        for (ExtrusionBucket b = buckets; b != null; b = b.next()) {
            sumIndices += b.numIndices;
            sumVertices += b.numVertices;
        }
        if (sumIndices == 0)
            return false;

        ShortBuffer vboData = MapRenderer.getShortBuffer(sumVertices * 4);
        ShortBuffer iboData = MapRenderer.getShortBuffer(sumIndices);

        for (ExtrusionBucket b = buckets; b != null; b = b.next())
            b.compile(vboData, iboData);

        int size = sumIndices * 2;
        if (iboData.position() != sumIndices) {
            int pos = iboData.position();
            log.error("invalid indice size: {} {}", sumIndices, pos);
            size = pos * 2;
        }
        ibo = BufferObject.get(GL.ELEMENT_ARRAY_BUFFER, size);
        ibo.loadBufferData(iboData.flip(), size);

        size = sumVertices * 4 * 2;
        if (vboData.position() != sumVertices * 4) {
            int pos = vboData.position();
            log.error("invalid vertex size: {} {}", sumVertices, pos);
            size = pos * 2;
        }

        vbo = BufferObject.get(GL.ARRAY_BUFFER, size);
        vbo.loadBufferData(vboData.flip(), size);

        compiled = true;

        return true;
    }

}
