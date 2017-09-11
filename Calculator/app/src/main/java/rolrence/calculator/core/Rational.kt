package rolrence.calculator.core

/**
 * Created by Rolrence on 9/11/2017.
 *
 */

class Rational : IValue {
    var numerator = 0L
        get
        private set

    var denumerator = 0L
        get
        private set

    override var value: Double = 0.0
        get() = numerator.toDouble() / denumerator.toDouble()

    var isValid: Boolean = false
        get() = (denumerator != 0L || numerator != 0L)

    constructor(numerator: Long, denumerator: Long) {
        this.numerator = numerator
        this.denumerator = denumerator
    }

    constructor(r: Rational) : this(r.numerator, r.denumerator)

    constructor(value: Double) : this(value, 1.0)

    constructor(numerator: Double, denumerator: Double) {
        initialize(numerator, denumerator)
    }

    fun initialize(numerator: Double, denumerator: Double) {
        var n = numerator
        var d = denumerator
        while (!isInt(n) || !isInt(d)) {
            n *= 10
            d *= 10
        }
        this.numerator = n.toLong()
        this.denumerator = d.toLong()
    }

    fun normalize() {
        val gcd = getGcd(numerator, denumerator)
        if (gcd != 1L) {
            numerator /= gcd
            denumerator /= gcd
        }
    }

    override fun toString(): String {
        if (denumerator == 1L) {
            return numerator.toString()
        } else if (denumerator == -1L) {
            return (-numerator).toString()
        } else {
            return "$numerator/$denumerator"
        }
    }

    operator fun plus(r: Rational): Rational {
        val n1 = this.numerator
        val d1 = this.denumerator
        val n2 = r.numerator
        val d2 = r.denumerator
        val new = Rational(n1 * d2 + n2 * d1, d1 * d2)
        new.normalize()
        return new
    }

    operator fun minus(r: Rational): Rational {
        val n1 = this.numerator
        val d1 = this.denumerator
        val n2 = r.numerator
        val d2 = r.denumerator
        val new = Rational(n1 * d2 - n2 * d1, d1 * d2)
        new.normalize()
        return new
    }

    operator fun times(r: Rational): Rational {
        val new = Rational(this.numerator * r.numerator, this.denumerator * r.denumerator)
        new.normalize()
        return new
    }

    operator fun div(r: Rational): Rational {
        val new = Rational(this.numerator * r.denumerator, this.denumerator * r.numerator)
        new.normalize()
        return new
    }

    override fun equals(other: Any?): Boolean {
        if (other is Rational) {
            val left = this
            left.normalize()
            other.normalize()
            return left.numerator == other.numerator && left.denumerator == other.denumerator
        } else if (other is Number) {
            if (isValid) {
                return other.equals(Number(numerator.toDouble() / denumerator.toDouble()))
            }
        }
        return false
    }

    override operator fun unaryPlus() = this

    override operator fun unaryMinus() = Rational(-this.numerator, this.denumerator)

    infix fun pow(e: Int): Rational {
        val n = Math.pow(this.numerator.toDouble(), e.toDouble()).toLong()
        val d = Math.pow(this.denumerator.toDouble(), e.toDouble()).toLong()
        val new = Rational(n, d)
        new.normalize()
        return new
    }

    private fun getGcd(n1: Long, n2: Long): Long {
        var n1 = Math.abs(n1)
        var n2 = Math.abs(n2)
        while (n1 != 0L && n2 != 0L) {
            if (n1 % n2 > 0) {
                n1 = n2
                n2 = n1 % n2
            } else {
                break
            }
        }
        return if (n1 != 0L && n2 != 0L) n2 else 1
    }

    private fun isInt(n: Double) = n % 1 == 0.0
}