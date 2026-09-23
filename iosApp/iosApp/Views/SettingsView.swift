import SwiftUI
import StoreKit

enum AppThemeMode: String, CaseIterable, Identifiable {
    case system = "System"
    case light = "Light"
    case dark = "Dark"

    var id: String { rawValue }
}

enum AiAgentMode: String, CaseIterable, Identifiable {
    case local = "On-Device (Local)"
    case cloud = "Cloud (Gemini)"

    var id: String { rawValue }
}

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @StateObject private var storeKitManager = StoreKitManager.shared
    @State private var showPaywall = false
    @State private var selectedTheme: AppThemeMode = .system
    @State private var selectedAiAgent: AiAgentMode = .cloud

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)
    private let lightTealPill = Color(red: 243/255, green: 232/255, blue: 255/255)

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
                            Text("Free Trial (0 PDF exports remaining)")
                                .font(.subheadline)
                                .bold()

                            Text("Upgrade to Field Report Pro for unlimited exports and priority support.")
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

                    // App Theme Section
                    VStack(alignment: .leading, spacing: 10) {
                        Text("App Theme")
                            .font(.headline)
                            .bold()

                        HStack(spacing: 0) {
                            ForEach(AppThemeMode.allCases) { theme in
                                Button(action: { selectedTheme = theme }) {
                                    HStack(spacing: 4) {
                                        if selectedTheme == theme {
                                            Image(systemName: "checkmark")
                                                .font(.caption)
                                        }
                                        Text(theme.rawValue)
                                            .font(.subheadline)
                                            .fontWeight(selectedTheme == theme ? .bold : .regular)
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(selectedTheme == theme ? lightTealPill : Color.clear)
                                    .foregroundColor(selectedTheme == theme ? .purple : .primary)
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
                                Button(action: { selectedAiAgent = mode }) {
                                    HStack(spacing: 4) {
                                        if selectedAiAgent == mode {
                                            Image(systemName: "checkmark")
                                                .font(.caption)
                                        }
                                        Text(mode.rawValue)
                                            .font(.subheadline)
                                            .fontWeight(selectedAiAgent == mode ? .bold : .regular)
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(selectedAiAgent == mode ? lightTealPill : Color.clear)
                                    .foregroundColor(selectedAiAgent == mode ? .purple : .primary)
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
                            TextField("Technician Name", text: $viewModel.defaultContractor)
                                .padding()
                                .background(Color(UIColor.systemBackground))
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                )

                            TextField("Business Name", text: $viewModel.companyName)
                                .padding()
                                .background(Color(UIColor.systemBackground))
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                )

                            HStack {
                                Text("Currency")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text("$ USD")
                                    .font(.subheadline)
                                    .bold()
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

struct PaywallView: View {
    @StateObject private var storeKitManager = StoreKitManager.shared
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Image(systemName: "sparkles")
                    .font(.system(size: 50))
                    .foregroundColor(.yellow)
                    .padding(.top)

                Text("Field Report AI Pro")
                    .font(.title)
                    .bold()

                Text("Unlock unlimited voice dictation, custom branding, multi-photo attachments, and direct Cloud PDF sync.")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                ScrollView {
                    VStack(spacing: 12) {
                        if storeKitManager.products.isEmpty {
                            SubscriptionPlanCard(
                                title: "Monthly Pass",
                                price: "$9.99 / month",
                                badge: nil,
                                isPurchased: storeKitManager.purchasedProductIDs.contains("com.fieldreport.ai.monthly")
                            ) {}

                            SubscriptionPlanCard(
                                title: "Annual Pro",
                                price: "$89.99 / year",
                                badge: "Save 25%",
                                isPurchased: storeKitManager.purchasedProductIDs.contains("com.fieldreport.ai.annual")
                            ) {}

                            SubscriptionPlanCard(
                                title: "Lifetime License",
                                price: "$149.99 one-time",
                                badge: "Best Value",
                                isPurchased: storeKitManager.purchasedProductIDs.contains("com.fieldreport.ai.lifetime")
                            ) {}
                        } else {
                            ForEach(storeKitManager.products, id: \.id) { product in
                                SubscriptionPlanCard(
                                    title: product.displayName,
                                    price: product.displayPrice,
                                    badge: product.id.contains("annual") ? "Save 25%" : (product.id.contains("lifetime") ? "Best Value" : nil),
                                    isPurchased: storeKitManager.purchasedProductIDs.contains(product.id)
                                ) {
                                    Task {
                                        _ = await storeKitManager.purchase(product)
                                    }
                                }
                            }
                        }
                    }
                    .padding(.horizontal)
                }

                Button(action: {
                    Task {
                        await storeKitManager.restorePurchases()
                    }
                }) {
                    Text("Restore Purchases")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }

                if let error = storeKitManager.purchaseError {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }

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
    let isPurchased: Bool
    let onPurchase: () -> Void

    var body: some View {
        Button(action: onPurchase) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.headline)
                        .foregroundColor(.primary)
                    Text(price)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                Spacer()
                if isPurchased {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                        .font(.title2)
                } else if let badge = badge {
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
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isPurchased ? Color.green : Color.clear, lineWidth: 2)
            )
        }
    }
}
