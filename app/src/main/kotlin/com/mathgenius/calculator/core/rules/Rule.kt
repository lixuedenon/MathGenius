// app/src/main/kotlin/com/mathgenius/calculator/core/rules/Rule.kt
// Kotlin Source File

package com.mathgenius.calculator.core.rules

import com.mathgenius.calculator.core.engine.Expr

/**
 * 数学规则接口
 * 定义数学规则的统一行为
 */
interface Rule {
    
    /**
     * 规则名称
     * 例如: "Power Rule", "Product Rule"
     */
    val name: String
    
    /**
     * 规则描述（多语言 Key）
     * 例如: "rule_power_rule_description"
     */
    val descriptionKey: String
    
    /**
     * 规则优先级
     * 数字越小，优先级越高
     * 用于决定规则的应用顺序
     */
    val priority: Int get() = 100
    
    /**
     * 判断规则是否可以应用于给定表达式
     * 
     * @param expr 待判断的表达式
     * @param varName 微分变量名（对于微分规则）
     * @return 是否可以应用
     */
    fun canApply(expr: Expr, varName: String): Boolean
    
    /**
     * 应用规则到表达式
     * 
     * @param expr 输入表达式
     * @param varName 微分变量名（对于微分规则）
     * @return 应用规则后的表达式
     */
    fun apply(expr: Expr, varName: String): Expr
    
    /**
     * 获取规则应用的详细说明参数
     * 用于生成步骤说明
     * 
     * @param expr 输入表达式
     * @param result 结果表达式
     * @return 模板参数映射
     */
    fun getExplanationParams(expr: Expr, result: Expr): Map<String, String> = emptyMap()
}