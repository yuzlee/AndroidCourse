package rolrence.hexgame

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.WebSettings
import android.content.pm.ActivityInfo
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import android.webkit.JsPromptResult
import android.webkit.WebView
import android.webkit.JsResult
import android.webkit.WebChromeClient
import rolrence.hexgame.R.id.webView
import rolrence.hexgame.js.JsBinder


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsBinder(this, webView)

        webView.getSettings().setJavaScriptEnabled(true)

        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setTitle("Alert")
                b.setMessage(message)
                b.setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which -> result.confirm() })
                b.setCancelable(false)
                b.create().show()
                return true
            }

            //设置响应js 的Confirm()函数
            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setTitle("Confirm")
                b.setMessage(message)
                b.setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which -> result.confirm() })
                b.setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which -> result.cancel() })
                b.create().show()
                return true
            }
        })

        webView.loadUrl("file:///android_asset/hex_main.svg")
    }
}
