package rolrence.calculator.core.nodes

import rolrence.calculator.core.Number
import rolrence.calculator.core.exceptions.ValueError

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

        fun get(name: String): Number {
            try {
                return _constants.get(name)!!
            } catch (e: NullPointerException) {
                throw ValueError("unknown constant: $name")
            }
        }
    }
}