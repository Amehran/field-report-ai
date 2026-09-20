package com.fieldreport.ai.data.db

import androidx.room.TypeConverter
import com.fieldreport.ai.data.model.AiAgentMode
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus

class Converters {
    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String = status.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = enumValueOf(value)

    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = enumValueOf(value)

    @TypeConverter
    fun fromPhotoLabel(label: PhotoLabel): String = label.name

    @TypeConverter
    fun toPhotoLabel(value: String): PhotoLabel = enumValueOf(value)

    @TypeConverter
    fun fromAiAgentMode(mode: AiAgentMode): String = mode.name

    @TypeConverter
    fun toAiAgentMode(value: String): AiAgentMode = enumValueOf(value)
}
