// MathGenius/app/src/main/kotlin/com/mathgenius/calculator/core/i18n/LanguageManager.kt
// Language Manager - Multi-language Support

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

    private var currentLanguage: Language = Language.getSystemLanguage()
    private var resources: Resources = context.resources

    init {
        applyLanguage(currentLanguage)
    }

    fun setLanguage(language: Language) {
        currentLanguage = language
        applyLanguage(language)
    }

    fun getCurrentLanguage(): Language = currentLanguage

    private fun applyLanguage(language: Language) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        resources = context.createConfigurationContext(configuration).resources
    }

    fun getString(key: String): String {
        return try {
            val resourceId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resourceId != 0) {
                resources.getString(resourceId)
            } else {
                "[$key]"
            }
        } catch (e: Exception) {
            "[$key]"
        }
    }

    fun getFormattedString(key: String, vararg formatArgs: Any): String {
        return try {
            val resourceId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resourceId != 0) {
                resources.getString(resourceId, *formatArgs)
            } else {
                "[$key]"
            }
        } catch (e: Exception) {
            "[$key: ${formatArgs.joinToString()}]"
        }
    }

    fun getAvailableLanguages(): List<Language> = Language.values().toList()

    fun getLanguageDisplayName(language: Language): String = language.displayName

    companion object {
        private const val PREFS_NAME = "MathGeniusPrefs"
        private const val KEY_LANGUAGE = "selected_language"

        fun saveLanguagePreference(context: Context, language: Language) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        }

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