package rolrence.hexgame.js


import android.webkit.JavascriptInterface
import rolrence.hexgame.hex.AlphaHexInterface

/**
 * Created by Rolrence on 9/12/2017.
 *
 */
class KotlinMethod constructor(val binder: JsBinder) {
    val alphaHex = AlphaHexInterface()

    init {
        alphaHex.init()
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
                binder.show(it)
                binder.execute("play_ok", it)
                gen_move()
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
            binder.show("gen: $it")
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