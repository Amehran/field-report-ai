package com.fieldreport.ai.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingRepository(
    private val context: Context,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val onPurchaseVerified: suspend (purchaseToken: String, productId: String, isLifetime: Boolean) -> Unit = { _, _, _ -> }
) : PurchasesUpdatedListener {

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val _billingConnected = MutableStateFlow(false)
    val billingConnected: StateFlow<Boolean> = _billingConnected.asStateFlow()

    private var billingClient: BillingClient? = null

    init {
        try {
            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build()
            startConnection()
        } catch (e: Throwable) {
            // Safe fallback for unit testing environments without Google Play Billing SDK
        }
    }

    fun startConnection() {
        val client = billingClient ?: return
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingConnected.value = true
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingConnected.value = false
            }
        })
    }

    fun queryProducts() {
        if (!_billingConnected.value) return
        val client = billingClient ?: return

        val subscriptionProducts = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("field_report_ai_pro_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("field_report_ai_pro_annual")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val inAppProducts = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("field_report_ai_pro_lifetime")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val paramsSubs = QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptionProducts)
            .build()

        client.queryProductDetailsAsync(paramsSubs) { resultSubs, productDetailsListSubs ->
            val paramsInApp = QueryProductDetailsParams.newBuilder()
                .setProductList(inAppProducts)
                .build()

            client.queryProductDetailsAsync(paramsInApp) { resultInApp, productDetailsListInApp ->
                val combined = mutableListOf<ProductDetails>()
                if (resultSubs.responseCode == BillingClient.BillingResponseCode.OK) {
                    combined.addAll(productDetailsListSubs)
                }
                if (resultInApp.responseCode == BillingClient.BillingResponseCode.OK) {
                    combined.addAll(productDetailsListInApp)
                }
                _products.value = combined
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails): BillingResult? {
        val client = billingClient ?: return null

        val productDetailsParamsList = if (productDetails.productType == BillingClient.ProductType.SUBS) {
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        } else {
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return client.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    val productId = purchase.products.firstOrNull() ?: ""
                    val isLifetime = productId.contains("lifetime")
                    externalScope.launch {
                        onPurchaseVerified(purchase.purchaseToken, productId, isLifetime)
                    }
                }
            }
        }
    }

    fun restorePurchases(onComplete: (Boolean) -> Unit) {
        val client = billingClient
        if (!_billingConnected.value || client == null) {
            onComplete(false)
            return
        }

        val subsParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        client.queryPurchasesAsync(subsParams) { resultSubs, purchasesSubs ->
            val inAppParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            client.queryPurchasesAsync(inAppParams) { resultInApp, purchasesInApp ->
                val allPurchases = purchasesSubs + purchasesInApp
                var restored = false

                for (purchase in allPurchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        restored = true
                        val productId = purchase.products.firstOrNull() ?: ""
                        val isLifetime = productId.contains("lifetime")
                        externalScope.launch {
                            onPurchaseVerified(purchase.purchaseToken, productId, isLifetime)
                        }
                    }
                }
                onComplete(restored)
            }
        }
    }

    fun endConnection() {
        billingClient?.endConnection()
    }
}
