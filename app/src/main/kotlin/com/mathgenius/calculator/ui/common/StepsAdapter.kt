// app/src/main/kotlin/com/mathgenius/calculator/ui/common/StepsAdapter.kt
package com.mathgenius.calculator.ui.common

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.core.steps.CalculationStep
import com.mathgenius.calculator.core.steps.StepType

/**
 * 步骤列表适配器
 * 支持展开/折叠子步骤，带有平滑动画效果
 *
 * @property steps 计算步骤列表
 * @property languageManager 多语言管理器
 */
class StepsAdapter(
    private val steps: List<CalculationStep>,
    private val languageManager: LanguageManager
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    private val formatter = MathFormatter()

    /**
     * ViewHolder 内部类
     * 持有并管理单个步骤视图及其子步骤
     */
    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStepNumber: TextView = itemView.findViewById(R.id.txt_step_number)
        val txtStepTitle: TextView = itemView.findViewById(R.id.txt_step_title)
        val txtStepExplanation: TextView = itemView.findViewById(R.id.txt_step_explanation)
        val txtStepExpression: TextView = itemView.findViewById(R.id.txt_step_expression)
        val imgExpand: ImageView = itemView.findViewById(R.id.img_expand)
        val layoutSubsteps: LinearLayout = itemView.findViewById(R.id.layout_substeps)
        val layoutMainStep: LinearLayout = itemView.findViewById(R.id.layout_main_step)

        private var isExpanded = false

        /**
         * 绑定步骤数据到视图
         *
         * @param step 计算步骤对象
         * @param position 在列表中的位置
         */
        fun bind(step: CalculationStep, position: Int) {
            txtStepNumber.text = step.stepNumber.toString()
            txtStepTitle.text = getStepTypeDisplayName(step.stepType)
            txtStepExplanation.text = step.getLocalizedExplanation(languageManager)

            txtStepExpression.text = buildString {
                append(formatter.format(step.expressionBefore))
                append(" → ")
                append(formatter.format(step.expressionAfter))
            }

            val backgroundColor = getStepTypeColor(step.stepType)
            txtStepNumber.setBackgroundColor(itemView.context.getColor(backgroundColor))

            if (step.hasSubSteps()) {
                imgExpand.visibility = View.VISIBLE
                layoutMainStep.setOnClickListener {
                    toggleSubSteps(step)
                }
            } else {
                imgExpand.visibility = View.GONE
                layoutMainStep.setOnClickListener(null)
            }

            layoutSubsteps.removeAllViews()
        }

        /**
         * 切换子步骤的展开/折叠状态
         *
         * @param step 包含子步骤的计算步骤
         */
        private fun toggleSubSteps(step: CalculationStep) {
            isExpanded = !isExpanded

            if (isExpanded) {
                expandSubSteps(step)
            } else {
                collapseSubSteps()
            }
        }

        /**
         * 展开子步骤
         * 添加子步骤视图并播放展开动画
         *
         * @param step 包含子步骤的计算步骤
         */
        private fun expandSubSteps(step: CalculationStep) {
            layoutSubsteps.visibility = View.VISIBLE

            imgExpand.animate()
                .rotation(180f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            step.subSteps.forEach { subStep ->
                val subStepView = createSubStepView(subStep)
                layoutSubsteps.addView(subStepView)
            }

            animateExpand(layoutSubsteps)
        }

        /**
         * 折叠子步骤
         * 播放折叠动画并移除子步骤视图
         */
        private fun collapseSubSteps() {
            imgExpand.animate()
                .rotation(0f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            animateCollapse(layoutSubsteps) {
                layoutSubsteps.removeAllViews()
            }
        }

        /**
         * 创建子步骤视图
         *
         * @param subStep 子步骤对象
         * @return 子步骤的视图
         */
        private fun createSubStepView(subStep: CalculationStep): View {
            val view = LayoutInflater.from(itemView.context)
                .inflate(R.layout.item_step, layoutSubsteps, false)

            view.findViewById<TextView>(R.id.txt_step_number).text =
                "${subStep.stepNumber}"
            view.findViewById<TextView>(R.id.txt_step_title).text =
                getStepTypeDisplayName(subStep.stepType)
            view.findViewById<TextView>(R.id.txt_step_explanation).text =
                subStep.getLocalizedExplanation(languageManager)
            view.findViewById<TextView>(R.id.txt_step_expression).text = buildString {
                append(formatter.format(subStep.expressionBefore))
                append(" → ")
                append(formatter.format(subStep.expressionAfter))
            }

            view.findViewById<ImageView>(R.id.img_expand).visibility = View.GONE

            val backgroundColor = getStepTypeColor(subStep.stepType)
            view.findViewById<TextView>(R.id.txt_step_number)
                .setBackgroundColor(itemView.context.getColor(backgroundColor))

            return view
        }

        /**
         * 展开动画
         * 从高度0平滑过渡到目标高度
         *
         * @param view 要展开的视图
         */
        private fun animateExpand(view: View) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    (view.parent as View).width,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val targetHeight = view.measuredHeight

            view.layoutParams.height = 0
            view.visibility = View.VISIBLE

            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.addUpdateListener { animation ->
                view.layoutParams.height = animation.animatedValue as Int
                view.requestLayout()
            }
            animator.duration = 300
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        }

        /**
         * 折叠动画
         * 从当前高度平滑过渡到0
         *
         * @param view 要折叠的视图
         * @param onEnd 动画结束回调
         */
        private fun animateCollapse(view: View, onEnd: () -> Unit) {
            val initialHeight = view.measuredHeight

            val animator = ValueAnimator.ofInt(initialHeight, 0)
            animator.addUpdateListener { animation ->
                view.layoutParams.height = animation.animatedValue as Int
                view.requestLayout()
            }
            animator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    view.visibility = View.GONE
                    onEnd()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            animator.duration = 300
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        }

        /**
         * 获取步骤类型的显示名称
         *
         * @param stepType 步骤类型枚举
         * @return 本地化的步骤类型名称
         */
        private fun getStepTypeDisplayName(stepType: StepType): String {
            return when (stepType) {
                StepType.IDENTIFY -> languageManager.getString("step_title_identify")
                StepType.APPLY_RULE -> languageManager.getString("step_title_apply_rule")
                StepType.SIMPLIFY -> languageManager.getString("step_title_simplify")
                StepType.RESULT -> languageManager.getString("step_title_result")
                else -> stepType.toString()
            }
        }

        /**
         * 获取步骤类型对应的颜色资源ID
         *
         * @param stepType 步骤类型枚举
         * @return 颜色资源ID
         */
        private fun getStepTypeColor(stepType: StepType): Int {
            return when (stepType) {
                StepType.IDENTIFY -> R.color.step_identify
                StepType.APPLY_RULE -> R.color.step_apply_rule
                StepType.SIMPLIFY -> R.color.step_simplify
                StepType.RESULT -> R.color.step_result
                else -> R.color.primary
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position], position)
    }

    override fun getItemCount() = steps.size
}