package com.fieldreport.ai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportReadyScreen(
    viewModel: ReportViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val report by viewModel.currentReport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report ready", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    Surface(
                        color = Emerald100,
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Approved",
                                tint = Emerald700,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate50)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.shareReportPdf(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal900)
                    ) {
                        Text(
                            text = "Share PDF",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = {
                        val summaryText = report?.customerSummary ?: "Job completed at ${report?.customerName}."
                        clipboardManager.setText(AnnotatedString(summaryText))
                        Toast.makeText(context, "Summary copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Copy customer summary", color = Teal600, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // PDF Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Dark Slate Contractor Header Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Charcoal900)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "NORTHLINE HOME SERVICES",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Job completion report • Sep 16, 2026",
                                color = Slate500,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Body
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = report?.customerName ?: "Miller Residence",
                            style = MaterialTheme.typography.titleLarge,
                            color = Slate900
                        )
                        Text(
                            text = report?.jobTitle ?: "Kitchen repair",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelMedium,
                            color = Teal600
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report?.customerSummary ?: "Cabinet hinge replaced and door aligned.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TextButton(
                            onClick = { viewModel.shareReportPdf(context) },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("VIEW FULL REPORT →", color = Teal600, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
