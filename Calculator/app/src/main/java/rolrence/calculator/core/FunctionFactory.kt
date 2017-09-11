package rolrence.calculator.core

import rolrence.calculator.core.exceptions.ParsingException
import rolrence.calculator.core.nodes.*

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class FunctionFactory {
    companion object {
        val _function = mapOf(
                Pair("sin", sin::class),
                Pair("cos", cos::class),
                Pair("tan", tan::class),
                Pair("asin", asin::class),
                Pair("acos", acos::class),
                Pair("atan", atan::class),
                Pair("sqrt", sqrt::class),
                Pair("exp", exp::class),
                Pair("ln", ln::class),
                Pair("log", log::class)
        )

        fun createFunction(name: String): IFunction? {
            val name = name.toLowerCase()
            if (_function.containsKey(name)) {
                return _function.get(name)?.objectInstance
            }
            throw ParsingException("unknown function: $name")
        }
    }
}