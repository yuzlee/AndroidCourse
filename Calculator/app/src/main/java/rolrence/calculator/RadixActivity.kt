package rolrence.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_radix.*

class RadixActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radix)

        txtOutput.setOnClickListener {
            val input = txtInput.text.toString()
            val inputType = spinnerInputType.selectedItem.toString()
            val outputType = spinnerOutputType.selectedItem.toString()
            txtOutput.text = convert(input, inputType, outputType)
        }
    }

    fun convert(value: String, inType: String, outType: String): String {
        val inT = radixType(inType)
        val outT = radixType(outType)
        return Integer.valueOf(value, inT).toString(outT)
    }

    fun radixType(type: String) = when (type) {
        "二进制" -> 2
        "八进制" -> 8
        "十进制" -> 10
        "十六进制" -> 16
        else -> 10
    }
}
