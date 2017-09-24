//
// Created by Rolrence on 9/17/2017.
//

#include "rolrence_hexgame_hex_AlphaHexNative.h"
#include <android/log.h>

#include "player/asyncplayer.h"
#include "player/aiplayer.h"
#include "hex/hexmatch.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1carrier
        (JNIEnv *env, jclass clazz) {
    Carrier::init();
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    async_player
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_async_1play
        (JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y) {
    auto async_player = (AsyncPlayer *) ptr;
    if (async_player) {
        async_player->play(x, y);
    }
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_ai_player
 * Signature: (IZ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1ai_1player
        (JNIEnv *env, jclass clazz, jint level, jboolean allowResign) {
    auto ai_player = new AiPlayer((AiPlayer::Level) level, allowResign);
    return (long) (new Poi<HexPlayer>(ai_player));
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_async_player
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1async_1player
        (JNIEnv *env, jclass clazz) {
    return (long) (new Poi<HexPlayer>((new AsyncPlayer())));
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_board
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1board
        (JNIEnv *env, jclass clazz, jint xs, jint ys) {
    return (long) (new HexBoard(xs, ys));
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    get_winner
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_get_1winner
        (JNIEnv *env, jclass clazz, jlong game_ptr) {
    auto game = (HexGame *) game_ptr;
    if (game) {
        return game->winner();
    } else {
        return -1;
    }
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_game
 * Signature: (JIZ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1game
        (JNIEnv *env, jclass clazz, jlong board_ptr, jint next, jboolean swappable) {
    auto board = (HexBoard *) board_ptr;
    if (board) {
        return (long) (new HexGame(*board, (HexMark) next, swappable));
    } else {
        return -1;
    }
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    init_match
 * Signature: (JJJ)J
 */
JNIEXPORT jlong JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_init_1match
        (JNIEnv *env, jclass clazz, jlong game_ptr, jlong vert_ptr, jlong hori_ptr) {
    return (long) (new HexMatch(
            *(HexGame *) game_ptr, *(Poi<HexPlayer> *) vert_ptr, *(Poi<HexPlayer> *) hori_ptr));
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    status
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_status
        (JNIEnv *env, jclass clazz, jlong match_ptr) {
    auto match = (HexMatch *) match_ptr;
    if (match) {
        return match->status();
    } else {
        return -1;
    }
}

/*
 * Class:     rolrence_hexgame_hex_AlphaHexNative
 * Method:    do_some
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_do_1some
        (JNIEnv *env, jclass clazz, jlong match_ptr) {
    auto match = ((HexMatch *) match_ptr);
    int x, y;
    if (match) {
        auto f = match->doSome().field();
        match->game().board().field2Coords(f, &x, &y);
        return ((x << 4) & 0xff00) & (y & 0x00ff);
    } else {
        return -1;
    }
}

JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_delete
        (JNIEnv *env, jclass clazz, jlong ptr) {
    delete ((void *) ptr);
}

JNIEXPORT void JNICALL Java_rolrence_hexgame_hex_AlphaHexNative_undo
        (JNIEnv *env, jclass clazz, jlong ptr) {
    auto game = (HexGame *) ptr;
    if (game && game->canBack()) {
        game->back();
    }
}

#ifdef __cplusplus
}
#endif


