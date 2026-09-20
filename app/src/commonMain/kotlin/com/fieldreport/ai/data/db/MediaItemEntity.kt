package com.fieldreport.ai.data.db

import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel

data class MediaItemEntity(
    val id: String,
    val reportId: String,
    val type: MediaType,
    val label: PhotoLabel,
    val localUri: String,
    val storagePath: String? = null,
    val sortOrder: Int = 0,
    val isUploaded: Boolean = false
)
