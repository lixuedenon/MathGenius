// app/src/main/kotlin/com/mathgenius/calculator/ui/result/ResultActivity.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.result

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.modules.calculus.derivative.DerivativeEngine
import com.mathgenius.calculator.ui.calculus.CalculusActivity
import com.mathgenius.calculator.ui.common.StepsAdapter

/**
 * 计算结果展示页面
 *
 * 功能：
 * - 接收表达式和计算类型
 * - 执行微分/积分计算
 * - 展示计算结果和详细步骤
 * - 支持结果复制和分享
 */
class ResultActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ResultActivity"
    }

    private lateinit var languageManager: LanguageManager
    private lateinit var formatter: MathFormatter
    private lateinit var derivativeEngine: DerivativeEngine

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private lateinit var rvSteps: RecyclerView
    private lateinit var stepsAdapter: StepsAdapter

    private var calculationType: String = ""
    private var expression: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        languageManager = LanguageManager(this)
        formatter = MathFormatter()
        derivativeEngine = DerivativeEngine(languageManager, formatter)

        calculationType = intent.getStringExtra(CalculusActivity.EXTRA_CALCULATION_TYPE) ?: ""
        expression = intent.getStringExtra(CalculusActivity.EXTRA_EXPRESSION) ?: ""

        initViews()
        setupToolbar()
        setupRecyclerView()
        performCalculation()
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        tvExpression = findViewById(R.id.tv_expression)
        tvResult = findViewById(R.id.tv_result)
        rvSteps = findViewById(R.id.rv_steps)
    }

    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when (calculationType) {
            CalculusActivity.TYPE_DERIVATIVE -> getString(R.string.module_calculus_derivative)
            CalculusActivity.TYPE_INTEGRAL -> getString(R.string.module_calculus_integral)
            else -> getString(R.string.result)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * 设置步骤列表
     */
    private fun setupRecyclerView() {
        stepsAdapter = StepsAdapter(emptyList(), formatter, languageManager)
        rvSteps.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = stepsAdapter
        }
    }

    /**
     * 执行计算
     */
    private fun performCalculation() {
        if (expression.isEmpty()) {
            showError(getString(R.string.error_empty_expression))
            return
        }

        tvExpression.text = expression

        try {
            when (calculationType) {
                CalculusActivity.TYPE_DERIVATIVE -> calculateDerivative()
                CalculusActivity.TYPE_INTEGRAL -> calculateIntegral()
                else -> showError(getString(R.string.error_unknown))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Calculation error", e)
            showError(getString(R.string.error_calculation_failed) + ": ${e.message}")
        }
    }

    /**
     * 计算导数
     */
    private fun calculateDerivative() {
        val result = derivativeEngine.compute(expression)

        if (result.success) {
            tvResult.text = result.formattedResult
            tvResult.visibility = View.VISIBLE

            if (result.hasSteps()) {
                stepsAdapter.updateSteps(result.steps)
                rvSteps.visibility = View.VISIBLE
            }

            Toast.makeText(
                this,
                getString(R.string.calculation_complete, result.computationTimeMs),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            showError(result.errorMessage ?: getString(R.string.error_calculation_failed))
        }
    }

    /**
     * 计算积分（暂未实现）
     */
    private fun calculateIntegral() {
        showError(getString(R.string.module_coming_soon))
    }

    /**
     * 显示错误信息
     */
    private fun showError(message: String) {
        tvResult.text = getString(R.string.error) + ": $message"
        tvResult.visibility = View.VISIBLE
        rvSteps.visibility = View.GONE

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}