import Foundation
import Combine
import shared

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var companyName: String = "Acme Construction"
    @Published var defaultContractor: String = "General Contracting LLC"
    @Published var userEmail: String = ""
    @Published var isDarkMode: Bool = true
    @Published var entitlementTier: String = "Free Trial"
    @Published var remainingPdfs: Int = 3

    private let settingsRepository: CommonSettingsRepository

    init(settingsRepository: CommonSettingsRepository = CommonSettingsRepository()) {
        self.settingsRepository = settingsRepository
        loadSettings()
    }

    func loadSettings() {
        let entitlement = settingsRepository.getEntitlementState()
        self.remainingPdfs = Int(entitlement.remainingPdfs)
        self.entitlementTier = "\(entitlement.tier)"
    }

    func saveSettings() {
        // Save preferences
        loadSettings()
    }

    func resetTrial() {
        settingsRepository.resetToFreeTrial()
        loadSettings()
    }
}
