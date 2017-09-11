package rolrence.calculator.core.nodes

import rolrence.calculator.core.Number

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Constant {
    companion object {
        val _constants = mapOf(
                "pi" to Number(Math.PI),
                "e" to Number(Math.E)
        )

        fun get(name: String) = _constants.get(name)!!
    }
}