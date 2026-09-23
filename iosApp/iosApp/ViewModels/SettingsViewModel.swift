import Foundation
import Combine
import SwiftUI
import shared

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

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var selectedTheme: AppThemeMode = .system {
        didSet {
            UserDefaults.standard.set(selectedTheme.rawValue, forKey: "app_theme_mode")
        }
    }

    @Published var selectedAiAgent: AiAgentMode = .cloud {
        didSet {
            UserDefaults.standard.set(selectedAiAgent.rawValue, forKey: "ai_agent_mode")
        }
    }

    @Published var companyName: String = "" {
        didSet {
            UserDefaults.standard.set(companyName, forKey: "company_name")
        }
    }

    @Published var defaultContractor: String = "" {
        didSet {
            UserDefaults.standard.set(defaultContractor, forKey: "default_contractor")
        }
    }

    @Published var currency: String = "$ USD" {
        didSet {
            UserDefaults.standard.set(currency, forKey: "currency_symbol")
        }
    }

    @Published var userEmail: String = ""
    @Published var entitlementTier: String = "Free Trial"
    @Published var remainingPdfs: Int = 3

    private let settingsRepository: CommonSettingsRepository

    init(settingsRepository: CommonSettingsRepository = CommonSettingsRepository()) {
        self.settingsRepository = settingsRepository

        // Load theme mode from UserDefaults
        if let savedThemeStr = UserDefaults.standard.string(forKey: "app_theme_mode"),
           let savedTheme = AppThemeMode(rawValue: savedThemeStr) {
            self.selectedTheme = savedTheme
        }

        // Load AI Agent mode from UserDefaults
        if let savedAiStr = UserDefaults.standard.string(forKey: "ai_agent_mode"),
           let savedAi = AiAgentMode(rawValue: savedAiStr) {
            self.selectedAiAgent = savedAi
        }

        // Load Company & Contractor Info
        if let savedCompany = UserDefaults.standard.string(forKey: "company_name") {
            self.companyName = savedCompany
        }
        if let savedContractor = UserDefaults.standard.string(forKey: "default_contractor") {
            self.defaultContractor = savedContractor
        }
        if let savedCurrency = UserDefaults.standard.string(forKey: "currency_symbol") {
            self.currency = savedCurrency
        }

        loadSettings()
    }

    var colorScheme: ColorScheme? {
        switch selectedTheme {
        case .system:
            return nil
        case .light:
            return .light
        case .dark:
            return .dark
        }
    }

    func loadSettings() {
        let entitlement = settingsRepository.getEntitlementState()
        self.remainingPdfs = Int(entitlement.remainingPdfs)
        self.entitlementTier = "\(entitlement.tier)"
    }

    func setTheme(_ theme: AppThemeMode) {
        self.selectedTheme = theme
    }

    func setAiAgent(_ mode: AiAgentMode) {
        self.selectedAiAgent = mode
    }

    func resetTrial() {
        settingsRepository.resetToFreeTrial()
        loadSettings()
    }
}
