// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/LnRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 自然对数导数规则
 * d/dx(ln(x)) = 1/x
 */
class LnRule : Rule {

    override val name: String = "Natural Logarithm Derivative"

    override val descriptionKey: String = "rule_ln"

    override val priority: Int = 83

    override fun canApply(expr: Expr, varName: String): Boolean {
        // ln(x) 形式，且参数是简单变量
        return expr is Expr.Unary &&
               expr.op == UnaryOp.LN &&
               expr.operand is Expr.Variable &&
               (expr.operand as Expr.Variable).name == varName
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val ln = expr as Expr.Unary
        // d/dx(ln(x)) = 1/x
        return Expr.Binary(Expr.Constant(1.0), BinaryOp.DIVIDE, ln.operand)
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "function" to "ln",
            "result" to "1/x"
        )
    }
}