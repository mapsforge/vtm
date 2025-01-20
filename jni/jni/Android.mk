LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
 
LOCAL_MODULE    := vtm-jni
LOCAL_C_INCLUDES := . libtess2/Include 
 
LOCAL_CFLAGS := $(LOCAL_C_INCLUDES:%=-I%) -O2 -Wall -D__ANDROID__ -Wall -O2 -ffast-math -DNDEBUG
LOCAL_CPPFLAGS := $(LOCAL_C_INCLUDES:%=-I%) -O2 -Wall -D__ANDROID__ -Wall -O2 -ffast-math -DNDEBUG
LOCAL_LDLIBS := -lm -llog -Wl,-z,max-page-size=0x4000
LOCAL_LDFLAGS += "-Wl,-z,max-page-size=16384"
LOCAL_ARM_MODE  := arm
APP_SUPPORT_FLEXIBLE_PAGE_SIZES := true

LOCAL_SRC_FILES := libtess2/Source/dict.c\
	libtess2/Source/tess.c\
	libtess2/Source/geom.c\
	libtess2/Source/bucketalloc.c\
	libtess2/Source/mesh.c\
	libtess2/Source/sweep.c\
	libtess2/Source/priorityq.c\
	gl/utils.c\
	org_oscim_utils_TessJNI.cpp

include $(BUILD_SHARED_LIBRARY)
