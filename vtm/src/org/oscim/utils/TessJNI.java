package org.oscim.utils;

public class TessJNI {
    private long inst;

    public TessJNI() {
        inst = newTess(0);
    }

    public TessJNI(int bucketSize) {
        inst = newTess(bucketSize);
    }

    @Override
    protected void finalize() {
        dispose();
    }

    public void dispose() {
        if (inst != 0) {
            freeTess(inst);
            inst = 0;
        }
    }

    protected long instance() {
        return inst;
    }

    /**
     * See OpenGL Red Book for description of the winding rules
     * http://www.glprogramming.com/red/chapter11.html
     */
    public static final class WindingRule {
        public static final int ODD = 0;
        public static final int NONZERO = 1;
        public static final int POSITIVE = 2;
        public static final int NEGATIVE = 3;
        public static final int ABS_GEQ_TWO = 4;
    }

    public static final class ElementType {
        public static final int POLYGONS = 0;
        public static final int CONNECTED_POLYGONS = 1;
        public static final int BOUNDARY_CONTOURS = 2;
    }

    public void addContour2D(float[] points) {
        addContour2D(points, 0, points.length >> 1);
    }

    public void addContour2D(float[] points, int offset, int length) {
        if (length < 6)
            return;
        if ((length % 2 != 0) || (offset % 2 != 0) || (points.length >> 1) < (offset + length))
            throw new IllegalArgumentException("Invalid input: length:" + length
                    + ", offset:" + offset
                    + ", points.length:" + points.length);
        addContour(inst, 2, points, 8, offset, length);
    }

    public void addContour2D(int[] index, double[] contour) {
        addMultiContour2D(inst, index, contour, 0, index.length);
    }

    public void addContour2D(int[] index, double[] contour, int idxStart, int idxEnd) {
        addMultiContour2D(inst, index, contour, idxStart, idxEnd);
    }

    public boolean tesselate() {
        return tessContour2D(inst,
                TessJNI.WindingRule.POSITIVE,
                TessJNI.ElementType.POLYGONS,
                3, 2) == 1;
    }

    public boolean tesselate(int windingRule, int elementType) {
        return tessContour2D(inst, windingRule, elementType, 3, 2) == 1;
    }

    public int getVertexCount() {
        return getVertexCount(inst);
    }

    public int getElementCount() {
        return getElementCount(inst);
    }

    public void getVertices(float[] out, int offset, int length) {
        getVertices(inst, out, offset, length);
    }

    public void getVertices(short[] out, int offset, int length, float scale) {
        getVerticesS(inst, out, offset, length, scale);
    }

    public void getElements(int[] out, int offset, int length) {
        getElements(inst, out, offset, length);
    }

    public void getElements(short[] out, int offset, int length) {
        getElementsS(inst, out, offset, length);
    }

    public void getVertexIndices(int[] out, int offset, int length) {
        getVertexIndices(inst, out, offset, length);
    }

    public void getElementsWithInputVertexIds(short[] dst, int dstOffset, int offset, int length) {
        getElementsWithInputVertexIds(inst, dst, dstOffset, offset, length);
    }

    // @formatter:off
    /*JNI
    #include <tesselator.h>
    #include <string.h>
    #include <stdlib.h>
    void* heapAlloc( void* userData, unsigned int size ){
        TESS_NOTUSED( userData );
        return malloc( size );
    }
    void* heapRealloc( void *userData, void* ptr, unsigned int size ){
        TESS_NOTUSED( userData );
        return realloc( ptr, size );
    }
    void heapFree( void* userData, void* ptr ){
        TESS_NOTUSED( userData );
        free( ptr );
    }
     */
    static native long newTess(int size); /* {
        if (size <= 0)
           return (long)tessNewTess(0);
        if (size > 10)
            size = 10;
        TESSalloc ma;
        memset(&ma, 0, sizeof(ma));
        ma.memalloc = heapAlloc;
        ma.memfree = heapFree;
        ma.memrealloc = heapRealloc;
        //ma.userData = (void*)&allocated;
        ma.meshEdgeBucketSize = 2 << size;   // 512
        ma.meshVertexBucketSize = 2 << size; // 512
        ma.meshFaceBucketSize = 1 << size;     // 256
        ma.dictNodeBucketSize = 2 << size;     // 512
        ma.regionBucketSize = 1 << size;     // 256
        ma.extraVertices = 8;
        //ma.extraVertices = 256;
        return (long)tessNewTess(&ma);
    } */

    static native void freeTess(long inst); /* {
        tessDeleteTess((TESStesselator*) inst);
    } */

    /**
     * Adds a contour to be tesselated.
     * The type of the vertex coordinates is assumed to be TESSreal.
     *
     * @param tess    - pointer to tesselator object.
     * @param size    - number of coordinates per vertex. Must be 2 or 3.
     * @param pointer - pointer to the first coordinate of the first vertex in the array.
     * @param stride  - defines offset in bytes between consecutive vertices.
     * @param count   - number of vertices in contour.
     */
    static native void addContour(long inst, int size, float[] contour, int stride, int offset, int count);/* {
        tessAddContour((TESStesselator*) inst, size, contour + (offset * stride), stride, count);
    } */

    static native void addMultiContour2D(long inst, int[] index, double[] contour, int idxStart, int idxCount);/* {
        TESStesselator* tess = (TESStesselator*) inst;
        int offset = 0;
        // start at 0 to get the correct offset in contour..
        for (int i = 0; i < idxStart + idxCount; i++){
            int len = index[i];
            if ((len % 2 != 0) || (len < 0))
                break;
            if (len < 6 || i < idxStart) {
                offset += len;
                continue;
            }
            tessAddContour(tess, 2, contour + offset, 8, len >> 1);
            offset += len;
        }
    } */

    /**
     * Tesselate contours.
     *
     * @param tess        - pointer to tesselator object.
     * @param windingRule - winding rules used for tesselation, must be one of TessWindingRule.
     * @param elementType - defines the tesselation result element type, must be one of TessElementType.
     * @param polySize    - defines maximum vertices per polygons if output is polygons.
     * @param vertexSize  - defines the number of coordinates in tesselation result vertex, must be 2 or 3.
     * @param normal      - defines the normal of the input contours, of null the normal is calculated automatically.
     * @return 1 if succeed, 0 if failed.
     */
    static native int tessContour2D(long inst, int windingRule, int elementType, int polySize, int vertexSize);/*{
        return tessTesselate((TESStesselator*) inst, windingRule, elementType, polySize, vertexSize, 0);
    } */

    static native int getVertexCount(long inst); /*{
        return tessGetVertexCount((TESStesselator*) inst);
    }*/

    /**
     * Returns pointer to first coordinate of first vertex.
     */
    static native boolean getVertices(long inst, float[] out, int offset, int length);/*{
        const TESSreal* vertices = tessGetVertices((TESStesselator*) inst);
        if (!vertices)
            return 0;
        memcpy(out, vertices + offset, length * sizeof(TESSreal));
        return 1;
    }*/

    /**
     * Returns pointer to first coordinate of first vertex.
     */
    static native void getVerticesS(long inst, short[] out, int offset, int length, float scale);/*{
        const TESSreal* vertices = tessGetVertices((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[i] = (short)(vertices[offset++] * scale + 0.5f);
    }*/

    /**
     * Returns pointer to first vertex index.
     * <p/>
     * Vertex indices can be used to map the generated vertices to the original vertices.
     * Every point added using tessAddContour() will get a new index starting at 0.
     * New vertices generated at the intersections of segments are assigned value TESS_UNDEF.
     */
    static native boolean getVertexIndices(long inst, int[] out, int offset, int length);/* {
        const TESSindex* indices = tessGetVertexIndices((TESStesselator*) inst);
        if (!indices)
            return 0;
        memcpy(out, indices + offset, length * sizeof(TESSindex));
        return 1;
    } */

    /**
     * Returns number of elements in the the tesselated output.
     */
    static native int getElementCount(long inst);/*{
        return tessGetElementCount((TESStesselator*) inst);
    }*/

    /**
     * Returns pointer to the first element.
     */
    static native boolean getElements(long inst, int[] out, int offset, int length);/*{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        if (!elements)
            return 0;
        memcpy(out, elements + offset, length * sizeof(TESSindex));
        return 1;
    }*/

    /**
     * Returns pointer to the first element.
     */
    static native void getElementsS(long inst, short[] out, int offset, int length);/*{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[i] = (short)elements[offset++];
    }*/

    /**
     * Returns list of triangles indices (or to the first element of convex polygons).
     */
    static native void getElementsWithInputVertexIds(long inst, short[] out, int dstOffset, int offset, int length);/*{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        const TESSindex* indices = tessGetVertexIndices((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[dstOffset++] = (short)(indices[elements[offset++]]);
    }*/
}