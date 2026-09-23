import SwiftUI
import shared

@main
struct iOSApp: App {
    @StateObject private var settingsViewModel = SettingsViewModel()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(settingsViewModel)
                .preferredColorScheme(settingsViewModel.colorScheme)
        }
    }
}
