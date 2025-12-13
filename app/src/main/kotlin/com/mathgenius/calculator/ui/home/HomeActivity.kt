// app/src/main/kotlin/com/mathgenius/calculator/ui/home/HomeActivity.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.LanguageManager
import com.mathgenius.calculator.ui.calculus.CalculusActivity
import com.mathgenius.calculator.ui.settings.SettingsActivity

/**
 * 主页面 Activity
 *
 * 功能：
 * - 显示所有可用的数学模块
 * - 支持模块选择和跳转
 * - 提供设置入口
 *
 * 架构：
 * - 纯模块展示页面
 * - 不包含计算功能
 * - 作为应用入口和导航中心
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var languageManager: LanguageManager
    private lateinit var recyclerModules: RecyclerView
    private lateinit var btnSettings: MaterialButton

    /**
     * 数学模块数据类
     */
    data class MathModule(
        val id: String,
        val nameResId: Int,
        val descResId: Int,
        val iconResId: Int,
        val enabled: Boolean = true
    )

    /**
     * 所有数学模块列表
     */
    private val modules = listOf(
        MathModule(
            id = "calculus",
            nameResId = R.string.module_calculus,
            descResId = R.string.module_calculus_desc,
            iconResId = R.drawable.ic_calculus,
            enabled = true
        ),
        MathModule(
            id = "linear_algebra",
            nameResId = R.string.module_linear_algebra,
            descResId = R.string.module_linear_algebra_desc,
            iconResId = R.drawable.ic_linear_algebra,
            enabled = false
        ),
        MathModule(
            id = "statistics",
            nameResId = R.string.module_statistics,
            descResId = R.string.module_statistics_desc,
            iconResId = R.drawable.ic_statistics,
            enabled = false
        ),
        MathModule(
            id = "diff_eq",
            nameResId = R.string.module_diff_eq,
            descResId = R.string.module_diff_eq_desc,
            iconResId = R.drawable.ic_diff_eq,
            enabled = false
        ),
        MathModule(
            id = "discrete",
            nameResId = R.string.module_discrete,
            descResId = R.string.module_discrete_desc,
            iconResId = R.drawable.ic_discrete,
            enabled = false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        languageManager = LanguageManager(this)

        initViews()
        setupModulesList()
        setupClickListeners()
    }

    /**
     * 初始化视图
     */
    private fun initViews() {
        recyclerModules = findViewById(R.id.recycler_modules)
        btnSettings = findViewById(R.id.btn_settings)
    }

    /**
     * 设置模块列表
     */
    private fun setupModulesList() {
        recyclerModules.layoutManager = GridLayoutManager(this, 2)
        recyclerModules.adapter = ModulesAdapter(modules) { module ->
            onModuleClicked(module)
        }
    }

    /**
     * 设置点击监听
     */
    private fun setupClickListeners() {
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    /**
     * 模块点击处理
     */
    private fun onModuleClicked(module: MathModule) {
        if (!module.enabled) {
            android.widget.Toast.makeText(
                this,
                R.string.module_coming_soon,
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (module.id) {
            "calculus" -> {
                startActivity(Intent(this, CalculusActivity::class.java))
            }
            "linear_algebra" -> {
                // TODO: 实现线性代数模块
            }
            "statistics" -> {
                // TODO: 实现统计学模块
            }
            "diff_eq" -> {
                // TODO: 实现微分方程模块
            }
            "discrete" -> {
                // TODO: 实现离散数学模块
            }
        }
    }
}