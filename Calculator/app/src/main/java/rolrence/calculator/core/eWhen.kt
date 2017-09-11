package rolrence.calculator.core

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
fun <T: Comparable<T>> eWhen(target: T, tester: Tester<T>.() -> Unit) {
    val test = Tester(target)
    test.tester()
    test.funFiltered?.invoke() ?: return
}

class Tester<T: Comparable<T>>(val it: T) {
    var funFiltered: (() -> Unit)? = null

    infix fun Boolean.then(block: () -> Unit) {
        if (this && funFiltered == null)
            funFiltered = block
    }

    fun lt(arg: T) = it < arg
    fun gt(arg: T) = it > arg
    fun ge(arg: T) = it >= arg
    fun le(arg: T) = it <= arg
    fun eq(arg: T) = it == arg
    fun ne(arg: T) = it != arg
    fun ct(arg: Collection<T>) = it in arg
    fun ct(arg: String) = it as String in arg
    fun nc(arg: Collection<T>) = it !in arg
    fun nc(arg: String) = it as String !in arg
}