package rolrence.calculator.core

import rolrence.calculator.core.exceptions.ParsingException
import kotlin.coroutines.experimental.buildSequence


/**
 * Created by Rolrence on 9/11/2017.
 *
 */
class Parser constructor(expText: String) {
    var currentPosition: Int = 0
        get
        private set

    val expText: String = expText
        get

    fun canParse() = currentPosition < expText.length

    fun tokenList() = parseAll().toMutableList()

    fun parseAll(): Iterable<Token> {
        val seq = buildSequence {
            while (true) {
                if (!canParse()) {
                    yield(Token(TokenKind.EofToken))
                    return@buildSequence
                }

                val current = expText[currentPosition]
                when(current) {
                    '+' -> {
                        currentPosition++
                        yield(Token(TokenKind.PlusToken))
                    }
                    '-' -> {
                        currentPosition++
                        yield(Token(TokenKind.MinusToken))
                    }
                    '*' -> {
                        val nextChar = expText.elementAtOrNull(currentPosition + 1)
                        currentPosition++
                        if (nextChar == '*') {
                            currentPosition++
                            yield(Token(TokenKind.AsteriskAsteriskToken))
                        } else {
                            yield(Token(TokenKind.AsteriskToken))
                        }
                    }
                    '/' -> {
                        currentPosition++
                        yield(Token(TokenKind.SlashToken))
                    }

                    '0' -> { yield(parseNumeric()) }
                    '1' -> { yield(parseNumeric()) }
                    '2' -> { yield(parseNumeric()) }
                    '3' -> { yield(parseNumeric()) }
                    '4' -> { yield(parseNumeric()) }
                    '5' -> { yield(parseNumeric()) }
                    '6' -> { yield(parseNumeric()) }
                    '7' -> { yield(parseNumeric()) }
                    '8' -> { yield(parseNumeric()) }
                    '9' -> { yield(parseNumeric()) }

                    '.' -> {}
                    ',' -> {
                        val nextChar = expText.elementAtOrNull(currentPosition + 1)
                        if (nextChar != null) {
                            if (nextChar in '0'..'9') {
                                yield(parseNumeric())
                            }
                        } else {
                            currentPosition++
                        }
                    }

                    '(' -> {
                        currentPosition++
                        yield(Token(TokenKind.OpenParenToken))
                    }
                    ')' -> {
                        currentPosition++
                        yield(Token(TokenKind.CloseParentToken))
                    }
                    else -> {
                        if (isFunctionOrConstantNamePart(current)) {
                            yield(parseFunctionOrConstant())
                        } else if (current == ' ') {
                            currentPosition++
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
        val start = currentPosition
        currentPosition++
        while (isDigit(expText.elementAtOrNull(currentPosition))) {
            currentPosition++
        }
        if (expText.elementAtOrNull(currentPosition) == '.') {
            currentPosition++
            while (isDigit(expText.elementAtOrNull(currentPosition))) {
                currentPosition++
            }
        }
        var end = currentPosition
        if (expText.elementAtOrNull(currentPosition) == 'E' || expText.elementAtOrNull(currentPosition) == 'e') {
            currentPosition++
            if (expText.elementAtOrNull(currentPosition) == '+' || expText.elementAtOrNull(currentPosition) == '-') {
                currentPosition++
            }
            if (isDigit(expText.elementAtOrNull(currentPosition))) {
                currentPosition++
                while (isDigit(expText.elementAtOrNull(currentPosition))) {
                    currentPosition++
                }
                end = currentPosition
            } else {
                throw ParsingException("expected digit after \"E\" in Number")
            }
        }
        val numberStr = expText.substring(start, end)
        return Token(TokenKind.NumericLiteral, numberStr)
    }

    fun parseFunctionOrConstant(): Token {
        val start = currentPosition
        currentPosition++
        while (canParse() && isFunctionOrConstantNamePart(expText.elementAtOrNull(currentPosition))) {
            currentPosition++
        }
        val name = expText.substring(start, currentPosition)
        val nextNode = expText.elementAtOrNull(currentPosition)
        if (nextNode == '(' || nextNode == ' ') {
            return Token(TokenKind.Function, name.trim())
        }
        return Token(TokenKind.Constant, name.trim())
    }

    private fun isDigit(ch: Char?) = ch in '0'..'9'

    private fun isFunctionOrConstantNamePart(ch: Char?): Boolean {
        return (ch in '0'..'9') || (ch in 'a'..'z') || (ch in 'A'..'Z') || (ch == '$') || (ch == '_') || (ch == '.')
    }
}