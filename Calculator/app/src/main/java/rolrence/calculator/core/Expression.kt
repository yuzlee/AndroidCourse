package rolrence.calculator.core

import rolrence.calculator.core.nodes.BinaryOpt
import rolrence.calculator.core.nodes.Constant

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Expression constructor(val tokens: MutableList<Token>) {
    constructor(itokens: Iterable<Token>): this(itokens.toMutableList())

    var position: Int = 0
        get
        private set

    var token: Token = Token(TokenKind.Unknown)
        get() = if (canEval()) tokens[position] else Token(TokenKind.EofToken)

    fun canEval() = position < tokens.count() && tokens[position].kind != TokenKind.EofToken

    fun status(): String {
        var str = ""
        if (0 < tokens.count()) str += "has ${tokens.count()} tokens\n"
        if (tokens[0].kind == TokenKind.EofToken) str += "start with EOF\n"
        return str
    }

    fun nextToken() {
        position++
    }

    fun lastToken() {
        position--
    }

    fun eval(): INode {
        position = 0
        if (!canEval()) {
            return EndNode()
        }
        return evalExp()!!
    }

    fun evalExp(currentPrecedence: Int = 0): IValue? {
        val value = evalUnaryOrValueExp()
        return evalBinaryExp(currentPrecedence, value)
    }

    fun evalUnaryOrValueExp(): IValue {
        when(token.kind) {
            TokenKind.PlusToken -> {
                nextToken()
                return +evalUnaryOrValueExp()
            }
            TokenKind.MinusToken -> {
                nextToken()
                return -evalUnaryOrValueExp()
            }
            else -> {
                return evalValueExp()!!
            }
        }
    }

    fun evalValueExp(): IValue? {
        when(token.kind) {
            TokenKind.NumericLiteral -> {
                return evalNumeric()
            }
            TokenKind.Function -> {
                return evalFunction()
            }
            TokenKind.OpenParenToken -> {
                return evalParentesizedExp()
            }
            else -> {
                return null
            }
        }
    }

    fun evalNumeric(): IValue {
        val num = Number(token.value)
        nextToken()
        return num
    }

    fun evalConstant(): IValue {
        val constant = Constant.get(token.value)
        nextToken()
        return constant
    }

    fun evalBinaryExp(currentPrecedence: Int, left: IValue): IValue {
        var left = left
        while (true) {
            val new = BinaryOpt.getPrecedence(token.kind)
            val consumeCurrentOperator = new > currentPrecedence

            if (!consumeCurrentOperator) {
                break
            }
            val _token = token
            nextToken()
            left = evalSimpleBinaryExp(left, _token.kind, evalExp(new)!!)!!
        }
        return left
    }

    fun evalSimpleBinaryExp(left: IValue, oper: TokenKind, right: IValue): IValue? {
        when(oper) {
            TokenKind.PlusToken -> return BinaryOpt.add(left, right)
            TokenKind.MinusToken -> return BinaryOpt.substract(left, right)
            TokenKind.AsteriskToken -> return BinaryOpt.multiply(left, right)
            TokenKind.SlashToken -> return BinaryOpt.divide(left, right)
            TokenKind.AsteriskAsteriskToken -> return BinaryOpt.pow(left, right)
            else -> return null
        }
    }

    fun evalParentesizedExp(): IValue {
        nextToken()
        val res = evalExp()
        var re: IValue? = null
        if (token.kind == TokenKind.CloseParentToken) {
            nextToken()
            re = res
        }
        return re!!
    }

    fun evalFunction(): IValue {
        val function = FunctionFactory.createFunction(token.value)
        nextToken()
        val value = evalUnaryOrValueExp()
        return function!!.execute(value)
    }

    companion object {
        fun eval(exp: String): INode {
            val parser = Parser(exp)
            val expr = Expression(parser.tokenList())
            return expr.eval()
        }
    }
}