// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/PowerRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 幂法则
 * d/dx(x^n) = n * x^(n-1)
 *
 * 例如：
 * d/dx(x^2) = 2*x
 * d/dx(x^3) = 3*x^2
 * d/dx(x^(-1)) = -x^(-2)
 */
class PowerRule : Rule {

    override val name: String = "Power Rule"

    override val descriptionKey: String = "rule_power"

    override val priority: Int = 30

    override fun canApply(expr: Expr, varName: String): Boolean {
        // x^n 形式
        if (expr is Expr.Binary && expr.op == BinaryOp.POWER) {
            val base = expr.left
            val exponent = expr.right

            // 底数必须是求导变量
            // 指数可以是任意不包含求导变量的表达式
            return base is Expr.Variable &&
                   base.name == varName &&
                   !exponent.contains(varName)
        }

        return false
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val power = expr as Expr.Binary
        val base = power.left as Expr.Variable
        val exponent = power.right

        // d/dx(x^n) = n * x^(n-1)

        // 计算 n - 1
        val newExponent = Expr.Binary(
            exponent,
            BinaryOp.SUBTRACT,
            Expr.Constant(1.0)
        )

        // 计算 x^(n-1)
        val newPower = Expr.Binary(
            base,
            BinaryOp.POWER,
            newExponent
        )

        // 计算 n * x^(n-1)
        return Expr.Binary(
            exponent,
            BinaryOp.MULTIPLY,
            newPower
        )
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val power = expr as Expr.Binary
        val exponent = power.right

        return mapOf(
            "base" to "x",
            "exponent" to exponent.toString(),
            "result" to result.toString()
        )
    }
}