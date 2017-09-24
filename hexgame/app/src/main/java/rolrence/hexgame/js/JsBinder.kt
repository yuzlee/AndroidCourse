package rolrence.hexgame.js

import android.app.Activity
import android.util.Log
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

    fun execute(js: String, vararg args: Any): Boolean {
        try {
            val jsArgs = argParser(args).joinToString(separator = ", ")
            val jsExpr = "javascript:$js($jsArgs)"

            Log.i("JsBinder", "[js function call] $jsExpr")

            view.post({
                view.evaluateJavascript(jsExpr, functions.getOrDefault(js, {}))
            })
            return true
        } catch (e: Exception) {
            show(e.message!!)
            return false
        }
    }

    private fun argParser(args: Array<out Any>) = args.map {
        when {
            it is String -> "\"$it\""
            else -> it
        }
    }
}