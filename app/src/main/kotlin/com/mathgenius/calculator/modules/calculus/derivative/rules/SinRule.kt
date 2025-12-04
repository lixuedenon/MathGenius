// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/SinRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 正弦函数导数规则
 * d/dx(sin(x)) = cos(x)
 */
class SinRule : Rule {

    override val name: String = "Sine Derivative"

    override val descriptionKey: String = "rule_sin"

    override val priority: Int = 80

    override fun canApply(expr: Expr, varName: String): Boolean {
        // sin(x) 形式，且参数是简单变量
        return expr is Expr.Unary &&
               expr.op == UnaryOp.SIN &&
               expr.operand is Expr.Variable &&
               (expr.operand as Expr.Variable).name == varName
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val sin = expr as Expr.Unary
        // d/dx(sin(x)) = cos(x)
        return Expr.Unary(UnaryOp.COS, sin.operand)
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "function" to "sin",
            "result" to "cos"
        )
    }
}