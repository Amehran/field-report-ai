package com.fieldreport.ai.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel

@Entity(
    tableName = "media_items",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reportId")]
)
data class MediaItemEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val type: MediaType,
    val label: PhotoLabel,
    val localUri: String,
    val storagePath: String? = null,
    val sortOrder: Int = 0,
    val isUploaded: Boolean = false
)
