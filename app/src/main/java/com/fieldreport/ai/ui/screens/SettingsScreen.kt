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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
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

    // Launchers for Logo & Signature
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.setCompanyLogoUri(uri.toString())
        }
    }

    val signaturePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.setSignatureUri(uri.toString())
        }
    }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            viewModel.setSignatureUri(tempCameraUri.toString())
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
}
