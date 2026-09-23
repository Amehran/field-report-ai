import SwiftUI
import StoreKit

struct SettingsView: View {
    @EnvironmentObject private var viewModel: SettingsViewModel
    @StateObject private var storeKitManager = StoreKitManager.shared
    @State private var showPaywall = false

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)

    let currencies = ["$ USD", "€ EUR", "£ GBP", "C$ CAD", "A$ AUD", "¥ JPY", "₹ INR", "CHF", "NZ$ NZD", "Mex$ MXN"]

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Account & Authentication Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Account & Authentication")
                            .font(.headline)
                            .bold()

                        VStack(alignment: .leading, spacing: 6) {
                            Text("Signed in as Guest (uhgxOQJw)")
                                .font(.subheadline)
                                .bold()

                            Text("Your report data and account settings are active.")
                                .font(.caption)
                                .foregroundColor(.secondary)

                            Button(action: {}) {
                                Text("Sign Out / Switch Account")
                                    .font(.subheadline)
                                    .bold()
                                    .foregroundColor(tealColor)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color(UIColor.systemBackground))
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(tealColor.opacity(0.3), lineWidth: 1)
                                    )
                            }
                            .padding(.top, 4)
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(16)

                    // Subscription & Plan Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Subscription & Plan")
                            .font(.headline)
                            .bold()

                        VStack(alignment: .leading, spacing: 6) {
                            Text(storeKitManager.isSubscribed || storeKitManager.isLifetime ? "Pro Subscription Active" : "Free Trial (\(viewModel.remainingPdfs) PDF exports remaining)")
                                .font(.subheadline)
                                .bold()

                            Text(storeKitManager.isSubscribed || storeKitManager.isLifetime ? "You have unlimited access to PDF exports & cloud AI features." : "Upgrade to Field Report Pro for unlimited exports and priority support.")
                                .font(.caption)
                                .foregroundColor(.secondary)

                            Button(action: { showPaywall = true }) {
                                Text(storeKitManager.isSubscribed || storeKitManager.isLifetime ? "Manage Pro Subscription" : "Upgrade to Pro")
                                    .font(.headline)
                                    .bold()
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(tealColor)
                                    .cornerRadius(12)
                            }
                            .padding(.top, 4)
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(16)

                    // App Theme Section (Live Theme Switching!)
                    VStack(alignment: .leading, spacing: 10) {
                        Text("App Theme")
                            .font(.headline)
                            .bold()

                        HStack(spacing: 0) {
                            ForEach(AppThemeMode.allCases) { theme in
                                ThemePillButton(
                                    title: theme.rawValue,
                                    isSelected: viewModel.selectedTheme == theme
                                ) {
                                    viewModel.setTheme(theme)
                                }
                            }
                        }
                        .background(Color(UIColor.systemBackground))
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                        )
                    }

                    // AI Agent Selection Section
                    VStack(alignment: .leading, spacing: 10) {
                        Text("AI Agent Selection")
                            .font(.headline)
                            .bold()

                        HStack(spacing: 0) {
                            ForEach(AiAgentMode.allCases) { mode in
                                ThemePillButton(
                                    title: mode.rawValue,
                                    isSelected: viewModel.selectedAiAgent == mode
                                ) {
                                    viewModel.setAiAgent(mode)
                                }
                            }
                        }
                        .background(Color(UIColor.systemBackground))
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                        )
                    }

                    // Business Information Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Business Information")
                            .font(.headline)
                            .bold()

                        VStack(spacing: 10) {
                            HStack {
                                TextField("Technician Name", text: $viewModel.defaultContractor)
                                if !viewModel.defaultContractor.isEmpty {
                                    Button(action: { viewModel.defaultContractor = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.gray)
                                    }
                                }
                            }
                            .padding()
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )

                            HStack {
                                TextField("Business Name", text: $viewModel.companyName)
                                if !viewModel.companyName.isEmpty {
                                    Button(action: { viewModel.companyName = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.gray)
                                    }
                                }
                            }
                            .padding()
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )

                            HStack {
                                Text("Currency")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                Spacer()
                                Menu {
                                    ForEach(currencies, id: \.self) { curr in
                                        Button(curr) {
                                            viewModel.currency = curr
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(viewModel.currency)
                                            .font(.subheadline)
                                            .bold()
                                            .foregroundColor(.primary)
                                        Image(systemName: "chevron.up.chevron.down")
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                }
                            }
                            .padding()
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(16)
                }
                .padding(16)
            }
            .navigationTitle("Settings")
            .sheet(isPresented: $showPaywall) {
                PaywallView()
            }
        }
    }
}

struct ThemePillButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)

    var body: some View {
        Button(action: action) {
            HStack(spacing: 4) {
                if isSelected {
                    Image(systemName: "checkmark")
                        .font(.caption)
                        .bold()
                }
                Text(title)
                    .font(.subheadline)
                    .fontWeight(isSelected ? .bold : .regular)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 10)
            .background(isSelected ? tealColor.opacity(0.18) : Color.clear)
            .foregroundColor(isSelected ? tealColor : .primary)
        }
    }
}
