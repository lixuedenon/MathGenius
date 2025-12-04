// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/ChainRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 链式法则
 * d/dx(f(g(x))) = f'(g(x)) * g'(x)
 *
 * 例如：
 * d/dx(sin(x^2)) = cos(x^2) * 2x
 * d/dx((x^2 + 1)^3) = 3(x^2 + 1)^2 * 2x
 */
class ChainRule : Rule {

    override val name: String = "Chain Rule"

    override val descriptionKey: String = "rule_chain"

    override val priority: Int = 70

    override fun canApply(expr: Expr, varName: String): Boolean {
        // 一元函数 f(g(x)) 形式
        if (expr is Expr.Unary) {
            val innerExpr = expr.operand
            // 内层函数不是简单的变量
            return innerExpr !is Expr.Variable && innerExpr.contains(varName)
        }

        // 幂函数 (g(x))^n 形式
        if (expr is Expr.Binary && expr.op == BinaryOp.POWER) {
            val base = expr.left
            // 底数不是简单的变量
            return base !is Expr.Variable && base.contains(varName)
        }

        return false
    }

    override fun apply(expr: Expr, varName: String): Expr {
        return when (expr) {
            is Expr.Unary -> applyToUnary(expr, varName)
            is Expr.Binary -> applyToBinary(expr, varName)
            else -> expr
        }
    }

    /**
     * 对一元函数应用链式法则
     */
    private fun applyToUnary(expr: Expr.Unary, varName: String): Expr {
        val g = expr.operand // 内层函数 g(x)
        val gPrime = differentiateSimple(g, varName) // g'(x)

        // 计算 f'(g(x))
        val fPrime = when (expr.op) {
            UnaryOp.SIN -> Expr.Unary(UnaryOp.COS, g)
            UnaryOp.COS -> Expr.Unary(UnaryOp.NEGATE, Expr.Unary(UnaryOp.SIN, g))
            UnaryOp.TAN -> {
                // sec^2(g) = 1/cos^2(g)
                val cosG = Expr.Unary(UnaryOp.COS, g)
                val cos2G = Expr.Binary(cosG, BinaryOp.POWER, Expr.Constant(2.0))
                Expr.Binary(Expr.Constant(1.0), BinaryOp.DIVIDE, cos2G)
            }
            UnaryOp.LN -> {
                // 1/g(x)
                Expr.Binary(Expr.Constant(1.0), BinaryOp.DIVIDE, g)
            }
            UnaryOp.EXP -> {
                // exp(g(x))
                Expr.Unary(UnaryOp.EXP, g)
            }
            UnaryOp.SQRT -> {
                // 1 / (2*sqrt(g))
                val denominator = Expr.Binary(
                    Expr.Constant(2.0),
                    BinaryOp.MULTIPLY,
                    Expr.Unary(UnaryOp.SQRT, g)
                )
                Expr.Binary(Expr.Constant(1.0), BinaryOp.DIVIDE, denominator)
            }
            else -> expr
        }

        // f'(g(x)) * g'(x)
        return Expr.Binary(fPrime, BinaryOp.MULTIPLY, gPrime)
    }

    /**
     * 对幂函数应用链式法则
     */
    private fun applyToBinary(expr: Expr.Binary, varName: String): Expr {
        val g = expr.left // 底数 g(x)
        val n = expr.right // 指数 n
        val gPrime = differentiateSimple(g, varName) // g'(x)

        // d/dx((g(x))^n) = n * (g(x))^(n-1) * g'(x)

        val nMinus1 = Expr.Binary(n, BinaryOp.SUBTRACT, Expr.Constant(1.0))
        val gPowerNMinus1 = Expr.Binary(g, BinaryOp.POWER, nMinus1)
        val nTimesGPower = Expr.Binary(n, BinaryOp.MULTIPLY, gPowerNMinus1)

        return Expr.Binary(nTimesGPower, BinaryOp.MULTIPLY, gPrime)
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
                    BinaryOp.ADD, BinaryOp.SUBTRACT -> {
                        Expr.Binary(
                            differentiateSimple(expr.left, varName),
                            expr.op,
                            differentiateSimple(expr.right, varName)
                        )
                    }
                    BinaryOp.MULTIPLY -> {
                        // 简单乘积法则
                        val u = expr.left
                        val v = expr.right
                        val uPrime = differentiateSimple(u, varName)
                        val vPrime = differentiateSimple(v, varName)

                        Expr.Binary(
                            Expr.Binary(uPrime, BinaryOp.MULTIPLY, v),
                            BinaryOp.ADD,
                            Expr.Binary(u, BinaryOp.MULTIPLY, vPrime)
                        )
                    }
                    else -> expr
                }
            }

            else -> expr
        }
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return when (expr) {
            is Expr.Unary -> {
                mapOf(
                    "outer_function" to expr.op.symbol,
                    "inner_function" to expr.operand.toString(),
                    "result" to result.toString()
                )
            }
            is Expr.Binary -> {
                mapOf(
                    "base" to expr.left.toString(),
                    "exponent" to expr.right.toString(),
                    "result" to result.toString()
                )
            }
            else -> emptyMap()
        }
    }
}