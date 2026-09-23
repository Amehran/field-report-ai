package com.fieldreport.ai.repository

import com.fieldreport.ai.model.GenerateReportRequest
import com.fieldreport.ai.model.GenerateReportResponse
import com.fieldreport.ai.model.SharedReport
import com.fieldreport.ai.network.CloudRunApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommonReportRepository(
    private val apiClient: CloudRunApiClient
) {
    constructor() : this(CloudRunApiClient())

    private val _reports = MutableStateFlow<List<SharedReport>>(emptyList())
    val reports: StateFlow<List<SharedReport>> = _reports.asStateFlow()

    fun getReportsList(): List<SharedReport> = _reports.value

    fun addReport(report: SharedReport) {
        val current = _reports.value.toMutableList()
        current.add(0, report)
        _reports.value = current
    }

    fun upsertReport(report: SharedReport) {
        val current = _reports.value.toMutableList()
        val index = current.indexOfFirst { it.id == report.id }
        if (index != -1) {
            current[index] = report
        } else {
            current.add(0, report)
        }
        _reports.value = current
    }

    fun deleteReport(reportId: String) {
        _reports.value = _reports.value.filter { it.id != reportId }
    }

    @kotlin.jvm.JvmOverloads
    suspend fun generatePdfReport(
        title: String,
        jobSite: String,
        inspectorName: String,
        notes: List<String>,
        userEmail: String? = null,
        existingReportId: String? = null
    ): GenerateReportResponse {
        val request = GenerateReportRequest(
            title = title,
            jobSite = jobSite,
            inspectorName = inspectorName,
            notes = notes,
            userEmail = userEmail
        )
        val response = apiClient.generateReport(request)
        if (response.success && response.pdfUrl != null) {
            val targetId = existingReportId ?: response.reportId ?: "report_${notes.hashCode()}"
            val newReport = SharedReport(
                id = targetId,
                title = title,
                jobSite = jobSite,
                inspectorName = inspectorName,
                notes = notes,
                summary = response.summary ?: "AI generated field report summary.",
                isDraft = false,
                pdfUrl = response.pdfUrl
            )
            upsertReport(newReport)
        }
        return response
    }
}
