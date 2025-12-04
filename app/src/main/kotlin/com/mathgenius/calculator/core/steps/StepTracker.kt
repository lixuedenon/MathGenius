// app/src/main/kotlin/com/mathgenius/calculator/core/steps/StepTracker.kt
// Kotlin Source File

package com.mathgenius.calculator.core.steps

import com.mathgenius.calculator.core.engine.Expr

/**
 * 步骤追踪器
 * 负责记录计算过程中的所有步骤
 */
class StepTracker {
    
    /**
     * 步骤列表
     */
    private val steps = mutableListOf<CalculationStep>()
    
    /**
     * 当前步骤编号
     */
    private var currentStepNumber = 0
    
    /**
     * 当前步骤级别（用于嵌套步骤）
     */
    private var currentLevel = 0
    
    /**
     * 添加一个步骤
     * 
     * @param stepType 步骤类型
     * @param exprBefore 步骤前的表达式
     * @param exprAfter 步骤后的表达式
     * @param ruleName 应用的规则名称
     * @param templateKey 解释模板的资源 Key
     * @param params 模板参数
     * @param subSteps 子步骤列表
     * @param note 额外注释
     */
    fun addStep(
        stepType: StepType,
        exprBefore: Expr,
        exprAfter: Expr,
        ruleName: String? = null,
        templateKey: String,
        params: Map<String, String> = emptyMap(),
        subSteps: List<CalculationStep> = emptyList(),
        note: String? = null
    ) {
        currentStepNumber++
        
        val step = CalculationStep(
            stepNumber = currentStepNumber,
            stepType = stepType,
            expressionBefore = exprBefore,
            expressionAfter = exprAfter,
            ruleApplied = ruleName,
            explanationTemplateKey = templateKey,
            templateParams = params,
            subSteps = subSteps,
            level = currentLevel,
            note = note
        )
        
        steps.add(step)
    }
    
    /**
     * 添加一个简单步骤（不需要表达式前后对比）
     * 
     * @param stepType 步骤类型
     * @param expr 当前表达式
     * @param templateKey 解释模板的资源 Key
     * @param params 模板参数
     */
    fun addSimpleStep(
        stepType: StepType,
        expr: Expr,
        templateKey: String,
        params: Map<String, String> = emptyMap()
    ) {
        addStep(
            stepType = stepType,
            exprBefore = expr,
            exprAfter = expr,
            templateKey = templateKey,
            params = params
        )
    }
    
    /**
     * 开始一个子步骤组
     * 增加步骤级别
     */
    fun beginSubSteps() {
        currentLevel++
    }
    
    /**
     * 结束子步骤组
     * 减少步骤级别
     */
    fun endSubSteps() {
        if (currentLevel > 0) {
            currentLevel--
        }
    }
    
    /**
     * 获取所有步骤
     * 
     * @return 步骤列表的只读副本
     */
    fun getSteps(): List<CalculationStep> = steps.toList()
    
    /**
     * 获取步骤数量
     */
    fun getStepCount(): Int = steps.size
    
    /**
     * 清空所有步骤
     */
    fun clear() {
        steps.clear()
        currentStepNumber = 0
        currentLevel = 0
    }
    
    /**
     * 判断是否有步骤
     */
    fun hasSteps(): Boolean = steps.isNotEmpty()
}