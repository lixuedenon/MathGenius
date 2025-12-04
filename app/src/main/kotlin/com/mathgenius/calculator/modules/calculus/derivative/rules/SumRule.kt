// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/SumRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 和差法则
 * d/dx(f + g) = f' + g'
 * d/dx(f - g) = f' - g'
 *
 * 例如：
 * d/dx(x^2 + 3x) = 2x + 3
 * d/dx(x^3 - 2x) = 3x^2 - 2
 */
class SumRule : Rule {

    override val name: String = "Sum Rule"

    override val descriptionKey: String = "rule_sum"

    override val priority: Int = 40

    override fun canApply(expr: Expr, varName: String): Boolean {
        // (f + g) 或 (f - g) 形式
        return expr is Expr.Binary &&
               (expr.op == BinaryOp.ADD || expr.op == BinaryOp.SUBTRACT)
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val binary = expr as Expr.Binary
        val left = binary.left
        val right = binary.right

        // 递归求导
        val leftDerivative = applyDerivative(left, varName)
        val rightDerivative = applyDerivative(right, varName)

        // f' op g' (保持原运算符)
        return Expr.Binary(
            leftDerivative,
            binary.op,
            rightDerivative
        )
    }

    /**
     * 辅助方法：对子表达式求导
     * 这里需要递归调用其他规则
     */
    private fun applyDerivative(expr: Expr, varName: String): Expr {
        // 这里暂时使用简单的递归逻辑
        // 实际应该通过 DerivativeEngine 的 differentiate 方法
        // 为了避免循环依赖，这里先做简化处理

        return when (expr) {
            is Expr.Constant -> Expr.Constant(0.0)
            is Expr.Variable -> {
                if (expr.name == varName) Expr.Constant(1.0)
                else Expr.Constant(0.0)
            }
            is Expr.Binary -> {
                when (expr.op) {
                    BinaryOp.POWER -> {
                        // 简单幂法则
                        if (expr.left is Expr.Variable &&
                            (expr.left as Expr.Variable).name == varName) {
                            val n = expr.right
                            val nMinus1 = Expr.Binary(n, BinaryOp.SUBTRACT, Expr.Constant(1.0))
                            Expr.Binary(
                                n,
                                BinaryOp.MULTIPLY,
                                Expr.Binary(expr.left, BinaryOp.POWER, nMinus1)
                            )
                        } else {
                            Expr.Constant(0.0)
                        }
                    }
                    BinaryOp.ADD, BinaryOp.SUBTRACT -> {
                        Expr.Binary(
                            applyDerivative(expr.left, varName),
                            expr.op,
                            applyDerivative(expr.right, varName)
                        )
                    }
                    else -> expr // 暂不处理其他情况
                }
            }
            else -> expr
        }
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val binary = expr as Expr.Binary

        return mapOf(
            "left" to binary.left.toString(),
            "right" to binary.right.toString(),
            "operator" to binary.op.symbol,
            "result" to result.toString()
        )
    }
}