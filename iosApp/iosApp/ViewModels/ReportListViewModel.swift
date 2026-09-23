import Foundation
import Combine
import UIKit
import shared

enum AiTone: String, CaseIterable, Identifiable {
    case standard = "Standard"
    case insurance = "Insurance Claim"
    case technical = "Detailed Technical"
    case client = "Client Summary"

    var id: String { rawValue }

    var description: String {
        switch self {
        case .standard: return "Balanced professional field report"
        case .insurance: return "Formal documentation formatted for insurance claim adjusters"
        case .technical: return "Comprehensive technical terminology & engineering observations"
        case .client: return "Simplified summary tailored for homeowner or client review"
        }
    }
}

@MainActor
class ReportListViewModel: ObservableObject {
    @Published var reports: [SharedReport] = []
    @Published var searchQuery: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil

    // Draft / New Report Form State
    @Published var customerName: String = ""
    @Published var jobName: String = ""
    @Published var selectedTone: AiTone = .standard
    @Published var capturedImages: [IdentifiableImage] = []
    @Published var isVoiceMode: Bool = true
    @Published var typedNotes: String = ""
    @Published var laborCost: String = ""
    @Published var materialsCost: String = ""
    @Published var totalCost: String = ""

    // Review & Ready Report State
    @Published var currentReviewReport: SharedReport? = nil
    @Published var currentIssueText: String = "Primary issue identified during initial inspection."
    @Published var currentWorkDoneText: String = "Executed primary repair, maintenance, and testing procedures."
    @Published var currentTechnicianName: String = ""
    @Published var currentCommentsText: String = ""

    private let reportRepository: CommonReportRepository

    init(reportRepository: CommonReportRepository = CommonReportRepository()) {
        self.reportRepository = reportRepository
        loadReports()
    }

    func loadReports() {
        self.reports = reportRepository.getReportsList()
    }

    var filteredReports: [SharedReport] {
        if searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return reports
        }
        return reports.filter { report in
            report.title.localizedCaseInsensitiveContains(searchQuery) ||
            report.jobSite.localizedCaseInsensitiveContains(searchQuery) ||
            report.summary.localizedCaseInsensitiveContains(searchQuery)
        }
    }

    func deleteReport(id: String) {
        reportRepository.deleteReport(reportId: id)
        loadReports()
    }

    func resetFormState() {
        customerName = ""
        jobName = ""
        selectedTone = .standard
        capturedImages.removeAll()
        isVoiceMode = true
        typedNotes = ""
        laborCost = ""
        materialsCost = ""
        totalCost = ""
        errorMessage = nil
    }

    func generateDraftReport(
        dictatedNotes: String,
        completion: @escaping (SharedReport) -> Void
    ) {
        isLoading = true
        errorMessage = nil

        let notesText = isVoiceMode ? dictatedNotes : typedNotes
        let rawNotes = notesText.isEmpty ? "General site inspection observations completed." : notesText
        let notesList = rawNotes.components(separatedBy: "\n").filter { !$0.isEmpty }

        let formattedDate = DateFormatter.localizedString(from: Date(), dateStyle: .medium, timeStyle: .short)
        let displayTitle = customerName.isEmpty ? (jobName.isEmpty ? "Field Inspection Report" : jobName) : customerName
        let displayJobSite = jobName.isEmpty ? "General Service" : jobName

        let newReport = SharedReport(
            id: UUID().uuidString,
            title: displayTitle,
            jobSite: displayJobSite,
            inspectorName: currentTechnicianName,
            date: formattedDate,
            clientName: customerName.isEmpty ? "Client" : customerName,
            summary: rawNotes,
            notes: notesList,
            photoUrls: [],
            isDraft: true,
            pdfUrl: nil
        )

        currentReviewReport = newReport
        currentIssueText = "Primary issue identified during initial inspection."
        currentWorkDoneText = rawNotes
        isLoading = false
        completion(newReport)
    }

    func approveAndGeneratePdf(
        report: SharedReport,
        completion: @escaping (SharedReport) -> Void
    ) {
        isLoading = true
        errorMessage = nil

        Task {
            do {
                let response = try await reportRepository.generatePdfReport(
                    title: report.title,
                    jobSite: report.jobSite,
                    inspectorName: currentTechnicianName.isEmpty ? report.inspectorName : currentTechnicianName,
                    notes: [currentIssueText, currentWorkDoneText],
                    userEmail: nil
                )
                self.loadReports()
                self.isLoading = false

                let finalizedReport = SharedReport(
                    id: report.id,
                    title: report.title,
                    jobSite: report.jobSite,
                    inspectorName: self.currentTechnicianName.isEmpty ? report.inspectorName : self.currentTechnicianName,
                    date: report.date,
                    clientName: report.clientName,
                    summary: self.currentWorkDoneText,
                    notes: [self.currentIssueText, self.currentWorkDoneText],
                    photoUrls: [],
                    isDraft: false,
                    pdfUrl: response.pdfUrl ?? "https://field-report-backend-598464152783.us-central1.run.app/pdf/\(report.id).pdf"
                )

                self.currentReviewReport = finalizedReport
                completion(finalizedReport)
            } catch {
                self.isLoading = false
                self.errorMessage = "Failed to finalize PDF: \(error.localizedDescription)"
                let fallbackReport = SharedReport(
                    id: report.id,
                    title: report.title,
                    jobSite: report.jobSite,
                    inspectorName: self.currentTechnicianName,
                    date: report.date,
                    clientName: report.clientName,
                    summary: self.currentWorkDoneText,
                    notes: [self.currentIssueText, self.currentWorkDoneText],
                    photoUrls: [],
                    isDraft: false,
                    pdfUrl: nil
                )
                completion(fallbackReport)
            }
        }
    }

    func generateReportFromAudio(
        title: String,
        jobSite: String,
        inspectorName: String,
        notesText: String,
        completion: @escaping (SharedReport?) -> Void
    ) {
        isLoading = true
        errorMessage = nil

        let notesList = notesText.components(separatedBy: "\n").filter { !$0.isEmpty }

        Task {
            do {
                let response = try await reportRepository.generatePdfReport(
                    title: title.isEmpty ? "Field Inspection Report" : title,
                    jobSite: jobSite,
                    inspectorName: inspectorName,
                    notes: notesList,
                    userEmail: nil
                )
                self.loadReports()
                self.isLoading = false
                if response.success {
                    completion(self.reports.first)
                } else {
                    self.errorMessage = response.message ?? "Report generation failed."
                    completion(nil)
                }
            } catch {
                self.errorMessage = "Failed to generate report: \(error.localizedDescription)"
                self.isLoading = false
                completion(nil)
            }
        }
    }
}
