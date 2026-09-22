package com.fieldreport.ai.platform

import android.content.Context

actual class PlatformContext(val context: Context)

actual fun getPlatformName(): String = "Android"
