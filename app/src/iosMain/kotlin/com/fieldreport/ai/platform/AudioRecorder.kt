package com.fieldreport.ai.platform

import platform.AVFoundation.*
import platform.Foundation.NSURL

actual class AudioRecorder {
    private var recorder: AVAudioRecorder? = null
    private var isRecordingState = false

    actual fun startRecording(outputPath: String) {
        val url = NSURL.fileURLWithPath(outputPath)
        val settings = mapOf<Any?, Any>()
        recorder = AVAudioRecorder(url, settings, null)
        recorder?.prepareToRecord()
        recorder?.record()
        isRecordingState = true
    }

    actual fun stopRecording(): String? {
        if (!isRecordingState) return null
        recorder?.stop()
        val path = recorder?.url?.path
        recorder = null
        isRecordingState = false
        return path
    }

    actual fun isRecording(): Boolean = isRecordingState
}
