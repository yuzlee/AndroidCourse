package rolrence.calculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import rolrence.calculator.core.Expression


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCalc.setOnClickListener({
            val exp = txtExp.text.toString().trim()
            try {
                var str = "result = ${Expression.tryParse(exp)}\n"
                txtResult.setText(str)
            } catch (e: Exception) {
                txtResult.setText(e.message)
            }
        })
    }
}
