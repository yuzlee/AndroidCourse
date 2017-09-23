package rolrence.hexgame.hex;

/**
 * Created by Rolrence on 9/17/2017.
 *
 */

public class AlphaHexNative {
    static {
        try {
            System.loadLibrary("AlphaHexNative");

            init_carrier();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static native void init_carrier();

    public static native void async_play(long ptr, int x, int y);

    public static native long init_ai_player(int level, boolean allowResign);

    public static native long init_async_player();

    public static native long init_board(int xs, int ys);

    public static native int get_winner(long game_ptr);

    public static native long init_game(long board_ptr, int next, boolean swappable);

    public static native long init_match(long game_ptr, long vert_ptr, long hori_ptr);

    public static native int status(long match_ptr);

    public static native int do_some(long match_ptr);

    public static native void delete(long ptr);

    public static native void undo(long game_ptr);
}
