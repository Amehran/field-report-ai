package com.fieldreport.ai.platform

import platform.Foundation.NSUserDefaults

actual class SecureStorage actual constructor(context: PlatformContext) {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return defaults.stringForKey(key) ?: defaultValue
    }

    actual fun saveInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), key)
    }

    actual fun getInt(key: String, defaultValue: Int): Int {
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key).toInt()
        } else {
            defaultValue
        }
    }

    actual fun clear() {
        val dictionary = defaults.dictionaryRepresentation()
        for (key in dictionary.keys) {
            defaults.removeObjectForKey(key as String)
        }
    }
}
