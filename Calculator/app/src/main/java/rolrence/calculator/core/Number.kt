package rolrence.calculator.core

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Number: IValue {
    override var value: Double = 0.0
        get

    constructor(value: String) {
        this.value = value.toDoubleOrNull()!!
    }

    constructor(value: IValue) {
        this.value = value.value
    }

    constructor(value: Double) {
        this.value = value
    }

    operator fun plus(r: Number) = Number(this.value + r.value)

    operator fun minus(r: Number) = Number(this.value - r.value)

    operator fun times(r: Number) = Number(this.value * r.value)

    operator fun div(r: Number) = Number(this.value / r.value)

    override operator fun unaryPlus() = this

    override operator fun unaryMinus() = Number(-this.value)

    override fun equals(other: Any?): Boolean {
        if (other is Number) {
            if (Math.abs(this.value - other.value) < epsilon) {
                return true
            }
        } else if (other is Rational) {
            return other.equals(this)
        }
        return false
    }

    override fun toString() = value.toString()

    infix fun pow(e: Double): Number = Number(Math.pow(this.value, e))

    companion object {
        val epsilon: Double = 1e-6
    }
}