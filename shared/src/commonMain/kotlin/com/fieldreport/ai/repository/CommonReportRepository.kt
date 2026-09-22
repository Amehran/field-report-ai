package com.fieldreport.ai.repository

import com.fieldreport.ai.model.GenerateReportRequest
import com.fieldreport.ai.model.GenerateReportResponse
import com.fieldreport.ai.model.SharedReport
import com.fieldreport.ai.network.CloudRunApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommonReportRepository(
    private val apiClient: CloudRunApiClient = CloudRunApiClient()
) {
    private val _reports = MutableStateFlow<List<SharedReport>>(emptyList())
    val reports: StateFlow<List<SharedReport>> = _reports.asStateFlow()

    fun addReport(report: SharedReport) {
        val current = _reports.value.toMutableList()
        current.add(0, report)
        _reports.value = current
    }

    fun deleteReport(reportId: String) {
        _reports.value = _reports.value.filter { it.id != reportId }
    }

    suspend fun generatePdfReport(
        title: String,
        jobSite: String,
        inspectorName: String,
        notes: List<String>,
        userEmail: String? = null
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
            val newReport = SharedReport(
                id = response.reportId ?: "report_${notes.hashCode()}",
                title = title,
                jobSite = jobSite,
                inspectorName = inspectorName,
                notes = notes,
                summary = response.summary ?: "AI generated field report summary.",
                isDraft = false,
                pdfUrl = response.pdfUrl
            )
            addReport(newReport)
        }
        return response
    }
}
