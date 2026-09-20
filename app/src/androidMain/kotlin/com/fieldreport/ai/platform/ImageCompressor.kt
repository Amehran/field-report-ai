package com.fieldreport.ai.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

actual class ImageCompressor(private val context: Context) {
    actual fun compressImage(inputPath: String, maxDimension: Int): String {
        return try {
            val file = File(inputPath)
            if (!file.exists()) return inputPath
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(inputPath, options)
            
            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }
            
            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = BitmapFactory.decodeFile(inputPath, decodeOptions) ?: return inputPath
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            inputPath
        } catch (e: Exception) {
            e.printStackTrace()
            inputPath
        }
    }
}
