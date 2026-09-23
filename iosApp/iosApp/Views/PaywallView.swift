import SwiftUI
import StoreKit

struct PaywallView: View {
    @Environment(\.presentationMode) var presentationMode
    @StateObject private var storeKitManager = StoreKitManager.shared
    @ObservedObject private var settingsViewModel = SettingsViewModel()

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    VStack(spacing: 8) {
                        Image(systemName: "sparkles")
                            .font(.system(size: 48))
                            .foregroundColor(tealColor)

                        Text("Upgrade to Field Report Pro")
                            .font(.title)
                            .bold()
                            .multilineTextAlignment(.center)

                        Text("Generate unlimited professional field reports, AI summary enhancements, and PDF exports.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                    .padding(.top, 20)

                    // Feature List
                    VStack(alignment: .leading, spacing: 14) {
                        FeatureRow(icon: "doc.text.fill", title: "Unlimited PDF Exports", subtitle: "Export & share clean PDF reports without monthly caps")
                        FeatureRow(icon: "brain.head.profile", title: "Advanced AI Enhancements", subtitle: "Transform voice dictations into formatted job summaries")
                        FeatureRow(icon: "building.2.fill", title: "Custom Branding & Signature", subtitle: "Add company logo, business details, and technician signatures")
                        FeatureRow(icon: "icloud.and.arrow.up.fill", title: "Cloud Synchronization", subtitle: "Seamless backup across all your iOS and Android devices")
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(16)

                    // Subscription Tiers
                    VStack(spacing: 12) {
                        SubscriptionTierCard(
                            title: "Monthly Pro",
                            price: "$9.99 / month",
                            description: "Flexible month-to-month billing",
                            isSelected: true,
                            badge: nil
                        ) {
                            storeKitManager.purchasePro()
                        }

                        SubscriptionTierCard(
                            title: "Annual Pro",
                            price: "$79.99 / year",
                            description: "Save 33% per year",
                            isSelected: false,
                            badge: "SAVE 33%"
                        ) {
                            storeKitManager.purchasePro()
                        }

                        SubscriptionTierCard(
                            title: "Lifetime Pass",
                            price: "$199.99 one-time",
                            description: "Pay once, own forever",
                            isSelected: false,
                            badge: "BEST VALUE"
                        ) {
                            storeKitManager.purchasePro()
                        }
                    }

                    Button(action: {
                        storeKitManager.restorePurchasesSync()
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Text("Restore Purchases")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding(.top, 8)
                }
                .padding(16)
            }
            .navigationBarTitleDisplayMode(.inline)
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

struct FeatureRow: View {
    let icon: String
    let title: String
    let subtitle: String

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)

    var body: some View {
        HStack(alignment: .top, spacing: 14) {
            Image(systemName: icon)
                .font(.title3)
                .foregroundColor(tealColor)
                .frame(width: 28)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.subheadline)
                    .bold()
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }
}

struct SubscriptionTierCard: View {
    let title: String
    let price: String
    let description: String
    let isSelected: Bool
    let badge: String?
    let onPurchase: () -> Void

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)

    var body: some View {
        Button(action: onPurchase) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(title)
                            .font(.headline)
                            .bold()
                            .foregroundColor(.primary)

                        if let badge = badge {
                            Text(badge)
                                .font(.caption2)
                                .bold()
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(tealColor.opacity(0.15))
                                .foregroundColor(tealColor)
                                .cornerRadius(6)
                        }
                    }

                    Text(description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                Spacer()

                Text(price)
                    .font(.subheadline)
                    .bold()
                    .foregroundColor(tealColor)
            }
            .padding(16)
            .background(Color(UIColor.systemBackground))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? tealColor : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
            )
        }
    }
}
