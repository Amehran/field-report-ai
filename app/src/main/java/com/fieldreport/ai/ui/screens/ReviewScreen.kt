package com.fieldreport.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import com.fieldreport.ai.data.model.ReportStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReportViewModel,
    onApproveSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val report by viewModel.currentReport.collectAsState()

    var workCompletedText by remember(report) {
        mutableStateOf(
            report?.workCompletedJson?.replace("||", "\n• ")
                ?: "Replaced damaged cabinet hinge and realigned the kitchen cabinet door."
        )
    }
    var findingsText by remember(report) {
        mutableStateOf(
            report?.findingsJson?.replace("||", "\n• ")
                ?: "Minor moisture marks were visible near the cabinet base."
        )
    }
    var recsText by remember(report) {
        mutableStateOf(
            report?.recommendationsJson?.replace("||", "\n• ")
                ?: "Monitor adjacent moisture and arrange an inspection if it returns."
        )
    }

    if (report?.status == ReportStatus.GENERATING) {
        Scaffold(containerColor = Slate50) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Teal600)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "AI is drafting your report...",
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Analyzing your voice notes and photos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review report", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.approveReport()
                            onApproveSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal900)
                    ) {
                        Text(
                            text = "Approve report",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
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
            // Amber AI Warning Banner
            Surface(
                color = Amber100,
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Amber800,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI DRAFT — REVIEW REQUIRED",
                        color = Amber800,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Card 1: Work Completed
            ReviewSectionCard(
                title = "Work completed",
                value = workCompletedText,
                onValueChange = { workCompletedText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Findings
            ReviewSectionCard(
                title = "Findings",
                value = findingsText,
                onValueChange = { findingsText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Recommended Next Steps
            ReviewSectionCard(
                title = "Recommended next steps",
                value = recsText,
                onValueChange = { recsText = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReviewSectionCard(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate900
                )
                TextButton(onClick = { isEditing = !isEditing }) {
                    Text(
                        text = if (isEditing) "Done" else "Edit",
                        color = Teal600,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate600
                )
            }
        }
    }
}
