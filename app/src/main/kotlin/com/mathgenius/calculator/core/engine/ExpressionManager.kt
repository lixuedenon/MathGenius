// app/src/main/kotlin/com/mathgenius/calculator/core/engine/ExpressionManager.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

class ExpressionManager {

    private val internal = StringBuilder()
    private val display = StringBuilder()

    private val superscriptMap = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '-' to '⁻', '+' to '⁺', '.' to '·'
    )

    fun appendNumber(num: String) {
        internal.append(num)
        display.append(num)
    }

    fun appendDecimal() {
        internal.append(".")
        display.append(".")
    }

    fun appendVariable(varName: String) {
        if (needsImplicitMultiply()) {
            internal.append("*")
        }
        internal.append(varName)
        display.append(varName)
    }

    fun appendFunction(funcName: String) {
        if (needsImplicitMultiply()) {
            internal.append("*")
        }
        internal.append("$funcName(")
        display.append("$funcName(")
    }

    fun appendOperator(op: String) {
        val internalOp = when (op) {
            "×" -> "*"
            "÷" -> "/"
            else -> op
        }
        internal.append(internalOp)
        display.append(op)
    }

    fun appendPower(exponent: String) {
        internal.append("^")
        internal.append(exponent)

        val superscript = toSuperscript(exponent)
        display.append(superscript)
    }

    fun appendParenthesis(paren: String) {
        if (paren == "(" && needsImplicitMultiply()) {
            internal.append("*")
        }
        internal.append(paren)
        display.append(paren)
    }

    fun appendConstant(constant: String) {
        if (needsImplicitMultiply()) {
            internal.append("*")
        }

        when (constant) {
            "π" -> {
                internal.append("3.14159265359")
                display.append("π")
            }
            "e" -> {
                internal.append("2.71828182846")
                display.append("e")
            }
            else -> {
                internal.append(constant)
                display.append(constant)
            }
        }
    }

    fun deleteLast() {
        if (internal.isEmpty()) return

        internal.deleteCharAt(internal.length - 1)

        if (display.isNotEmpty()) {
            val lastDisplay = display.last()
            display.deleteCharAt(display.length - 1)

            if (lastDisplay in superscriptMap.values) {
                if (display.isNotEmpty() && display.last() == '^') {
                    display.deleteCharAt(display.length - 1)
                }
            }
        }
    }

    fun clear() {
        internal.clear()
        display.clear()
    }

    fun getDisplayText(): String {
        return display.toString()
    }

    fun getInternalExpression(): String {
        return internal.toString()
    }

    fun isEmpty(): Boolean {
        return internal.isEmpty()
    }

    fun setFromText(text: String) {
        try {
            val parser = ExpressionParser(enableImplicitMultiplication = true)
            val expr = parser.parse(text)

            internal.clear()
            internal.append(exprToInternalString(expr))

            display.clear()
            display.append(formatForDisplay(text))
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid expression: ${e.message}")
        }
    }

    private fun needsImplicitMultiply(): Boolean {
        if (internal.isEmpty()) return false

        val lastChar = internal.last()
        return when {
            lastChar.isDigit() -> true
            lastChar.isLetter() -> true
            lastChar == ')' -> true
            else -> false
        }
    }

    private fun toSuperscript(text: String): String {
        return if (text.length == 1 && text[0].isDigit()) {
            superscriptMap[text[0]]?.toString() ?: "^$text"
        } else {
            "^($text)"
        }
    }

    private fun exprToInternalString(expr: Expr): String {
        return when (expr) {
            is Expr.Constant -> expr.value.toString()
            is Expr.Variable -> expr.name
            is Expr.Binary -> {
                val left = exprToInternalString(expr.left)
                val right = exprToInternalString(expr.right)
                "($left${expr.op.symbol}$right)"
            }
            is Expr.Unary -> {
                val operand = exprToInternalString(expr.operand)
                "${expr.op.symbol}($operand)"
            }
        }
    }

    private fun formatForDisplay(text: String): String {
        var result = text

        result = result.replace(Regex("(\\d)\\*(\\w)"), "$1$2")
        result = result.replace(Regex("(\\d)\\*(sin|cos|tan|ln|exp|log|sqrt|abs)"), "$1$2")
        result = result.replace(Regex("(\\d)\\*\\("), "$1(")
        result = result.replace(Regex("\\)\\*(\\d)"), ")$1")
        result = result.replace(Regex("\\)\\*(\\w)"), ")$1")
        result = result.replace(Regex("\\)\\*\\("), ")(")

        for ((digit, superscript) in superscriptMap) {
            if (digit.isDigit()) {
                result = result.replace("^$digit", superscript.toString())
            }
        }

        return result
    }
}