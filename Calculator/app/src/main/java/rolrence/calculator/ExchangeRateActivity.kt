package rolrence.calculator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_exchange_rate.*
import rolrence.calculator.converter.ExchangeRate
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


class ExchangeRateActivity : AppCompatActivity() {
    private val h = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.peekData()
            val converter = bundle.getSerializable("converter") as ExchangeRate

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)

            val input = txtInput.text.toString().toDoubleOrNull()
            val inputType = spinnerInputType.selectedItem.toString()
            val outputType = spinnerOutputType.selectedItem.toString()
            if (input != null) {
                txtOutput.text = (converter.convert(inputType, outputType)(input))
            }

            val list = converter.list(inputType, 1.0).map {
                "1 $inputType = ${it.value} ${it.key}"
            }
            listExchangeRate.adapter = ArrayAdapter<String>(
                    this@ExchangeRateActivity,
                    android.R.layout.simple_list_item_1, list)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rate)

        txtOutput.setOnClickListener {
            Thread {
                val msg = Message()
                val bundle = Bundle()
                bundle.putSerializable("converter", ExchangeRate())
                msg.data = bundle
                h.sendMessage(msg)
            }.start()
        }
    }
}
