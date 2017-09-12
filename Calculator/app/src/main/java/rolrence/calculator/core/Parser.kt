package rolrence.calculator.core

import rolrence.calculator.core.exceptions.ParsingException
import kotlin.coroutines.experimental.buildSequence


/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Parser constructor(expr: String) {
    var pos: Int = 0
        get
        private set

    val expr: String = expr
        get

    fun canParse() = pos < expr.length

    fun tokenList() = parseAll().toMutableList()

    fun parseAll(): Iterable<Token> {
        val seq = buildSequence {
            while (true) {
                if (!canParse()) {
                    yield(Token(TokenKind.EofToken))
                    return@buildSequence
                }

                val current = expr[pos]
                when (current) {
                    '+' -> {
                        pos++
                        yield(Token(TokenKind.PlusToken))
                    }
                    '-' -> {
                        pos++
                        yield(Token(TokenKind.MinusToken))
                    }
                    '*' -> {
                        val nextChar = expr.elementAtOrNull(pos + 1)
                        pos++
                        if (nextChar == '*') {
                            pos++
                            yield(Token(TokenKind.AsteriskAsteriskToken))
                        } else {
                            yield(Token(TokenKind.AsteriskToken))
                        }
                    }
                    '/' -> {
                        pos++
                        yield(Token(TokenKind.SlashToken))
                    }

                    '0' -> {
                        yield(parseNumeric())
                    }
                    '1' -> {
                        yield(parseNumeric())
                    }
                    '2' -> {
                        yield(parseNumeric())
                    }
                    '3' -> {
                        yield(parseNumeric())
                    }
                    '4' -> {
                        yield(parseNumeric())
                    }
                    '5' -> {
                        yield(parseNumeric())
                    }
                    '6' -> {
                        yield(parseNumeric())
                    }
                    '7' -> {
                        yield(parseNumeric())
                    }
                    '8' -> {
                        yield(parseNumeric())
                    }
                    '9' -> {
                        yield(parseNumeric())
                    }

                    '.' -> {
                    }
                    ',' -> {
                        val nextChar = expr.elementAtOrNull(pos + 1)
                        if (isDigit(nextChar)) { yield(parseNumeric()) }
                        else { pos++ }
                    }

                    '(' -> {
                        pos++
                        yield(Token(TokenKind.OpenParentToken))
                    }
                    ')' -> {
                        pos++
                        yield(Token(TokenKind.CloseParentToken))
                    }
                    else -> {
                        if (isFunctionOrConstantNamePart(current)) {
                            yield(parseFunctionOrConstant())
                        } else if (current == ' ') {
                            pos++
                        } else {
                            throw ParsingException("unknown symbol: $current")
                        }
                    }
                }
            }
        }
        return seq.asIterable()
    }

    fun parseNumeric(): Token {
        val start = pos
        pos++
        while (isDigit(expr.elementAtOrNull(pos))) {
            pos++
        }
        if (expr.elementAtOrNull(pos) == '.') {
            pos++
            while (isDigit(expr.elementAtOrNull(pos))) {
                pos++
            }
        }
        var end = pos
        if (expr.elementAtOrNull(pos) == 'E' || expr.elementAtOrNull(pos) == 'e') {
            pos++
            if (expr.elementAtOrNull(pos) == '+' || expr.elementAtOrNull(pos) == '-') {
                pos++
            }
            if (isDigit(expr.elementAtOrNull(pos))) {
                pos++
                while (isDigit(expr.elementAtOrNull(pos))) {
                    pos++
                }
                end = pos
            } else {
                throw ParsingException("expected digit after \"E\" in Number")
            }
        }
        val numberStr = expr.substring(start, end)
        return Token(TokenKind.NumericLiteral, numberStr)
    }

    fun parseFunctionOrConstant(): Token {
        val start = pos
        pos++
        while (canParse() && isFunctionOrConstantNamePart(expr.elementAtOrNull(pos))) {
            pos++
        }
        val name = expr.substring(start, pos)
        val nextNode = expr.elementAtOrNull(pos)
        if (nextNode == '(' || nextNode == ' ') {
            return Token(TokenKind.Function, name.trim())
        }
        return Token(TokenKind.Constant, name.trim())
    }

    private fun isDigit(ch: Char?) = (ch != null) && (ch in '0'..'9')

    private fun isFunctionOrConstantNamePart(ch: Char?): Boolean {
        if (ch == null) {
            return false
        }
        return (ch in '0'..'9') || (ch in 'a'..'z') || (ch in 'A'..'Z') || (ch == '$') || (ch == '_') || (ch == '.')
    }
}