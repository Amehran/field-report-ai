package com.fieldreport.ai.network

import com.fieldreport.ai.model.GenerateReportRequest
import com.fieldreport.ai.model.GenerateReportResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CloudRunApiClient(
    private val baseUrl: String = "https://field-report-backend-598464152783.us-central1.run.app"
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun checkHealth(): Boolean {
        return try {
            val response = client.get("$baseUrl/health")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    suspend fun generateReport(request: GenerateReportRequest): GenerateReportResponse {
        return try {
            val response = client.post("$baseUrl/generate-report") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                response.body()
            } else {
                GenerateReportResponse(
                    success = false,
                    message = "Backend error: ${response.status.value}"
                )
            }
        } catch (e: Exception) {
            GenerateReportResponse(
                success = false,
                message = e.message ?: "Network error during report generation"
            )
        }
    }
}
