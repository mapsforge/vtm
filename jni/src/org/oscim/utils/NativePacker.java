/*
 * Copyright 2018 Longri
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
package org.oscim.utils;


/**
 * Created by Longri on 15.02.2018.
 */
public class NativePacker {

    //@off
    /*JNI

    #include "pack.h"
    #include <cstring>
    #include <algorithm>

    int* packRecArray(short *valueArray, int count, int maxTexSize, bool allowFlipp, bool debug) {


    rect_xywhf rects[count], *ptr_rects[count];

    int index = 0;
    for (int i = 0; i < count; ++i) {
        rects[i] = rect_xywhf(valueArray[index + 0], //index
                              valueArray[index + 1], // x
                              valueArray[index + 2], // y
                              valueArray[index + 3], // width
                              valueArray[index + 4]); // height
        ptr_rects[i] = rects + i;
        index += 7;
    }

    std::vector<bin> bins;

    if (pack(ptr_rects, count, maxTexSize, allowFlipp, bins)) {
        if (debug) {
            printf("bins: %d\n", bins.size());
        }

        int *ret = new int[(bins.size()*2)+1];
        ret[0] = bins.size();
        int idx=1;
        for (int i = 0; i < bins.size(); ++i) {
            if (debug) {
                printf("\n\nbin: %dx%d, rects: %d\n", bins[i].size.w, bins[i].size.h, bins[i].rects.size());
            }

            ret[idx++] = bins[i].size.w;
            ret[idx++] = bins[i].size.h;

            for (int r = 0; r < bins[i].rects.size(); ++r) {
                rect_xywhf *rect = bins[i].rects[r];

                int recIndex = rect->index * 7;
                valueArray[recIndex + 1] = static_cast<short>(rect->x);
                valueArray[recIndex + 2] = static_cast<short>(rect->y);
                valueArray[recIndex + 3] = static_cast<short>(rect->w);
                valueArray[recIndex + 4] = static_cast<short>(rect->h);
                valueArray[recIndex + 5] = static_cast<short>(rect->flipped ? 1 : 0);
                valueArray[recIndex + 6] = static_cast<short>(i);

                if (debug) {
                    printf("REC index: %d x: %d, y: %d, w: %d, h: %d, was flipped: %s\n", rect->index, rect->x,
                           rect->y, rect->w, rect->h,
                           rect->flipped ? "yes" : " no");
                }
            }
        }
        return ret;

        if (debug) {
            printf("\n Array result:\n");


            //print ValueArray
            index = 0;
            for (int i = 0; i < count; ++i) {
                printf("REC index: %d x: %d, y: %d, w: %d, h: %d, was flipped: %s textureIndex:  %d \n" //
                        , valueArray[index + 0] // index
                        , valueArray[index + 1] // x
                        , valueArray[index + 2] // y
                        , valueArray[index + 3] // width
                        , valueArray[index + 4] // height
                        , valueArray[index + 5] > 0 ? "yes" : " no" // flipped
                        , valueArray[index + 6]); // texture index

                index += 7;
            }
        }
    } else {
        printf("failed: there's a rectangle with width/height bigger than max_size!\n");
    }
    int *ret = new int[1];
    ret[0]=0;
    return ret;
}

JNIEXPORT jintArray JNICALL Java_org_oscim_utils_NativePacker_privatePackNative(JNIEnv* env, jclass clazz, jshortArray obj_valueArray, jint count, jint maxTextureSize, jboolean allowFlip, jboolean writeDebug) {

	short* valueArray = (short*)env->GetPrimitiveArrayCritical(obj_valueArray, 0);
	int *ret = packRecArray(valueArray, count, maxTextureSize, allowFlip, writeDebug);
    env->ReleasePrimitiveArrayCritical(obj_valueArray, valueArray, 0);

    jintArray result;
    int size = (ret[0]*2)+1;
    result = (env)->NewIntArray( size);
    if (result == NULL) {
        return NULL;
    }
    int i;
    // fill a temp structure to use to populate the java int array
    jint fill[size];
        for (i = 0; i < size; i++) {
        fill[i] = ret[i]; // put whatever logic you want to populate the values here.
        }
        // move from the temp structure to the java structure
        (env)->SetIntArrayRegion(result, 0, size, fill);
        return result;
}




     */

    public static int[] packNative(short[] valueArray, int count, int maxTextureSize, boolean allowFlip, boolean writeDebug) {
        return privatePackNative(valueArray, count, maxTextureSize, allowFlip, writeDebug);
    }


    private static native int[] privatePackNative(short[] valueArray, int count, int maxTextureSize, boolean allowFlip, boolean writeDebug);

}
