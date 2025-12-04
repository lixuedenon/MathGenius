// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/rules/ProductRule.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative.rules

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.rules.Rule

/**
 * 乘积法则
 * d/dx(u * v) = u' * v + u * v'
 * 
 * 例如：
 * d/dx(x^2 * sin(x)) = 2x * sin(x) + x^2 * cos(x)
 * d/dx(x * e^x) = 1 * e^x + x * e^x = e^x(1 + x)
 */
class ProductRule : Rule {
    
    override val name: String = "Product Rule"
    
    override val descriptionKey: String = "rule_product"
    
    override val priority: Int = 50
    
    override fun canApply(expr: Expr, varName: String): Boolean {
        // u * v 形式，且 u 和 v 都包含求导变量
        if (expr is Expr.Binary && expr.op == BinaryOp.MULTIPLY) {
            val left = expr.left
            val right = expr.right
            
            // 至少一个包含求导变量
            return left.contains(varName) || right.contains(varName)
        }
        
        return false
    }
    
    override fun apply(expr: Expr, varName: String): Expr {
        val product = expr as Expr.Binary
        val u = product.left
        val v = product.right
        
        // 计算 u' 和 v'
        val uPrime = differentiateSimple(u, varName)
        val vPrime = differentiateSimple(v, varName)
        
        // u' * v
        val leftTerm = Expr.Binary(uPrime, BinaryOp.MULTIPLY, v)
        
        // u * v'
        val rightTerm = Expr.Binary(u, BinaryOp.MULTIPLY, vPrime)
        
        // u' * v + u * v'
        return Expr.Binary(leftTerm, BinaryOp.ADD, rightTerm)
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
                            // x^n -> n*x^(n-1)
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
                    else -> expr
                }
            }
            
            is Expr.Unary -> {
                when (expr.op) {
                    UnaryOp.SIN -> {
                        // d/dx(sin(u)) = cos(u) * u'
                        val uPrime = differentiateSimple(expr.operand, varName)
                        Expr.Binary(
                            Expr.Unary(UnaryOp.COS, expr.operand),
                            BinaryOp.MULTIPLY,
                            uPrime
                        )
                    }
                    UnaryOp.COS -> {
                        // d/dx(cos(u)) = -sin(u) * u'
                        val uPrime = differentiateSimple(expr.operand, varName)
                        Expr.Binary(
                            Expr.Unary(UnaryOp.NEGATE, Expr.Unary(UnaryOp.SIN, expr.operand)),
                            BinaryOp.MULTIPLY,
                            uPrime
                        )
                    }
                    else -> expr
                }
            }
        }
    }
    
    override fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> {
        val product = expr as Expr.Binary
        val u = product.left
        val v = product.right
        
        return mapOf(
            "u" to u.toString(),
            "v" to v.toString(),
            "u_prime" to differentiateSimple(u, "x").toString(),
            "v_prime" to differentiateSimple(v, "x").toString()
        )
    }
}