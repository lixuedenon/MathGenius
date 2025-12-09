// app/src/main/kotlin/com/mathgenius/calculator/core/i18n/MathFormatter.kt
// Enhanced Math Formatter with Unicode Superscript Support

package com.mathgenius.calculator.core.i18n

import com.mathgenius.calculator.core.engine.Expr
import com.mathgenius.calculator.core.engine.BinaryOp
import com.mathgenius.calculator.core.engine.UnaryOp
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * 数学公式格式化器 (增强版)
 * 将表达式树转换为可读的字符串表示
 * 支持 Unicode 上标和智能省略乘号
 */
class MathFormatter {

    /**
     * 格式化类型
     */
    enum class FormatType {
        TEXT,       // 纯文本
        LATEX,      // LaTeX 格式
        HTML        // HTML 格式
    }

    /**
     * Unicode 上标映射表 (0-9)
     */
    private val superscriptMap = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '-' to '⁻', '+' to '⁺', '.' to '·'
    )

    /**
     * 数字格式化器
     */
    private val decimalFormat = DecimalFormat("#.####", DecimalFormatSymbols(Locale.US))

    /**
     * 格式化表达式为文本
     *
     * @param expr 表达式
     * @param formatType 格式类型
     * @return 格式化后的字符串
     */
    fun format(expr: Expr, formatType: FormatType = FormatType.TEXT): String {
        return when (formatType) {
            FormatType.TEXT -> formatText(expr)
            FormatType.LATEX -> formatLatex(expr)
            FormatType.HTML -> formatHtml(expr)
        }
    }

    /**
     * 格式化为纯文本 (带 Unicode 上标)
     */
    private fun formatText(expr: Expr, parentOp: BinaryOp? = null): String {
        return when (expr) {
            is Expr.Constant -> formatNumber(expr.value)
            is Expr.Variable -> expr.name
            is Expr.Binary -> formatBinaryText(expr, parentOp)
            is Expr.Unary -> formatUnaryText(expr)
        }
    }

    /**
     * 格式化二元运算为文本
     */
    private fun formatBinaryText(expr: Expr.Binary, parentOp: BinaryOp?): String {
        val left = formatText(expr.left, expr.op)
        val right = formatText(expr.right, expr.op)

        val result = when (expr.op) {
            BinaryOp.POWER -> {
                // 使用 Unicode 上标
                val base = if (needsParentheses(expr.left, expr.op)) {
                    "($left)"
                } else {
                    left
                }
                val exponent = toSuperscript(right)
                "$base$exponent"
            }
            BinaryOp.MULTIPLY -> {
                // 智能省略乘号
                if (shouldOmitMultiplySign(expr.left, expr.right)) {
                    "$left$right"
                } else {
                    "$left × $right"  // 使用 × 符号更清晰
                }
            }
            BinaryOp.DIVIDE -> {
                "$left / $right"
            }
            else -> {
                "$left ${expr.op.symbol} $right"
            }
        }

        // 判断是否需要括号
        return if (needsParenthesesForParent(expr.op, parentOp)) {
            "($result)"
        } else {
            result
        }
    }

    /**
     * 格式化一元运算为文本
     */
    private fun formatUnaryText(expr: Expr.Unary): String {
        val operand = formatText(expr.operand)
        return when (expr.op) {
            UnaryOp.NEGATE -> {
                // 如果操作数需要括号
                if (expr.operand is Expr.Binary &&
                    (expr.operand.op == BinaryOp.ADD || expr.operand.op == BinaryOp.SUBTRACT)) {
                    "-($operand)"
                } else {
                    "-$operand"
                }
            }
            else -> "${expr.op.symbol}($operand)"
        }
    }

    /**
     * 将字符串转换为 Unicode 上标
     * 只转换 0-9 的数字,其他字符保持原样
     */
    private fun toSuperscript(text: String): String {
        // 检查是否只包含 0-9 和 -
        if (text.all { it.isDigit() || it == '-' || it == '.' }) {
            return text.map { superscriptMap[it] ?: it }.joinToString("")
        }
        // 如果包含其他字符,保持原样并用括号包裹
        return "^($text)"
    }

    /**
     * 判断是否应该省略乘号
     */
    private fun shouldOmitMultiplySign(left: Expr, right: Expr): Boolean {
        // 情况 1: 数字 * 变量 (2 * x → 2x)
        if (left is Expr.Constant && right is Expr.Variable) {
            return true
        }

        // 情况 2: 数字 * (任意表达式) (2 * (x+1) → 2(x+1))
        if (left is Expr.Constant && right is Expr.Binary) {
            return true
        }

        // 情况 3: 数字 * 函数 (2 * sin(x) → 2sin(x))
        if (left is Expr.Constant && right is Expr.Unary) {
            return true
        }

        // 情况 4: 变量 * 变量 (x * y → xy)
        if (left is Expr.Variable && right is Expr.Variable) {
            return true
        }

        // 情况 5: (表达式) * 变量 ((x+1) * y → (x+1)y)
        if (left is Expr.Binary && right is Expr.Variable) {
            return true
        }

        return false
    }

    /**
     * 判断表达式是否需要括号 (基于子表达式类型)
     */
    private fun needsParentheses(expr: Expr, currentOp: BinaryOp): Boolean {
        return when (expr) {
            is Expr.Binary -> {
                // 加减法在乘除幂中需要括号
                when (currentOp) {
                    BinaryOp.MULTIPLY, BinaryOp.DIVIDE, BinaryOp.POWER -> {
                        expr.op == BinaryOp.ADD || expr.op == BinaryOp.SUBTRACT
                    }
                    else -> false
                }
            }
            else -> false
        }
    }

    /**
     * 判断是否需要括号 (基于父运算符)
     */
    private fun needsParenthesesForParent(currentOp: BinaryOp, parentOp: BinaryOp?): Boolean {
        if (parentOp == null) return false

        // 当前运算符优先级低于父运算符时需要括号
        return currentOp.precedence < parentOp.precedence
    }

    /**
     * 格式化为 LaTeX
     */
    private fun formatLatex(expr: Expr): String {
        return when (expr) {
            is Expr.Constant -> formatNumber(expr.value)
            is Expr.Variable -> expr.name
            is Expr.Binary -> formatBinaryLatex(expr)
            is Expr.Unary -> formatUnaryLatex(expr)
        }
    }

    /**
     * 格式化二元运算为 LaTeX
     */
    private fun formatBinaryLatex(expr: Expr.Binary): String {
        val left = formatLatex(expr.left)
        val right = formatLatex(expr.right)

        return when (expr.op) {
            BinaryOp.ADD -> "$left + $right"
            BinaryOp.SUBTRACT -> "$left - $right"
            BinaryOp.MULTIPLY -> {
                if (shouldOmitMultiplySign(expr.left, expr.right)) {
                    "$left$right"
                } else {
                    "$left \\cdot $right"
                }
            }
            BinaryOp.DIVIDE -> "\\frac{$left}{$right}"
            BinaryOp.POWER -> "$left^{$right}"
        }
    }

    /**
     * 格式化一元运算为 LaTeX
     */
    private fun formatUnaryLatex(expr: Expr.Unary): String {
        val operand = formatLatex(expr.operand)

        return when (expr.op) {
            UnaryOp.NEGATE -> "-$operand"
            UnaryOp.SQRT -> "\\sqrt{$operand}"
            UnaryOp.ABS -> "\\left|$operand\\right|"
            UnaryOp.LN -> "\\ln($operand)"
            UnaryOp.LOG -> "\\log($operand)"
            UnaryOp.EXP -> "e^{$operand}"
            else -> "\\${expr.op.symbol}($operand)"
        }
    }

    /**
     * 格式化为 HTML
     */
    private fun formatHtml(expr: Expr): String {
        return when (expr) {
            is Expr.Constant -> formatNumber(expr.value)
            is Expr.Variable -> "<i>${expr.name}</i>"
            is Expr.Binary -> formatBinaryHtml(expr)
            is Expr.Unary -> formatUnaryHtml(expr)
        }
    }

    /**
     * 格式化二元运算为 HTML
     */
    private fun formatBinaryHtml(expr: Expr.Binary): String {
        val left = formatHtml(expr.left)
        val right = formatHtml(expr.right)

        return when (expr.op) {
            BinaryOp.POWER -> "$left<sup>$right</sup>"
            BinaryOp.DIVIDE -> "<div class='fraction'><span class='numerator'>$left</span><span class='denominator'>$right</span></div>"
            BinaryOp.MULTIPLY -> {
                if (shouldOmitMultiplySign(expr.left, expr.right)) {
                    "$left$right"
                } else {
                    "$left × $right"
                }
            }
            else -> "$left ${expr.op.symbol} $right"
        }
    }

    /**
     * 格式化一元运算为 HTML
     */
    private fun formatUnaryHtml(expr: Expr.Unary): String {
        val operand = formatHtml(expr.operand)
        return when (expr.op) {
            UnaryOp.NEGATE -> "-$operand"
            UnaryOp.SQRT -> "√($operand)"
            else -> "${expr.op.symbol}($operand)"
        }
    }

    /**
     * 格式化数字
     */
    private fun formatNumber(value: Double): String {
        // 如果是整数,不显示小数点
        return if (value == value.toInt().toDouble()) {
            value.toInt().toString()
        } else {
            decimalFormat.format(value)
        }
    }
}