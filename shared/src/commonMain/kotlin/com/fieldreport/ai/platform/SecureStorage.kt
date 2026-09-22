package com.fieldreport.ai.platform

expect class SecureStorage(context: PlatformContext) {
    fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?
    fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun clear()
}
