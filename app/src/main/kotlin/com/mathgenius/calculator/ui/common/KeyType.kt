// app/src/main/kotlin/com/mathgenius/calculator/ui/common/KeyType.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.common

/**
 * 计算器按键类型枚举
 * 定义所有可能的按键类型
 */
enum class KeyType {
    /**
     * 数字键 (0-9)
     */
    NUMBER,

    /**
     * 小数点
     */
    DECIMAL,

    /**
     * 变量 (x, y, z)
     */
    VARIABLE,

    /**
     * 运算符 (+, -, ×, ÷)
     */
    OPERATOR,

    /**
     * 函数 (sin, cos, tan, ln, exp, log, sqrt, abs)
     */
    FUNCTION,

    /**
     * 幂运算 (x², xⁿ)
     */
    POWER,

    /**
     * 括号 (, )
     */
    PARENTHESIS,

    /**
     * 数学常数 (π, e)
     */
    CONSTANT,

    /**
     * 退格键
     */
    BACKSPACE,

    /**
     * 清除键
     */
    CLEAR,

    /**
     * 确认键
     */
    CONFIRM
}