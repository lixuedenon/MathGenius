// app/src/main/kotlin/com/mathgenius/calculator/core/engine/ExpressionTree.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

/**
 * 表达式抽象语法树 (AST)
 * 所有数学表达式的统一表示形式
 */
sealed class Expr {

    /**
     * 常量节点
     * 例如: 5, 3.14, -2
     */
    data class Constant(val value: Double) : Expr() {
        override fun toString(): String = value.toString()
    }

    /**
     * 变量节点
     * 例如: x, y, t
     */
    data class Variable(val name: String) : Expr() {
        override fun toString(): String = name
    }

    /**
     * 二元运算节点
     * 例如: x + 2, 3 * x, x^2
     */
    data class Binary(
        val left: Expr,
        val op: BinaryOp,
        val right: Expr
    ) : Expr() {
        override fun toString(): String = when (op) {
            BinaryOp.ADD -> "($left + $right)"
            BinaryOp.SUBTRACT -> "($left - $right)"
            BinaryOp.MULTIPLY -> "($left * $right)"
            BinaryOp.DIVIDE -> "($left / $right)"
            BinaryOp.POWER -> "($left^$right)"
        }
    }

    /**
     * 一元运算节点
     * 例如: sin(x), ln(x), -x
     */
    data class Unary(
        val op: UnaryOp,
        val operand: Expr
    ) : Expr() {
        override fun toString(): String = "${op.symbol}($operand)"
    }

    /**
     * 判断表达式是否为零
     */
    fun isZero(): Boolean = this is Constant && value == 0.0

    /**
     * 判断表达式是否为一
     */
    fun isOne(): Boolean = this is Constant && value == 1.0

    /**
     * 判断表达式是否包含指定变量
     */
    fun contains(varName: String): Boolean = when (this) {
        is Constant -> false
        is Variable -> name == varName
        is Binary -> left.contains(varName) || right.contains(varName)
        is Unary -> operand.contains(varName)
    }

    /**
     * 深度克隆表达式
     */
    fun clone(): Expr = when (this) {
        is Constant -> Constant(value)
        is Variable -> Variable(name)
        is Binary -> Binary(left.clone(), op, right.clone())
        is Unary -> Unary(op, operand.clone())
    }
}

/**
 * 二元运算符
 */
enum class BinaryOp(val symbol: String, val precedence: Int) {
    ADD("+", 1),
    SUBTRACT("-", 1),
    MULTIPLY("*", 2),
    DIVIDE("/", 2),
    POWER("^", 3);

    companion object {
        fun fromSymbol(symbol: String): BinaryOp? = values().find { it.symbol == symbol }
    }
}

/**
 * 一元运算符
 */
enum class UnaryOp(val symbol: String) {
    // 三角函数
    SIN("sin"),
    COS("cos"),
    TAN("tan"),
    COT("cot"),
    SEC("sec"),
    CSC("csc"),

    // 反三角函数
    ASIN("arcsin"),
    ACOS("arccos"),
    ATAN("arctan"),

    // 对数指数
    LN("ln"),
    LOG("log"),
    EXP("exp"),

    // 其他
    SQRT("sqrt"),
    ABS("abs"),
    NEGATE("-");

    companion object {
        fun fromSymbol(symbol: String): UnaryOp? = values().find { it.symbol == symbol }
    }
}