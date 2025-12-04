// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/VariableRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.Expr
import com.mathgenius.calculator.core.rules.Rule

/**
 * 变量规则
 * d/dx(x) = 1
 * d/dx(y) = 0 (y 是常数)
 *
 * 对求导变量本身求导结果为 1
 * 对其他变量求导结果为 0
 */
class VariableRule : Rule {

    override val name: String = "Variable Rule"

    override val descriptionKey: String = "rule_variable"

    override val priority: Int = 20

    override fun canApply(expr: Expr, varName: String): Boolean {
        return expr is Expr.Variable
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val variable = expr as Expr.Variable

        // 如果是求导变量，导数为 1
        // 否则视为常数，导数为 0
        return if (variable.name == varName) {
            Expr.Constant(1.0)
        } else {
            Expr.Constant(0.0)
        }
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val variable = expr as Expr.Variable
        return mapOf(
            "variable" to variable.name,
            "result" to result.toString()
        )
    }
}