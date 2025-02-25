package org.oscim.utils;

import com.google.gwt.core.client.*;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Int32Array;
import org.oscim.core.GeometryBuffer;
import org.oscim.renderer.bucket.VertexData;

import java.util.logging.Logger;

public class Tessellator {
    private static final Logger log = Logger.getLogger(Tessellator.class.getName());

    public static int tessellate(GeometryBuffer geom, float scale,
                                 VertexData outPoints, VertexData outTris, int vertexOffset) {

        int numIndices = 0;
        int indexPos = 0;
        int pointPos = 0;
        int indexEnd = geom.index.length;

        JsArrayNumber jspoints = JsArrayUtils.readOnlyJsArray(geom.points);
        JsArrayInteger jsindex = JsArrayUtils.readOnlyJsArray(geom.index);

        for (int idx = 0; idx < indexEnd && geom.index[idx] > 0; idx++) {
            indexPos = idx;

            int numRings = 1;
            int numPoints = geom.index[idx++];

            for (; idx < indexEnd && geom.index[idx] > 0; idx++) {
                numRings++;
                numPoints += geom.index[idx];
            }

            if (numPoints <= 0 && numRings == 1) {
                log.fine("tessellation skip empty");
                pointPos += numPoints;
                continue;
            }

            TessResult res;
            try {
                res = tessellate2(jspoints, pointPos, numPoints,
                        jsindex, indexPos, numRings);
            } catch (JavaScriptException e) {
                e.printStackTrace();
                return 0;
            }
            pointPos += numPoints;

            if (res == null) {
                log.fine("tessellation failed");
                continue;
            }

            Int32Array io = res.getIndices(res);
            int resIndices = io.length();
            numIndices += resIndices;

            for (int k = 0, cnt = 0; k < resIndices; k += cnt) {
                VertexData.Chunk chunk = outTris.obtainChunk();

                cnt = VertexData.SIZE - chunk.used;

                if (k + cnt > resIndices)
                    cnt = resIndices - k;

                for (int i = 0; i < cnt; i++)
                    chunk.vertices[chunk.used + i] =
                            (short) (vertexOffset + io.get(k + i));

                chunk.used += cnt;
                outTris.releaseChunk();
            }

            Float32Array po = res.getPoints(res);
            int resPoints = po.length();

            vertexOffset += (resPoints >> 1);

            for (int k = 0, cnt = 0; k < resPoints; k += cnt) {
                VertexData.Chunk chunk = outPoints.obtainChunk();

                cnt = VertexData.SIZE - chunk.used;

                if (k + cnt > resPoints)
                    cnt = resPoints - k;

                for (int i = 0; i < cnt; i++)
                    chunk.vertices[chunk.used + i] =
                            (short) (po.get(k + i) * scale);

                chunk.used += cnt;
                outPoints.releaseChunk();
            }

            if (idx >= indexEnd || geom.index[idx] < 0)
                break;
        }

        return numIndices;
    }

    public static int tessellate(float[] points, int ppos, int plen, int[] index,
                                 int ipos, int rings, int vertexOffset, VertexData outTris) {

        Int32Array io;
        try {
            io = tessellate(JsArrayUtils.readOnlyJsArray(points), ppos, plen,
                    JsArrayUtils.readOnlyJsArray(index), ipos, rings);
        } catch (JavaScriptException e) {
            e.printStackTrace();
            return 0;
        }

        if (io == null) {
            //log.debug("building tessellation failed");
            return 0;
        }

        //        if (vo.length() != plen) {
        //            // TODO handle different output points
        //            log.debug(" + io.length());
        //
        //            //for (int i = 0; i < vo.length(); i += 2)
        //            //    log.debug(vo.get(i) + " " + vo.get(i + 1));
        //            //for (int i = ppos; i < ppos + plen; i += 2)
        //            //    log.debug( points[i]+ " " + points[i + 1]);
        //
        //            return 0;
        //        }

        int numIndices = io.length();

        for (int k = 0, cnt = 0; k < numIndices; k += cnt) {
            VertexData.Chunk chunk = outTris.obtainChunk();

            cnt = VertexData.SIZE - chunk.used;

            if (k + cnt > numIndices)
                cnt = numIndices - k;

            for (int i = 0; i < cnt; i++) {
                int idx = (vertexOffset + io.get(k + i));
                chunk.vertices[chunk.used + i] = (short) idx;
            }
            chunk.used += cnt;
            outTris.releaseChunk();
        }

        return numIndices;
    }

    public static int tessellate(GeometryBuffer geom, GeometryBuffer out) {
        return 0;
    }

    static native Int32Array tessellate(JsArrayNumber points, int pOffset, int pLength,
                                        JsArrayInteger bounds, int bOffset, int bLength)/*-{

        return $wnd.tessellate(points, pOffset, pOffset + pLength, bounds,
                bOffset, bOffset + bLength, false);
    }-*/;

    static native TessResult tessellate2(JsArrayNumber points, int pOffset, int pLength,
                                         JsArrayInteger bounds, int bOffset, int bLength)
    /*-{

        return $wnd.tessellate(points, pOffset, pOffset + pLength, bounds,
                bOffset, bOffset + bLength, true);
    }-*/;

    static final class TessResult extends JavaScriptObject {
        protected TessResult() {
        }

        native Float32Array getPoints(JavaScriptObject result)/*-{
            return result.vertices;
        }-*/;

        native Int32Array getIndices(JavaScriptObject result)/*-{
            return result.triangles;
        }-*/;
    }
}
