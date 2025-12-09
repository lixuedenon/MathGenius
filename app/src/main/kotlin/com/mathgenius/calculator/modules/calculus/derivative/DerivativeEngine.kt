// app/src/main/kotlin/com/mathgenius/calculator/modules/calculus/derivative/DerivativeEngine.kt
// Kotlin Source File

package com.mathgenius.calculator.modules.calculus.derivative

import com.mathgenius.calculator.core.engine.*
import com.mathgenius.calculator.core.steps.*
import com.mathgenius.calculator.core.rules.RuleRegistry
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.modules.calculus.derivative.rules.*
import android.util.Log

/**
 * 微分引擎
 * 负责计算函数的导数并生成详细步骤
 */
class DerivativeEngine(
    private val languageManager: LanguageManager,
    private val formatter: MathFormatter = MathFormatter()
) : MathEngine {

    companion object {
        private const val TAG = "DerivativeEngine"
    }

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
        Log.d(TAG, "=== Initializing DerivativeEngine ===")
        registerDerivativeRules()
        Log.d(TAG, "Registered ${ruleRegistry.getAllRules().size} rules")
    }

    /**
     * 注册微分规则
     */
    private fun registerDerivativeRules() {
        Log.d(TAG, "Registering derivative rules...")

        ruleRegistry.registerAll(listOf(
            ConstantRule(),           // 优先级 10 - 常数规则
            VariableRule(),           // 优先级 20 - 变量规则
            PowerRule(),              // 优先级 30 - 幂法则
            ConstantMultipleRule(),   // 优先级 35 - 常数倍法则 (新增!)
            SumRule(),                // 优先级 40 - 和差法则
            ProductRule(),            // 优先级 50 - 乘积法则
            QuotientRule(),           // 优先级 60 - 商法则
            ChainRule(),              // 优先级 70 - 链式法则
            SinRule(),                // 优先级 80 - sin 导数
            CosRule(),                // 优先级 81 - cos 导数
            TanRule(),                // 优先级 82 - tan 导数
            LnRule(),                 // 优先级 83 - ln 导数
            ExpRule()                 // 优先级 84 - exp 导数
        ))

        Log.d(TAG, "Rules registered successfully")
    }

    override fun compute(input: String): ComputationResult {
        val startTime = System.currentTimeMillis()

        Log.d(TAG, "=== compute() called ===")
        Log.d(TAG, "Input: '$input'")

        try {
            stepTracker.clear()

            Log.d(TAG, "Parsing expression...")
            val expr = parser.parse(input)
            Log.d(TAG, "Parsed expression: $expr")

            stepTracker.addSimpleStep(
                stepType = StepType.IDENTIFY,
                expr = expr,
                templateKey = "step_identify_function",
                params = mapOf("function" to formatter.format(expr))
            )

            Log.d(TAG, "Computing derivative...")
            val derivative = differentiate(expr, "x")
            Log.d(TAG, "Derivative: $derivative")

            Log.d(TAG, "Simplifying...")
            val simplified = simplifier.simplify(derivative)
            Log.d(TAG, "Simplified: $simplified")

            stepTracker.addStep(
                stepType = StepType.RESULT,
                exprBefore = derivative,
                exprAfter = simplified,
                templateKey = "step_final_result",
                params = mapOf("result" to formatter.format(simplified))
            )

            val elapsedTime = System.currentTimeMillis() - startTime
            val formattedResult = formatter.format(simplified)

            Log.d(TAG, "Formatted result: $formattedResult")
            Log.d(TAG, "Computation time: ${elapsedTime}ms")
            Log.d(TAG, "=== compute() success ===")

            return ComputationResult(
                result = simplified,
                steps = stepTracker.getSteps(),
                formattedResult = formattedResult,
                success = true,
                computationTimeMs = elapsedTime
            )

        } catch (e: Exception) {
            Log.e(TAG, "=== compute() failed ===", e)
            Log.e(TAG, "Error message: ${e.message}")
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
        Log.d(TAG, "  differentiate() - expr: $expr, varName: $varName")

        val rule = ruleRegistry.findApplicableRule(expr, varName)

        if (rule == null) {
            Log.e(TAG, "  No applicable rule found for: $expr")
            throw IllegalStateException("No applicable rule found for expression: $expr")
        }

        Log.d(TAG, "  Applying rule: ${rule.name} (priority: ${rule.priority})")

        val exprBefore = expr
        val result = rule.apply(expr, varName)

        Log.d(TAG, "  Result after rule: $result")

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
            null
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