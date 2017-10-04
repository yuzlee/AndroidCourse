package rolrence.calculator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_unit_conversion.*
import rolrence.calculator.converter.MeasureUnit

class UnitConversionActivity : AppCompatActivity() {

    private val h = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.peekData()
            val converter = bundle.getStringArray("converter")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)

            listExchangeRate.adapter = ArrayAdapter<String>(
                    this@UnitConversionActivity,
                    android.R.layout.simple_list_item_1, converter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_conversion)

        spinnerInputType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerInputUnit.adapter = ArrayAdapter<String>(
                        this@UnitConversionActivity,
                        android.R.layout.simple_spinner_item,
                        when (spinnerInputType.selectedItem.toString()) {
                            "长度" -> resources.getStringArray(R.array.length_unit_list)
                            "面积" -> resources.getStringArray(R.array.area_unit_list)
                            "重量" -> resources.getStringArray(R.array.weight_unit_list)
                            else -> resources.getStringArray(R.array.length_unit_list)
                        })
            }
        }

        btnConvert.setOnClickListener {
            Thread {
                val input = txtInput.text.toString()
                val inputType = spinnerInputType.selectedItem.toString()
                val inputUnit = spinnerInputUnit.selectedItem.toString()

                if (input != "") {
                    val msg = Message()
                    val bundle = Bundle()
                    bundle.putStringArray("converter", MeasureUnit.list(inputType, input, inputUnit).map {
                        "$input $inputUnit = ${it.value} ${it.key}"
                    }.toTypedArray())
                    msg.data = bundle
                    h.sendMessage(msg)
                }
            }.start()
        }
    }
}
