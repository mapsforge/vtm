#include "tessellate.h"
#include <stdlib.h>
#include <stdint.h>
#include <jni.h>

#ifdef __ANDROID__
#include <android/log.h>

#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, "Tesselate", __VA_ARGS__)
#endif

#define CAST_CTX(x) (TessContext *)(uintptr_t) x

void Java_org_oscim_utils_Tessellator_tessFinish(JNIEnv *env, jclass c, jlong ptr_context) {

   TessContext *ctx = CAST_CTX(ptr_context);

   while (ctx->latest_v) {
      Vertex *prev = ctx->latest_v->prev;
      free(ctx->latest_v);
      ctx->latest_v = prev;
   }

   while (ctx->latest_t) {
      Triangle *prev = ctx->latest_t->prev;
      free(ctx->latest_t);
      ctx->latest_t = prev;
   }

   //destroy_tess_context(ctx);
   free(ctx);
}

Vertex *reverse(Vertex *root) {
   Vertex *start = 0;

   while (root) {
      Vertex *prev = root->prev;
      root->prev = start;
      start = root;
      root = prev;
   }
   return start;
}

jint Java_org_oscim_utils_Tessellator_tessGetVerticesWO(JNIEnv *env, jclass c,
      jlong ptr_context, jshortArray obj_coords, jint offset, jfloat scale) {

   TessContext *ctx = CAST_CTX(ptr_context);

   int length = (*env)->GetArrayLength(env, obj_coords);

   jshort* coords = (jshort*) (*env)->GetPrimitiveArrayCritical(env, obj_coords, 0);
   if (coords == NULL) {
      return 0;
   }

   if (!ctx->reversed) {
      ctx->reversed = 1;
      ctx->latest_v = reverse(ctx->latest_v);
   }

   int pos = offset;

   for (; ctx->latest_v && pos < length; pos += 2) {
      coords[pos + 0] = (ctx->latest_v->pt[0] * scale) + 0.5f;
      coords[pos + 1] = (ctx->latest_v->pt[1] * scale) + 0.5f;
      Vertex *prev = ctx->latest_v->prev;
      free(ctx->latest_v);
      ctx->latest_v = prev;
   }
   (*env)->ReleasePrimitiveArrayCritical(env, obj_coords, coords, JNI_ABORT);

   return pos - offset;
}

jint Java_org_oscim_utils_Tessellator_tessGetVertices(JNIEnv *env, jclass c,
      jlong ptr_context, jshortArray obj_coords, jfloat scale) {

   return Java_org_oscim_utils_Tessellator_tessGetVerticesWO(env, c, ptr_context, obj_coords, 0,
         scale);
}

jint Java_org_oscim_utils_Tessellator_tessGetVerticesD(JNIEnv *env, jclass c,
      jlong ptr_context, jdoubleArray obj_coords) {

   TessContext *ctx = CAST_CTX(ptr_context);

   int length = (*env)->GetArrayLength(env, obj_coords);

   jdouble* coords = (jdouble*) (*env)->GetPrimitiveArrayCritical(env, obj_coords, 0);
   if (coords == NULL) {
      return 0;
   }

   if (!ctx->reversed) {
      ctx->reversed = 1;
      ctx->latest_v = reverse(ctx->latest_v);
   }

   int cnt = 0;
   for (; ctx->latest_v && cnt < length; cnt += 2) {
      coords[cnt + 0] = ctx->latest_v->pt[0];
      coords[cnt + 1] = ctx->latest_v->pt[1];
      Vertex *prev = ctx->latest_v->prev;
      free(ctx->latest_v);
      ctx->latest_v = prev;
   }

   (*env)->ReleasePrimitiveArrayCritical(env, obj_coords, coords, JNI_ABORT);

   return cnt;
}

jint Java_org_oscim_utils_Tessellator_tessGetIndicesWO(JNIEnv *env, jclass c,
      jlong ptr_context, jshortArray obj_indices, int offset) {

   TessContext *ctx = CAST_CTX(ptr_context);

   int length = (*env)->GetArrayLength(env, obj_indices);

   jshort* tris = (jshort*) (*env)->GetPrimitiveArrayCritical(env, obj_indices, 0);
   if (tris == NULL) {
      return 0;
   }

   int n_tris_copy = ctx->n_tris;

   int pos = offset;

   for (; ctx->latest_t && pos < length; pos += 3) {
      tris[pos + 0] = ctx->latest_t->v[0];
      tris[pos + 1] = ctx->latest_t->v[1];
      tris[pos + 2] = ctx->latest_t->v[2];
      Triangle *prev = ctx->latest_t->prev;

      free(ctx->latest_t);
      ctx->latest_t = prev;
      n_tris_copy--;
   }

   ctx->n_tris = n_tris_copy;

   (*env)->ReleasePrimitiveArrayCritical(env, obj_indices, tris, JNI_ABORT);

   return pos - offset;
}


jint Java_org_oscim_utils_Tessellator_tessGetIndices(JNIEnv *env, jclass c,
      jlong ptr_context, jshortArray obj_indices, int offset){

   return Java_org_oscim_utils_Tessellator_tessGetIndicesWO(env, c,
         ptr_context, obj_indices, 0);
}

jlong Java_org_oscim_utils_Tessellator_tessellate(JNIEnv *env, jclass c,
      jfloatArray obj_points, jint pos,
      jshortArray obj_index, jint ipos,
      jint num_rings, jintArray obj_out) {

   //printf("add %d %d %d\n", pos, ipos, num_rings);
   jboolean isCopy;

   float* orig_points = (float*) (*env)->GetPrimitiveArrayCritical(env, obj_points, &isCopy);
   if (orig_points == NULL)
      return 0;

   const float *points = orig_points + pos;

   jshort* orig_indices = (jshort*) (*env)->GetPrimitiveArrayCritical(env, obj_index, &isCopy);
   if (orig_indices == NULL) {
      (*env)->ReleasePrimitiveArrayCritical(env, obj_points, orig_points, JNI_ABORT);
      return 0;
   }

   jshort* indices = orig_indices + ipos;

   const float **rings = malloc(sizeof(float*) * (num_rings + 1));
   int offset = 0;
   for (int i = 0; i < num_rings; i++) {
      rings[i] = points + offset;
      offset += indices[i];
   }

   (*env)->ReleasePrimitiveArrayCritical(env, obj_index, orig_indices, JNI_ABORT);
   (*env)->ReleasePrimitiveArrayCritical(env, obj_points, orig_points, JNI_ABORT);

   rings[num_rings] = points + offset;

   int nverts, ntris;

   TessContext *ctx = tessellate(NULL, &nverts, NULL, &ntris,
         rings, rings + (num_rings + 1));

   free(rings);

   nverts = 1 + ctx->latest_v->index;
   ntris = ctx->n_tris;

   jint* out = (jint*) (*env)->GetPrimitiveArrayCritical(env, obj_out, &isCopy);
   if (out == NULL) {
      return 0;
   }

   out[0] = nverts;
   out[1] = ntris;

   (*env)->ReleasePrimitiveArrayCritical(env, obj_out, out, JNI_ABORT);

   return (long) ctx;
}

jlong Java_org_oscim_renderer_sublayers_MeshLayer_tessellateD(JNIEnv *env, jclass c,
      jdoubleArray obj_points, jint pos,
      jshortArray obj_index, jint ipos,
      jint num_rings) { //, jintArray obj_out) {

   jboolean isCopy;

   //printf("add %d %d %d\n", pos, ipos, num_rings);

   double* orig_points = (double*) (*env)->GetPrimitiveArrayCritical(env, obj_points, &isCopy);
   if (orig_points == NULL)
      return 0;

   const double *points = orig_points + pos;

   jshort* orig_indices = (jshort*) (*env)->GetPrimitiveArrayCritical(env, obj_index, &isCopy);
   if (orig_indices == NULL) {
      (*env)->ReleasePrimitiveArrayCritical(env, obj_points, orig_points, JNI_ABORT);
      return 0;
   }

   jshort* indices = orig_indices + ipos;

   const double **rings = malloc(sizeof(double*) * (num_rings + 1));
   int offset = 0;
   for (int i = 0; i < num_rings; i++) {
      rings[i] = points + offset;
      offset += indices[i];
   }
   rings[num_rings] = points + offset;

   int nverts, ntris;

   TessContext *ctx = tessellateD(NULL, &nverts, NULL, &ntris,
         rings, rings + (num_rings + 1));

   free(rings);

   (*env)->ReleasePrimitiveArrayCritical(env, obj_index, orig_indices, JNI_ABORT);
   (*env)->ReleasePrimitiveArrayCritical(env, obj_points, orig_points, JNI_ABORT);

   return (long) ctx;
}
