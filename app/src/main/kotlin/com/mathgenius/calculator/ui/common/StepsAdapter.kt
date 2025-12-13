// app/src/main/kotlin/com/mathgenius/calculator/ui/common/StepsAdapter.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.core.steps.CalculationStep

/**
 * 计算步骤列表适配器
 *
 * 功能：
 * - 展示计算的详细步骤
 * - 支持步骤展开/折叠
 * - 格式化数学表达式
 */
class StepsAdapter(
    private var steps: List<CalculationStep>,
    private val formatter: MathFormatter,
    private val languageManager: LanguageManager
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    /**
     * 更新步骤列表
     */
    fun updateSteps(newSteps: List<CalculationStep>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStepNumber: TextView = itemView.findViewById(R.id.tv_step_number)
        private val tvStepTitle: TextView = itemView.findViewById(R.id.tv_step_title)
        private val tvStepExpression: TextView = itemView.findViewById(R.id.tv_step_expression)
        private val tvStepExplanation: TextView = itemView.findViewById(R.id.tv_step_explanation)
        private val ivExpandIcon: ImageView = itemView.findViewById(R.id.iv_expand_icon)

        private var isExpanded = false

        fun bind(step: CalculationStep) {
            // 步骤编号
            tvStepNumber.text = step.stepNumber.toString()

            // 步骤标题（根据步骤类型显示）
            tvStepTitle.text = getStepTypeTitle(step.stepType)

            // 表达式变化：before → after
            val expressionText = buildString {
                append(formatter.format(step.expressionBefore.toString()))
                append(" → ")
                append(formatter.format(step.expressionAfter.toString()))
            }
            tvStepExpression.text = expressionText

            // 步骤说明
            val explanation = step.getLocalizedExplanation(languageManager)
            if (explanation.isNotEmpty() || step.hasSubSteps()) {
                tvStepExplanation.text = explanation
                tvStepExplanation.visibility = if (isExpanded) View.VISIBLE else View.GONE
                ivExpandIcon.visibility = View.VISIBLE

                itemView.setOnClickListener {
                    isExpanded = !isExpanded
                    tvStepExplanation.visibility = if (isExpanded) View.VISIBLE else View.GONE
                    ivExpandIcon.rotation = if (isExpanded) 180f else 0f
                }
            } else {
                tvStepExplanation.visibility = View.GONE
                ivExpandIcon.visibility = View.GONE
                itemView.setOnClickListener(null)
            }
        }

        /**
         * 根据步骤类型获取标题
         */
        private fun getStepTypeTitle(stepType: StepType): String {
            return when (stepType) {
                StepType.IDENTIFY -> itemView.context.getString(R.string.step_title_identify)
                StepType.APPLY_RULE -> itemView.context.getString(R.string.step_title_apply_rule)
                StepType.SIMPLIFY -> itemView.context.getString(R.string.step_title_simplify)
                StepType.RESULT -> itemView.context.getString(R.string.step_title_result)
            }
        }
    }
}