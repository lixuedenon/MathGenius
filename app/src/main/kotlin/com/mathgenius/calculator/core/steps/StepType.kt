// app/src/main/kotlin/com/mathgenius/calculator/core/steps/StepType.kt
// Kotlin Source File

package com.mathgenius.calculator.core.steps

/**
 * 计算步骤类型枚举
 * 用于分类和标识不同类型的计算步骤
 */
enum class StepType(val displayNameKey: String) {
    /**
     * 识别表达式类型
     * 例如: "识别为多项式"
     */
    IDENTIFY("step_type_identify"),

    /**
     * 应用数学规则
     * 例如: "应用乘积法则"
     */
    APPLY_RULE("step_type_apply_rule"),

    /**
     * 展开表达式
     * 例如: "(x+1)^2 展开为 x^2 + 2x + 1"
     */
    EXPAND("step_type_expand"),

    /**
     * 化简表达式
     * 例如: "2x + 3x 化简为 5x"
     */
    SIMPLIFY("step_type_simplify"),

    /**
     * 代入数值或表达式
     * 例如: "将 x=2 代入"
     */
    SUBSTITUTE("step_type_substitute"),

    /**
     * 因式分解
     * 例如: "x^2 - 1 分解为 (x+1)(x-1)"
     */
    FACTOR("step_type_factor"),

    /**
     * 合并同类项
     * 例如: "合并 3x 和 2x"
     */
    COMBINE_LIKE_TERMS("step_type_combine_like_terms"),

    /**
     * 重新排列
     * 例如: "按降幂排列"
     */
    REARRANGE("step_type_rearrange"),

    /**
     * 最终结果
     */
    RESULT("step_type_result"),

    /**
     * 警告或注意事项
     */
    WARNING("step_type_warning"),

    /**
     * 说明或提示
     */
    INFO("step_type_info");

    companion object {
        /**
         * 从字符串获取步骤类型
         */
        fun fromString(value: String): StepType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}