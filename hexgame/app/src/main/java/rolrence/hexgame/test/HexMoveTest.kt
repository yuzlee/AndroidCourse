package rolrence.hexgame.test

import rolrence.hexgame.hex.HexMove

/**
 * Created by Rolrence on 9/24/2017.
 *
 */

fun main(args: Array<String>) {
    val move = HexMove(3, 4)
    println(move)
    val move_1 = HexMove(move.move)
    println(move_1)
}
