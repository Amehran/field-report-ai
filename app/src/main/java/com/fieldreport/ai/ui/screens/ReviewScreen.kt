package com.fieldreport.ai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.model.ReportStatus
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReportViewModel,
    onApproveSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val report by viewModel.currentReport.collectAsState()
    val mediaItems by viewModel.currentMedia.collectAsState()
    val currencySymbol by viewModel.currency.collectAsState(initial = "$ USD")
    val settingsTechName by viewModel.technicianName.collectAsState(initial = "")

    val initialTechName = remember(report?.id, settingsTechName) {
        report?.technicianName?.takeIf { it.isNotBlank() } ?: settingsTechName
    }

    var technicianName by remember(report?.id, initialTechName) {
        mutableStateOf(initialTechName)
    }

    LaunchedEffect(initialTechName) {
        if (technicianName.isBlank() && initialTechName.isNotBlank()) {
            technicianName = initialTechName
        }
    }

    var issueDescription by remember(report?.id) {
        mutableStateOf(
            report?.initialStatus?.takeIf { it.isNotBlank() }
                ?: report?.findingsJson?.replace("||", "\n• ")?.takeIf { it.isNotBlank() }
                ?: "Primary issue identified during initial inspection."
        )
    }

    var workDoneText by remember(report?.id) {
        mutableStateOf(
            report?.resolutionStepsJson?.replace("||", "\n• ")?.takeIf { it.isNotBlank() }
                ?: report?.workCompletedJson?.replace("||", "\n• ")?.takeIf { it.isNotBlank() }
                ?: report?.typedNotes?.takeIf { it.isNotBlank() }
                ?: report?.rawTranscript?.takeIf { it.isNotBlank() }
                ?: "Executed primary repair, maintenance, and testing procedures."
        )
    }

    var technicianComments by remember(report?.id) {
        mutableStateOf(report?.technicianComments ?: "")
    }

    if (report?.status == ReportStatus.GENERATING) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Drafting report", style = MaterialTheme.typography.titleLarge, color = Slate900) },
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
                title = { Text("Review report", style = MaterialTheme.typography.titleLarge, color = Slate900) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate900)
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
                val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            viewModel.updateReportReviewData(
                                issueDescription = issueDescription,
                                workDoneText = workDoneText,
                                technicianComments = technicianComments,
                                technicianName = technicianName
                            )
                            viewModel.approveReport()
                            onApproveSuccess()
                        },
                        modifier = Modifier
                            .widthIn(max = 700.dp)
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
                // SECTION 1: HEADER INFO CARD (Technician, Customer/Job, Date)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Technician Inline Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Technician",
                                tint = Teal600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TECHNICIAN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500,
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                BasicTextField(
                                    value = technicianName,
                                    onValueChange = { technicianName = it },
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        color = Slate900,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        if (technicianName.isBlank()) {
                                            Text(
                                                text = "Add Technician Name...",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Slate500.copy(alpha = 0.5f),
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Customer & Job and Date Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CUSTOMER & JOB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500,
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${report?.customerName.orEmpty().ifBlank { "Customer" }} • ${report?.jobTitle.orEmpty().ifBlank { "General Job" }}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Slate900,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            val dateStr = report?.createdAt?.let {
                                SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(it))
                            } ?: SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "DATE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500,
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate600,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION 2: ISSUE CARD (Description & Photos)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Issue",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate900,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = issueDescription,
                            onValueChange = { issueDescription = it },
                            label = { Text("Issue Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Slate200,
                                focusedBorderColor = Teal600,
                                focusedLabelColor = Teal600,
                                unfocusedContainerColor = Slate50,
                                focusedContainerColor = Slate50
                            )
                        )

                        if (mediaItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "ATTACHED PHOTOS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(mediaItems) { media ->
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .background(Slate100, RoundedCornerShape(10.dp))
                                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                                    ) {
                                        AsyncImage(
                                            model = media.localUri,
                                            contentDescription = "Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Surface(
                                            color = Teal600,
                                            shape = RoundedCornerShape(bottomEnd = 6.dp, topStart = 10.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = media.label.name,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION 3: SERVICE CARD (What Work Done & Cost)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Service",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate900,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = workDoneText,
                            onValueChange = { workDoneText = it },
                            label = { Text("What Work Done") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Slate200,
                                focusedBorderColor = Teal600,
                                focusedLabelColor = Teal600,
                                unfocusedContainerColor = Slate50,
                                focusedContainerColor = Slate50
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "COST SUMMARY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val labor = report?.laborCost
                        val parts = report?.partsCost
                        val total = report?.totalCost ?: ((labor ?: 0.0) + (parts ?: 0.0))

                        Surface(
                            color = Slate50,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (labor != null && labor > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Labor:", style = MaterialTheme.typography.bodyMedium, color = Slate600)
                                        Text(String.format("%s %.2f", currencySymbol, labor), style = MaterialTheme.typography.bodyMedium, color = Slate900, fontWeight = FontWeight.Medium)
                                    }
                                }
                                if (parts != null && parts > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Parts:", style = MaterialTheme.typography.bodyMedium, color = Slate600)
                                        Text(String.format("%s %.2f", currencySymbol, parts), style = MaterialTheme.typography.bodyMedium, color = Slate900, fontWeight = FontWeight.Medium)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Slate200)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Amount:", style = MaterialTheme.typography.titleMedium, color = Slate900, fontWeight = FontWeight.Bold)
                                    Text(
                                        String.format("%s %.2f", currencySymbol, total),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Teal600,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION 4: COMMENTS CARD (Technician Comments)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate900,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Additional technician notes or observations for the customer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = technicianComments,
                            onValueChange = { technicianComments = it },
                            placeholder = { Text("Add any extra notes or comments here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Slate200,
                                focusedBorderColor = Teal600,
                                focusedLabelColor = Teal600,
                                unfocusedContainerColor = Slate50,
                                focusedContainerColor = Slate50
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
