package demo.intent_startup_test

import android.annotation.SuppressLint
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import android.webkit.WebSettings



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        var webView = (findViewById(R.id.webView) as WebView)
        webView.getSettings().setJavaScriptEnabled(true)

        val url = intent.data.toString()
        try {
            webView.loadUrl(url)
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}
