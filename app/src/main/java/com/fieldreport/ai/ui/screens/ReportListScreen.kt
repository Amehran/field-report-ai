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
    onNavigateToReview: (String) -> Unit,
    onNavigateToReportReady: (String) -> Unit = {}
) {
    val reports by viewModel.allReports.collectAsState(initial = emptyList())
    var showMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            FieldReportTopBar(
                title = "Field Reports",
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.onSurface)
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
        containerColor = MaterialTheme.colorScheme.background
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
                            when (report.status) {
                                ReportStatus.DRAFT, ReportStatus.WAITING_ONLINE, ReportStatus.GENERATING -> onNavigateToCapture()
                                ReportStatus.NEEDS_REVIEW -> onNavigateToReview(report.id)
                                ReportStatus.APPROVED, ReportStatus.GENERATED, ReportStatus.SHARED, ReportStatus.COMPLETED -> onNavigateToReportReady(report.id)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        color = MaterialTheme.colorScheme.onSurface,
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
                Icon(
                    androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete Report",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: ReportStatus) {
    val isDark = MaterialTheme.colorScheme.surface.red < 0.5f
    val (bgColor, textColor, text) = when (status) {
        ReportStatus.DRAFT -> if (isDark) Triple(Color(0xFF334155), Color(0xFFE2E8F0), "Draft") else Triple(Color(0xFFE2E8F0), Color(0xFF475569), "Draft")
        ReportStatus.WAITING_ONLINE -> if (isDark) Triple(Color(0xFF713F12), Color(0xFFFEF08A), "Waiting Online") else Triple(Color(0xFFFEF08A), Color(0xFF854D0E), "Waiting Online")
        ReportStatus.GENERATING -> if (isDark) Triple(Color(0xFF1E3A8A), Color(0xFFBFDBFE), "Generating") else Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "Generating")
        ReportStatus.NEEDS_REVIEW -> if (isDark) Triple(Color(0xFF78350F), Color(0xFFFDE68A), "Needs Review") else Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Needs Review")
        ReportStatus.APPROVED -> if (isDark) Triple(Color(0xFF14532D), Color(0xFFBBF7D0), "Approved") else Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Approved")
        ReportStatus.GENERATED -> if (isDark) Triple(Color(0xFF14532D), Color(0xFFBBF7D0), "Generated") else Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Generated")
        ReportStatus.SHARED -> if (isDark) Triple(Color(0xFF0369A1), Color(0xFFE0F2FE), "Shared") else Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Shared")
        ReportStatus.COMPLETED -> if (isDark) Triple(Color(0xFF14532D), Color(0xFFBBF7D0), "Completed") else Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Completed")
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
