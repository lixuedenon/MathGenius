// app/src/main/kotlin/com/mathgenius/calculator/core/engine/ComputationResult.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

import com.mathgenius.calculator.core.steps.CalculationStep

/**
 * 计算结果数据模型
 * 包含最终结果、详细步骤和格式化输出
 */
data class ComputationResult(
    /**
     * 计算结果表达式
     */
    val result: Expr,

    /**
     * 详细计算步骤列表
     */
    val steps: List<CalculationStep>,

    /**
     * 格式化后的结果字符串
     * 用于 UI 显示
     */
    val formattedResult: String,

    /**
     * 计算是否成功
     */
    val success: Boolean = true,

    /**
     * 错误信息（如果失败）
     */
    val errorMessage: String? = null,

    /**
     * 计算耗时（毫秒）
     */
    val computationTimeMs: Long = 0
) {

    /**
     * 判断是否有步骤
     */
    fun hasSteps(): Boolean = steps.isNotEmpty()

    /**
     * 获取步骤数量
     */
    fun getStepCount(): Int = steps.size

    companion object {
        /**
         * 创建失败结果
         */
        fun failure(errorMessage: String): ComputationResult {
            return ComputationResult(
                result = Expr.Constant(0.0),
                steps = emptyList(),
                formattedResult = "",
                success = false,
                errorMessage = errorMessage
            )
        }
    }
}