package com.fieldreport.ai.platform

import android.content.Context
import android.content.SharedPreferences

actual class SecureStorage actual constructor(context: PlatformContext) {
    private val prefs: SharedPreferences = context.context.getSharedPreferences(
        "field_report_secure_prefs",
        Context.MODE_PRIVATE
    )

    actual fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    actual fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    actual fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
