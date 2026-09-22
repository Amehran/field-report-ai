package com.fieldreport.ai.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.ThemeMode
import com.fieldreport.ai.ui.components.FieldReportTopBar
import com.fieldreport.ai.ui.components.PaywallSheet
import com.fieldreport.ai.ui.viewmodel.ReportViewModel
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.Create
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val aiAgentMode by viewModel.aiAgentMode.collectAsState(initial = AiAgentMode.CLOUD)
    val businessName by viewModel.businessName.collectAsState(initial = "")
    val settingsTechnicianName by viewModel.technicianName.collectAsState(initial = "")
    val currency by viewModel.currency.collectAsState(initial = "$")
    val companyLogoUri by viewModel.companyLogoUri.collectAsState(initial = null)
    val signatureUri by viewModel.signatureUri.collectAsState(initial = null)

    val billingProducts by viewModel.billingProducts.collectAsState()
    val freePdfsRemaining by viewModel.freePdfsRemaining.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val isLifetime by viewModel.isLifetime.collectAsState()

    var showPaywallSheet by remember { mutableStateOf(false) }
    var localTechnicianName by remember(settingsTechnicianName) { mutableStateOf(settingsTechnicianName) }
    var localBusinessName by remember(businessName) { mutableStateOf(businessName) }

    var showSignaturePadDialog by remember { mutableStateOf(false) }

    // Helper to copy selected/captured URIs to internal storage for permanent access
    fun copyUriToInternalStorage(sourceUri: Uri, fileName: String): String? {
        return try {
            val brandingDir = File(context.filesDir, "branding")
            if (!brandingDir.exists()) brandingDir.mkdirs()
            val destFile = File(brandingDir, fileName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.toURI().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToInternalStorage(bitmap: Bitmap, fileName: String): String? {
        return try {
            val brandingDir = File(context.filesDir, "branding")
            if (!brandingDir.exists()) brandingDir.mkdirs()
            val destFile = File(brandingDir, fileName)
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            destFile.toURI().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Launchers for Logo & Signature
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = copyUriToInternalStorage(uri, "company_logo.png") ?: uri.toString()
            viewModel.setCompanyLogoUri(savedPath)
        }
    }

    val signaturePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = copyUriToInternalStorage(uri, "signature.png") ?: uri.toString()
            viewModel.setSignatureUri(savedPath)
        }
    }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            val savedPath = copyUriToInternalStorage(tempCameraUri!!, "signature.png") ?: tempCameraUri.toString()
            viewModel.setSignatureUri(savedPath)
        }
    }

    LaunchedEffect(localTechnicianName) {
        kotlinx.coroutines.delay(500)
        if (localTechnicianName != settingsTechnicianName) {
            viewModel.setTechnicianName(localTechnicianName)
        }
    }

    LaunchedEffect(localBusinessName) {
        kotlinx.coroutines.delay(500)
        if (localBusinessName != businessName) {
            viewModel.setBusinessName(localBusinessName)
        }
    }

    var showProSuccessDialog by remember { mutableStateOf(false) }

    if (showProSuccessDialog) {
        com.fieldreport.ai.ui.components.ProSuccessDialog(
            onDismiss = { showProSuccessDialog = false }
        )
    }

    if (showPaywallSheet) {
        PaywallSheet(
            products = billingProducts,
            onDismiss = { showPaywallSheet = false },
            onPurchaseTier = { tier, productDetails ->
                if (activity != null && productDetails != null) {
                    viewModel.launchBillingFlow(activity, productDetails)
                } else {
                    viewModel.simulatePurchase(tier) {
                        showPaywallSheet = false
                        showProSuccessDialog = true
                    }
                }
            },
            onSimulatePurchase = { tier ->
                viewModel.simulatePurchase(tier) {
                    showPaywallSheet = false
                    showProSuccessDialog = true
                }
            },
            onRestorePurchases = {
                viewModel.restorePurchases { success ->
                    if (success) {
                        Toast.makeText(context, "Purchases restored!", Toast.LENGTH_SHORT).show()
                        showPaywallSheet = false
                        showProSuccessDialog = true
                    } else {
                        Toast.makeText(context, "No active subscriptions found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FieldReportTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Account & Authentication", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (currentUser?.email != null) currentUser.email!! else if (currentUser != null) "Signed in as ${if (currentUser.isAnonymous) "Guest (${currentUser.uid.take(8)})" else currentUser.uid.take(8)}" else "Not Signed In",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentUser != null) "Your report data and account settings are active." else "Sign in to synchronize reports across devices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            onNavigateToLogin()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (currentUser != null) "Sign Out / Switch Account" else "Log In")
                    }
                }
            }
            Text("Subscription & Plan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isLifetime) "Pro Lifetime Member" else if (isSubscribed) "Pro Subscription Active" else "Free Trial ($freePdfsRemaining PDF exports remaining)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSubscribed || isLifetime) "You have unlimited access to PDF exports & cloud AI features." else "Upgrade to Field Report Pro for unlimited exports and priority support.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!isSubscribed && !isLifetime) {
                        Button(
                            onClick = { showPaywallSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upgrade to Pro")
                        }
                    }
                }
            }

            Text("App Theme", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            val currentThemeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = currentThemeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("System")
                }
                SegmentedButton(
                    selected = currentThemeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Light")
                }
                SegmentedButton(
                    selected = currentThemeMode == ThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Dark")
                }
            }

            Text("AI Agent Selection", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = aiAgentMode == AiAgentMode.ON_DEVICE,
                    onClick = { viewModel.setAiAgentMode(AiAgentMode.ON_DEVICE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("On-Device (Local)")
                }
                SegmentedButton(
                    selected = aiAgentMode == AiAgentMode.CLOUD,
                    onClick = { viewModel.setAiAgentMode(AiAgentMode.CLOUD) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Cloud (Gemini)")
                }
            }

            Text("Business Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            OutlinedTextField(
                value = localTechnicianName,
                onValueChange = { localTechnicianName = it },
                label = { Text("Technician Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localBusinessName,
                onValueChange = { localBusinessName = it },
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth()
            )

            var isCurrencyDropdownExpanded by remember { mutableStateOf(false) }
            val currencyOptions = remember {
                listOf("$ USD", "$ CAD", "€ EUR", "£ GBP", "$ AUD", "¥ JPY", "₹ INR", "CHF", "$ NZD", "$ MXN")
            }
            val selectedCurrency = when (currency) {
                "$" -> "$ USD"
                in currencyOptions -> currency
                else -> currency.ifBlank { "$ USD" }
            }

            ExposedDropdownMenuBox(
                expanded = isCurrencyDropdownExpanded,
                onExpandedChange = { isCurrencyDropdownExpanded = !isCurrencyDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isCurrencyDropdownExpanded,
                    onDismissRequest = { isCurrencyDropdownExpanded = false }
                ) {
                    currencyOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.setCurrency(option)
                                isCurrencyDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Text("Company Logo & Signature", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            // Company Logo Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Company Logo",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!companyLogoUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = companyLogoUri,
                                contentDescription = "Company Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { logoPickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (companyLogoUri.isNullOrBlank()) "Upload Logo" else "Change Logo")
                        }

                        if (!companyLogoUri.isNullOrBlank()) {
                            IconButton(onClick = { viewModel.setCompanyLogoUri(null) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Logo", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // Technician Signature Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Technician Signature",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!signatureUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = signatureUri,
                                contentDescription = "Technician Signature",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showSignaturePadDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Draw")
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val photoFile = File(context.cacheDir, "sig_capture_${System.currentTimeMillis()}.jpg")
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open camera.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }

                        OutlinedButton(
                            onClick = { signaturePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery")
                        }

                        if (!signatureUri.isNullOrBlank()) {
                            IconButton(onClick = { viewModel.setSignatureUri(null) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Signature", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSignaturePadDialog) {
        SignaturePadDialog(
            onDismiss = { showSignaturePadDialog = false },
            onSaveSignature = { bitmap ->
                val savedPath = saveBitmapToInternalStorage(bitmap, "signature.png")
                viewModel.setSignatureUri(savedPath)
                showSignaturePadDialog = false
            }
        )
    }
}

@Composable
fun SignaturePadDialog(
    onDismiss: () -> Unit,
    onSaveSignature: (Bitmap) -> Unit
) {
    var paths by remember { mutableStateOf(listOf<androidx.compose.ui.graphics.Path>()) }
    var currentPath by remember { mutableStateOf<androidx.compose.ui.graphics.Path?>(null) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Draw Technician Signature",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(androidx.compose.ui.graphics.Color.White)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    currentPath = newPath
                                },
                                onDrag = { change, _ ->
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    currentPath = currentPath?.let { p ->
                                        androidx.compose.ui.graphics.Path().apply { addPath(p) }
                                    }
                                },
                                onDragEnd = {
                                    currentPath?.let { p ->
                                        paths = paths + p
                                    }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        paths.forEach { path ->
                            drawPath(
                                path = path,
                                color = androidx.compose.ui.graphics.Color(0xFF0F766E),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 4f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )
                        }
                        currentPath?.let { path ->
                            drawPath(
                                path = path,
                                color = androidx.compose.ui.graphics.Color(0xFF0F766E),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 4f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        paths = emptyList()
                        currentPath = null
                    }) {
                        Text("Clear")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (paths.isNotEmpty()) {
                                val bitmap = Bitmap.createBitmap(600, 300, Bitmap.Config.ARGB_8888)
                                val canvas = android.graphics.Canvas(bitmap)
                                canvas.drawColor(android.graphics.Color.WHITE)
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.parseColor("#0F766E")
                                    strokeWidth = 6f
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeCap = android.graphics.Paint.Cap.ROUND
                                    strokeJoin = android.graphics.Paint.Join.ROUND
                                    isAntiAlias = true
                                }
                                paths.forEach { composePath ->
                                    val androidPath = composePath.asAndroidPath()
                                    val matrix = android.graphics.Matrix()
                                    matrix.postScale(2f, 1.5f)
                                    val scaledPath = android.graphics.Path()
                                    androidPath.transform(matrix, scaledPath)
                                    canvas.drawPath(scaledPath, paint)
                                }
                                onSaveSignature(bitmap)
                            }
                        }
                    ) {
                        Text("Save Signature")
                    }
                }
            }
        }
    }
}
