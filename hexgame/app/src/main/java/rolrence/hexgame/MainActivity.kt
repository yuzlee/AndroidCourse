package rolrence.hexgame

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_dialog.view.*
import rolrence.hexgame.hex.HexMark
import rolrence.hexgame.hex.LevelT
import rolrence.hexgame.js.JsBinder


class MainActivity : AppCompatActivity() {
    var binder: JsBinder? = null
    var dialog: AlertDialog? = null

    var ai = HexMark.HEX_MARK_HORI
    var level = LevelT.BEGINNER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binder = JsBinder(this, webView)

        webView.getSettings().setJavaScriptEnabled(true)
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setTitle("")
                b.setMessage(message)
                b.setPositiveButton("ok", { dialog, which -> result.confirm() })
                b.setCancelable(false)
                b.create().show()
                return true
            }

            //设置响应js 的Confirm()函数
            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setTitle("")
                b.setMessage(message)
                b.setPositiveButton("ok", { dialog, which -> result.confirm() })
                b.setNegativeButton("cancel", { dialog, which -> result.cancel() })
                b.create().show()
                return true
            }
        })
        this.dialog = makeDialog()
        initGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.opt_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemSettings -> this.dialog?.show()
            R.id.itemClearBoard -> {
                initGame()
            }
            R.id.itemAbout -> {
                AlertDialog.Builder(this)
                        .setTitle("关于")
                        .setMessage("Hex Game v1.0")
                        .create()
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initGame() {
        webView.loadUrl("file:///android_asset/hex_main.svg")
        binder?.initGameOption(ai = ai, aiLevel = level)
    }

    fun makeDialog(): AlertDialog {
        val factory = LayoutInflater.from(this)
        val dialogView = factory.inflate(R.layout.activity_dialog, null)
        val dlg = AlertDialog.Builder(this)
                .setTitle("游戏设置")
                .setView(dialogView)
                .setPositiveButton("Yes", { dialog, which ->
                    ai = when (dialogView.spinnerWhoFirst.selectedItem) {
                        "用户" -> HexMark.HEX_MARK_HORI
                        "AI" -> HexMark.HEX_MARK_VERT
                        else -> HexMark.HEX_MARK_EMPTY
                    }
                    level = when (dialogView.spinnerAiLevel.selectedItem) {
                        "初级" -> LevelT.BEGINNER
                        "中级" -> LevelT.INTERMEDIATE
                        "高级" -> LevelT.ADVANCED
                        "专家" -> LevelT.EXPERT
                        else -> LevelT.BEGINNER
                    }
                    initGame()
                })
                .setNegativeButton("Cancel", { dialog, which ->
                    Toast.makeText(this, "do nothing", Toast.LENGTH_SHORT).show()
                })
                .create()
        return dlg
    }
}
