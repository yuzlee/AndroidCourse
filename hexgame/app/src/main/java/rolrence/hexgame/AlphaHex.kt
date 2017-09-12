package rolrence.hexgame

/**
 * Created by Rolrence on 9/12/2017.
 *
 */
class AlphaHex {
    companion object {
        init {
            System.loadLibrary("alphahex-lib")
        }

        val commands = listOf<String>(
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

    fun name() = "Alpha Hex"

    fun version() = "1.0.0"

    fun protocol_version() = "1.0.0"

    fun known_command(cmd: String) = cmd in commands

    fun list_commands() = commands.joinToString(separator = ", ")

    fun quit() {

    }

    fun board_size() {

    }

    fun size() {

    }

    fun clear_board() {}

    fun play() {}

    fun undo() {

    }

    fun gen_move() {

    }

    fun show_board() {

    }

    fun print() {}

    fun set_time() {

    }

    fun winner() {

    }

    fun agent() = "${name()}-${version()} GTP${protocol_version()}"
}