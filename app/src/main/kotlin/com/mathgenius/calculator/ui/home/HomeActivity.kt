// app/src/main/kotlin/com/mathgenius/calculator/ui/home/HomeActivity.kt
package com.mathgenius.calculator.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.steps.CalculationStep
import com.mathgenius.calculator.modules.calculus.derivative.DerivativeEngine
import com.mathgenius.calculator.ui.common.StepsAdapter
import com.mathgenius.calculator.ui.settings.SettingsActivity

/**
 * 主界面 Activity
 * 显示模块选择和微分计算功能
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var languageManager: LanguageManager
    private lateinit var derivativeEngine: DerivativeEngine
    
    private lateinit var recyclerModules: RecyclerView
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var editInput: EditText
    private lateinit var btnCalculate: Button
    private lateinit var txtResult: TextView
    private lateinit var recyclerSteps: RecyclerView
    private lateinit var scrollView: ScrollView
    
    private var currentSteps: List<CalculationStep> = emptyList()

    companion object {
        private const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeComponents()
        initializeViews()
        setupModuleList()
        setupListeners()
        setupSettingsButton()
        testDerivativeEngine()
    }

    /**
     * 初始化核心组件
     */
    private fun initializeComponents() {
        languageManager = LanguageManager(this)
        derivativeEngine = DerivativeEngine(languageManager)
    }

    /**
     * 初始化视图组件
     */
    private fun initializeViews() {
        recyclerModules = findViewById(R.id.recycler_modules)
        editInput = findViewById(R.id.edit_input)
        btnCalculate = findViewById(R.id.btn_calculate)
        txtResult = findViewById(R.id.txt_result)
        recyclerSteps = findViewById(R.id.recycler_steps)
        scrollView = findViewById(R.id.scroll_view)
    }

    /**
     * 设置模块列表
     * 定义所有数学模块并配置 RecyclerView
     */
    private fun setupModuleList() {
        val modules = listOf(
            ModuleData(
                id = "calculus",
                nameResId = R.string.module_calculus,
                descriptionResId = R.string.module_calculus_desc,
                iconResId = R.drawable.ic_calculate,
                colorResId = R.color.module_calculus,
                isAvailable = true
            ),
            ModuleData(
                id = "linear_algebra",
                nameResId = R.string.module_linear_algebra,
                descriptionResId = R.string.module_linear_algebra_desc,
                iconResId = R.drawable.ic_calculate,
                colorResId = R.color.module_linear_algebra,
                isAvailable = false
            ),
            ModuleData(
                id = "statistics",
                nameResId = R.string.module_statistics,
                descriptionResId = R.string.module_statistics_desc,
                iconResId = R.drawable.ic_calculate,
                colorResId = R.color.module_statistics,
                isAvailable = false
            ),
            ModuleData(
                id = "diffeq",
                nameResId = R.string.module_diff_eq,
                descriptionResId = R.string.module_diff_eq_desc,
                iconResId = R.drawable.ic_calculate,
                colorResId = R.color.module_diffeq,
                isAvailable = false
            ),
            ModuleData(
                id = "discrete",
                nameResId = R.string.module_discrete,
                descriptionResId = R.string.module_discrete_desc,
                iconResId = R.drawable.ic_calculate,
                colorResId = R.color.module_discrete,
                isAvailable = false
            )
        )

        moduleAdapter = ModuleAdapter(modules) { module ->
            handleModuleClick(module)
        }

        recyclerModules.adapter = moduleAdapter
        recyclerModules.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 处理模块点击事件
     * 
     * @param module 被点击的模块数据
     */
    private fun handleModuleClick(module: ModuleData) {
        when (module.id) {
            "calculus" -> {
                scrollView.smoothScrollTo(0, 0)
                editInput.requestFocus()
            }
            else -> {
                Toast.makeText(
                    this,
                    R.string.module_coming_soon,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * 设置事件监听器
     */
    private fun setupListeners() {
        btnCalculate.setOnClickListener {
            calculateDerivative()
        }
    }

    /**
     * 设置设置按钮
     * 点击后跳转到设置页面
     */
    private fun setupSettingsButton() {
        findViewById<ImageButton>(R.id.btn_settings)?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 计算导数
     * 获取输入表达式，调用微分引擎计算，并显示结果和步骤
     */
    private fun calculateDerivative() {
        val input = editInput.text.toString().trim()

        if (input.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_input, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val result = derivativeEngine.compute(input)

            txtResult.text = buildString {
                append(languageManager.getString("result"))
                append(": ")
                append(result.formattedResult)
            }

            currentSteps = result.steps
            displaySteps(result.steps)

            Log.d(TAG, "Calculation successful: ${result.formattedResult}")

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "${languageManager.getString("error_calculation")}: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            Log.e(TAG, "Calculation error", e)
        }
    }

    /**
     * 显示计算步骤
     * 使用 RecyclerView 和 StepsAdapter 显示步骤列表
     * 
     * @param steps 计算步骤列表
     */
    private fun displaySteps(steps: List<CalculationStep>) {
        if (steps.isNotEmpty()) {
            val adapter = StepsAdapter(steps, languageManager)
            recyclerSteps.adapter = adapter
            recyclerSteps.layoutManager = LinearLayoutManager(this)
            recyclerSteps.visibility = View.VISIBLE
        } else {
            recyclerSteps.visibility = View.GONE
        }
    }

    /**
     * 测试微分引擎
     * 在应用启动时运行一系列测试用例并输出日志
     */
    private fun testDerivativeEngine() {
        Log.d(TAG, "=== Testing Derivative Engine ===")

        val testCases = listOf(
            "x^2",
            "2*x + 1",
            "x^3 + 2*x^2 + x",
            "sin(x)",
            "x^2 * cos(x)",
            "ln(x)",
            "exp(x)",
            "tan(x)"
        )

        testCases.forEach { expr ->
            try {
                val result = derivativeEngine.compute(expr)
                Log.d(TAG, "Input: $expr")
                Log.d(TAG, "Output: ${result.formattedResult}")
                Log.d(TAG, "Steps: ${result.steps.size}")
                Log.d(TAG, "---")
            } catch (e: Exception) {
                Log.e(TAG, "Test failed for: $expr", e)
            }
        }

        Log.d(TAG, "=== Test Complete ===")
    }
}