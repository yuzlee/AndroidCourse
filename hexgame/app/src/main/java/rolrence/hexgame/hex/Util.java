package rolrence.hexgame.hex;

/**
 * Created by Rolrence on 9/24/2017.
 *
 */

public class Util {
    static byte shl(byte x, int bits) {
        return (byte)(x << bits);
    }

    static byte shr(byte x, int bits) {
        return (byte)(x >> bits);
    }
}
