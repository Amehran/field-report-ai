import Foundation
import Combine
import shared

@MainActor
class ReportListViewModel: ObservableObject {
    @Published var reports: [SharedReport] = []
    @Published var searchQuery: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil

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
