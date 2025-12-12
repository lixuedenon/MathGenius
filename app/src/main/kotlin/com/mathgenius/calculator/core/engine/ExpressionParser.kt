// app/src/main/kotlin/com/mathgenius/calculator/core/engine/ExpressionParser.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

import kotlin.math.pow

class ExpressionParser(
    private val enableImplicitMultiplication: Boolean = false
) {

    private enum class TokenType {
        NUMBER, VARIABLE, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN, EOF
    }

    private data class Token(
        val type: TokenType,
        val value: String,
        val position: Int
    )

    private var tokens = listOf<Token>()
    private var currentIndex = 0

    private val currentToken: Token
        get() = if (currentIndex < tokens.size) tokens[currentIndex] else Token(TokenType.EOF, "", -1)

    fun parse(input: String): Expr {
        try {
            val preprocessed = if (enableImplicitMultiplication) {
                preprocessImplicitMultiplication(input)
            } else {
                input
            }

            tokens = tokenize(preprocessed)
            currentIndex = 0

            val expr = parseExpression()

            if (currentToken.type != TokenType.EOF) {
                throw ParseException("Unexpected token: ${currentToken.value} at position ${currentToken.position}")
            }

            return expr
        } catch (e: Exception) {
            throw ParseException("Failed to parse expression: ${e.message}", e)
        }
    }

    private fun preprocessImplicitMultiplication(input: String): String {
        val result = StringBuilder()
        var i = 0

        while (i < input.length) {
            val current = input[i]
            result.append(current)

            var j = i + 1
            while (j < input.length && input[j].isWhitespace()) {
                result.append(input[j])
                j++
            }

            if (j < input.length) {
                val next = input[j]

                val remainingText = input.substring(j)
                val isFollowedByFunction = isStartOfFunction(remainingText)
                val isOperator = current in "+-*/^"

                val needsMultiply = when {
                    isOperator -> false
                    isFollowedByFunction -> false
                    current.isDigit() && next.isLetter() -> true
                    current.isDigit() && next == '(' -> true
                    current == ')' && next.isDigit() -> true
                    current == ')' && next.isLetter() && !isFollowedByFunction -> true
                    current == ')' && next == '(' -> true
                    current.isLetter() && next == '(' && !isFollowedByFunction -> true
                    else -> false
                }

                if (needsMultiply) {
                    result.append('*')
                }
            }

            i++
        }

        return result.toString()
    }

    private fun isStartOfFunction(text: String): Boolean {
        val functionNames = listOf(
            "sin", "cos", "tan", "cot", "sec", "csc",
            "arcsin", "arccos", "arctan", "asin", "acos", "atan",
            "ln", "log", "exp", "sqrt", "abs"
        )

        return functionNames.any { func ->
            text.startsWith(func) &&
            (text.length == func.length || !text[func.length].isLetterOrDigit())
        }
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < input.length) {
            val char = input[i]

            when {
                char.isWhitespace() -> {
                    i++
                }

                char.isDigit() || char == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) {
                        i++
                    }
                    tokens.add(Token(TokenType.NUMBER, input.substring(start, i), start))
                }

                char.isLetter() -> {
                    val start = i
                    while (i < input.length && input[i].isLetterOrDigit()) {
                        i++
                    }
                    val word = input.substring(start, i)

                    if (isFunction(word)) {
                        tokens.add(Token(TokenType.FUNCTION, word, start))
                    } else {
                        tokens.add(Token(TokenType.VARIABLE, word, start))
                    }
                }

                char in "+-*/^" -> {
                    tokens.add(Token(TokenType.OPERATOR, char.toString(), i))
                    i++
                }

                char == '(' -> {
                    tokens.add(Token(TokenType.LEFT_PAREN, char.toString(), i))
                    i++
                }
                char == ')' -> {
                    tokens.add(Token(TokenType.RIGHT_PAREN, char.toString(), i))
                    i++
                }

                else -> {
                    throw ParseException("Unexpected character: $char at position $i")
                }
            }
        }

        return tokens
    }

    private fun isFunction(word: String): Boolean {
        return word.lowercase() in listOf(
            "sin", "cos", "tan", "cot", "sec", "csc",
            "arcsin", "arccos", "arctan", "asin", "acos", "atan",
            "ln", "log", "exp", "sqrt", "abs"
        )
    }

    private fun parseExpression(): Expr {
        return parseAdditive()
    }

    private fun parseAdditive(): Expr {
        var left = parseMultiplicative()

        while (currentToken.type == TokenType.OPERATOR &&
               currentToken.value in listOf("+", "-")) {
            val op = when (currentToken.value) {
                "+" -> BinaryOp.ADD
                "-" -> BinaryOp.SUBTRACT
                else -> throw ParseException("Invalid operator: ${currentToken.value}")
            }
            advance()
            val right = parseMultiplicative()
            left = Expr.Binary(left, op, right)
        }

        return left
    }

    private fun parseMultiplicative(): Expr {
        var left = parsePower()

        while (currentToken.type == TokenType.OPERATOR &&
               currentToken.value in listOf("*", "/")) {
            val op = when (currentToken.value) {
                "*" -> BinaryOp.MULTIPLY
                "/" -> BinaryOp.DIVIDE
                else -> throw ParseException("Invalid operator: ${currentToken.value}")
            }
            advance()
            val right = parsePower()
            left = Expr.Binary(left, op, right)
        }

        return left
    }

    private fun parsePower(): Expr {
        var left = parseUnary()

        if (currentToken.type == TokenType.OPERATOR && currentToken.value == "^") {
            advance()
            val right = parsePower()
            left = Expr.Binary(left, BinaryOp.POWER, right)
        }

        return left
    }

    private fun parseUnary(): Expr {
        if (currentToken.type == TokenType.OPERATOR && currentToken.value == "-") {
            advance()
            val operand = parseUnary()
            return Expr.Unary(UnaryOp.NEGATE, operand)
        }

        if (currentToken.type == TokenType.FUNCTION) {
            val funcName = currentToken.value
            advance()

            if (currentToken.type != TokenType.LEFT_PAREN) {
                throw ParseException("Expected '(' after function $funcName")
            }
            advance()

            val arg = parseExpression()

            if (currentToken.type != TokenType.RIGHT_PAREN) {
                throw ParseException("Expected ')' after function argument")
            }
            advance()

            val op = UnaryOp.fromSymbol(funcName.lowercase())
                ?: throw ParseException("Unknown function: $funcName")

            return Expr.Unary(op, arg)
        }

        return parsePrimary()
    }

    private fun parsePrimary(): Expr {
        return when (currentToken.type) {
            TokenType.NUMBER -> {
                val value = currentToken.value.toDoubleOrNull()
                    ?: throw ParseException("Invalid number: ${currentToken.value}")
                advance()
                Expr.Constant(value)
            }

            TokenType.VARIABLE -> {
                val name = currentToken.value
                advance()
                Expr.Variable(name)
            }

            TokenType.LEFT_PAREN -> {
                advance()
                val expr = parseExpression()
                if (currentToken.type != TokenType.RIGHT_PAREN) {
                    throw ParseException("Expected ')' but got ${currentToken.value}")
                }
                advance()
                expr
            }

            else -> {
                throw ParseException("Unexpected token: ${currentToken.value} at position ${currentToken.position}")
            }
        }
    }

    private fun advance() {
        if (currentIndex < tokens.size) {
            currentIndex++
        }
    }
}

class ParseException(message: String, cause: Throwable? = null) : Exception(message, cause)