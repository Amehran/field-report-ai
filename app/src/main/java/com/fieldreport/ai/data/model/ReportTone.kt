package com.fieldreport.ai.data.model

enum class ReportTone(val displayName: String, val description: String) {
    STANDARD("Standard", "Balanced professional field report"),
    INSURANCE("Insurance Claim", "Formal code compliance, root cause & replacement justification"),
    TECHNICAL("Detailed Technical", "Comprehensive technical specs, measurements & tolerances"),
    CLIENT("Client Summary", "Clear, reassuring & easy-to-understand homeowner summary")
}
