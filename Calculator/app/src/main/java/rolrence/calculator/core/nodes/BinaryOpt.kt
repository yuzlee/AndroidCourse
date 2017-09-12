package rolrence.calculator.core.nodes

import rolrence.calculator.core.IValue
import rolrence.calculator.core.Number
import rolrence.calculator.core.Rational
import rolrence.calculator.core.TokenKind
import rolrence.calculator.core.exceptions.ValueError


/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class BinaryOpt {
    companion object {
        val _precedence = mapOf(
                TokenKind.PlusToken to 1,
                TokenKind.MinusToken to 1,

                TokenKind.AsteriskToken to 2,
                TokenKind.SlashToken to 2,

                TokenKind.AsteriskAsteriskToken to 3
        )

        fun getPrecedence(kind: TokenKind): Int {
            if (_precedence.containsKey(kind)) {
                return _precedence.get(kind)!!
            }
            return -1
        }

        fun add(left: IValue, right: IValue): IValue {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l + r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l + r }
            }
            throw ValueError("invalid value [$left, $right]")
        }

        fun substract(left: IValue, right: IValue): IValue {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l - r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l - r }
            }
            throw ValueError("invalid value [$left, $right]")
        }

        fun multiply(left: IValue, right: IValue): IValue {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l * r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l * r }
            }
            throw ValueError("invalid value [$left, $right]")
        }

        fun divide(left: IValue, right: IValue): IValue {
            if (left is Rational || right is Rational) {
                return evaluate<Rational>(left, right) { l, r -> l / r }
            } else if (left is Number || right is Number) {
                return evaluate<Number>(left, right) { l, r -> l / r }
            }
            throw ValueError("invalid value [$left, $right]")
        }

        fun pow(left: IValue, right: IValue): IValue {
            val e = right.value
            if (left is Rational) {
                return left pow e.toInt()
            } else if (left is Number) {
                return left pow e
            }
            throw ValueError("invalid value [$left]")
        }

        fun <T> evaluate(left: IValue, right: IValue, opt: (T, T) -> IValue): IValue {
            try {
                val l = left as T
                val r = right as T
                return opt(l, r)
            } catch (e: Exception) {
                throw ValueError("invalid value [$left, $right]")
            }
        }
    }
}