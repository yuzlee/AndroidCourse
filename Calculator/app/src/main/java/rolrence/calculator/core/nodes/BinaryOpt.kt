package rolrence.calculator.core.nodes

import rolrence.calculator.core.IValue
import rolrence.calculator.core.Rational
import rolrence.calculator.core.Number
import rolrence.calculator.core.TokenKind


/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class BinaryOpt {
    companion object {
        val _precedence = mapOf(
                Pair(TokenKind.PlusToken, 1),
                Pair(TokenKind.MinusToken, 1),

                Pair(TokenKind.AsteriskToken, 2),
                Pair(TokenKind.SlashToken, 2),

                Pair(TokenKind.AsteriskAsteriskToken, 3)
        )

        fun getPrecedence(kind: TokenKind): Int {
            if (_precedence.containsKey(kind)) {
                return _precedence.get(kind)!!
            }
            return -1
        }

        fun add(left: IValue, right: IValue): IValue? {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l + r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l + r }
            } else {
                return null
            }
        }

        fun substract(left: IValue, right: IValue): IValue? {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l - r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l - r }
            }
            return null
        }

        fun multiply(left: IValue, right: IValue): IValue? {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l * r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l * r }
            }
            return null
        }

        fun divide(left: IValue, right: IValue): IValue? {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l / r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l / r }
            }
            return null
        }

        fun pow(left: IValue, right: IValue): IValue? {
            val e = right.value
            if (left is Rational) {
                return left pow e.toInt()
            } else if (left is Number) {
                return left pow e
            }
            return null
        }

        fun <T> evaluate(left: IValue, right: IValue, opt: (T, T) -> IValue): IValue {
            val l = left as T
            val r = right as T
            return opt(l, r)
        }
    }
}