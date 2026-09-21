package com.fieldreport.ai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel

import android.app.Activity
import com.fieldreport.ai.ui.components.PaywallSheet
import com.fieldreport.ai.ui.components.FieldReportTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportReadyScreen(
    viewModel: ReportViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val clipboardManager = LocalClipboardManager.current
    val report by viewModel.currentReport.collectAsState()
    val billingProducts by viewModel.billingProducts.collectAsState()
    val freePdfsRemaining by viewModel.freePdfsRemaining.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val isLifetime by viewModel.isLifetime.collectAsState()

    var showDeletePrompt by remember { mutableStateOf(false) }
    var showPaywallSheet by remember { mutableStateOf(false) }

    val handleShare = {
        viewModel.checkPdfExportEligibility { canExport, requiresPaywall ->
            if (canExport) {
                viewModel.shareReportPdf(context)
                showDeletePrompt = true
            } else if (requiresPaywall) {
                showPaywallSheet = true
            }
        }
    }

    if (showPaywallSheet) {
        PaywallSheet(
            products = billingProducts,
            onDismiss = { showPaywallSheet = false },
            onPurchaseTier = { _, productDetails ->
                if (activity != null && productDetails != null) {
                    viewModel.launchBillingFlow(activity, productDetails)
                } else {
                    Toast.makeText(context, "Google Play Store unavailable in emulator mode.", Toast.LENGTH_SHORT).show()
                }
            },
            onRestorePurchases = {
                viewModel.restorePurchases { success ->
                    if (success) {
                        Toast.makeText(context, "Purchases restored!", Toast.LENGTH_SHORT).show()
                        showPaywallSheet = false
                    } else {
                        Toast.makeText(context, "No active subscriptions found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    if (showDeletePrompt) {
        AlertDialog(
            onDismissRequest = { showDeletePrompt = false },
            title = { Text("Delete Report Data?") },
            text = {
                Text("You have shared this report. Would you like to delete the report record and all associated media (photos, audio) from your device?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePrompt = false
                        report?.id?.let { viewModel.deleteReport(it) }
                        onDone()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeletePrompt = false
                        onDone()
                    }
                ) {
                    Text("Keep")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FieldReportTopBar(
                title = "Report Ready",
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val statusText = if (isSubscribed || isLifetime) "Pro Active" else "$freePdfsRemaining free exports"
                    val statusColor = if (isSubscribed || isLifetime) Emerald700 else Color.White
                    val statusBg = if (isSubscribed || isLifetime) Emerald100 else Color.White.copy(alpha = 0.2f)

                    Surface(
                        color = statusBg,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { if (!isSubscribed && !isLifetime) showPaywallSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 700.dp)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { handleShare() },
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
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 700.dp)
                    .fillMaxWidth()
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
                            onClick = { handleShare() },
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
}
