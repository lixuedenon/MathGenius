// app/src/main/kotlin/com/mathgenius/calculator/core/i18n/MathFormatter.kt
// Kotlin Source File

package com.mathgenius.calculator.core.i18n

import com.mathgenius.calculator.core.engine.Expr
import com.mathgenius.calculator.core.engine.BinaryOp
import com.mathgenius.calculator.core.engine.UnaryOp
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * 数学公式格式化器
 * 将表达式树转换为可读的字符串表示
 * 支持多种格式：文本、LaTeX、HTML
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
     * 格式化为纯文本
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
        val opSymbol = expr.op.symbol

        val result = when (expr.op) {
            BinaryOp.POWER -> "$left^$right"
            else -> "$left $opSymbol $right"
        }

        // 判断是否需要括号
        return if (needsParentheses(expr.op, parentOp)) {
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
            UnaryOp.NEGATE -> "-$operand"
            else -> "${expr.op.symbol}($operand)"
        }
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
                // 智能乘号省略
                if (shouldOmitMultiply(expr.left, expr.right)) {
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
        // 如果是整数，不显示小数点
        return if (value == value.toInt().toDouble()) {
            value.toInt().toString()
        } else {
            decimalFormat.format(value)
        }
    }

    /**
     * 判断是否需要括号
     */
    private fun needsParentheses(currentOp: BinaryOp, parentOp: BinaryOp?): Boolean {
        if (parentOp == null) return false
        return currentOp.precedence < parentOp.precedence
    }

    /**
     * 判断是否可以省略乘号
     * 例如：2*x 可以写成 2x，但 x*y 不能省略
     */
    private fun shouldOmitMultiply(left: Expr, right: Expr): Boolean {
        return (left is Expr.Constant && right is Expr.Variable) ||
               (left is Expr.Constant && right is Expr.Binary)
    }
}