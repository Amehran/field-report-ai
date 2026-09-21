package com.fieldreport.ai.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.ui.theme.Slate50
import com.fieldreport.ai.ui.theme.Slate900
import com.fieldreport.ai.ui.viewmodel.ReportViewModel

import android.app.Activity
import android.widget.Toast
import com.fieldreport.ai.ui.components.PaywallSheet

import com.fieldreport.ai.ui.components.FieldReportTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? Activity
    val aiAgentMode by viewModel.aiAgentMode.collectAsState(initial = AiAgentMode.CLOUD)
    val businessName by viewModel.businessName.collectAsState(initial = "")
    val settingsTechnicianName by viewModel.technicianName.collectAsState(initial = "")
    val currency by viewModel.currency.collectAsState(initial = "$")

    val billingProducts by viewModel.billingProducts.collectAsState()
    val freePdfsRemaining by viewModel.freePdfsRemaining.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val isLifetime by viewModel.isLifetime.collectAsState()
    val subscriptionTier by viewModel.subscriptionTier.collectAsState()

    var showPaywallSheet by remember { mutableStateOf(false) }
    var localTechnicianName by remember(settingsTechnicianName) { mutableStateOf(settingsTechnicianName) }
    var localBusinessName by remember(businessName) { mutableStateOf(businessName) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = androidx.compose.ui.graphics.Color.White)
                    }
                }
            )
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Subscription & Plan", style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isLifetime) "Pro Lifetime Member" else if (isSubscribed) "Pro Subscription Active" else "Free Trial ($freePdfsRemaining PDF exports remaining)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSubscribed || isLifetime) "You have unlimited access to PDF exports & cloud AI features." else "Upgrade to Field Report Pro for unlimited exports and priority support.",
                        style = MaterialTheme.typography.bodySmall
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
            Text("AI Agent Selection", style = MaterialTheme.typography.titleMedium)
            
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
            
            Text("Business Information", style = MaterialTheme.typography.titleMedium)

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
        }
    }
}
