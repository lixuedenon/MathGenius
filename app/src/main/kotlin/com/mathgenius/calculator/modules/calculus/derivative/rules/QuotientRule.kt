// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/QuotientRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 商法则
 * d/dx(u / v) = (u' * v - u * v') / v^2
 *
 * 例如：
 * d/dx(x^2 / x) = (2x * x - x^2 * 1) / x^2 = x / x^2 = 1/x
 * d/dx(sin(x) / x) = (cos(x) * x - sin(x) * 1) / x^2
 */
class QuotientRule : Rule {

    override val name: String = "Quotient Rule"

    override val descriptionKey: String = "rule_quotient"

    override val priority: Int = 60

    override fun canApply(expr: Expr, varName: String): Boolean {
        // u / v 形式，且至少一个包含求导变量
        if (expr is Expr.Binary && expr.op == BinaryOp.DIVIDE) {
            val numerator = expr.left
            val denominator = expr.right

            return numerator.contains(varName) || denominator.contains(varName)
        }

        return false
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val quotient = expr as Expr.Binary
        val u = quotient.left
        val v = quotient.right

        // 计算 u' 和 v'
        val uPrime = differentiateSimple(u, varName)
        val vPrime = differentiateSimple(v, varName)

        // u' * v
        val uPrimeV = Expr.Binary(uPrime, BinaryOp.MULTIPLY, v)

        // u * v'
        val uVPrime = Expr.Binary(u, BinaryOp.MULTIPLY, vPrime)

        // u' * v - u * v'
        val numerator = Expr.Binary(uPrimeV, BinaryOp.SUBTRACT, uVPrime)

        // v^2
        val denominator = Expr.Binary(v, BinaryOp.POWER, Expr.Constant(2.0))

        // (u' * v - u * v') / v^2
        return Expr.Binary(numerator, BinaryOp.DIVIDE, denominator)
    }

    /**
     * 简单求导辅助方法
     */
    private fun differentiateSimple(expr: Expr, varName: String): Expr {
        return when (expr) {
            is Expr.Constant -> Expr.Constant(0.0)

            is Expr.Variable -> {
                if (expr.name == varName) Expr.Constant(1.0)
                else Expr.Constant(0.0)
            }

            is Expr.Binary -> {
                when (expr.op) {
                    BinaryOp.POWER -> {
                        if (expr.left is Expr.Variable &&
                            (expr.left as Expr.Variable).name == varName &&
                            !expr.right.contains(varName)) {
                            val n = expr.right
                            val nMinus1 = Expr.Binary(n, BinaryOp.SUBTRACT, Expr.Constant(1.0))
                            Expr.Binary(
                                n,
                                BinaryOp.MULTIPLY,
                                Expr.Binary(expr.left, BinaryOp.POWER, nMinus1)
                            )
                        } else {
                            expr
                        }
                    }
                    else -> expr
                }
            }

            else -> expr
        }
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val quotient = expr as Expr.Binary
        val u = quotient.left
        val v = quotient.right

        return mapOf(
            "u" to u.toString(),
            "v" to v.toString(),
            "u_prime" to differentiateSimple(u, "x").toString(),
            "v_prime" to differentiateSimple(v, "x").toString()
        )
    }
}