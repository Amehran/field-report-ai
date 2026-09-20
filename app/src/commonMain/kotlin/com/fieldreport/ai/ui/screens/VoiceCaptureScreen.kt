package com.fieldreport.ai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.platform.AudioRecorder
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCaptureScreen(
    viewModel: ReportViewModel,
    onStopRecording: () -> Unit,
    onBack: () -> Unit
) {
    var secondsElapsed by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    val audioRecorder = remember { AudioRecorder() }

    fun stopAndSaveRecording() {
        try {
            val recordedPath = audioRecorder.stopRecording()
            if (!recordedPath.isNullOrBlank()) {
                viewModel.setAudioRecording(recordedPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onStopRecording()
    }

    LaunchedEffect(Unit) {
        try {
            audioRecorder.startRecording("voice_${kotlin.random.Random.nextInt(1000, 999999)}.m4a")
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                secondsElapsed++
            }
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val timerText = if (seconds < 10) "$minutes:0$seconds" else "$minutes:$seconds"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Describe the work", style = MaterialTheme.typography.titleLarge, color = Slate900) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate50)
            )
        },
        containerColor = Slate50
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Teal100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Recording mic",
                            tint = Teal600,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = timerText,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Listening...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(12) { index ->
                            val infiniteTransition = rememberInfiniteTransition(label = "wave")
                            val height by infiniteTransition.animateFloat(
                                initialValue = 12f,
                                targetValue = 48f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 300 + (index * 60), easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "height"
                            )
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(height.dp)
                                    .background(Teal600, RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tell us what you completed and what the customer should know.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Slate600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Rose600, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                stopAndSaveRecording()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop recording",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
