import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = ReportListViewModel()
    @State private var showDictationSheet = false
    @State private var selectedReport: SharedReport? = nil

    var body: some View {
        TabView {
            // Tab 1: Field Reports List
            NavigationView {
                VStack {
                    if viewModel.filteredReports.isEmpty {
                        VStack(spacing: 16) {
                            Image(systemName: "doc.text.magnifyingglass")
                                .font(.system(size: 56))
                                .foregroundColor(.secondary)
                            Text("No Field Reports Found")
                                .font(.title3)
                                .bold()
                            Text("Tap the '+' button or start dictating to generate your first professional field report.")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else {
                        List {
                            ForEach(viewModel.filteredReports, id: \.id) { report in
                                NavigationLink(destination: ReportDetailView(report: report, viewModel: viewModel)) {
                                    VStack(alignment: .leading, spacing: 6) {
                                        HStack {
                                            Text(report.title.isEmpty ? "Untitled Report" : report.title)
                                                .font(.headline)
                                            Spacer()
                                            Text(report.date)
                                                .font(.caption)
                                                .foregroundColor(.secondary)
                                        }

                                        if !report.jobSite.isEmpty {
                                            HStack {
                                                Image(systemName: "mappin.and.ellipse")
                                                    .font(.caption)
                                                    .foregroundColor(.secondary)
                                                Text(report.jobSite)
                                                    .font(.subheadline)
                                                    .foregroundColor(.secondary)
                                            }
                                        }

                                        if !report.summary.isEmpty {
                                            Text(report.summary)
                                                .font(.caption)
                                                .foregroundColor(.primary)
                                                .lineLimit(2)
                                        }
                                    }
                                    .padding(.vertical, 4)
                                }
                            }
                            .onDelete { indexSet in
                                for index in indexSet {
                                    let report = viewModel.filteredReports[index]
                                    viewModel.deleteReport(id: report.id)
                                }
                            }
                        }
                        .searchable(text: $viewModel.searchQuery, prompt: "Search reports...")
                    }
                }
                .navigationTitle("Field Reports")
                .toolbar {
                    ToolbarItem(placement: .primaryAction) {
                        Button(action: { showDictationSheet = true }) {
                            Image(systemName: "mic.badge.plus")
                                .font(.headline)
                        }
                    }
                }
                .sheet(isPresented: $showDictationSheet) {
                    DictationView(viewModel: viewModel)
                }
            }
            .tabItem {
                Label("Reports", systemImage: "doc.text.fill")
            }

            // Tab 2: Settings
            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gearshape.fill")
                }
        }
    }
}
