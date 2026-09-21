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
    fun reportStatus_legacyConversion_mapsSafely() {
        assertEquals(ReportStatus.REPORT_CREATED, converters.toReportStatus("NEEDS_REVIEW"))
        assertEquals(ReportStatus.DRAFT, converters.toReportStatus("WAITING_ONLINE"))
        assertEquals(ReportStatus.DRAFT, converters.toReportStatus("GENERATING"))
        assertEquals(ReportStatus.APPROVED, converters.toReportStatus("GENERATED"))
        assertEquals(ReportStatus.FINAL_REPORT, converters.toReportStatus("COMPLETED"))
        assertEquals(ReportStatus.DRAFT, converters.toReportStatus("UNKNOWN_INVALID"))
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

    @Test
    fun aiAgentMode_conversion_isAccurate() {
        com.fieldreport.ai.data.model.AiAgentMode.values().forEach { mode ->
            val stringVal = converters.fromAiAgentMode(mode)
            assertEquals(mode.name, stringVal)
            val convertedBack = converters.toAiAgentMode(stringVal)
            assertEquals(mode, convertedBack)
        }
    }
}
