import SwiftUI
import shared

struct ShareSheet: UIViewControllerRepresentable {
    var activityItems: [Any]
    var applicationActivities: [UIActivity]? = nil

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: applicationActivities)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

struct ReportReadyView: View {
    let report: SharedReport
    @ObservedObject var viewModel: ReportListViewModel

    @State private var showShareSheet = false
    @State private var showCopiedAlert = false
    @ObservedObject private var settingsViewModel = SettingsViewModel()

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)
    private let darkHeaderColor = Color(red: 15/255, green: 23/255, blue: 42/255) // #0F172A

    var body: some View {
        VStack(spacing: 20) {
            // Main Report Ready Card Preview
            VStack(alignment: .leading, spacing: 0) {
                // Dark Header Banner
                VStack(alignment: .leading, spacing: 4) {
                    Text(settingsViewModel.companyName.isEmpty ? "NORTHLINE HOME SERVICES" : settingsViewModel.companyName.uppercased())
                        .font(.headline)
                        .bold()
                        .foregroundColor(.white)

                    Text("Job completion report • \(report.date)")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.7))
                }
                .padding(16)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(darkHeaderColor)

                // Content Body
                VStack(alignment: .leading, spacing: 14) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Customer")
                            .font(.headline)
                            .bold()
                        Text(report.jobSite.isEmpty ? "General Service" : report.jobSite)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        Text("COMPLETED")
                            .font(.caption2)
                            .bold()
                            .foregroundColor(tealColor)

                        Text(report.summary.isEmpty ? "Completed General Service for Customer." : report.summary)
                            .font(.subheadline)
                            .foregroundColor(.primary)
                    }

                    Button(action: {
                        showShareSheet = true
                    }) {
                        HStack {
                            Text("VIEW FULL REPORT →")
                                .font(.subheadline)
                                .bold()
                                .foregroundColor(tealColor)
                            Spacer()
                        }
                    }
                    .padding(.top, 8)
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
            }
            .cornerRadius(16)
            .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
            .padding(.horizontal, 16)
            .padding(.top, 16)

            Spacer()

            // Bottom Actions
            VStack(spacing: 14) {
                Button(action: {
                    showShareSheet = true
                }) {
                    Text("Share PDF")
                        .font(.headline)
                        .bold()
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(tealColor)
                        .cornerRadius(14)
                }

                Button(action: copySummaryToClipboard) {
                    Text("Copy customer summary")
                        .font(.subheadline)
                        .bold()
                        .foregroundColor(tealColor)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 24)
        }
        .navigationTitle("Report Ready")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showShareSheet) {
            ShareSheet(activityItems: [
                report.pdfUrl ?? "Field Report for \(report.title)",
                "Completed Field Report summary: \(report.summary)"
            ])
        }
        .alert(isPresented: $showCopiedAlert) {
            Alert(
                title: Text("Summary Copied"),
                message: Text("Customer summary has been copied to your clipboard."),
                dismissButton: .default(Text("OK"))
            )
        }
    }

    private func copySummaryToClipboard() {
        UIPasteboard.general.string = """
        \(settingsViewModel.companyName) - Job Completion Report
        Customer: \(report.title)
        Job: \(report.jobSite)
        Summary: \(report.summary)
        Date: \(report.date)
        """
        showCopiedAlert = true
    }
}
