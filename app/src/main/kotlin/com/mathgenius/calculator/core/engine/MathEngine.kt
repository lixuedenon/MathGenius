// app/src/main/kotlin/com/mathgenius/calculator/core/engine/MathEngine.kt
// Kotlin Source File

package com.mathgenius.calculator.core.engine

/**
 * 数学引擎接口
 * 所有数学模块（微分、积分、矩阵等）的统一接口
 */
interface MathEngine {

    /**
     * 计算数学表达式
     *
     * @param input 输入表达式字符串
     * @return 计算结果（包含结果、步骤、格式化输出）
     */
    fun compute(input: String): ComputationResult

    /**
     * 获取支持的操作列表
     * 例如: ["derivative", "integral", "simplify"]
     *
     * @return 操作名称列表
     */
    fun getSupportedOperations(): List<String>

    /**
     * 获取引擎名称
     * 例如: "DerivativeEngine", "IntegralEngine"
     *
     * @return 引擎名称
     */
    fun getEngineName(): String

    /**
     * 验证输入是否有效
     *
     * @param input 输入表达式字符串
     * @return 验证结果（成功返回 null，失败返回错误信息）
     */
    fun validateInput(input: String): String?
}