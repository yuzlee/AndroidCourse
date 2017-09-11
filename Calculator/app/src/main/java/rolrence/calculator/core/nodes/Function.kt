package rolrence.calculator.core.nodes

import rolrence.calculator.core.IFunction
import rolrence.calculator.core.IValue
import rolrence.calculator.core.Number

/**
 * Created by Rolrence on 9/11/2017.
 *
 */

class sin: IFunction {
    override fun execute(arg: IValue) = Number(Math.sin(arg.value))
}

class cos: IFunction {
    override fun execute(arg: IValue) = Number(Math.cos(arg.value))
}

class tan: IFunction {
    override fun execute(arg: IValue) = Number(Math.tan(arg.value))
}

class asin: IFunction {
    override fun execute(arg: IValue) = Number(Math.asin(arg.value))
}

class acos: IFunction {
    override fun execute(arg: IValue) = Number(Math.acos(arg.value))
}

class atan: IFunction {
    override fun execute(arg: IValue) = Number(Math.atan(arg.value))
}

class sqrt: IFunction {
    override fun execute(arg: IValue) = Number(Math.sqrt(arg.value))
}

class exp: IFunction {
    override fun execute(arg: IValue) = Number(Math.exp(arg.value))
}

class ln: IFunction {
    override fun execute(arg: IValue) = Number(Math.log(arg.value))
}

class log: IFunction {
    override fun execute(arg: IValue) = Number(Math.log10(arg.value))
}