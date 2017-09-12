package rolrence.hexgame

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.WebSettings
import android.content.pm.ActivityInfo



class MainActivity : Activity() {
    val webSettings = webView.getSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webSettings.setLoadWithOverviewMode(true)
        webSettings.setUseWideViewPort(true)
        webSettings.setJavaScriptEnabled(true)

        webView.loadUrl("file:///android_asset/hex_main.svg")
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
