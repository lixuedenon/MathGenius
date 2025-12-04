// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/ConstantRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.Expr
import com.mathgenius.calculator.core.rules.Rule

/**
 * 常数规则
 * d/dx(c) = 0
 *
 * 常数的导数为零
 */
class ConstantRule : Rule {

    override val name: String = "Constant Rule"

    override val descriptionKey: String = "rule_constant"

    override val priority: Int = 10

    override fun canApply(expr: Expr, varName: String): Boolean {
        // 如果表达式是常数，或者不包含求导变量，则可以应用常数规则
        return expr is Expr.Constant || !expr.contains(varName)
    }

    override fun apply(expr: Expr, varName: String): Expr {
        return Expr.Constant(0.0)
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "constant" to expr.toString(),
            "result" to "0"
        )
    }
}