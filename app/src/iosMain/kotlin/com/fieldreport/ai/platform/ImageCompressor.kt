package com.fieldreport.ai.platform

import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

actual class ImageCompressor {
    actual fun compressImage(inputPath: String, maxDimension: Int): String {
        val image = UIImage.imageWithContentsOfFile(inputPath) ?: return inputPath
        val data = UIImageJPEGRepresentation(image, 0.8) ?: return inputPath
        data.writeToFile(inputPath, true)
        return inputPath
    }
}
