// app/src/main/kotlin/com/mathgenius/calculator/core/engine/Simplifier.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

import kotlin.math.*

/**
 * 代数化简器
 * 对表达式树进行化简和优化
 */
class Simplifier {

    /**
     * 化简表达式
     *
     * @param expr 输入表达式
     * @param deep 是否进行深度化简（递归化简子表达式）
     * @return 化简后的表达式
     */
    fun simplify(expr: Expr, deep: Boolean = true): Expr {
        // 递归化简子表达式
        val simplified = if (deep) {
            when (expr) {
                is Expr.Binary -> {
                    val left = simplify(expr.left, true)
                    val right = simplify(expr.right, true)
                    Expr.Binary(left, expr.op, right)
                }
                is Expr.Unary -> {
                    val operand = simplify(expr.operand, true)
                    Expr.Unary(expr.op, operand)
                }
                else -> expr
            }
        } else {
            expr
        }

        // 应用化简规则
        return applySimplificationRules(simplified)
    }

    /**
     * 应用化简规则
     */
    private fun applySimplificationRules(expr: Expr): Expr {
        var current = expr
        var changed = true

        // 反复应用规则直到不再变化
        while (changed) {
            val next = applySinglePass(current)
            changed = next != current
            current = next
        }

        return current
    }

    /**
     * 应用一轮化简规则
     */
    private fun applySinglePass(expr: Expr): Expr {
        return when (expr) {
            is Expr.Constant -> expr
            is Expr.Variable -> expr
            is Expr.Binary -> simplifyBinary(expr)
            is Expr.Unary -> simplifyUnary(expr)
        }
    }

    /**
     * 化简二元运算
     */
    private fun simplifyBinary(expr: Expr.Binary): Expr {
        val left = expr.left
        val right = expr.right

        return when (expr.op) {
            BinaryOp.ADD -> simplifyAddition(left, right)
            BinaryOp.SUBTRACT -> simplifySubtraction(left, right)
            BinaryOp.MULTIPLY -> simplifyMultiplication(left, right)
            BinaryOp.DIVIDE -> simplifyDivision(left, right)
            BinaryOp.POWER -> simplifyPower(left, right)
        }
    }

    /**
     * 化简加法
     */
    private fun simplifyAddition(left: Expr, right: Expr): Expr {
        // 常量折叠：a + b = (a+b)
        if (left is Expr.Constant && right is Expr.Constant) {
            return Expr.Constant(left.value + right.value)
        }

        // 零元规则：0 + x = x
        if (left.isZero()) {
            return right
        }

        // 零元规则：x + 0 = x
        if (right.isZero()) {
            return left
        }

        // 同类项合并：x + x = 2*x
        if (left == right) {
            return Expr.Binary(
                Expr.Constant(2.0),
                BinaryOp.MULTIPLY,
                left
            )
        }

        return Expr.Binary(left, BinaryOp.ADD, right)
    }

    /**
     * 化简减法
     */
    private fun simplifySubtraction(left: Expr, right: Expr): Expr {
        // 常量折叠：a - b = (a-b)
        if (left is Expr.Constant && right is Expr.Constant) {
            return Expr.Constant(left.value - right.value)
        }

        // 零元规则：x - 0 = x
        if (right.isZero()) {
            return left
        }

        // 零元规则：0 - x = -x
        if (left.isZero()) {
            return Expr.Unary(UnaryOp.NEGATE, right)
        }

        // 相消规则：x - x = 0
        if (left == right) {
            return Expr.Constant(0.0)
        }

        return Expr.Binary(left, BinaryOp.SUBTRACT, right)
    }

    /**
     * 化简乘法
     */
    private fun simplifyMultiplication(left: Expr, right: Expr): Expr {
        // 常量折叠：a * b = (a*b)
        if (left is Expr.Constant && right is Expr.Constant) {
            return Expr.Constant(left.value * right.value)
        }

        // 零元规则：0 * x = 0
        if (left.isZero() || right.isZero()) {
            return Expr.Constant(0.0)
        }

        // 单位元规则：1 * x = x
        if (left.isOne()) {
            return right
        }

        // 单位元规则：x * 1 = x
        if (right.isOne()) {
            return left
        }

        // 负号提取：(-1) * x = -x
        if (left is Expr.Constant && left.value == -1.0) {
            return Expr.Unary(UnaryOp.NEGATE, right)
        }

        // 幂合并：x * x = x^2
        if (left == right) {
            return Expr.Binary(left, BinaryOp.POWER, Expr.Constant(2.0))
        }

        return Expr.Binary(left, BinaryOp.MULTIPLY, right)
    }

    /**
     * 化简除法
     */
    private fun simplifyDivision(left: Expr, right: Expr): Expr {
        // 常量折叠：a / b = (a/b)
        if (left is Expr.Constant && right is Expr.Constant) {
            if (right.value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            return Expr.Constant(left.value / right.value)
        }

        // 零元规则：0 / x = 0 (x != 0)
        if (left.isZero()) {
            return Expr.Constant(0.0)
        }

        // 单位元规则：x / 1 = x
        if (right.isOne()) {
            return left
        }

        // 相消规则：x / x = 1
        if (left == right) {
            return Expr.Constant(1.0)
        }

        return Expr.Binary(left, BinaryOp.DIVIDE, right)
    }

    /**
     * 化简幂运算
     */
    private fun simplifyPower(base: Expr, exponent: Expr): Expr {
        // 常量折叠：a ^ b = (a^b)
        if (base is Expr.Constant && exponent is Expr.Constant) {
            return Expr.Constant(base.value.pow(exponent.value))
        }

        // 指数为 0：x^0 = 1
        if (exponent.isZero()) {
            return Expr.Constant(1.0)
        }

        // 指数为 1：x^1 = x
        if (exponent.isOne()) {
            return base
        }

        // 底数为 0：0^x = 0 (x > 0)
        if (base.isZero()) {
            return Expr.Constant(0.0)
        }

        // 底数为 1：1^x = 1
        if (base.isOne()) {
            return Expr.Constant(1.0)
        }

        return Expr.Binary(base, BinaryOp.POWER, exponent)
    }

    /**
     * 化简一元运算
     */
    private fun simplifyUnary(expr: Expr.Unary): Expr {
        val operand = expr.operand

        // 常量折叠
        if (operand is Expr.Constant) {
            return when (expr.op) {
                UnaryOp.NEGATE -> Expr.Constant(-operand.value)
                UnaryOp.SIN -> Expr.Constant(sin(operand.value))
                UnaryOp.COS -> Expr.Constant(cos(operand.value))
                UnaryOp.TAN -> Expr.Constant(tan(operand.value))
                UnaryOp.LN -> Expr.Constant(ln(operand.value))
                UnaryOp.EXP -> Expr.Constant(exp(operand.value))
                UnaryOp.SQRT -> Expr.Constant(sqrt(operand.value))
                UnaryOp.ABS -> Expr.Constant(abs(operand.value))
                else -> expr
            }
        }

        // 双重负号：-(-x) = x
        if (expr.op == UnaryOp.NEGATE && operand is Expr.Unary && operand.op == UnaryOp.NEGATE) {
            return operand.operand
        }

        return expr
    }

    /**
     * 展开表达式
     * 例如：(x+1)^2 -> x^2 + 2*x + 1
     *
     * @param expr 输入表达式
     * @return 展开后的表达式
     */
    fun expand(expr: Expr): Expr {
        return when (expr) {
            is Expr.Binary -> {
                when (expr.op) {
                    BinaryOp.MULTIPLY -> expandMultiplication(expr)
                    BinaryOp.POWER -> expandPower(expr)
                    else -> {
                        val left = expand(expr.left)
                        val right = expand(expr.right)
                        Expr.Binary(left, expr.op, right)
                    }
                }
            }
            is Expr.Unary -> {
                val operand = expand(expr.operand)
                Expr.Unary(expr.op, operand)
            }
            else -> expr
        }
    }

    /**
     * 展开乘法：(a+b) * c = a*c + b*c
     */
    private fun expandMultiplication(expr: Expr.Binary): Expr {
        val left = expand(expr.left)
        val right = expand(expr.right)

        // (a+b) * c = a*c + b*c
        if (left is Expr.Binary && left.op == BinaryOp.ADD) {
            val ac = Expr.Binary(left.left, BinaryOp.MULTIPLY, right)
            val bc = Expr.Binary(left.right, BinaryOp.MULTIPLY, right)
            return expand(Expr.Binary(ac, BinaryOp.ADD, bc))
        }

        // c * (a+b) = c*a + c*b
        if (right is Expr.Binary && right.op == BinaryOp.ADD) {
            val ca = Expr.Binary(left, BinaryOp.MULTIPLY, right.left)
            val cb = Expr.Binary(left, BinaryOp.MULTIPLY, right.right)
            return expand(Expr.Binary(ca, BinaryOp.ADD, cb))
        }

        return Expr.Binary(left, BinaryOp.MULTIPLY, right)
    }

    /**
     * 展开幂运算（仅支持小整数幂）
     */
    private fun expandPower(expr: Expr.Binary): Expr {
        val base = expand(expr.base)
        val exponent = expr.exponent

        // 只展开小整数幂
        if (exponent is Expr.Constant && exponent.value > 0 && exponent.value <= 5 && exponent.value == exponent.value.toInt().toDouble()) {
            val n = exponent.value.toInt()
            var result = base
            repeat(n - 1) {
                result = Expr.Binary(result, BinaryOp.MULTIPLY, base)
            }
            return expand(result)
        }

        return Expr.Binary(base, BinaryOp.POWER, exponent)
    }
}