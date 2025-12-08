// MathGenius/app/src/main/kotlin/com/mathgenius/calculator/core/i18n/LanguageManager.kt
// Language Manager - Multi-language Support (FIXED VERSION)

package com.mathgenius.calculator.core.i18n

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español");

    companion object {
        fun fromCode(code: String): Language? {
            return values().find { it.code == code }
        }

        fun getSystemLanguage(): Language {
            val systemLang = Locale.getDefault().language
            return fromCode(systemLang) ?: ENGLISH
        }
    }
}

class LanguageManager(private val context: Context) {

    private var currentLanguage: Language
    private var localizedContext: Context

    init {
        // 从 SharedPreferences 加载保存的语言设置
        currentLanguage = loadLanguagePreference(context)
        localizedContext = createLocalizedContext(context, currentLanguage)
    }

    /**
     * 设置语言
     *
     * @param language 要设置的语言
     */
    fun setLanguage(language: Language) {
        currentLanguage = language
        saveLanguagePreference(context, language)
        localizedContext = createLocalizedContext(context, language)
    }

    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(): Language = currentLanguage

    /**
     * 获取本地化字符串
     *
     * @param key 字符串资源的名称
     * @return 本地化的字符串
     */
    fun getString(key: String): String {
        return try {
            // 使用 localizedContext 来获取资源 ID
            val resourceId = localizedContext.resources.getIdentifier(
                key,
                "string",
                context.packageName
            )
            if (resourceId != 0) {
                localizedContext.getString(resourceId)
            } else {
                // 如果找不到资源,返回 key 本身
                key
            }
        } catch (e: Exception) {
            key
        }
    }

    /**
     * 获取格式化字符串
     *
     * @param key 字符串资源的名称
     * @param formatArgs 格式化参数
     * @return 格式化后的本地化字符串
     */
    fun getFormattedString(key: String, vararg formatArgs: Any): String {
        return try {
            val resourceId = localizedContext.resources.getIdentifier(
                key,
                "string",
                context.packageName
            )
            if (resourceId != 0) {
                localizedContext.getString(resourceId, *formatArgs)
            } else {
                "$key: ${formatArgs.joinToString()}"
            }
        } catch (e: Exception) {
            "$key: ${formatArgs.joinToString()}"
        }
    }

    /**
     * 获取所有可用语言
     */
    fun getAvailableLanguages(): List<Language> = Language.values().toList()

    /**
     * 获取语言显示名称
     */
    fun getLanguageDisplayName(language: Language): String = language.displayName

    /**
     * 创建本地化的 Context
     *
     * @param context 原始 Context
     * @param language 目标语言
     * @return 本地化后的 Context
     */
    private fun createLocalizedContext(context: Context, language: Language): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }

    companion object {
        private const val PREFS_NAME = "MathGeniusPrefs"
        private const val KEY_LANGUAGE = "selected_language"

        /**
         * 保存语言偏好到 SharedPreferences
         */
        fun saveLanguagePreference(context: Context, language: Language) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        }

        /**
         * 从 SharedPreferences 加载语言偏好
         */
        fun loadLanguagePreference(context: Context): Language {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val languageCode = prefs.getString(KEY_LANGUAGE, null)
            return if (languageCode != null) {
                Language.fromCode(languageCode) ?: Language.getSystemLanguage()
            } else {
                Language.getSystemLanguage()
            }
        }
    }
}