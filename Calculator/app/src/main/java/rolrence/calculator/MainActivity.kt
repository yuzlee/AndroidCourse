package rolrence.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import rolrence.calculator.core.Expression
import rolrence.calculator.core.Parser
import rolrence.calculator.core.exceptions.ParsingException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCalc.setOnClickListener({
            val exp = txtExp.text.toString().trim()
            try {
                var str = "result = ${Expression.eval(exp)}\n"
                txtResult.setText(str)
            } catch (e: ParsingException) {
                txtResult.setText(e.message)
            }
        })
    }
}
