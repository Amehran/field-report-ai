package com.fieldreport.ai.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioRecorder
import platform.Foundation.NSURL

actual class AudioRecorder {
    private var recorder: AVAudioRecorder? = null
    private var isRecordingState = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun startRecording(outputPath: String) {
        val url = NSURL.fileURLWithPath(outputPath)
        recorder = AVAudioRecorder(url, emptyMap<Any?, Any>(), null)
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
