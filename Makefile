PWD := $(shell pwd)
AAP_JUCE_DIR := $(PWD)/external/aap-juce

APP_NAME := USF2Plugin
APP_BUILD_DIR := $(PWD)
APP_SRC_DIR := $(PWD)/external/usf2plugin

# Upstream populates and patches JUCE through CPM during CMake configuration.
BUILD_USES_FETCHCONTENT := 1

PATCH_FILE := $(PWD)/aap-juce-support.patch
PATCH_DEPTH := 1

include $(AAP_JUCE_DIR)/Makefile.cmake-common
