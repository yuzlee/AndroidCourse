package rolrence.calculator.core.nodes

import rolrence.calculator.core.Number

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Constant {
    companion object {
        val _constants = mapOf(
                Pair("pi", Number(Math.PI)),
                Pair("e", Number(Math.E))
        )

        fun get(name: String) = _constants.get(name)!!
    }
}