package com.fieldreport.ai.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.ui.theme.Slate50
import com.fieldreport.ai.ui.theme.Teal600
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.fieldreport.ai.ui.theme.Slate900

import com.fieldreport.ai.ui.components.FieldReportTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(
    viewModel: ReportViewModel,
    onNavigateToCapture: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReview: (String) -> Unit
) {
    val reports by viewModel.allReports.collectAsState(initial = emptyList())
    var showMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            FieldReportTopBar(
                title = "Field Reports",
                subtitle = "${reports.size} active report${if (reports.size == 1) "" else "s"}",
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "More options", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete All", color = Color.Red) },
                                onClick = {
                                    viewModel.deleteAllReports()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startNewReport("", "")
                    onNavigateToCapture()
                },
                containerColor = Teal600,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Report")
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved reports yet.\nTap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    ReportListItem(
                        report = report,
                        onClick = {
                            viewModel.setCurrentReportId(report.id)
                            if (report.status == ReportStatus.DRAFT || report.status == ReportStatus.NEEDS_REVIEW) {
                                onNavigateToCapture()
                            } else {
                                onNavigateToReview(report.id)
                            }
                        },
                        onDelete = {
                            viewModel.deleteReport(report.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportListItem(report: ReportEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.jobTitle.ifBlank { "Untitled Job" },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(status = report.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.customerName.ifBlank { "Unknown Customer" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(report.createdAt))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(androidx.compose.material.icons.Icons.Default.Delete, contentDescription = "Delete Report", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun StatusChip(status: ReportStatus) {
    val (bgColor, textColor, text) = when (status) {
        ReportStatus.DRAFT -> Triple(Color(0xFFE2E8F0), Color(0xFF475569), "Draft")
        ReportStatus.WAITING_ONLINE -> Triple(Color(0xFFFEF08A), Color(0xFF854D0E), "Waiting Online")
        ReportStatus.GENERATING -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "Generating")
        ReportStatus.NEEDS_REVIEW -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Needs Review")
        ReportStatus.APPROVED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Approved")
        ReportStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Completed")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
