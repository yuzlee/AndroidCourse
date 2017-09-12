package rolrence.hexgame.js

import android.webkit.WebView

/**
 * Created by Rolrence on 9/12/2017.
 *
 */
class JsBinder constructor(val view: WebView) {
    val functions = mutableMapOf<String, (String) -> Unit>()

    init {
        view.addJavascriptInterface(KotlinMethod(), "kotlin")
    }

    fun bind(js: String, callback: (String) -> Unit) {
        functions[js] = callback
    }

    fun execute(js: String, vararg args: String): Boolean {
        try {
            val jsArgs = args.joinToString(separator = ", ")
            view.evaluateJavascript("javascript:$js($jsArgs)", functions.get(js)!!)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}