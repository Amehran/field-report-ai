import SwiftUI
import StoreKit

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @StateObject private var storeKitManager = StoreKitManager.shared
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
                            Text(storeKitManager.currentTierName)
                                .font(.headline)
                                .foregroundColor(.primary)
                        }
                        Spacer()
                        Button(action: { showPaywall = true }) {
                            Text(storeKitManager.isSubscribed || storeKitManager.isLifetime ? "Manage" : "Upgrade")
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
                            ) {
                                // Default fallback purchase trigger
                            }

                            SubscriptionPlanCard(
                                title: "Annual Pro",
                                price: "$89.99 / year",
                                badge: "Save 25%",
                                isPurchased: storeKitManager.purchasedProductIDs.contains("com.fieldreport.ai.annual")
                            ) {
                                // Default fallback purchase trigger
                            }

                            SubscriptionPlanCard(
                                title: "Lifetime License",
                                price: "$149.99 one-time",
                                badge: "Best Value",
                                isPurchased: storeKitManager.purchasedProductIDs.contains("com.fieldreport.ai.lifetime")
                            ) {
                                // Default fallback purchase trigger
                            }
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
