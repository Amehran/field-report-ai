package com.fieldreport.ai.ui.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val aiAgentMode by viewModel.aiAgentMode.collectAsState(initial = AiAgentMode.CLOUD)
    val businessName by viewModel.businessName.collectAsState(initial = "")
    val settingsTechnicianName by viewModel.technicianName.collectAsState(initial = "")
    val currency by viewModel.currency.collectAsState(initial = "$")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, color = Slate900) },
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
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                value = settingsTechnicianName,
                onValueChange = { viewModel.setTechnicianName(it) },
                label = { Text("Technician Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = businessName,
                onValueChange = { viewModel.setBusinessName(it) },
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
