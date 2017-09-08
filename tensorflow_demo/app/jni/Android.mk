LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

MY_CPP_LIST := $(wildcard $(LOCAL_PATH)/**/*.cc)
 
LOCAL_MODULE    := tensorflow_demo
LOCAL_SRC_FILES := $(MY_CPP_LIST:$(LOCAL_PATH)/%=%)

include $(BUILD_SHARED_LIBRARY)

LOCAL_CFLAGS := -DSTANDALONE_DEMO_LIB -DPLATFORM_POSIX_ANDROID -std=c++11 \
				-fno-exceptions -fno-rtti -O2 -Wno-narrowing -fPIE