#include <org_oscim_utils_TessJNI.h>

//@line:114

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
     JNIEXPORT jlong JNICALL Java_org_oscim_utils_TessJNI_newTess(JNIEnv* env, jclass clazz, jint size) {


//@line:131
 {
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
    } 

}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_freeTess(JNIEnv* env, jclass clazz, jlong inst) {


//@line:152
 {
        tessDeleteTess((TESStesselator*) inst);
    } 

}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_addContour(JNIEnv* env, jclass clazz, jlong inst, jint size, jfloatArray obj_contour, jint stride, jint offset, jint count) {
	float* contour = (float*)env->GetPrimitiveArrayCritical(obj_contour, 0);


//@line:166
 {
        tessAddContour((TESStesselator*) inst, size, contour + (offset * stride), stride, count);
    } 
	env->ReleasePrimitiveArrayCritical(obj_contour, contour, 0);

}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_addMultiContour2D(JNIEnv* env, jclass clazz, jlong inst, jintArray obj_index, jfloatArray obj_contour, jint idxStart, jint idxCount) {
	int* index = (int*)env->GetPrimitiveArrayCritical(obj_index, 0);
	float* contour = (float*)env->GetPrimitiveArrayCritical(obj_contour, 0);


//@line:170
 {
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
    } 
	env->ReleasePrimitiveArrayCritical(obj_index, index, 0);
	env->ReleasePrimitiveArrayCritical(obj_contour, contour, 0);

}

JNIEXPORT jint JNICALL Java_org_oscim_utils_TessJNI_tessContour2D(JNIEnv* env, jclass clazz, jlong inst, jint windingRule, jint elementType, jint polySize, jint vertexSize) {


//@line:198
{
        return tessTesselate((TESStesselator*) inst, windingRule, elementType, polySize, vertexSize, 0);
    } 

}

JNIEXPORT jint JNICALL Java_org_oscim_utils_TessJNI_getVertexCount(JNIEnv* env, jclass clazz, jlong inst) {


//@line:202
{
        return tessGetVertexCount((TESStesselator*) inst);
    }

}

static inline jboolean wrapped_Java_org_oscim_utils_TessJNI_getVertices
(JNIEnv* env, jclass clazz, jlong inst, jfloatArray obj_out, jint offset, jint length, float* out) {

//@line:209
{
        const TESSreal* vertices = tessGetVertices((TESStesselator*) inst);
        if (!vertices)
            return 0;
        memcpy(out, vertices + offset, length * sizeof(TESSreal));
        return 1;
    }
}

JNIEXPORT jboolean JNICALL Java_org_oscim_utils_TessJNI_getVertices(JNIEnv* env, jclass clazz, jlong inst, jfloatArray obj_out, jint offset, jint length) {
	float* out = (float*)env->GetPrimitiveArrayCritical(obj_out, 0);

	jboolean JNI_returnValue = wrapped_Java_org_oscim_utils_TessJNI_getVertices(env, clazz, inst, obj_out, offset, length, out);

	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

	return JNI_returnValue;
}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_getVerticesS(JNIEnv* env, jclass clazz, jlong inst, jshortArray obj_out, jint offset, jint length, jfloat scale) {
	short* out = (short*)env->GetPrimitiveArrayCritical(obj_out, 0);


//@line:220
{
        const TESSreal* vertices = tessGetVertices((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[i] = (short)(vertices[offset++] * scale + 0.5f);
    }
	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

}

static inline jboolean wrapped_Java_org_oscim_utils_TessJNI_getVertexIndices
(JNIEnv* env, jclass clazz, jlong inst, jintArray obj_out, jint offset, jint length, int* out) {

//@line:233
 {
        const TESSindex* indices = tessGetVertexIndices((TESStesselator*) inst);
        if (!indices)
            return 0;
        memcpy(out, indices + offset, length * sizeof(TESSindex));
        return 1;
    } 
}

JNIEXPORT jboolean JNICALL Java_org_oscim_utils_TessJNI_getVertexIndices(JNIEnv* env, jclass clazz, jlong inst, jintArray obj_out, jint offset, jint length) {
	int* out = (int*)env->GetPrimitiveArrayCritical(obj_out, 0);

	jboolean JNI_returnValue = wrapped_Java_org_oscim_utils_TessJNI_getVertexIndices(env, clazz, inst, obj_out, offset, length, out);

	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

	return JNI_returnValue;
}

JNIEXPORT jint JNICALL Java_org_oscim_utils_TessJNI_getElementCount(JNIEnv* env, jclass clazz, jlong inst) {


//@line:244
{
        return tessGetElementCount((TESStesselator*) inst);
    }

}

static inline jboolean wrapped_Java_org_oscim_utils_TessJNI_getElements
(JNIEnv* env, jclass clazz, jlong inst, jintArray obj_out, jint offset, jint length, int* out) {

//@line:251
{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        if (!elements)
            return 0;
        memcpy(out, elements + offset, length * sizeof(TESSindex));
        return 1;
    }
}

JNIEXPORT jboolean JNICALL Java_org_oscim_utils_TessJNI_getElements(JNIEnv* env, jclass clazz, jlong inst, jintArray obj_out, jint offset, jint length) {
	int* out = (int*)env->GetPrimitiveArrayCritical(obj_out, 0);

	jboolean JNI_returnValue = wrapped_Java_org_oscim_utils_TessJNI_getElements(env, clazz, inst, obj_out, offset, length, out);

	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

	return JNI_returnValue;
}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_getElementsS(JNIEnv* env, jclass clazz, jlong inst, jshortArray obj_out, jint offset, jint length) {
	short* out = (short*)env->GetPrimitiveArrayCritical(obj_out, 0);


//@line:262
{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[i] = (short)elements[offset++];
    }
	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

}

JNIEXPORT void JNICALL Java_org_oscim_utils_TessJNI_getElementsWithInputVertexIds(JNIEnv* env, jclass clazz, jlong inst, jshortArray obj_out, jint dstOffset, jint offset, jint length) {
	short* out = (short*)env->GetPrimitiveArrayCritical(obj_out, 0);


//@line:271
{
        const TESSindex* elements = tessGetElements((TESStesselator*) inst);
        const TESSindex* indices = tessGetVertexIndices((TESStesselator*) inst);
        for(int i = 0; i < length; i++)
            out[dstOffset++] = (short)(indices[elements[offset++]]);
    }
	env->ReleasePrimitiveArrayCritical(obj_out, out, 0);

}

