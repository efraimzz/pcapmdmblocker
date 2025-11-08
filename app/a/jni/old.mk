LOCAL_PATH := $(call my-dir)



include $(CLEAR_VARS)
LOCAL_MODULE    := capture
#CFLAGS     += -Ilog -Ithird_party/include -Indpi_api.h -Indpi_config.h -Indpi_define.h
LOCAL_LDLIBS    := -llog
# Define the root of the nDPI submodule relative to the Android.mk file or your project root
# Assuming 'submodules/nDPI' is the correct relative path.
NDPI_ROOT := $(LOCAL_PATH)/submodules/nDPI
#NDPI_ROOT := submodules/nDPI
NDPI_SRC := $(NDPI_ROOT)/src/lib
#NDPI_SRC := $(NDPI_ROOT)
NDPI_THIRD_PARTY_SRC := $(NDPI_ROOT)/src/lib/third_party/src
#NDPI_THIRD_PARTY_SRC := $(NDPI_ROOT)/src/lib/third_party/src

# --- Start of Module Definition ---
# Every Android.mk must start with 'clear_vars' and end with 'build_module'

#LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)

# 📦 Module Name
#LOCAL_MODULE := ndpi

# 📂 Include Directories (Corresponds to include_directories)
LOCAL_C_INCLUDES := \
    $(NDPI_ROOT)/src/include \
    $(NDPI_ROOT)/src/lib/third_party/include

# ⚙️ Compiler Flags / Definitions (Corresponds to add_definitions)
LOCAL_CFLAGS := \
    -DNDPI_LIB_COMPILATION \
    -D__bswap_64=bswap_64 \
    -DNDPI_SLIM

# Header files are often just listed in C_INCLUDES, but if explicitly required:

# ❌ Manual Source File Exclusion (Corresponds to list(REMOVE_ITEM ...))
# Note: For simplicity and reliability in Android.mk, it is often better to
# explicitly list the required files rather than using 'wildcard' and 'exclude'.
# The list below removes the files that were explicitly excluded in the CMake file.
LOCAL_EXCLUDE_FILES := \
    $(NDPI_THIRD_PARTY_SRC)/libinjection_html5.c \
    $(NDPI_THIRD_PARTY_SRC)/libinjection_xss.c \
    $(NDPI_THIRD_PARTY_SRC)/libinjection_sqli.c \
    $(NDPI_THIRD_PARTY_SRC)/roaring.c \
    $(NDPI_THIRD_PARTY_SRC)/roaring_v2.c \
    $(NDPI_SRC)/ndpi_bitmap.c \
    $(NDPI_SRC)/ndpi_bitmap64_fuse.c \
    $(NDPI_SRC)/ndpi_binary_bitmap.c \
    $(NDPI_SRC)/ndpi_filter.c


# 📝 Source Files (Corresponds to AUX_SOURCE_DIRECTORY and ADD_LIBRARY)
# Use 'wildcard' to automatically find source files, similar to AUX_SOURCE_DIRECTORY.
# Source files are explicitly listed below and the excluded files are NOT included.
LOCAL_SRC_FILES := \
  $(wildcard $(NDPI_ROOT)/src/lib/*.c) \
  submodules/nDPI/src/include/ndpi_api.h \
   $(wildcard $(NDPI_THIRD_PARTY_SRC)/*.c) \
   $(wildcard $(NDPI_THIRD_PARTY_SRC)/hll/*.c) \
   $(wildcard $(NDPI_ROOT)/src/lib/protocols/*.c) \
ndpi_config.c \
vpnrelay.c \
zdtun.c \
utils.c \
common/jni_utils.c \
common/uid_resolver.c \
common/utils.c \
ip_lru.c \
log_writer.c \
port_map.c \
third_party/libchash.c \
blacklist.c \

 # build ndpi only when change in ndpi...




    
# 🔗 Linkage Type (Corresponds to ADD_LIBRARY(ndpi SHARED ...))
#LOCAL_MODULE_FILENAME := libndpi
include $(BUILD_SHARED_LIBRARY)

# ⚠️ Note on 'configure_file':
# The CMake 'configure_file' command is a pre-build step that generates header files.
# In the NDK build, this generation would need to be handled by a separate script
# or a custom pre-build rule, as Android.mk does not have a direct equivalent for template processing.
# The generated files would then be placed in $(NDPI_ROOT)/src/include to be
# picked up by LOCAL_C_INCLUDES.
