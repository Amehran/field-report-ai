import SwiftUI

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @State private var showPaywall = false

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Account & Entitlements")) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Current Subscription")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Text(viewModel.entitlementTier.capitalized)
                                .font(.headline)
                                .foregroundColor(.primary)
                        }
                        Spacer()
                        Button(action: { showPaywall = true }) {
                            Text("Upgrade")
                                .font(.subheadline)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .padding(.horizontal, 16)
                                .padding(.vertical, 8)
                                .background(Color.blue)
                                .cornerRadius(8)
                        }
                    }
                }

                Section(header: Text("Company Defaults")) {
                    TextField("Company Name", text: $viewModel.companyName)
                    TextField("Default Contractor", text: $viewModel.defaultContractor)
                    TextField("User Email", text: $viewModel.userEmail)
                }

                Section(header: Text("App Preferences")) {
                    Toggle("Dark Mode", isOn: $viewModel.isDarkMode)
                }

                Section(header: Text("About Field Report AI")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.3 (KMP Unified)")
                            .foregroundColor(.secondary)
                    }
                    HStack {
                        Text("Backend Cloud Run")
                        Spacer()
                        Text("Connected")
                            .foregroundColor(.green)
                    }
                }
            }
            .navigationTitle("Settings")
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        viewModel.saveSettings()
                    }
                }
            }
            .sheet(isPresented: $showPaywall) {
                PaywallView()
            }
        }
    }
}

struct PaywallView: View {
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                Image(systemName: "sparkles")
                    .font(.system(size: 60))
                    .foregroundColor(.yellow)
                    .padding(.top)

                Text("Field Report AI Pro")
                    .font(.largeTitle)
                    .bold()

                Text("Unlock unlimited voice dictation, custom branding, multi-photo attachments, and direct Cloud PDF sync.")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                VStack(spacing: 12) {
                    SubscriptionPlanCard(title: "Monthly Pass", price: "$9.99 / month", badge: nil)
                    SubscriptionPlanCard(title: "Annual Pro", price: "$89.99 / year", badge: "Save 25%")
                    SubscriptionPlanCard(title: "Lifetime License", price: "$149.99 one-time", badge: "Best Value")
                }
                .padding(.horizontal)

                Spacer()
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
    }
}

struct SubscriptionPlanCard: View {
    let title: String
    let price: String
    let badge: String?

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                Text(price)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            Spacer()
            if let badge = badge {
                Text(badge)
                    .font(.caption)
                    .bold()
                    .foregroundColor(.white)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.orange)
                    .cornerRadius(6)
            }
        }
        .padding()
        .background(Color(UIColor.secondarySystemBackground))
        .cornerRadius(12)
    }
}
