// app/src/main/kotlin/com/mathgenius/calculator/ui/common/CalculatorKeyboard.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.engine.ExpressionManager

/**
 * 自定义计算器键盘
 *
 * 功能:
 * - 提供数字、运算符、函数等按键
 * - 自动管理表达式 (内部格式 + 显示格式)
 * - 支持退格、清除、确认操作
 * - 实时更新显示区
 *
 * 布局:
 * Row 1: [7] [8] [9] [÷] [sin] [cos] [tan]
 * Row 2: [4] [5] [6] [×] [ln]  [exp] [log]
 * Row 3: [1] [2] [3] [-] [x²]  [xⁿ]  [√]
 * Row 4: [0] [.] [x] [+] [(]   [)]   [π]
 * Row 5: [清除 C]         [确认计算 ✓]
 */
class CalculatorKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    /**
     * 表达式管理器
     */
    private val expressionManager = ExpressionManager()

    /**
     * 显示区 TextView
     */
    private var displayView: TextView? = null

    /**
     * 确认回调
     */
    private var onConfirmListener: ((String) -> Unit)? = null

    /**
     * 所有按键定义
     */
    private val buttons = listOf(
        // Row 1
        CalculatorButton("7", KeyType.NUMBER, "7"),
        CalculatorButton("8", KeyType.NUMBER, "8"),
        CalculatorButton("9", KeyType.NUMBER, "9"),
        CalculatorButton("÷", KeyType.OPERATOR, "÷"),
        CalculatorButton("sin", KeyType.FUNCTION, "sin"),
        CalculatorButton("cos", KeyType.FUNCTION, "cos"),
        CalculatorButton("tan", KeyType.FUNCTION, "tan"),

        // Row 2
        CalculatorButton("4", KeyType.NUMBER, "4"),
        CalculatorButton("5", KeyType.NUMBER, "5"),
        CalculatorButton("6", KeyType.NUMBER, "6"),
        CalculatorButton("×", KeyType.OPERATOR, "×"),
        CalculatorButton("ln", KeyType.FUNCTION, "ln"),
        CalculatorButton("exp", KeyType.FUNCTION, "exp"),
        CalculatorButton("log", KeyType.FUNCTION, "log"),

        // Row 3
        CalculatorButton("1", KeyType.NUMBER, "1"),
        CalculatorButton("2", KeyType.NUMBER, "2"),
        CalculatorButton("3", KeyType.NUMBER, "3"),
        CalculatorButton("-", KeyType.OPERATOR, "-"),
        CalculatorButton("x²", KeyType.POWER, "2"),
        CalculatorButton("xⁿ", KeyType.POWER, "n"),
        CalculatorButton("√", KeyType.FUNCTION, "sqrt"),

        // Row 4
        CalculatorButton("0", KeyType.NUMBER, "0"),
        CalculatorButton(".", KeyType.DECIMAL, "."),
        CalculatorButton("x", KeyType.VARIABLE, "x"),
        CalculatorButton("+", KeyType.OPERATOR, "+"),
        CalculatorButton("(", KeyType.PARENTHESIS, "("),
        CalculatorButton(")", KeyType.PARENTHESIS, ")"),
        CalculatorButton("π", KeyType.CONSTANT, "π"),

        // Row 5
        CalculatorButton("←", KeyType.BACKSPACE, ""),
        CalculatorButton("C", KeyType.CLEAR, ""),
        CalculatorButton("✓", KeyType.CONFIRM, "")
    )

    init {
        columnCount = 7
        rowCount = 5

        inflateButtons()
    }

    /**
     * 动态创建按键
     */
    private fun inflateButtons() {
        buttons.forEach { button ->
            val btn = MaterialButton(context).apply {
                text = button.label
                contentDescription = button.description

                setOnClickListener {
                    onKeyPressed(button)
                }

                layoutParams = LayoutParams().apply {
                    width = 0
                    height = LayoutParams.WRAP_CONTENT
                    columnSpec = spec(UNDEFINED, 1f)
                    setMargins(4, 4, 4, 4)
                }
            }

            addView(btn)
        }
    }

    /**
     * 设置显示区
     */
    fun setDisplayView(textView: TextView) {
        this.displayView = textView
        updateDisplay()
    }

    /**
     * 设置确认监听器
     */
    fun setOnConfirmListener(listener: (String) -> Unit) {
        this.onConfirmListener = listener
    }

    /**
     * 按键点击处理
     */
    private fun onKeyPressed(button: CalculatorButton) {
        when (button.type) {
            KeyType.NUMBER -> {
                expressionManager.appendNumber(button.value)
                updateDisplay()
            }

            KeyType.DECIMAL -> {
                expressionManager.appendDecimal()
                updateDisplay()
            }

            KeyType.VARIABLE -> {
                expressionManager.appendVariable(button.value)
                updateDisplay()
            }

            KeyType.OPERATOR -> {
                expressionManager.appendOperator(button.value)
                updateDisplay()
            }

            KeyType.FUNCTION -> {
                expressionManager.appendFunction(button.value)
                updateDisplay()
            }

            KeyType.POWER -> {
                if (button.value == "2") {
                    expressionManager.appendPower("2")
                    updateDisplay()
                } else {
                    showPowerInputDialog()
                }
            }

            KeyType.PARENTHESIS -> {
                expressionManager.appendParenthesis(button.value)
                updateDisplay()
            }

            KeyType.CONSTANT -> {
                expressionManager.appendConstant(button.value)
                updateDisplay()
            }

            KeyType.BACKSPACE -> {
                expressionManager.deleteLast()
                updateDisplay()
            }

            KeyType.CLEAR -> {
                expressionManager.clear()
                updateDisplay()
            }

            KeyType.CONFIRM -> {
                onConfirm()
            }
        }
    }

    /**
     * 更新显示区
     */
    private fun updateDisplay() {
        val displayText = expressionManager.getDisplayText()
        displayView?.text = displayText
    }

    /**
     * 显示幂次输入对话框
     */
    private fun showPowerInputDialog() {
        val input = android.widget.EditText(context).apply {
            hint = context.getString(R.string.keyboard_power_hint)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(context)
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
     * 确认计算
     */
    private fun onConfirm() {
        if (expressionManager.isEmpty()) {
            return
        }

        val displayText = expressionManager.getDisplayText()
        val internalExpr = expressionManager.getInternalExpression()

        AlertDialog.Builder(context)
            .setTitle(R.string.keyboard_confirm_title)
            .setMessage(context.getString(R.string.keyboard_confirm_message, displayText))
            .setPositiveButton(R.string.keyboard_confirm) { _, _ ->
                onConfirmListener?.invoke(internalExpr)
            }
            .setNegativeButton(R.string.keyboard_cancel, null)
            .show()
    }

    /**
     * 获取当前表达式（显示格式）
     */
    fun getDisplayExpression(): String {
        return expressionManager.getDisplayText()
    }

    /**
     * 获取当前表达式（内部格式）
     */
    fun getInternalExpression(): String {
        return expressionManager.getInternalExpression()
    }

    /**
     * 清除表达式
     */
    fun clearExpression() {
        expressionManager.clear()
        updateDisplay()
    }
}