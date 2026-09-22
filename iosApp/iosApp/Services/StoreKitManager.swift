import Foundation
import StoreKit
import shared

@MainActor
class StoreKitManager: ObservableObject {
    static let shared = StoreKitManager()

    let productIDs: [String] = [
        "com.fieldreport.ai.monthly",
        "com.fieldreport.ai.annual",
        "com.fieldreport.ai.lifetime"
    ]

    @Published var products: [Product] = []
    @Published var purchasedProductIDs: Set<String> = []
    @Published var isSubscribed: Bool = false
    @Published var isLifetime: Bool = false
    @Published var currentTierName: String = "Free Trial"
    @Published var purchaseError: String? = nil

    private var updateListenerTask: Task<Void, Error>? = nil
    private let settingsRepository: CommonSettingsRepository

    init(settingsRepository: CommonSettingsRepository = CommonSettingsRepository()) {
        self.settingsRepository = settingsRepository
        updateListenerTask = listenForTransactions()

        Task {
            await fetchProducts()
            await updateEntitlements()
        }
    }

    deinit {
        updateListenerTask?.cancel()
    }

    func fetchProducts() async {
        do {
            let storeProducts = try await Product.products(for: productIDs)
            self.products = storeProducts
        } catch {
            print("Failed to fetch StoreKit products: \(error.localizedDescription)")
        }
    }

    func purchase(_ product: Product) async -> Bool {
        purchaseError = nil
        do {
            let result = try await product.purchase()
            switch result {
            case .success(let verification):
                let transaction = try checkVerified(verification)
                await transaction.finish()
                await updateEntitlements()
                return true

            case .userCancelled:
                return false

            case .pending:
                purchaseError = "Purchase is pending approval."
                return false

            @unknown default:
                return false
            }
        } catch {
            purchaseError = "Purchase failed: \(error.localizedDescription)"
            return false
        }
    }

    func restorePurchases() async {
        try? await AppStore.sync()
        await updateEntitlements()
    }

    func updateEntitlements() async {
        var activeSub = false
        var activeLifetime = false
        var tier = SharedTier.freeTrial
        var purchasedIDs: Set<String> = []

        for await result in Transaction.currentEntitlements {
            do {
                let transaction = try checkVerified(result)
                purchasedIDs.insert(transaction.productID)

                if transaction.productID == "com.fieldreport.ai.lifetime" {
                    activeLifetime = true
                    tier = SharedTier.lifetimePass
                } else if transaction.productID == "com.fieldreport.ai.annual" || transaction.productID == "com.fieldreport.ai.monthly" {
                    activeSub = true
                    tier = SharedTier.proSubscribed
                }
            } catch {
                print("Failed entitlement verification: \(error.localizedDescription)")
            }
        }

        self.purchasedProductIDs = purchasedIDs
        self.isSubscribed = activeSub
        self.isLifetime = activeLifetime

        switch tier {
        case SharedTier.lifetimePass:
            self.currentTierName = "Pro Lifetime"
        case SharedTier.proSubscribed:
            self.currentTierName = "Pro Subscribed"
        default:
            self.currentTierName = "Free Trial"
        }

        settingsRepository.updateEntitlement(
            remainingPdfs: activeSub || activeLifetime ? 9999 : 3,
            isSubscribed: activeSub,
            isLifetime: activeLifetime,
            tier: tier
        )
    }

    private func listenForTransactions() -> Task<Void, Error> {
        return Task.detached {
            for await result in Transaction.updates {
                do {
                    let transaction = try self.checkVerified(result)
                    await self.updateEntitlements()
                    await transaction.finish()
                } catch {
                    print("Transaction update verification failed: \(error)")
                }
            }
        }
    }

    nonisolated private func checkVerified<T>(_ result: VerificationResult<T>) throws -> T {
        switch result {
        case .unverified(_, let error):
            throw error
        case .verified(let safe):
            return safe
        }
    }
}
