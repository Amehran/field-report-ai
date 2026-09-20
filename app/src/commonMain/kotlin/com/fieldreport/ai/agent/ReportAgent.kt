package com.fieldreport.ai.agent

import com.fieldreport.ai.data.db.MediaItemEntity
import com.fieldreport.ai.data.db.ReportEntity
import com.fieldreport.ai.data.model.AiAgentMode

interface ReportAgent {
    suspend fun generateDraft(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        typedNotes: String?
    ): Result<ReportEntity>
}

class ReportAgentFactory {
    companion object {
        fun createAgent(mode: AiAgentMode): ReportAgent {
            return when (mode) {
                AiAgentMode.CLOUD -> CloudGeminiAgent()
                AiAgentMode.ON_DEVICE -> OnDeviceAgent()
            }
        }
    }
}

class CloudGeminiAgent : ReportAgent {
    override suspend fun generateDraft(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        typedNotes: String?
    ): Result<ReportEntity> {
        return Result.success(report)
    }
}

class OnDeviceAgent : ReportAgent {
    override suspend fun generateDraft(
        report: ReportEntity,
        mediaItems: List<MediaItemEntity>,
        typedNotes: String?
    ): Result<ReportEntity> {
        return Result.success(report)
    }
}
