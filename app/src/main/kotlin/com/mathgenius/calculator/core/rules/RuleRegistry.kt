// app/src/main/kotlin/com/mathgenius/calculator/core/rules/RuleRegistry.kt
// Kotlin Source File

package com.mathgenius.calculator.core.rules

import com.mathgenius.calculator.core.engine.Expr

/**
 * 规则注册中心
 * 管理所有数学规则的注册、查询和应用
 */
class RuleRegistry {
    
    /**
     * 已注册的规则列表
     * 按优先级排序
     */
    private val rules = mutableListOf<Rule>()
    
    /**
     * 规则名称到规则对象的映射
     */
    private val rulesByName = mutableMapOf<String, Rule>()
    
    /**
     * 注册一个规则
     * 
     * @param rule 要注册的规则
     */
    fun register(rule: Rule) {
        if (!rulesByName.containsKey(rule.name)) {
            rules.add(rule)
            rulesByName[rule.name] = rule
            
            // 按优先级排序
            rules.sortBy { it.priority }
        }
    }
    
    /**
     * 批量注册规则
     * 
     * @param rules 规则列表
     */
    fun registerAll(rules: List<Rule>) {
        rules.forEach { register(it) }
    }
    
    /**
     * 根据名称获取规则
     * 
     * @param name 规则名称
     * @return 规则对象，如果不存在则返回 null
     */
    fun getRule(name: String): Rule? = rulesByName[name]
    
    /**
     * 查找第一个可以应用于表达式的规则
     * 
     * @param expr 表达式
     * @param varName 变量名
     * @return 可应用的规则，如果没有则返回 null
     */
    fun findApplicableRule(expr: Expr, varName: String): Rule? {
        return rules.firstOrNull { it.canApply(expr, varName) }
    }
    
    /**
     * 查找所有可以应用于表达式的规则
     * 
     * @param expr 表达式
     * @param varName 变量名
     * @return 可应用的规则列表
     */
    fun findAllApplicableRules(expr: Expr, varName: String): List<Rule> {
        return rules.filter { it.canApply(expr, varName) }
    }
    
    /**
     * 获取所有已注册的规则
     * 
     * @return 规则列表
     */
    fun getAllRules(): List<Rule> = rules.toList()
    
    /**
     * 获取已注册规则的数量
     */
    fun getRuleCount(): Int = rules.size
    
    /**
     * 清空所有规则
     */
    fun clear() {
        rules.clear()
        rulesByName.clear()
    }
}