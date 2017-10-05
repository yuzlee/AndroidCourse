package rolrence.hexgame.js


import android.webkit.JavascriptInterface
import rolrence.hexgame.hex.AlphaHexInterface
import rolrence.hexgame.hex.HexMark
import rolrence.hexgame.hex.LevelT

/**
 * Created by Rolrence on 9/12/2017.
 *
 */
class KotlinMethod constructor(val binder: JsBinder) {
    val alphaHex = AlphaHexInterface()

    fun init(size: Int = 7,
             first: HexMark = HexMark.HEX_MARK_VERT,
             ai: HexMark = HexMark.HEX_MARK_HORI,
             aiLevel: LevelT = LevelT.BEGINNER) {
        alphaHex.init(size, first, ai, aiLevel)

        if (first == ai) {
            binder.execute("setOptions", "V", "V")
        } else {
            binder.execute("setOptions", "V", "H")
        }

        binder.show("env has been initialized")
    }


    @JavascriptInterface
    fun name() = alphaHex.name()

    @JavascriptInterface
    fun version() = alphaHex.version()

    @JavascriptInterface
    fun protocol_version() = alphaHex.protocol_version()

    @JavascriptInterface
    fun known_command(cmd: String) = alphaHex.known_command(cmd)

    @JavascriptInterface
    fun list_command() = alphaHex.list_commands()

    @JavascriptInterface
    fun quit() {
        alphaHex.quit({
            binder.execute("quit_ok", it)
        })
    }

    @JavascriptInterface
    fun board_size(xs: Int, ys: Int) = alphaHex.size(xs, ys)

    @JavascriptInterface
    fun size() = alphaHex.size().toString()

    @JavascriptInterface
    fun play(x: Int, y: Int) {
        try {
            alphaHex.play(x, y, {
                binder.execute("play_ok", it)
            })
        } catch (e: Exception) {
            binder.show(e.message!!)
        }
    }

    @JavascriptInterface
    fun undo() = alphaHex.undo()

    @JavascriptInterface
    fun gen_move() {
        alphaHex.gen_move {
            binder.execute("genmove_ok", it)
        }
    }

    @JavascriptInterface
    fun show_board() = alphaHex.show_board()

    @JavascriptInterface
    fun print() = alphaHex.show_board()

    @JavascriptInterface
    fun set_time() = alphaHex.set_time()

    @JavascriptInterface
    fun winner() = alphaHex.winner()

    @JavascriptInterface
    fun hexgui_analyze_commands() = ""

    @JavascriptInterface
    fun agent() = alphaHex.agent()
}