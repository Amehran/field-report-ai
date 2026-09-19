package com.fieldreport.ai.data.db

import com.fieldreport.ai.data.model.MediaType
import com.fieldreport.ai.data.model.PhotoLabel
import com.fieldreport.ai.data.model.ReportStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    @Test
    fun reportStatus_conversion_isAccurate() {
        ReportStatus.values().forEach { status ->
            val stringVal = converters.fromReportStatus(status)
            assertEquals(status.name, stringVal)
            val convertedBack = converters.toReportStatus(stringVal)
            assertEquals(status, convertedBack)
        }
    }

    @Test
    fun photoLabel_conversion_isAccurate() {
        PhotoLabel.values().forEach { label ->
            val stringVal = converters.fromPhotoLabel(label)
            assertEquals(label.name, stringVal)
            val convertedBack = converters.toPhotoLabel(stringVal)
            assertEquals(label, convertedBack)
        }
    }

    @Test
    fun mediaType_conversion_isAccurate() {
        MediaType.values().forEach { type ->
            val stringVal = converters.fromMediaType(type)
            assertEquals(type.name, stringVal)
            val convertedBack = converters.toMediaType(stringVal)
            assertEquals(type, convertedBack)
        }
    }
}
