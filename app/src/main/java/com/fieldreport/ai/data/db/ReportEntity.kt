package com.fieldreport.ai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.ReportStatus

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val status: ReportStatus,
    val customerName: String,
    val jobTitle: String,
    val address: String? = null,
    val referenceNumber: String? = null,
    val typedNotes: String? = null,
    val audioLocalUri: String? = null,
    val audioStoragePath: String? = null,
    val rawTranscript: String? = null,
    val customerSummary: String? = null,
    val workCompletedJson: String? = null,
    val findingsJson: String? = null,
    val recommendationsJson: String? = null,
    val pdfLocalPath: String? = null,
    val laborCost: Double? = null,
    val partsCost: Double? = null,
    val totalCost: Double? = null,
    val initialStatus: String? = null,
    val resolutionStepsJson: String? = null,
    val currentOperationalState: String? = null,
    val technicianName: String? = null,
    val technicianComments: String? = null,
    val aiAgentMode: AiAgentMode = AiAgentMode.CLOUD,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null
)
