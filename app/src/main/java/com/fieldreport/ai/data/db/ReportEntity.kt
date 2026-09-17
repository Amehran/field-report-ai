package com.fieldreport.ai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null
)
