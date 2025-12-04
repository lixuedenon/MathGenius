// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/CosRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 余弦函数导数规则
 * d/dx(cos(x)) = -sin(x)
 */
class CosRule : Rule {

    override val name: String = "Cosine Derivative"

    override val descriptionKey: String = "rule_cos"

    override val priority: Int = 81

    override fun canApply(expr: Expr, varName: String): Boolean {
        // cos(x) 形式，且参数是简单变量
        return expr is Expr.Unary &&
               expr.op == UnaryOp.COS &&
               expr.operand is Expr.Variable &&
               (expr.operand as Expr.Variable).name == varName
    }

    override fun apply(expr: Expr, varName: String): Expr {
        val cos = expr as Expr.Unary
        // d/dx(cos(x)) = -sin(x)
        return Expr.Unary(UnaryOp.NEGATE, Expr.Unary(UnaryOp.SIN, cos.operand))
    }

    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        return mapOf(
            "function" to "cos",
            "result" to "-sin"
        )
    }
}