import SwiftUI
import shared

struct ReportDetailView: View {
    @State var report: SharedReport
    @ObservedObject var viewModel: ReportListViewModel
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        Form {
            Section(header: Text("General Information")) {
                Text("Title: \(report.title)")
                    .font(.headline)
                Text("Job Site: \(report.jobSite)")
                Text("Inspector: \(report.inspectorName)")
                Text("Date: \(report.date)")
                    .foregroundColor(.secondary)
            }

            Section(header: Text("Executive Summary")) {
                Text(report.summary.isEmpty ? "No summary generated." : report.summary)
                    .font(.body)
            }

            if let pdfUrl = report.pdfUrl, !pdfUrl.isEmpty {
                Section(header: Text("PDF Document")) {
                    Link(destination: URL(string: pdfUrl)!) {
                        HStack {
                            Image(systemName: "doc.richtext.fill")
                            Text("Open Cloud Generated PDF")
                        }
                        .foregroundColor(.blue)
                    }
                }
            }
        }
        .navigationTitle(report.title.isEmpty ? "Field Report" : report.title)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button("Done") {
                    presentationMode.wrappedValue.dismiss()
                }
            }
        }
    }
}
