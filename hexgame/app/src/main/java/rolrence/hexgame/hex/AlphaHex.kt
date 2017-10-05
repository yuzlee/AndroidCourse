package rolrence.hexgame.hex

import android.util.Log
import kotlin.experimental.and

/**
 * Created by Rolrence on 9/12/2017.
 *
 */

typealias Pointer = Long

enum class HexMark { HEX_MARK_EMPTY, HEX_MARK_VERT, HEX_MARK_HORI }

enum class LevelT { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

class Player {
    class PlayerInfo(val name: String, val level: Int, val allowResign: Boolean, val ptr: Pointer) {
        enum class PLAYER_TYPE { AI, ASYNC, UNKNOWN }

        var type: PLAYER_TYPE

        init {
            type = when {
                level == -1 -> PLAYER_TYPE.ASYNC
                level > -1 -> PLAYER_TYPE.AI
                else -> PLAYER_TYPE.UNKNOWN
            }
        }

        fun delete() = AlphaHexNative.delete(ptr);

        override fun toString(): String {
            var info = "[rolrence.hexgame.hex.Player Info]\n"
            info += "  name: $name\n"
            if (level > -1) {
                info += " level: $level\n"
                info += "resign: $allowResign\n"
            }
            return info
        }
    }

    companion object {
        private val playerPool = mutableMapOf<String, PlayerInfo>()

        fun reg(name: String, level: Int = -1, allowResign: Boolean = false) {
            when (level) {
                -1 -> playerPool.put(name,
                        PlayerInfo(name, level, allowResign, init_async_player()))
                else -> playerPool.put(name,
                        PlayerInfo(name, level, allowResign, init_ai_player(level, allowResign)))
            }
        }

        fun ptr(name: String): Pointer {
            try {
                return playerPool[name]!!.ptr
            } catch (e: Exception) {
                return 0
            }
        }

        fun delete() {
            for (p in playerPool) {
                p.value.delete()
            }
        }

        fun play(name: String, x: Int, y: Int) {
            if (playerPool.containsKey(name)) {
                try {
                    val p = playerPool.get(name)!!
                    if (p.type == PlayerInfo.PLAYER_TYPE.ASYNC) {
                        async_play(p.ptr, x, y)
                    }
                } catch (e: Exception) {
                    throw Exception("[Error] play for async_$name ($x, $y)")
                }
            }
        }

        fun async_play(ptr: Pointer, x: Int, y: Int) = AlphaHexNative.async_play(ptr, x, y)

        /**
         *  @return Poi of the player
         */
        fun init_ai_player(level: Int, allowResign: Boolean = false) = AlphaHexNative.init_ai_player(level, allowResign)

        fun init_async_player(): Pointer = AlphaHexNative.init_async_player()
    }
}

class HexBoard {
    companion object {
        var board: Pointer? = null
        var xs: Int = 0
        var ys: Int = 0

        fun init(xs: Int = 11, ys: Int = 11) {
            Companion.xs = xs
            Companion.ys = ys
            board = init_board(xs, ys)
        }

        fun ptr(): Pointer {
            if (board == null) {
                throw Exception("[ERROR] init hex board ${xs}X${ys}")
            }
            return board!!
        }

        fun init_board(xs: Int = 11, ys: Int = 11) = AlphaHexNative.init_board(xs, ys)

        fun delete() = AlphaHexNative.delete(ptr())
    }
}

class HexGame {
    companion object {
        var game: Pointer? = null
        var xs: Int = 0
        var ys: Int = 0

        fun init(size: Int = 11, next: HexMark, swappable: Boolean = false) {
            HexBoard.init(size, size)
            game = init_game(HexBoard.ptr(), next, swappable)
        }

        fun winner(): HexMark = get_winner(ptr())

        fun ptr(): Pointer {
            if (game == null) {
                throw Exception("[ERROR] init hex game ${xs}X${ys}")
            }
            return game!!
        }

        fun delete() = AlphaHexNative.delete(ptr())

        fun get_winner(ptr: Pointer): HexMark = HexMark.values()[AlphaHexNative.get_winner(ptr)]

        fun init_game(board_ptr: Pointer,
                      next: HexMark = HexMark.HEX_MARK_VERT,
                      swappable: Boolean = false) = AlphaHexNative.init_game(board_ptr, next.ordinal, swappable)

        fun undo() = AlphaHexNative.undo(ptr())
    }
}

class HexMove {
    var x: Int = 0
    var y: Int = 0
    var move: Int = 0

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
        this.move = tuple_to_move(this.x, this.y)
    }

    constructor(move: Int) {
        assert(move != -1)
        val tuple = move_to_tuple(move)
        this.move = move
        this.x = tuple[0]
        this.y = tuple[1]
    }

    override fun toString() = "$x,$y"

    companion object {
        val lval: Int = 0xffff0000.toInt()
        val rval: Int = 0x0000ffff

        fun move_to_tuple(move: Int): Array<Int> {
            val x = (move shr 16) and rval
            val y = move and rval
            return arrayOf(x, y)
        }

        fun tuple_to_move(x: Int, y: Int) = (((x shl 16) and lval) or (y and rval))
    }
}

class HexMatch {
    enum class HexMatchStatus { MATCH_ON, MATCH_OFF, MATCH_FINISHED }

    companion object {
        var match: Pointer? = null
        var xs: Int = 0
        var ys: Int = 0

        fun init(game_ptr: Pointer, vert_ptr: Pointer, hori_palyer: Pointer) {
            match = init_match(game_ptr, vert_ptr, hori_palyer)
        }

        fun delete() = AlphaHexNative.delete(ptr())

        fun ptr(): Pointer {
            if (match == null) {
                throw Exception("[ERROR] match is null")
            }
            return match!!
        }

        fun init_match(game_ptr: Pointer,
                       vert_ptr: Pointer,
                       hori_ptr: Pointer): Pointer = AlphaHexNative.init_match(game_ptr, vert_ptr, hori_ptr)

        fun status(): Int = AlphaHexNative.status(ptr())

        fun finished() = status() == HexMatchStatus.MATCH_FINISHED.ordinal

        /**
         * @return compressed (x,y)
         */
        fun do_some() = AlphaHexNative.do_some(ptr())
    }
}


class AlphaHexInterface {
    companion object {
        val commands = listOf(
                "name",
                "version",
                "protocol_version",
                "known_commands",
                "list_commands",
                "quit",
                "board_size",
                "size",
                "clear_board",
                "play",
                "undo",
                "gen_move",
                "show_board",
                "print",
                "set_time",
                "winner",
                "agent"
        )
    }

    var xs: Int = 0
    var ys: Int = 0

    fun name() = "Alpha Hex"

    fun version() = "1.0.0"

    fun protocol_version() = "1.0.0"

    fun known_command(cmd: String) = cmd in commands

    fun list_commands() = commands.joinToString(separator = ", ")

    fun quit(callback: (String) -> Unit) {
        try {
            HexMatch.delete()
            HexGame.delete()
            HexBoard.delete()
            Player.delete()
            callback("[OK]")
        } catch (e: Exception) {
            callback("[ERROR] ${e.message}")
        }
    }

    fun size(xs: Int, ys: Int) {
        this.xs = xs
        this.ys = ys
    }

    fun size() = listOf(xs, ys)

    fun ai() = "ai"
    fun human() = "human"

    fun init(size: Int = 7,
             first: HexMark = HexMark.HEX_MARK_VERT,
             ai: HexMark = HexMark.HEX_MARK_HORI,
             aiLevel: LevelT = LevelT.BEGINNER) {
        HexGame.init(size, first, swappable = false)

        Player.reg(ai(), level = aiLevel.ordinal)
        Player.reg(human())

        if (ai == HexMark.HEX_MARK_VERT) {
            HexMatch.init(HexGame.ptr(), Player.ptr(ai()), Player.ptr(human()))
        } else if (ai == HexMark.HEX_MARK_HORI) {
            HexMatch.init(HexGame.ptr(), Player.ptr(human()), Player.ptr(ai()))
        } else {
            throw Exception("invalid player setting")
        }
    }

    fun winner() = when (HexGame.winner()) {
        HexMark.HEX_MARK_HORI -> "H"
        HexMark.HEX_MARK_VERT -> "V"
        else -> "N"
    }

    fun callbackParams(move: Int) = "${HexMove(move)},${winner()},${if (HexMatch.finished()) 1 else 0}"

    fun play(x: Int, y: Int, callback: (String) -> Unit) {
        try {
            if (!HexMatch.finished()) {
                Player.play(human(), x, y)
                val move = HexMatch.do_some()
                callback(callbackParams(move))
            } else {
                callback("[INFO] match has been finished.")
            }
        } catch (e: Exception) {
            callback("[ERROR] ${e.message}")
        }
    }

    fun gen_move(callback: (String) -> Unit) {
        if (!HexMatch.finished()) {
            val move = HexMatch.do_some()
            callback(callbackParams(move))
        } else {
            callback("[INFO] match has been finished.")
        }
    }

    fun undo() = HexGame.undo()

    fun show_board() = ""

    fun print() = ""

    fun set_time() = ""

    fun agent() = "${name()}-${version()} GTP${protocol_version()}"
}

