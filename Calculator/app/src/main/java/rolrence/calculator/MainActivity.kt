package rolrence.calculator

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.BoringLayout
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import rolrence.calculator.core.Expression


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemUnitConvert -> {
                val intent = Intent()
                intent.setClass(this, UnitConversionActivity::class.java)
                startActivity(intent)
            }
            R.id.itemExchangeRateConvert -> {
                val intent = Intent()
                intent.setClass(this, ExchangeRateActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
