// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/ExpRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 指数函数导数规则
 * d/dx(exp(x)) = exp(x)
 * d/dx(e^x) = e^x
 */
class ExpRule : Rule {

    override val name: String = "Exponential Derivative"

    override val descriptionKey: String = "rule_exp"

    override val priority: Int = 84

    override fun canApply(expr: Expr, varName: String): Boolean {
        // exp(x) 形式，且参数是简单变量
        return expr is Expr.Unary &&
               expr.op == UnaryOp.EXP &&
               expr.operand is Expr.Variable &&
               (expr.operand as Expr.Variable).name == varName
    }

    override fun apply(expr: Expr, varName: String): Expr {
        // d/dx(exp(x)) = exp(x)
        return expr
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "function" to "exp",
            "result" to "exp"
        )
    }
}