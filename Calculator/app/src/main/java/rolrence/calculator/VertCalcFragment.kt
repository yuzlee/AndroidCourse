package rolrence.calculator

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_vert_calc.*
import kotlinx.android.synthetic.main.fragment_vert_calc.view.*
import rolrence.calculator.core.Expression
import rolrence.calculator.core.Number
import android.view.WindowManager
import android.R.attr.orientation
import android.content.res.Configuration


class VertCalcFragment : Fragment() {
    val expr = mutableListOf<String>()

    val records = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_vert_calc, container, false)

        // hide the soft input
        view.txtInput.setOnTouchListener { v, e ->
            view.txtInput.setInputType(InputType.TYPE_NULL)
            return@setOnTouchListener false
        }

        view.txtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (TextUtils.isEmpty(s)) {
//                    txtView.visibility = View.VISIBLE
//                } else {
//                    txtView.visibility = View.GONE
//                }
            }

            override fun afterTextChanged(s: Editable?) {
                txtInput.setSelection(txtInput.text.length)
            }
        })

        view.btnClear.setOnClickListener {
            expr.clear()
            txtInput.setText("")
            set("")
        }
        view.btnDel.setOnClickListener {
            if (expr.isNotEmpty()) {
                expr.removeAt(expr.size - 1)
            }
            setInput(expr.joinToString(separator = ""))
            set("")
        }

        view.btnOne.setOnClickListener { appendAndCalc("1") }
        view.btnTwo.setOnClickListener { appendAndCalc("2") }
        view.btnThree.setOnClickListener { appendAndCalc("3") }
        view.btnFour.setOnClickListener { appendAndCalc("4") }
        view.btnFive.setOnClickListener { appendAndCalc("5") }
        view.btnSix.setOnClickListener { appendAndCalc("6") }
        view.btnSeven.setOnClickListener { appendAndCalc("7") }
        view.btnEight.setOnClickListener { appendAndCalc("8") }
        view.btnNine.setOnClickListener { appendAndCalc("9") }
        view.btnZero.setOnClickListener { appendAndCalc("0") }
        view.btnPoint.setOnClickListener { appendAndCalc(".") }

        view.btnEquals.setOnClickListener {
            val result = calc()
            if (result != "NaN") {
                expr.clear()
                expr.add(result)
                setInput(result)
                set("")
            }
        }

        view.btnMC.setOnClickListener { records.clear() }
        view.btnMPlus.setOnClickListener { records.add(calc(view.txtInput.text.toString())) }
        view.btnMMinus.setOnClickListener { records.add("-${calc(view.txtInput.text.toString())}") }
        view.btnMR.setOnClickListener {
            val result = calc(records.joinToString(separator = "+"))
            set(result)
            setInput(result)
        }

        view.btnPlus.setOnClickListener { appendAndCalc("+") }
        view.btnMinus.setOnClickListener { appendAndCalc("-") }
        view.btnMul.setOnClickListener { appendAndCalc("×") }
        view.btnDiv.setOnClickListener { appendAndCalc("÷") }
        view.btnPercentage?.setOnClickListener { appendAndCalc("%") }

        view.btnLeftBracket?.setOnClickListener { appendAndCalc("(") }
        view.btnRightBracket?.setOnClickListener { appendAndCalc(")") }
        view.btnReciprocal?.setOnClickListener { appendAndCalc("**(-1)") }

        view.btnSquare?.setOnClickListener { appendAndCalc("**2") }
        view.btnCube?.setOnClickListener { appendAndCalc("**3") }
        view.btnPow?.setOnClickListener { appendAndCalc("**") }

        view.btnFactorial?.setOnClickListener { appendAndCalc("fac(") }
        view.btnSqrt?.setOnClickListener { appendAndCalc("**(1/2)") }
        view.btnRoot?.setOnClickListener { appendAndCalc("**(1/") }

        view.btnE?.setOnClickListener { appendAndCalc("e") }
        view.btnLn?.setOnClickListener { appendAndCalc("ln(") }
        view.btnLog10?.setOnClickListener { appendAndCalc("log(") }

        view.btnSin?.setOnClickListener { appendAndCalc("sin(") }
        view.btnCos?.setOnClickListener { appendAndCalc("cos(") }
        view.btnTan?.setOnClickListener { appendAndCalc("tan(") }

        view.btnASin?.setOnClickListener { appendAndCalc("asin(") }
        view.btnACos?.setOnClickListener { appendAndCalc("acos(") }
        view.btnATan?.setOnClickListener { appendAndCalc("atan(") }

        view.btnPi?.setOnClickListener { appendAndCalc("pi") }

        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val attr = activity.window.attributes
            attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            activity.window.attributes = attr
        }
    }

    fun calc(exp: String) = Expression.tryParse(exp).toString()
    fun calc() = calc(expr.joinToString(separator = ""))

    fun set(s: String) {
        txtResultView.setText(s)
    }

    fun setInput(s: String) {
        txtInput.setText(s)
    }

    fun append(s: String) = txtInput.append(s)

    fun appendAndCalc(s: String) {
        expr.add(s)
        append(s)
        val result = calc()
        if (result != "NaN") {
            set(result)
        } else {
            set("")
        }
    }
}
