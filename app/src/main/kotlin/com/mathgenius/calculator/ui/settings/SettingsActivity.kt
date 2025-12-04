// app/src/main/kotlin/com/mathgenius/calculator/ui/settings/SettingsActivity.kt
package com.mathgenius.calculator.ui.settings

import android.content.Context
import android.os.Bundle
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

    companion object {
        private const val PREFS_NAME = "MathGeniusPrefs"
        private const val KEY_THEME = "theme"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_EYE_CARE = "eye_care"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        setContentView(R.layout.activity_settings)

        languageManager = LanguageManager(this)

        initializeViews()
        loadCurrentSettings()
        setupListeners()
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

        val currentTheme = loadThemePreference()
        val themeRadioId = when (currentTheme) {
            THEME_LIGHT -> R.id.radio_light
            THEME_DARK -> R.id.radio_dark
            THEME_EYE_CARE -> R.id.radio_eye_care
            else -> R.id.radio_light
        }
        radioGroupTheme.check(themeRadioId)
    }

    /**
     * 设置事件监听器
     */
    private fun setupListeners() {
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

            LanguageManager.saveLanguagePreference(this, language)
            languageManager.setLanguage(language)
            recreate()
        }

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.radio_light -> THEME_LIGHT
                R.id.radio_dark -> THEME_DARK
                R.id.radio_eye_care -> THEME_EYE_CARE
                else -> THEME_LIGHT
            }

            saveThemePreference(theme)
            applyTheme(theme)
            recreate()
        }
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
    }

    /**
     * 应用主题
     *
     * @param theme 主题字符串
     */
    private fun applyTheme(theme: String) {
        when (theme) {
            THEME_LIGHT -> setTheme(R.style.Theme_MathGenius_Light)
            THEME_DARK -> setTheme(R.style.Theme_MathGenius_Dark)
            THEME_EYE_CARE -> setTheme(R.style.Theme_MathGenius_EyeCare)
        }
    }

    /**
     * 处理返回按钮点击
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}