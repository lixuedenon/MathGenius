// app/src/main/kotlin/com/mathgenius/calculator/ui/calculus/CalculusActivity.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.calculus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.engine.ExpressionManager
import com.mathgenius.calculator.core.engine.ExpressionParser
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.core.i18n.MathFormatter
import com.mathgenius.calculator.modules.calculus.derivative.DerivativeEngine
import com.mathgenius.calculator.ui.result.ResultActivity

/**
 * 微积分计算页面
 *
 * 功能：
 * - 提供计算器键盘输入表达式
 * - 实时显示表达式（数学符号格式）
 * - 支持微分计算
 * - 支持积分计算
 * - 跳转到结果页面展示计算过程和结果
 *
 * 架构：
 * - ExpressionManager: 管理双层表达式（显示格式 + 内部格式）
 * - 自定义键盘布局: 7x5 网格，包含数字、运算符、函数
 * - 两个计算按钮: 微分和积分
 */
class CalculusActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CalculusActivity"
        const val EXTRA_CALCULATION_TYPE = "calculation_type"
        const val EXTRA_EXPRESSION = "expression"
        const val TYPE_DERIVATIVE = "derivative"
        const val TYPE_INTEGRAL = "integral"
    }

    private lateinit var languageManager: LanguageManager
    private lateinit var formatter: MathFormatter
    private lateinit var derivativeEngine: DerivativeEngine
    private lateinit var expressionManager: ExpressionManager

    private lateinit var tvDisplay: TextView
    private lateinit var gridKeyboard: GridLayout
    private lateinit var btnBackspace: Button
    private lateinit var btnClear: Button
    private lateinit var btnDerivative: Button
    private lateinit var btnIntegral: Button

    /**
     * 键盘按键定义
     * 每个按键包含：显示文本、按键类型、内部值
     */
    private data class KeyButton(
        val label: String,
        val type: KeyType,
        val value: String = label
    )

    private enum class KeyType {
        NUMBER, OPERATOR, FUNCTION, VARIABLE, CONSTANT, POWER, PARENTHESIS
    }

    /**
     * 键盘布局定义（7列 x 4行）
     */
    private val keyboardLayout = listOf(
        // Row 1: 7 8 9 ÷ sin cos tan
        KeyButton("7", KeyType.NUMBER),
        KeyButton("8", KeyType.NUMBER),
        KeyButton("9", KeyType.NUMBER),
        KeyButton("÷", KeyType.OPERATOR, "/"),
        KeyButton("sin", KeyType.FUNCTION),
        KeyButton("cos", KeyType.FUNCTION),
        KeyButton("tan", KeyType.FUNCTION),

        // Row 2: 4 5 6 × ln exp log
        KeyButton("4", KeyType.NUMBER),
        KeyButton("5", KeyType.NUMBER),
        KeyButton("6", KeyType.NUMBER),
        KeyButton("×", KeyType.OPERATOR, "*"),
        KeyButton("ln", KeyType.FUNCTION),
        KeyButton("exp", KeyType.FUNCTION),
        KeyButton("log", KeyType.FUNCTION),

        // Row 3: 1 2 3 - x² xⁿ √
        KeyButton("1", KeyType.NUMBER),
        KeyButton("2", KeyType.NUMBER),
        KeyButton("3", KeyType.NUMBER),
        KeyButton("-", KeyType.OPERATOR),
        KeyButton("x²", KeyType.POWER, "2"),
        KeyButton("xⁿ", KeyType.POWER, "n"),
        KeyButton("√", KeyType.FUNCTION, "sqrt"),

        // Row 4: 0 . x + ( ) π
        KeyButton("0", KeyType.NUMBER),
        KeyButton(".", KeyType.NUMBER),
        KeyButton("x", KeyType.VARIABLE),
        KeyButton("+", KeyType.OPERATOR),
        KeyButton("(", KeyType.PARENTHESIS),
        KeyButton(")", KeyType.PARENTHESIS),
        KeyButton("π", KeyType.CONSTANT)
    )

    override fun attachBaseContext(newBase: Context) {
        val langManager = LanguageManager(newBase)
        val context = langManager.applyLanguageToActivity(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculus)

        languageManager = LanguageManager(this)
        formatter = MathFormatter()
        derivativeEngine = DerivativeEngine(languageManager, formatter)
        expressionManager = ExpressionManager()

        initViews()
        setupKeyboard()
        setupButtons()
        setupToolbar()
    }

    /**
     * 初始化视图组件
     */
    private fun initViews() {
        tvDisplay = findViewById(R.id.tv_display)
        gridKeyboard = findViewById(R.id.grid_keyboard)
        btnBackspace = findViewById(R.id.btn_backspace)
        btnClear = findViewById(R.id.btn_clear)
        btnDerivative = findViewById(R.id.btn_derivative)
        btnIntegral = findViewById(R.id.btn_integral)
    }

    /**
     * 设置工具栏返回按钮
     */
    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.module_calculus)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * 动态创建键盘按钮
     */
    private fun setupKeyboard() {
        gridKeyboard.removeAllViews()
        gridKeyboard.columnCount = 7
        gridKeyboard.rowCount = 4

        keyboardLayout.forEach { key ->
            val button = Button(this).apply {
                text = key.label
                textSize = when (key.type) {
                    KeyType.NUMBER, KeyType.OPERATOR -> 20f
                    KeyType.VARIABLE -> 20f
                    else -> 16f
                }

                // 设置按钮样式
                setBackgroundResource(getButtonBackground(key.type))
                setTextColor(getButtonTextColor(key.type))

                // 设置布局参数
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = resources.getDimensionPixelSize(R.dimen.keyboard_button_height)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(4, 4, 4, 4)
                }
                layoutParams = params

                // 设置点击事件
                setOnClickListener {
                    handleKeyPress(key)
                }
            }

            gridKeyboard.addView(button)
        }
    }

    /**
     * 根据按键类型获取背景资源
     */
    private fun getButtonBackground(type: KeyType): Int {
        return when (type) {
            KeyType.NUMBER -> R.drawable.bg_button_number
            KeyType.OPERATOR -> R.drawable.bg_button_operator
            KeyType.FUNCTION -> R.drawable.bg_button_function
            KeyType.VARIABLE -> R.drawable.bg_button_variable
            KeyType.CONSTANT -> R.drawable.bg_button_constant
            KeyType.POWER -> R.drawable.bg_button_function
            KeyType.PARENTHESIS -> R.drawable.bg_button_parenthesis
        }
    }

    /**
     * 根据按键类型获取文字颜色
     */
    private fun getButtonTextColor(type: KeyType): Int {
        return when (type) {
            KeyType.NUMBER, KeyType.OPERATOR, KeyType.VARIABLE ->
                resources.getColor(R.color.white, null)
            else ->
                resources.getColor(R.color.primary, null)
        }
    }

    /**
     * 设置辅助按钮（退格、清除、计算）
     */
    private fun setupButtons() {
        btnBackspace.setOnClickListener {
            expressionManager.deleteLast()
            updateDisplay()
        }

        btnClear.setOnClickListener {
            expressionManager.clear()
            updateDisplay()
        }

        btnDerivative.setOnClickListener {
            performCalculation(TYPE_DERIVATIVE)
        }

        btnIntegral.setOnClickListener {
            performCalculation(TYPE_INTEGRAL)
        }
    }

    /**
     * 处理按键点击
     */
    private fun handleKeyPress(key: KeyButton) {
        when (key.type) {
            KeyType.NUMBER -> {
                if (key.value == ".") {
                    expressionManager.appendDecimal()
                } else {
                    expressionManager.appendNumber(key.value)
                }
            }

            KeyType.VARIABLE -> {
                expressionManager.appendVariable(key.value)
            }

            KeyType.OPERATOR -> {
                expressionManager.appendOperator(key.label)
            }

            KeyType.FUNCTION -> {
                expressionManager.appendFunction(key.value)
            }

            KeyType.CONSTANT -> {
                expressionManager.appendConstant(key.label)
            }

            KeyType.POWER -> {
                if (key.value == "2") {
                    expressionManager.appendPower("2")
                } else {
                    showPowerInputDialog()
                }
            }

            KeyType.PARENTHESIS -> {
                expressionManager.appendParenthesis(key.value)
            }
        }

        updateDisplay()
    }

    /**
     * 更新表达式显示
     */
    private fun updateDisplay() {
        val displayText = expressionManager.getDisplayText()
        tvDisplay.text = if (displayText.isEmpty()) {
            getString(R.string.keyboard_display_hint)
        } else {
            displayText
        }
    }

    /**
     * 显示指数输入对话框（用于 xⁿ）
     */
    private fun showPowerInputDialog() {
        val input = android.widget.EditText(this).apply {
            hint = getString(R.string.keyboard_power_hint)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.keyboard_power_title)
            .setView(input)
            .setPositiveButton(R.string.keyboard_confirm) { _, _ ->
                val exponent = input.text.toString()
                if (exponent.isNotEmpty()) {
                    expressionManager.appendPower(exponent)
                    updateDisplay()
                }
            }
            .setNegativeButton(R.string.keyboard_cancel, null)
            .show()
    }

    /**
     * 执行计算（微分或积分）
     */
    private fun performCalculation(type: String) {
        if (expressionManager.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_expression, Toast.LENGTH_SHORT).show()
            return
        }

        val displayExpr = expressionManager.getDisplayText()
        val internalExpr = expressionManager.getInternalExpression()

        // 验证表达式
        try {
            val parser = ExpressionParser(enableImplicitMultiplication = false)
            parser.parse(internalExpr)
        } catch (e: Exception) {
            Log.e(TAG, "Expression parse error", e)
            Toast.makeText(
                this,
                getString(R.string.error_invalid_expression) + ": ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // 显示确认对话框
        showCalculationConfirmDialog(displayExpr, type) {
            navigateToResult(type, internalExpr)
        }
    }

    /**
     * 显示计算确认对话框
     */
    private fun showCalculationConfirmDialog(
        expression: String,
        type: String,
        onConfirm: () -> Unit
    ) {
        val title = if (type == TYPE_DERIVATIVE) {
            getString(R.string.confirm_derivative_title)
        } else {
            getString(R.string.confirm_integral_title)
        }

        val message = if (type == TYPE_DERIVATIVE) {
            getString(R.string.confirm_derivative_message, expression)
        } else {
            getString(R.string.confirm_integral_message, expression)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.keyboard_confirm) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(R.string.keyboard_cancel, null)
            .show()
    }

    /**
     * 跳转到结果页面
     */
    private fun navigateToResult(type: String, expression: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(EXTRA_CALCULATION_TYPE, type)
            putExtra(EXTRA_EXPRESSION, expression)
        }
        startActivity(intent)
    }
}