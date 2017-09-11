package rolrence.calculator.core

/**
 * Created by Rolrence on 9/11/2017.
 *
 */
enum class TokenKind {
    Unknown,
    EofToken,

    NumericLiteral,

    OpenParenToken,
    CloseParentToken,
    PlusToken,
    MinusToken,
    AsteriskToken,
    AsteriskAsteriskToken,
    SlashToken,

    Function,
    Constant
}

class Token constructor(kind: TokenKind, value: String) {
    var kind: TokenKind = kind
        get

    var value: String = value
        get

    constructor(kind: TokenKind): this(kind, "")

    override fun toString() = if (!value.isEmpty()) "$kind=$value" else "$kind"
}