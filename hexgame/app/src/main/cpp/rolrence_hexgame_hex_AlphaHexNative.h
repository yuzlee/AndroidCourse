/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>

/* Header for class rolrence_hexgame_hex_AlphaHexNative */

#ifndef _Included_rolrence_hexgame_hex_AlphaHexNative
#define _Included_rolrence_hexgame_hex_AlphaHexNative
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    async_player
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_async_1play
        (JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_ai_player
 * Signature: (IZ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1ai_1player
        (JNIEnv *env, jclass clazz, jint level, jboolean allowResign);
/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_async_player
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1async_1player
        (JNIEnv *env, jclass clazz);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_board
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1board
        (JNIEnv *env, jclass clazz, jint xs, jint ys);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    get_winner
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_get_1winner
        (JNIEnv *env, jclass clazz, jlong game_ptr);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_game
 * Signature: (JIZ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1game
        (JNIEnv *env, jclass clazz, jlong board_ptr, jint next, jboolean swappable);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_match
 * Signature: (JJJ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1match
        (JNIEnv *env, jclass clazz, jlong game_ptr, jlong vert_ptr, jlong hori_ptr);

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    status
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_status
        (JNIEnv *env, jclass clazz, jlong match_ptr);
/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    do_some
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_do_1some
        (JNIEnv *env, jclass clazz, jlong match_ptr);


JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_delete
        (JNIEnv *env, jclass clazz, jlong ptr);

JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_undo
        (JNIEnv *env, jclass clazz, jlong ptr);

JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1carrier
        (JNIEnv *env, jclass clazz);

#ifdef __cplusplus
}
#endif
#endif
