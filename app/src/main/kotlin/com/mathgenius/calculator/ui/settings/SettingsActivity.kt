// app/src/main/kotlin/com/mathgenius/calculator/ui/settings/SettingsActivity.kt
// 设置页面 Activity - 真正的最终修复版本
// 修改日期: 2025-12-09
// 修改方案: 使用 savedInstanceState 保存设置变化标志,确保 recreate() 后保持

package com.mathgenius.calculator.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import java.util.Locale
import android.util.Log
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.mathgenius.calculator.R
import com.mathgenius.calculator.core.i18n.Language
import com.mathgenius.calculator.core.i18n.LanguageManager

/**
 * 设置页面 Activity
 * 提供语言切换、主题切换和应用信息展示功能
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var languageManager: LanguageManager
    private lateinit var radioGroupLanguage: RadioGroup
    private lateinit var radioGroupTheme: RadioGroup

    // ===== 修改开始: 添加标志变量 =====
    // 修改日期: 2025-12-09
    // 修改原因: 跟踪设置是否改变
    // 修改内容: 添加 settingsChanged 标志变量
    private var settingsChanged = false
    // ===== 修改结束: 添加标志变量 =====

    companion object {
        private const val TAG = "SettingsActivity"
        private const val PREFS_NAME = "MathGeniusPrefs"
        private const val KEY_THEME = "theme"

        // ===== 修改开始: 添加状态保存键 =====
        // 修改日期: 2025-12-09
        // 修改原因: 保存设置变化标志到 savedInstanceState
        // 修改内容: 添加 KEY_SETTINGS_CHANGED 常量
        private const val KEY_SETTINGS_CHANGED = "settings_changed"
        // ===== 修改结束: 添加状态保存键 =====

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_EYE_CARE = "eye_care"
    }

    // ===== 新增开始: 应用语言配置到 Activity Context =====
    // 修改日期: 2025-12-10
    // 修改原因: 语言更改后 recreate() 不生效,需要在 Activity 创建时应用语言配置
    // 修改内容: 覆盖 attachBaseContext() 方法,在 Activity 创建前应用语言配置
    override fun attachBaseContext(newBase: Context) {
        // 从 SharedPreferences 加载当前语言
        val language = LanguageManager.loadLanguagePreference(newBase)

        // 创建带语言配置的 Context
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)

        val localizedContext = newBase.createConfigurationContext(configuration)

        Log.d(TAG, "attachBaseContext: Applied language=${language.name} (${language.code})")

        super.attachBaseContext(localizedContext)
    }
    // ===== 新增结束: 应用语言配置到 Activity Context =====

    override fun onCreate(savedInstanceState: Bundle?) {
        // 在 setContentView 之前应用主题
        applyTheme()

        super.onCreate(savedInstanceState)

        // ===== 修改开始: 恢复设置变化标志 =====
        // 修改日期: 2025-12-09
        // 修改原因: 从 savedInstanceState 恢复标志,确保 recreate() 后保持
        // 修改内容: 如果有保存的状态,恢复 settingsChanged
        if (savedInstanceState != null) {
            settingsChanged = savedInstanceState.getBoolean(KEY_SETTINGS_CHANGED, false)
            Log.d(TAG, "Restored settingsChanged from savedInstanceState: $settingsChanged")
        }
        // ===== 修改结束: 恢复设置变化标志 =====

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        setContentView(R.layout.activity_settings)

        Log.d(TAG, "=== SettingsActivity onCreate ===")

        languageManager = LanguageManager(this)

        initializeViews()
        loadCurrentSettings()
        setupListeners()
    }

    /**
     * 应用主题
     * 必须在 setContentView 之前调用
     */
    private fun applyTheme() {
        val currentTheme = loadThemePreference()
        when (currentTheme) {
            THEME_LIGHT -> setTheme(R.style.Theme_MathGenius_Light)
            THEME_DARK -> setTheme(R.style.Theme_MathGenius_Dark)
            THEME_EYE_CARE -> setTheme(R.style.Theme_MathGenius_EyeCare)
        }
        Log.d(TAG, "Applied theme: $currentTheme")
    }

    /**
     * 初始化视图组件
     */
    private fun initializeViews() {
        radioGroupLanguage = findViewById(R.id.radio_group_language)
        radioGroupTheme = findViewById(R.id.radio_group_theme)
    }

    /**
     * 加载当前设置
     * 从 SharedPreferences 读取并应用到 UI
     */
    private fun loadCurrentSettings() {
        // 加载语言设置
        val currentLanguage = LanguageManager.loadLanguagePreference(this)
        val languageRadioId = when (currentLanguage) {
            Language.ENGLISH -> R.id.radio_english
            Language.CHINESE -> R.id.radio_chinese
            Language.JAPANESE -> R.id.radio_japanese
            Language.KOREAN -> R.id.radio_korean
            Language.FRENCH -> R.id.radio_french
            Language.GERMAN -> R.id.radio_german
            Language.SPANISH -> R.id.radio_spanish
        }
        radioGroupLanguage.check(languageRadioId)

        Log.d(TAG, "Loaded language: $currentLanguage")

        // 加载主题设置
        val currentTheme = loadThemePreference()
        val themeRadioId = when (currentTheme) {
            THEME_LIGHT -> R.id.radio_light
            THEME_DARK -> R.id.radio_dark
            THEME_EYE_CARE -> R.id.radio_eye_care
            else -> R.id.radio_light
        }
        radioGroupTheme.check(themeRadioId)

        Log.d(TAG, "Loaded theme: $currentTheme")
    }

    /**
     * 设置事件监听器
     */
    private fun setupListeners() {
        // ===== 修改开始: 语言切换监听器 =====
        // 修改日期: 2025-12-09
        // 修改原因: 标记设置已更改
        // 修改内容: 设置 settingsChanged = true
        radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.radio_english -> Language.ENGLISH
                R.id.radio_chinese -> Language.CHINESE
                R.id.radio_japanese -> Language.JAPANESE
                R.id.radio_korean -> Language.KOREAN
                R.id.radio_french -> Language.FRENCH
                R.id.radio_german -> Language.GERMAN
                R.id.radio_spanish -> Language.SPANISH
                else -> Language.ENGLISH
            }

            Log.d(TAG, "Language changed to: $language")

            // 保存语言设置
            LanguageManager.saveLanguagePreference(this, language)

            // 更新 LanguageManager
            languageManager.setLanguage(language)

            // *** 修改: 标记设置已更改 ***
            settingsChanged = true
            Log.d(TAG, "Settings changed flag set to true")

            // 重新创建当前 Activity 以应用新语言
            recreate()
        }
        // ===== 修改结束: 语言切换监听器 =====

        // ===== 修改开始: 主题切换监听器 =====
        // 修改日期: 2025-12-09
        // 修改原因: 标记设置已更改
        // 修改内容: 设置 settingsChanged = true
        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.radio_light -> THEME_LIGHT
                R.id.radio_dark -> THEME_DARK
                R.id.radio_eye_care -> THEME_EYE_CARE
                else -> THEME_LIGHT
            }

            Log.d(TAG, "Theme changed to: $theme")

            // 保存主题设置
            saveThemePreference(theme)

            // *** 修改: 标记设置已更改 ***
            settingsChanged = true
            Log.d(TAG, "Settings changed flag set to true")

            // 重新创建当前 Activity 以应用新主题
            recreate()
        }
        // ===== 修改结束: 主题切换监听器 =====
    }

    /**
     * 从 SharedPreferences 加载主题偏好
     *
     * @return 主题字符串（light/dark/eye_care）
     */
    private fun loadThemePreference(): String {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, THEME_LIGHT) ?: THEME_LIGHT
    }

    /**
     * 保存主题偏好到 SharedPreferences
     *
     * @param theme 主题字符串
     */
    private fun saveThemePreference(theme: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme).apply()
        Log.d(TAG, "Theme preference saved: $theme")
    }

    // ===== 修改开始: 保存设置变化标志 =====
    // 修改日期: 2025-12-09
    // 修改原因: 保存标志到 savedInstanceState,确保 recreate() 后保持
    // 修改内容: 新增 onSaveInstanceState() 方法
    /**
     * 保存实例状态
     * 在 recreate() 时保存设置变化标志
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SETTINGS_CHANGED, settingsChanged)
        Log.d(TAG, "Saved settingsChanged to outState: $settingsChanged")
    }
    // ===== 修改结束: 保存设置变化标志 =====

    // ===== 修改开始: 在 finish 时设置返回结果 =====
    // 修改日期: 2025-12-09
    // 修改原因: 在 Activity 结束时根据标志设置返回结果
    // 修改内容: 重写 finish() 方法
    /**
     * Activity 结束时设置返回结果
     */
    override fun finish() {
        if (settingsChanged) {
            Log.d(TAG, "finish(): Settings changed, setting result to RESULT_OK")
            setResult(RESULT_OK)
        } else {
            Log.d(TAG, "finish(): Settings not changed, setting result to RESULT_CANCELED")
            setResult(RESULT_CANCELED)
        }
        super.finish()
    }
    // ===== 修改结束: 在 finish 时设置返回结果 =====

    /**
     * 处理返回按钮点击
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}