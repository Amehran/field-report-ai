package com.fieldreport.ai.platform

expect class AudioRecorder {
    fun startRecording(outputPath: String)
    fun stopRecording(): String?
    fun isRecording(): Boolean
}
