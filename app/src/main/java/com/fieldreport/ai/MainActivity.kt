package com.fieldreport.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fieldreport.ai.ui.screens.*
import com.fieldreport.ai.ui.theme.FieldReportAITheme
import com.fieldreport.ai.ui.viewmodel.ReportViewModel

import android.content.pm.ActivityInfo

class MainActivity : ComponentActivity() {

    private val reportViewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            FieldReportAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("capture") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("capture") {
                            CaptureScreen(
                                viewModel = reportViewModel,
                                onNavigateToRecord = {
                                    navController.navigate("voice_capture")
                                },
                                onNavigateToReview = {
                                    navController.navigate("review")
                                },
                                onClose = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("voice_capture") {
                            VoiceCaptureScreen(
                                viewModel = reportViewModel,
                                onStopRecording = {
                                    navController.popBackStack()
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("review") {
                            ReviewScreen(
                                viewModel = reportViewModel,
                                onApproveSuccess = {
                                    navController.navigate("report_ready")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("report_ready") {
                            ReportReadyScreen(
                                viewModel = reportViewModel,
                                onDone = {
                                    navController.navigate("capture") {
                                        popUpTo("capture") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
