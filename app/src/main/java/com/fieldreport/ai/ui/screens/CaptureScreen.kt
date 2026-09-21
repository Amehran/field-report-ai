package com.fieldreport.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.ui.theme.*
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import com.fieldreport.ai.ui.components.FieldReportTopBar

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

class ExplicitTakePictureContract : ActivityResultContract<Uri, Boolean>() {
    override fun createIntent(context: Context, input: Uri): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, input)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            clipData = ClipData.newRawUri("ImageCapture", input)
        }

        try {
            val resInfoList = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    input,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    viewModel: ReportViewModel,
    onNavigateToRecord: () -> Unit,
    onNavigateToReview: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var typedNotes by remember { mutableStateOf("") }
    var isTypingNotes by remember { mutableStateOf(false) }

    var laborCostStr by remember { mutableStateOf("") }
    var partsCostStr by remember { mutableStateOf("") }
    var totalCostStr by remember { mutableStateOf("") }

    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf(PhotoLabel.BEFORE) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val currentReport by viewModel.currentReport.collectAsState()
    val mediaItems by viewModel.currentMedia.collectAsState()

    LaunchedEffect(currentReport?.id, currentReport?.laborCost, currentReport?.partsCost, currentReport?.totalCost) {
        currentReport?.let { report ->
            customerName = report.customerName
            jobTitle = report.jobTitle
            val notes = report.typedNotes ?: ""
            typedNotes = notes
            if (notes.isNotBlank()) {
                isTypingNotes = true
            }
            val l = report.laborCost ?: 0.0
            val p = report.partsCost ?: 0.0
            val t = report.totalCost ?: (l + p)

            laborCostStr = if (report.laborCost != null && report.laborCost != 0.0) formatCostValue(l) else (if (report.laborCost == 0.0) "0" else "")
            partsCostStr = if (report.partsCost != null && report.partsCost != 0.0) formatCostValue(p) else (if (report.partsCost == 0.0) "0" else "")
            totalCostStr = formatCostValue(t)
        }
    }

    LaunchedEffect(customerName, jobTitle) {
        kotlinx.coroutines.delay(500)
        val report = currentReport
        if (report != null && (customerName != report.customerName || jobTitle != report.jobTitle)) {
            viewModel.updateReportHeader(customerName, jobTitle)
        }
    }

    LaunchedEffect(laborCostStr, partsCostStr, totalCostStr) {
        kotlinx.coroutines.delay(500)
        val report = currentReport
        val l = laborCostStr.toDoubleOrNull() ?: 0.0
        val p = partsCostStr.toDoubleOrNull() ?: 0.0
        val t = totalCostStr.toDoubleOrNull() ?: (l + p)
        if (report != null && (l != (report.laborCost ?: 0.0) || p != (report.partsCost ?: 0.0) || t != (report.totalCost ?: 0.0))) {
            viewModel.updateCosts(l, p, t)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addPhoto(it.toString(), selectedLabel)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ExplicitTakePictureContract()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            viewModel.addPhoto(tempCameraUri.toString(), selectedLabel)
        }
    }

    fun startCameraIntent() {
        try {
            val imageDir = File(context.cacheDir, "images")
            if (!imageDir.exists()) imageDir.mkdirs()
            val file = File(imageDir, "photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open camera. Opening gallery...", Toast.LENGTH_SHORT).show()
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCameraIntent()
        } else {
            Toast.makeText(context, "Camera permission denied. Opening gallery...", Toast.LENGTH_SHORT).show()
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startCameraIntent()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun launchGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    LaunchedEffect(Unit) {
        if (currentReport == null) {
            viewModel.startNewReport(customerName, jobTitle)
        }
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Add Photo", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Capture a new photo with your camera or select an existing photo from gallery.") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    launchCamera()
                }) {
                    Text("📷 Camera", color = Teal600)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    launchGallery()
                }) {
                    Text("🖼️ Gallery", color = Slate600)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FieldReportTopBar(
                title = "New Field Report",
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.generateReportDraft(customerName, jobTitle, typedNotes.ifBlank { null })
                            onNavigateToReview()
                        },
                        modifier = Modifier
                            .widthIn(max = 700.dp)
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal900)
                    ) {
                        Text(
                            text = "Generate report",
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
            // Customer & Job Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    currentReport?.let { report ->
                        val dateStr = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date(report.createdAt))
                        Text(
                            text = "Report Date: $dateStr",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate500,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        placeholder = { Text("e.g. Miller Residence or John Smith") },
                        trailingIcon = {
                            if (customerName.isNotEmpty()) {
                                IconButton(onClick = { customerName = "" }) {
                                    Icon(androidx.compose.material.icons.Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Job Name") },
                        placeholder = { Text("e.g. Kitchen Repair or HVAC Service") },
                        trailingIcon = {
                            if (jobTitle.isNotEmpty()) {
                                IconButton(onClick = { jobTitle = "" }) {
                                    Icon(androidx.compose.material.icons.Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Photos Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Photos",
                    style = MaterialTheme.typography.titleLarge,
                    color = Slate900
                )
                TextButton(onClick = {
                    selectedLabel = PhotoLabel.BEFORE
                    showPhotoDialog = true
                }) {
                    Text("+ Add photo", color = Teal600, style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo List Carousel
            if (mediaItems.isEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    DummyPhotoCard("BEFORE", Slate100, Slate600) {
                        selectedLabel = PhotoLabel.BEFORE
                        showPhotoDialog = true
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    DummyPhotoCard("AFTER", Sky100, Sky700) {
                        selectedLabel = PhotoLabel.AFTER
                        showPhotoDialog = true
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(mediaItems) { media ->
                        PhotoBadgeCard(item = media) {
                            viewModel.togglePhotoLabel(media)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Describe the Work Section
            Text(
                text = "Describe the work",
                style = MaterialTheme.typography.titleLarge,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input Mode Selector Tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                SegmentedButton(
                    selected = !isTypingNotes,
                    onClick = { isTypingNotes = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("🎙️ Voice Note")
                }
                SegmentedButton(
                    selected = isTypingNotes,
                    onClick = { isTypingNotes = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("✏️ Type Notes")
                }
            }

            if (isTypingNotes) {
                OutlinedTextField(
                    value = typedNotes,
                    onValueChange = { typedNotes = it },
                    label = { Text("Work Description") },
                    placeholder = { Text("Describe the work completed, findings, and recommendations...") },
                    trailingIcon = {
                        if (typedNotes.isNotEmpty()) {
                            IconButton(onClick = { typedNotes = "" }) {
                                Icon(androidx.compose.material.icons.Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                val hasAudio = currentReport?.audioLocalUri != null
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecord() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (hasAudio) Emerald100.copy(alpha = 0.4f) else Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (hasAudio) Emerald700 else Slate200))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (hasAudio) Emerald100 else Teal100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = if (hasAudio) "Voice Note Recorded" else "Record",
                                tint = if (hasAudio) Emerald700 else Teal600
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (hasAudio) "Voice note recorded ✓" else "Record a voice note",
                                style = MaterialTheme.typography.titleLarge,
                                color = Slate900
                            )
                            Text(
                                text = if (hasAudio) "Tap to re-record or update voice recording" else "Tap to open voice recorder",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (hasAudio) Slate600 else Slate500
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing Section
            Text(
                text = "Pricing (Optional)",
                style = MaterialTheme.typography.titleLarge,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = laborCostStr,
                            onValueChange = { newValue ->
                                laborCostStr = newValue
                                totalCostStr = calculateTotalCostString(newValue, partsCostStr)
                            },
                            label = { Text("Labor Cost") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = partsCostStr,
                            onValueChange = { newValue ->
                                partsCostStr = newValue
                                totalCostStr = calculateTotalCostString(laborCostStr, newValue)
                            },
                            label = { Text("Parts Cost") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = totalCostStr,
                        onValueChange = { totalCostStr = it },
                        label = { Text("Total Cost") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
}

@Composable
fun PhotoBadgeCard(item: MediaItemEntity, onToggleLabel: () -> Unit) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .background(Slate200, RoundedCornerShape(12.dp))
            .clickable { onToggleLabel() }
    ) {
        if (!item.localUri.startsWith("dummy_")) {
            AsyncImage(
                model = item.localUri,
                contentDescription = "Photo ${item.label.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Slate200, RoundedCornerShape(12.dp))
            )
        }

        val (bgColor, textColor) = when (item.label) {
            PhotoLabel.BEFORE -> Slate100 to Slate600
            PhotoLabel.AFTER -> Sky100 to Sky700
            PhotoLabel.GENERAL -> Slate100 to Slate900
        }

        Surface(
            color = bgColor,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text(
                text = item.label.name,
                color = textColor,
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DummyPhotoCard(label: String, bgColor: Color, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .background(Slate200, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            color = bgColor,
            shape = CircleShape,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

private fun formatCostValue(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(java.util.Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    }
}

private fun calculateTotalCostString(laborStr: String, partsStr: String): String {
    val l = laborStr.toDoubleOrNull() ?: 0.0
    val p = partsStr.toDoubleOrNull() ?: 0.0
    return formatCostValue(l + p)
}
