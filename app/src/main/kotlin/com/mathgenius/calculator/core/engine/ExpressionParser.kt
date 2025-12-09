// app/src/main/kotlin/com/mathgenius/calculator/core/engine/ExpressionParser.kt
// Enhanced Expression Parser with Implicit Multiplication Support

package com.mathgenius.calculator.core.engine

import kotlin.math.pow

/**
 * 表达式解析器 (增强版)
 * 使用递归下降算法将字符串解析为表达式树
 * 支持隐式乘法: 2x → 2*x, 2(x+1) → 2*(x+1)
 */
class ExpressionParser {

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

    /**
     * 解析表达式字符串
     * 自动处理隐式乘法
     */
    fun parse(input: String): Expr {
        try {
            // 1. 预处理: 添加隐式乘号
            val preprocessed = preprocessImplicitMultiplication(input)

            // 2. 词法分析
            tokens = tokenize(preprocessed)
            currentIndex = 0

            // 3. 语法分析
            val expr = parseExpression()

            // 4. 检查是否还有未解析的 Token
            if (currentToken.type != TokenType.EOF) {
                throw ParseException("Unexpected token: ${currentToken.value} at position ${currentToken.position}")
            }

            return expr
        } catch (e: Exception) {
            throw ParseException("Failed to parse expression: ${e.message}", e)
        }
    }

    /**
     * 预处理: 自动添加隐式乘号
     *
     * 规则:
     * 1. 数字后跟变量: 2x → 2*x
     * 2. 数字后跟左括号: 2(x+1) → 2*(x+1)
     * 3. 右括号后跟数字: (x+1)2 → (x+1)*2
     * 4. 右括号后跟变量: (x+1)y → (x+1)*y
     * 5. 右括号后跟左括号: (x+1)(x+2) → (x+1)*(x+2)
     * 6. 变量后跟左括号: x(x+1) → x*(x+1)
     */
    private fun preprocessImplicitMultiplication(input: String): String {
        val result = StringBuilder()
        var i = 0

        while (i < input.length) {
            val current = input[i]
            result.append(current)

            // 查看下一个非空白字符
            var j = i + 1
            while (j < input.length && input[j].isWhitespace()) {
                result.append(input[j])
                j++
            }

            if (j < input.length) {
                val next = input[j]

                // 判断是否需要插入乘号
                val needsMultiply = when {
                    // 规则 1: 数字后跟字母 (2x → 2*x)
                    current.isDigit() && next.isLetter() -> true

                    // 规则 2: 数字后跟左括号 (2( → 2*()
                    current.isDigit() && next == '(' -> true

                    // 规则 3: 右括号后跟数字 () 2 → ()*2)
                    current == ')' && next.isDigit() -> true

                    // 规则 4: 右括号后跟字母 () x → ()*x)
                    current == ')' && next.isLetter() -> true

                    // 规则 5: 右括号后跟左括号 ()( → ()*(()
                    current == ')' && next == '(' -> true

                    // 规则 6: 字母后跟左括号 (x( → x*()
                    current.isLetter() && next == '(' -> true

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

    /**
     * 词法分析：将字符串转换为 Token 列表
     */
    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < input.length) {
            val char = input[i]

            when {
                // 跳过空白字符
                char.isWhitespace() -> {
                    i++
                }

                // 数字
                char.isDigit() || char == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) {
                        i++
                    }
                    tokens.add(Token(TokenType.NUMBER, input.substring(start, i), start))
                }

                // 变量或函数
                char.isLetter() -> {
                    val start = i
                    while (i < input.length && input[i].isLetterOrDigit()) {
                        i++
                    }
                    val word = input.substring(start, i)

                    // 判断是函数还是变量
                    if (isFunction(word)) {
                        tokens.add(Token(TokenType.FUNCTION, word, start))
                    } else {
                        tokens.add(Token(TokenType.VARIABLE, word, start))
                    }
                }

                // 运算符
                char in "+-*/^" -> {
                    tokens.add(Token(TokenType.OPERATOR, char.toString(), i))
                    i++
                }

                // 括号
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

    /**
     * 判断是否为函数名
     */
    private fun isFunction(word: String): Boolean {
        return word.lowercase() in listOf(
            "sin", "cos", "tan", "cot", "sec", "csc",
            "arcsin", "arccos", "arctan", "asin", "acos", "atan",
            "ln", "log", "exp", "sqrt", "abs"
        )
    }

    /**
     * 解析表达式（入口）
     */
    private fun parseExpression(): Expr {
        return parseAdditive()
    }

    /**
     * 解析加法和减法（优先级 1）
     */
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

    /**
     * 解析乘法和除法（优先级 2）
     */
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

    /**
     * 解析幂运算（优先级 3）
     */
    private fun parsePower(): Expr {
        var left = parseUnary()

        if (currentToken.type == TokenType.OPERATOR && currentToken.value == "^") {
            advance()
            val right = parsePower() // 右结合
            left = Expr.Binary(left, BinaryOp.POWER, right)
        }

        return left
    }

    /**
     * 解析一元运算（优先级 4）
     */
    private fun parseUnary(): Expr {
        // 处理负号
        if (currentToken.type == TokenType.OPERATOR && currentToken.value == "-") {
            advance()
            val operand = parseUnary()
            return Expr.Unary(UnaryOp.NEGATE, operand)
        }

        // 处理函数
        if (currentToken.type == TokenType.FUNCTION) {
            val funcName = currentToken.value
            advance()

            // 期望左括号
            if (currentToken.type != TokenType.LEFT_PAREN) {
                throw ParseException("Expected '(' after function $funcName")
            }
            advance()

            // 解析参数
            val arg = parseExpression()

            // 期望右括号
            if (currentToken.type != TokenType.RIGHT_PAREN) {
                throw ParseException("Expected ')' after function argument")
            }
            advance()

            // 创建一元运算节点
            val op = UnaryOp.fromSymbol(funcName.lowercase())
                ?: throw ParseException("Unknown function: $funcName")

            return Expr.Unary(op, arg)
        }

        return parsePrimary()
    }

    /**
     * 解析基本元素（优先级最高）
     */
    private fun parsePrimary(): Expr {
        return when (currentToken.type) {
            // 数字
            TokenType.NUMBER -> {
                val value = currentToken.value.toDoubleOrNull()
                    ?: throw ParseException("Invalid number: ${currentToken.value}")
                advance()
                Expr.Constant(value)
            }

            // 变量
            TokenType.VARIABLE -> {
                val name = currentToken.value
                advance()
                Expr.Variable(name)
            }

            // 括号表达式
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

    /**
     * 移动到下一个 Token
     */
    private fun advance() {
        if (currentIndex < tokens.size) {
            currentIndex++
        }
    }
}

/**
 * 解析异常
 */
class ParseException(message: String, cause: Throwable? = null) : Exception(message, cause)