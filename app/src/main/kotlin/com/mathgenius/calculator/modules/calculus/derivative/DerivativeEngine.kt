// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/DerivativeEngine.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.steps.*
import com.mathgenius.calculator.core.rules.RuleRegistry
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.modules.calculus.derivative.rules.*

/**
 * 微分引擎
 * 负责计算函数的导数并生成详细步骤
 */
class DerivativeEngine(
    private val languageManager: LanguageManager,
    private val formatter: MathFormatter = MathFormatter()
) : MathEngine {

    /**
     * 规则注册中心
     */
    private val ruleRegistry = RuleRegistry()

    /**
     * 步骤追踪器
     */
    private val stepTracker = StepTracker()

    /**
     * 表达式解析器
     */
    private val parser = ExpressionParser()

    /**
     * 代数化简器
     */
    private val simplifier = Simplifier()

    init {
        // 注册所有微分规则（按优先级排序）
        registerDerivativeRules()
    }

    /**
     * 注册微分规则
     */
    private fun registerDerivativeRules() {
        ruleRegistry.registerAll(listOf(
            ConstantRule(),           // 常数规则
            VariableRule(),           // 变量规则
            PowerRule(),              // 幂法则
            SumRule(),                // 和差法则
            ProductRule(),            // 乘积法则
            QuotientRule(),           // 商法则
            ChainRule(),              // 链式法则
            SinRule(),                // sin 导数
            CosRule(),                // cos 导数
            TanRule(),                // tan 导数
            LnRule(),                 // ln 导数
            ExpRule()                 // exp 导数
        ))
    }

    override fun compute(input: String): ComputationResult {
        val startTime = System.currentTimeMillis()

        try {
            // 1. 清空步骤追踪器
            stepTracker.clear()

            // 2. 解析输入表达式
            val expr = parser.parse(input)

            // 3. 记录初始步骤
            stepTracker.addSimpleStep(
                stepType = StepType.IDENTIFY,
                expr = expr,
                templateKey = "step_identify_function",
                params = mapOf("function" to formatter.format(expr))
            )

            // 4. 计算导数（默认对 x 求导）
            val derivative = differentiate(expr, "x")

            // 5. 化简结果
            val simplified = simplifier.simplify(derivative)

            // 6. 记录最终结果
            stepTracker.addStep(
                stepType = StepType.RESULT,
                exprBefore = derivative,
                exprAfter = simplified,
                templateKey = "step_final_result",
                params = mapOf("result" to formatter.format(simplified))
            )

            // 7. 计算耗时
            val elapsedTime = System.currentTimeMillis() - startTime

            // 8. 返回结果
            return ComputationResult(
                result = simplified,
                steps = stepTracker.getSteps(),
                formattedResult = formatter.format(simplified),
                success = true,
                computationTimeMs = elapsedTime
            )

        } catch (e: Exception) {
            return ComputationResult.failure("计算失败: ${e.message}")
        }
    }

    /**
     * 对表达式求导
     *
     * @param expr 输入表达式
     * @param varName 求导变量名
     * @return 导数表达式
     */
    private fun differentiate(expr: Expr, varName: String): Expr {
        // 查找适用的规则
        val rule = ruleRegistry.findApplicableRule(expr, varName)
            ?: throw IllegalStateException("No applicable rule found for expression: $expr")

        // 应用规则前的表达式
        val exprBefore = expr

        // 应用规则
        val result = rule.apply(expr, varName)

        // 记录步骤
        stepTracker.addStep(
            stepType = StepType.APPLY_RULE,
            exprBefore = exprBefore,
            exprAfter = result,
            ruleName = rule.name,
            templateKey = rule.descriptionKey,
            params = rule.getExplanationParams(expr, result)
        )

        return result
    }

    override fun getSupportedOperations(): List<String> {
        return listOf(
            "derivative",
            "differentiate",
            "d/dx"
        )
    }

    override fun getEngineName(): String = "DerivativeEngine"

    override fun validateInput(input: String): String? {
        return try {
            parser.parse(input)
            null // 验证成功
        } catch (e: ParseException) {
            e.message
        } catch (e: Exception) {
            "无效的输入表达式"
        }
    }

    /**
     * 批量求导（支持高阶导数）
     *
     * @param input 输入表达式字符串
     * @param varName 求导变量
     * @param order 导数阶数
     * @return 计算结果
     */
    fun computeHigherOrder(input: String, varName: String = "x", order: Int = 1): ComputationResult {
        if (order < 1) {
            return ComputationResult.failure("导数阶数必须大于 0")
        }

        var current = parser.parse(input)

        repeat(order) { i ->
            stepTracker.addSimpleStep(
                stepType = StepType.INFO,
                expr = current,
                templateKey = "step_computing_order",
                params = mapOf("order" to (i + 1).toString())
            )

            current = differentiate(current, varName)
            current = simplifier.simplify(current)
        }

        return ComputationResult(
            result = current,
            steps = stepTracker.getSteps(),
            formattedResult = formatter.format(current),
            success = true
        )
    }
}