package com.fieldreport.ai.data.model

enum class ReportStatus {
    DRAFT,
    REPORT_CREATED,
    APPROVED,
    SHARED,
    FINAL_REPORT
}

enum class AiAgentMode {
    CLOUD,
    ON_DEVICE
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class PhotoLabel {
    BEFORE,
    AFTER,
    GENERAL
}

enum class MediaType {
    PHOTO,
    AUDIO
}

data class LocalMediaItem(
    val id: String,
    val reportId: String,
    val type: MediaType,
    val label: PhotoLabel,
    val localUri: String,
    val storagePath: String? = null,
    val sortOrder: Int = 0
)

data class ReportDraftData(
    val customerSummary: String = "",
    val workCompleted: List<String> = emptyList(),
    val findings: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val uncertainties: List<String> = emptyList()
)
