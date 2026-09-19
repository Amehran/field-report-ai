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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    viewModel: ReportViewModel,
    onNavigateToRecord: () -> Unit,
    onNavigateToReview: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("Miller Residence") }
    var jobTitle by remember { mutableStateOf("Kitchen repair") }
    var typedNotes by remember { mutableStateOf("") }
    var isTypingNotes by remember { mutableStateOf(false) }

    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf(PhotoLabel.BEFORE) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val currentReport by viewModel.currentReport.collectAsState()
    val mediaItems by viewModel.currentMedia.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addPhoto(it.toString(), selectedLabel)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            viewModel.addPhoto(tempCameraUri.toString(), selectedLabel)
        }
    }

    fun launchCamera() {
        val imageDir = File(context.cacheDir, "images")
        if (!imageDir.exists()) imageDir.mkdirs()
        val file = File(imageDir, "photo_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
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
            TopAppBar(
                title = { Text("New report", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.generateReportDraft(typedNotes.ifBlank { null })
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
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer / Job Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Work Description / Trade") },
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

            if (isTypingNotes) {
                OutlinedTextField(
                    value = typedNotes,
                    onValueChange = { typedNotes = it },
                    placeholder = { Text("Type your job notes here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                TextButton(onClick = { isTypingNotes = false }) {
                    Text("Switch to Voice Note", color = Teal600)
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecord() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Teal100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Record",
                                tint = Teal600
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Record a voice note",
                                style = MaterialTheme.typography.titleLarge,
                                color = Slate900
                            )
                            Text(
                                text = "or type notes instead",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate500,
                                modifier = Modifier.clickable { isTypingNotes = true }
                            )
                        }
                    }
                }
            }
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
