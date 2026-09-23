import SwiftUI
import shared

struct ReviewReportView: View {
    let report: SharedReport
    @ObservedObject var viewModel: ReportListViewModel

    @State private var navigateToReady = false
    @State private var finalizedReport: SharedReport? = nil

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)
    private let lightTealPill = Color(red: 243/255, green: 232/255, blue: 255/255)

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Customer & Job Top Card
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("CUSTOMER & JOB")
                                .font(.caption2)
                                .bold()
                                .foregroundColor(.secondary)
                            Text("Customer: \(report.title)")
                                .font(.headline)
                                .bold()
                        }
                        Spacer()
                        VStack(alignment: .trailing, spacing: 4) {
                            Text("DATE")
                                .font(.caption2)
                                .bold()
                                .foregroundColor(.secondary)
                            Text(report.date)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }

                    Divider()

                    VStack(alignment: .leading, spacing: 4) {
                        Text("TECHNICIAN")
                            .font(.caption2)
                            .bold()
                            .foregroundColor(.secondary)
                        TextField("Add Technician Name...", text: $viewModel.currentTechnicianName)
                            .font(.subheadline)
                    }

                    Divider()

                    // AI Tone Selector
                    VStack(alignment: .leading, spacing: 8) {
                        Text("AI REPORT TONE")
                            .font(.caption2)
                            .bold()
                            .foregroundColor(.secondary)

                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(AiTone.allCases) { tone in
                                    Button(action: {
                                        viewModel.selectedTone = tone
                                    }) {
                                        HStack(spacing: 4) {
                                            if viewModel.selectedTone == tone {
                                                Image(systemName: "checkmark")
                                                    .font(.caption2)
                                            }
                                            Text(tone.rawValue)
                                                .font(.caption)
                                                .fontWeight(viewModel.selectedTone == tone ? .bold : .regular)
                                        }
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 6)
                                        .background(viewModel.selectedTone == tone ? lightTealPill : Color(UIColor.systemBackground))
                                        .foregroundColor(viewModel.selectedTone == tone ? .purple : .primary)
                                        .cornerRadius(14)
                                    }
                                }
                            }
                        }
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(16)

                // Issue Section Card
                VStack(alignment: .leading, spacing: 10) {
                    Text("Issue")
                        .font(.headline)
                        .bold()

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Issue Description")
                            .font(.caption)
                            .foregroundColor(.secondary)

                        TextEditor(text: $viewModel.currentIssueText)
                            .frame(height: 70)
                            .padding(6)
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(16)

                // Service Section Card
                VStack(alignment: .leading, spacing: 10) {
                    Text("Service")
                        .font(.headline)
                        .bold()

                    VStack(alignment: .leading, spacing: 4) {
                        Text("What Work Done")
                            .font(.caption)
                            .foregroundColor(.secondary)

                        TextEditor(text: $viewModel.currentWorkDoneText)
                            .frame(height: 90)
                            .padding(6)
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                    }

                    // Cost Summary
                    VStack(alignment: .leading, spacing: 8) {
                        Text("COST SUMMARY")
                            .font(.caption2)
                            .bold()
                            .foregroundColor(.secondary)

                        HStack {
                            Text("Total Amount:")
                                .font(.subheadline)
                                .bold()
                            Spacer()
                            Text(viewModel.totalCost.isEmpty ? "$ 0.00" : "$\(viewModel.totalCost)")
                                .font(.headline)
                                .bold()
                                .foregroundColor(tealColor)
                        }
                        .padding(12)
                        .background(Color(UIColor.systemBackground))
                        .cornerRadius(10)
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(16)

                // Comments Card
                VStack(alignment: .leading, spacing: 10) {
                    Text("Comments")
                        .font(.headline)
                        .bold()

                    TextEditor(text: $viewModel.currentCommentsText)
                        .frame(height: 60)
                        .padding(6)
                        .background(Color(UIColor.systemBackground))
                        .cornerRadius(10)
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(16)

                // Bottom Action Button: Approve report
                Button(action: approveReportClicked) {
                    HStack {
                        if viewModel.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Approve report")
                                .font(.headline)
                                .bold()
                        }
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(tealColor)
                    .cornerRadius(14)
                }
                .disabled(viewModel.isLoading)
                .padding(.top, 8)
            }
            .padding(16)
        }
        .navigationTitle("Review Report")
        .navigationBarTitleDisplayMode(.inline)
        .background(
            NavigationLink(
                destination: Group {
                    if let finalReport = finalizedReport {
                        ReportReadyView(report: finalReport, viewModel: viewModel)
                    }
                },
                isActive: $navigateToReady
            ) {
                EmptyView()
            }
        )
    }

    private func approveReportClicked() {
        viewModel.approveAndGeneratePdf(report: report) { finalReport in
            self.finalizedReport = finalReport
            self.navigateToReady = true
        }
    }
}
