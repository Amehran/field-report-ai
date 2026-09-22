package com.fieldreport.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class SharedReport(
    val id: String = "",
    val title: String = "Field Inspection Report",
    val jobSite: String = "",
    val inspectorName: String = "",
    val date: String = "",
    val clientName: String = "",
    val summary: String = "",
    val notes: List<String> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val isDraft: Boolean = true,
    val pdfUrl: String? = null
)

@Serializable
data class GenerateReportRequest(
    val title: String,
    val jobSite: String,
    val inspectorName: String,
    val notes: List<String>,
    val userEmail: String? = null
)

@Serializable
data class GenerateReportResponse(
    val success: Boolean,
    val reportId: String? = null,
    val pdfUrl: String? = null,
    val summary: String? = null,
    val message: String? = null
)
