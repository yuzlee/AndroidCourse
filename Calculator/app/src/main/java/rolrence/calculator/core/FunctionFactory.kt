package rolrence.calculator.core

import rolrence.calculator.core.exceptions.ParsingException
import rolrence.calculator.core.nodes.*

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Function constructor(val opt: (IValue) -> Number): IFunction {
    override fun execute(arg: IValue) = opt(arg)
}

class FunctionFactory {
    companion object {
        val _function = mapOf(
                "sin" to Function { Number(Math.sin(it.value)) },
                "cos" to Function { Number(Math.cos(it.value)) },
                "tan" to Function { Number(Math.tan(it.value)) },
                "asin" to Function { Number(Math.asin(it.value)) },
                "acos" to Function { Number(Math.acos(it.value)) },
                "atan" to Function { Number(Math.atan(it.value)) },
                "sqrt" to Function { Number(Math.sqrt(it.value)) },
                "exp" to Function { Number(Math.exp(it.value)) },
                "ln" to Function { Number(Math.log(it.value)) },
                "log" to Function { Number(Math.log10(it.value)) }
        )

        fun createFunction(name: String): IFunction {
            val name = name.toLowerCase()
            if (_function.containsKey(name)) {
                return _function.get(name)!!
            }
            throw ParsingException("unknown function: $name")
        }
    }
}