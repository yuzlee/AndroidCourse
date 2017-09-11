package rolrence.calculator.core.test

import rolrence.calculator.core.Expression
import rolrence.calculator.core.IValue
import rolrence.calculator.core.Parser

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class ExpressionTests {
    companion object {
        fun runTest() {
            test(Expressions.e1, Expressions.a1)
            test(Expressions.e2, Expressions.a2)
            test(Expressions.e3, Expressions.a3)
            test(Expressions.e4, Expressions.a4)
            test(Expressions.e5, Expressions.a5)
            test(Expressions.e6, Expressions.a6)
            test(Expressions.e7, Expressions.a7)
            test(Expressions.e8, Expressions.a8)
        }

        fun test(exp: String, ans: Double) {
            val parser = Parser(exp)
            val expression = Expression(parser.tokenList())

            val calcAns = expression.eval()

            assert(calcAns is IValue)
            if (calcAns is IValue) {
                if (calcAns.equals(ans)) {
                    println("[$exp] = $calcAns = $ans")
                }
            }
        }
    }
}
