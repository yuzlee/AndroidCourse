package rolrence.hexgame.js

import android.app.Activity
import android.webkit.WebView
import android.widget.Toast
import kotlin.concurrent.thread

/**
 * Created by Rolrence on 9/12/2017.
 *
 */
class JsBinder constructor(val content: Activity, val view: WebView) {
    val functions = mutableMapOf<String, (String) -> Unit>()

    init {
        view.addJavascriptInterface(KotlinMethod(this), "kotlin")
    }

    fun show(str: String) = Toast.makeText(content, str, Toast.LENGTH_LONG).show()

    fun bind(js: String, callback: (String) -> Unit) {
        functions[js] = callback
    }

    fun execute(js: String, vararg args: String): Boolean {
        try {
            val jsArgs = args.joinToString(separator = ", ")
            view.evaluateJavascript("javascript:$js($jsArgs)", functions.getOrDefault(js, {}))
            return true
        } catch (e: Exception) {
            show(e.message!!)
            return false
        }
    }
}