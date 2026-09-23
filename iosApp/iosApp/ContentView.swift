import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = ReportListViewModel()
    @State private var showNewReportSheet = false
    @State private var showDeleteAllConfirm = false

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
                                NavigationLink(destination: ReportReadyView(report: report, viewModel: viewModel)) {
                                    HStack {
                                        VStack(alignment: .leading, spacing: 6) {
                                            HStack {
                                                Text(report.title.isEmpty ? "Untitled Report" : report.title)
                                                    .font(.headline)
                                                    .bold()

                                                Spacer()

                                                StatusBadge(isDraft: report.isDraft)
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

                                            Text(report.date)
                                                .font(.caption2)
                                                .foregroundColor(.secondary)
                                        }
                                        .padding(.vertical, 4)
                                    }
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
                        HStack(spacing: 12) {
                            Button(action: {
                                viewModel.resetFormState()
                                showNewReportSheet = true
                            }) {
                                Image(systemName: "plus.circle.fill")
                                    .font(.title3)
                                    .foregroundColor(Color(red: 15/255, green: 118/255, blue: 110/255))
                            }

                            Menu {
                                Button(role: .destructive, action: {
                                    showDeleteAllConfirm = true
                                }) {
                                    Label("Delete All Reports", systemImage: "trash")
                                }
                            } label: {
                                Image(systemName: "ellipsis.circle")
                                    .font(.title3)
                                    .foregroundColor(.primary)
                            }
                        }
                    }
                }
                .sheet(isPresented: $showNewReportSheet) {
                    NewReportView(viewModel: viewModel)
                }
                .alert("Delete All Reports?", isPresented: $showDeleteAllConfirm) {
                    Button("Delete All", role: .destructive) {
                        for report in viewModel.reports {
                            viewModel.deleteReport(id: report.id)
                        }
                    }
                    Button("Cancel", role: .cancel) {}
                } message: {
                    Text("This will permanently remove all saved field reports from your device.")
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

struct StatusBadge: View {
    let isDraft: Bool

    var body: some View {
        Text(isDraft ? "Draft" : "Final")
            .font(.caption2)
            .bold()
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(isDraft ? Color.slateTagBg : Color.emeraldTagBg)
            .foregroundColor(isDraft ? Color.slateTagFg : Color.emeraldTagFg)
            .cornerRadius(8)
    }
}

extension Color {
    static let slateTagBg = Color(red: 226/255, green: 232/255, blue: 240/255)
    static let slateTagFg = Color(red: 71/255, green: 85/255, blue: 105/255)
    static let emeraldTagBg = Color(red: 220/255, green: 252/255, blue: 231/255)
    static let emeraldTagFg = Color(red: 22/255, green: 101/255, blue: 52/255)
}
