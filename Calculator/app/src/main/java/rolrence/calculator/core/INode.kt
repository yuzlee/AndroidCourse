package rolrence.calculator.core

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
interface INode {}

interface IValue: INode {
    var value: Double
        get

    operator fun unaryPlus(): IValue
    operator fun unaryMinus(): IValue
}

interface IFunction: INode {
    fun execute(arg: IValue): IValue
}

class EndNode: INode {
    override fun toString() = "NaN"
}