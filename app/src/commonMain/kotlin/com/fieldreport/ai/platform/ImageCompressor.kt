package com.fieldreport.ai.platform

expect class ImageCompressor {
    fun compressImage(inputPath: String, maxDimension: Int = 1200): String
}
