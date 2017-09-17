cmake_minimum_required(VERSION 3.4.1)


# set(CMAKE_CXX_FLAGS -isystem D:\\Workspace\\Resource\\AndroidSDK\\ndk-bundle\\sysroot/usr/include/arm-linux-androideabi)

set(SOURCE_FILES
        src/main/cpp/ai/batch.cpp
        src/main/cpp/ai/batch.h
        src/main/cpp/ai/carrier.cpp
        src/main/cpp/ai/carrier.h
        src/main/cpp/ai/circuit.cpp
        src/main/cpp/ai/circuit.h
        src/main/cpp/ai/connector.cpp
        src/main/cpp/ai/connector.h
        src/main/cpp/ai/group.cpp
        src/main/cpp/ai/group.h
        src/main/cpp/config.h
        src/main/cpp/hex/hexboard.cpp
        src/main/cpp/hex/hexboard.h
        src/main/cpp/hex/hexfield.h
        src/main/cpp/hex/hexgame.cpp
        src/main/cpp/hex/hexgame.h
        src/main/cpp/hex/hexgamestate.cpp
        src/main/cpp/hex/hexgamestate.h
        src/main/cpp/hex/hexmark.cpp
        src/main/cpp/hex/hexmark.h
        src/main/cpp/hex/hexmatch.cpp
        src/main/cpp/hex/hexmatch.h
        src/main/cpp/hex/hexmove.cpp
        src/main/cpp/hex/hexmove.h
        src/main/cpp/hex/hexplayer.h
        src/main/cpp/util/lssolve.cpp
        src/main/cpp/util/lssolve.h
        src/main/cpp/util/mat.h
        src/main/cpp/util/misc.cpp
        src/main/cpp/util/misc.h
        src/main/cpp/util/poi.h
        src/main/cpp/util/slicedtask.h
        src/main/cpp/util/vec.h
        src/main/cpp/player/aiplayer.cpp
        src/main/cpp/player/aiplayer.h
        src/main/cpp/player/cmdplayer.cpp
        src/main/cpp/player/cmdplayer.h
        src/main/cpp/player/asyncplayer.cpp
        src/main/cpp/player/asyncplayer.h
        src/main/cpp/hex/hexgamewrapper.h )


set(JNI_SOURCES
        src/main/cpp/rolrence_hexgame_hex_AlphaHexNative.h
        src/main/cpp/AlphaHexNative.cpp
        )


add_library(AlphaHexNative SHARED ${SOURCE_FILES} ${JNI_SOURCES})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       AlphaHexNative

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )