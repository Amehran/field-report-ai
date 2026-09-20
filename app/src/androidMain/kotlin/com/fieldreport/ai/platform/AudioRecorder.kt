package com.fieldreport.ai.platform

import android.content.Context
import android.media.MediaRecorder
import java.io.File

actual class AudioRecorder() {
    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var recordingState = false

    actual fun startRecording(outputPath: String) {
        currentFilePath = outputPath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputPath)
            prepare()
            start()
        }
        recordingState = true
    }

    actual fun stopRecording(): String? {
        if (!recordingState) return null
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            recordingState = false
            currentFilePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun isRecording(): Boolean = recordingState
}
