// app/src/main/kotlin/com/mathgenius/calculator/ui/common/CalculatorButton.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.common

/**
 * 计算器按钮数据模型
 *
 * @property label 按钮显示文本
 * @property type 按钮类型
 * @property value 按钮值 (传递给 ExpressionManager)
 * @property description 按钮描述 (用于无障碍访问)
 */
data class CalculatorButton(
    val label: String,
    val type: KeyType,
    val value: String,
    val description: String = label
)