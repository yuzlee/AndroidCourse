package rolrence.calculator

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

//        btnCalc.setOnClickListener({
//            val exp = txtExp.text.toString().trim()
//            try {
//                var str = "result = ${Expression.tryParse(exp)}\n"
//                txtResult.setText(str)
//            } catch (e: Exception) {
//                txtResult.setText(e.message)
//            }
//        })
    }
}
