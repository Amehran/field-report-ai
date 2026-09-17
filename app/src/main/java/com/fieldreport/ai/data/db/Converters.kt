package com.fieldreport.ai.data.db

import androidx.room.TypeConverter
import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus

class Converters {

    @TypeConverter
    fun fromReportStatus(value: ReportStatus): String = value.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = enumValueOf(value)

    @TypeConverter
    fun fromPhotoLabel(value: PhotoLabel): String = value.name

    @TypeConverter
    fun toPhotoLabel(value: String): PhotoLabel = enumValueOf(value)

    @TypeConverter
    fun fromMediaType(value: MediaType): String = value.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = enumValueOf(value)
}
