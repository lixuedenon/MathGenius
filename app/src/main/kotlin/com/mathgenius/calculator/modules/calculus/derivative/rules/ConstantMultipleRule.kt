// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/ConstantMultipleRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 常数倍法则
 *
 * 数学公式: d/dx[c*f(x)] = c*f'(x)
 * 其中 c 是常数, f(x) 是关于 x 的函数
 *
 * 示例:
 * - d/dx(2*x^5) = 2*5*x^4 = 10*x^4
 * - d/dx(3*sin(x)) = 3*cos(x)
 * - d/dx(5*(x^2+x)) = 5*(2*x+1)
 */
class ConstantMultipleRule : Rule {

    override val name: String = "Constant Multiple Rule"

    override val descriptionKey: String = "rule_constant_multiple"

    override val priority: Int = 35

    /**
     * 判断规则是否适用
     * 条件: 表达式是乘法,且一边是常数,另一边包含求导变量
     */
    override fun canApply(expr: Expr, varName: String): Boolean {
        if (expr !is Expr.Binary || expr.op != BinaryOp.MULTIPLY) {
            return false
        }

        val left = expr.left
        val right = expr.right

        if (!left.contains(varName) && right.contains(varName)) {
            return true
        }

        if (left.contains(varName) && !right.contains(varName)) {
            return true
        }

        return false
    }

    /**
     * 应用常数倍法则
     */
    override fun apply(expr: Expr, varName: String): Expr {
        val multiply = expr as Expr.Binary
        val left = multiply.left
        val right = multiply.right

        val (constant, function) = if (!left.contains(varName)) {
            Pair(left, right)
        } else {
            Pair(right, left)
        }

        val functionDerivative = differentiateSimple(function, varName)

        return Expr.Binary(constant, BinaryOp.MULTIPLY, functionDerivative)
    }

    /**
     * 简单求导辅助方法
     * 用于对函数部分进行求导
     */
    private fun differentiateSimple(expr: Expr, varName: String): Expr {
        return when (expr) {
            is Expr.Constant -> Expr.Constant(0.0)

            is Expr.Variable -> {
                if (expr.name == varName) {
                    Expr.Constant(1.0)
                } else {
                    Expr.Constant(0.0)
                }
            }

            is Expr.Binary -> {
                when (expr.op) {
                    BinaryOp.POWER -> {
                        if (expr.left is Expr.Variable &&
                            (expr.left as Expr.Variable).name == varName &&
                            !expr.right.contains(varName)) {

                            val base = expr.left
                            val exponent = expr.right

                            val newExponent = Expr.Binary(
                                exponent,
                                BinaryOp.SUBTRACT,
                                Expr.Constant(1.0)
                            )

                            val powerPart = Expr.Binary(
                                base,
                                BinaryOp.POWER,
                                newExponent
                            )

                            return Expr.Binary(
                                exponent,
                                BinaryOp.MULTIPLY,
                                powerPart
                            )
                        } else {
                            return expr
                        }
                    }

                    BinaryOp.ADD, BinaryOp.SUBTRACT -> {
                        val leftDerivative = differentiateSimple(expr.left, varName)
                        val rightDerivative = differentiateSimple(expr.right, varName)
                        return Expr.Binary(leftDerivative, expr.op, rightDerivative)
                    }

                    else -> expr
                }
            }

            is Expr.Unary -> {
                when (expr.op) {
                    UnaryOp.SIN -> {
                        if (expr.operand is Expr.Variable &&
                            (expr.operand as Expr.Variable).name == varName) {
                            return Expr.Unary(UnaryOp.COS, expr.operand)
                        } else {
                            return expr
                        }
                    }

                    UnaryOp.COS -> {
                        if (expr.operand is Expr.Variable &&
                            (expr.operand as Expr.Variable).name == varName) {
                            return Expr.Unary(
                                UnaryOp.NEGATE,
                                Expr.Unary(UnaryOp.SIN, expr.operand)
                            )
                        } else {
                            return expr
                        }
                    }

                    else -> expr
                }
            }
        }
    }

    /**
     * 获取规则说明的参数
     */
    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val multiply = expr as Expr.Binary
        val left = multiply.left
        val right = multiply.right

        val (constant, function) = if (!left.contains("x")) {
            Pair(left, right)
        } else {
            Pair(right, left)
        }

        return mapOf(
            "constant" to constant.toString(),
            "function" to function.toString(),
            "result" to result.toString()
        )
    }
}