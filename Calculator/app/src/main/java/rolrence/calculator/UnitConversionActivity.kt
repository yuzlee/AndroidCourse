package rolrence.calculator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_unit_conversion.*
import rolrence.calculator.converter.MeasureUnit

class UnitConversionActivity : AppCompatActivity() {

    private val h = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.peekData()
            val converter = bundle.getStringArray("converter")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)

            listExchangeRate.adapter = ArrayAdapter<String>(
                    this@UnitConversionActivity,
                    android.R.layout.simple_list_item_1, converter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_conversion)

        btnConvert.setOnClickListener {
            Thread {
                val input = txtInput.text.toString()
                val inputType = spinnerInputType.selectedItem.toString()

                if (input != "") {
                    val msg = Message()
                    val bundle = Bundle()
                    bundle.putStringArray("converter", MeasureUnit.list(input, inputType).map {
                        "$input $inputType = ${it.value} ${it.key}"
                    }.toTypedArray())
                    msg.data = bundle
                    h.sendMessage(msg)
                }
            }.start()
        }
    }
}
