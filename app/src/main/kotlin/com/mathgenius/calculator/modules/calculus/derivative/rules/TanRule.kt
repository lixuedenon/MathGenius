// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/TanRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 正切函数导数规则
 * d/dx(tan(x)) = sec^2(x) = 1/cos^2(x)
 */
class TanRule : Rule {

    override val name: String = "Tangent Derivative"

    override val descriptionKey: String = "rule_tan"

    override val priority: Int = 82

    override fun canApply(expr: Expr, varName: String): Boolean {
        // tan(x) 形式，且参数是简单变量
        return expr is Expr.Unary &&
               expr.op == UnaryOp.TAN &&
               expr.operand is Expr.Variable &&
               (expr.operand as Expr.Variable).name == varName
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val tan = expr as Expr.Unary

        // d/dx(tan(x)) = sec^2(x) = 1/cos^2(x)
        val cos = Expr.Unary(UnaryOp.COS, tan.operand)
        val cos2 = Expr.Binary(cos, BinaryOp.POWER, Expr.Constant(2.0))

        return Expr.Binary(Expr.Constant(1.0), BinaryOp.DIVIDE, cos2)
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "function" to "tan",
            "result" to "sec²"
        )
    }
}