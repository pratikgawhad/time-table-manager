package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var areAlarmsGloballyEnabled: Boolean
        get() = prefs.getBoolean(KEY_ALARMS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ALARMS_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "light") ?: "light"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var appLanguage: String
        get() = prefs.getString(KEY_APP_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_APP_LANGUAGE, value).apply()

    var saveCount: Int
        get() = prefs.getInt(KEY_SAVE_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_SAVE_COUNT, value).apply()

    companion object {
        private const val PREFS_NAME = "timetable_prefs"
        private const val KEY_ALARMS_ENABLED = "key_alarms_enabled"
        private const val KEY_VIBRATION_ENABLED = "key_vibration_enabled"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_HAS_SEEN_ONBOARDING = "key_has_seen_onboarding"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"
        private const val KEY_SAVE_COUNT = "key_save_count"
    }
}
