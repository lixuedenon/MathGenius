// MathGenius/app/src/main/kotlin/com/mathgenius/calculator/core/steps/CalculationStep.kt
// Data Model - Calculation Step

package com.mathgenius.calculator.core.steps

import com.mathgenius.calculator.core.engine.Expr
import com.mathgenius.calculator.core.i18n.LanguageManager

data class CalculationStep(
    val stepNumber: Int,
    val stepType: StepType,
    val expressionBefore: Expr,
    val expressionAfter: Expr,
    val ruleApplied: String? = null,
    val explanationTemplateKey: String,
    val templateParams: Map<String, String> = emptyMap(),
    val subSteps: List<CalculationStep> = emptyList(),
    val level: Int = 0,
    val isExpandable: Boolean = subSteps.isNotEmpty()
) {

    fun getLocalizedExplanation(languageManager: LanguageManager): String {
        return if (templateParams.isEmpty()) {
            languageManager.getString(explanationTemplateKey)
        } else {
            val paramValues = templateParams.values.toTypedArray()
            languageManager.getFormattedString(explanationTemplateKey, *paramValues)
        }
    }

    fun hasSubSteps(): Boolean = subSteps.isNotEmpty()

    fun getSubStepCount(): Int = subSteps.size

    fun getAllStepsFlattened(): List<CalculationStep> {
        val result = mutableListOf(this)
        subSteps.forEach { subStep ->
            result.addAll(subStep.getAllStepsFlattened())
        }
        return result
    }

    fun getIndentPrefix(): String = "  ".repeat(level)

    fun toFormattedString(languageManager: LanguageManager): String {
        return buildString {
            append(getIndentPrefix())
            append("Step $stepNumber: ")
            append(getLocalizedExplanation(languageManager))
            append("\n")
            append(getIndentPrefix())
            append("  ")
            append(expressionBefore.toString())
            append(" → ")
            append(expressionAfter.toString())

            if (subSteps.isNotEmpty()) {
                append("\n")
                subSteps.forEach { subStep ->
                    append(subStep.toFormattedString(languageManager))
                }
            }
        }
    }
}