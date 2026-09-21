import { google } from 'googleapis';
import * as admin from 'firebase-admin';

export interface VerificationResult {
  valid: boolean;
  tier: 'PRO_MONTHLY' | 'PRO_ANNUAL' | 'PRO_LIFETIME' | 'FREE_TRIAL' | 'EXPIRED';
  isSubscribed: boolean;
  isLifetime: boolean;
  expiryTimeMillis?: number;
  autoRenewing?: boolean;
}

const PACKAGE_NAME = process.env.ANDROID_PACKAGE_NAME || 'com.fieldreport.ai';

export async function verifyGooglePlayPurchase(
  subscriptionOrProductId: string,
  purchaseToken: string,
  isLifetime: boolean = false
): Promise<VerificationResult> {
  // If test token or mock environment
  if (purchaseToken.startsWith('mock_token_') || process.env.NODE_ENV === 'test') {
    const isMockLifetime = isLifetime || subscriptionOrProductId.includes('lifetime');
    return {
      valid: true,
      tier: isMockLifetime ? 'PRO_LIFETIME' : (subscriptionOrProductId.includes('annual') ? 'PRO_ANNUAL' : 'PRO_MONTHLY'),
      isSubscribed: !isMockLifetime,
      isLifetime: isMockLifetime,
      expiryTimeMillis: isMockLifetime ? undefined : Date.now() + 30 * 24 * 60 * 60 * 1000,
      autoRenewing: !isMockLifetime,
    };
  }

  try {
    const auth = new google.auth.GoogleAuth({
      scopes: ['https://www.googleapis.com/auth/androidpublisher'],
    });

    const androidPublisher = google.androidpublisher({
      version: 'v3',
      auth,
    });

    if (isLifetime || subscriptionOrProductId.includes('lifetime')) {
      const purchase = await androidPublisher.purchases.products.get({
        packageName: PACKAGE_NAME,
        productId: subscriptionOrProductId,
        token: purchaseToken,
      });

      const isValid = purchase.data.purchaseState === 0; // 0 = Purchased
      return {
        valid: isValid,
        tier: isValid ? 'PRO_LIFETIME' : 'EXPIRED',
        isSubscribed: false,
        isLifetime: isValid,
      };
    } else {
      const subscription = await androidPublisher.purchases.subscriptions.get({
        packageName: PACKAGE_NAME,
        subscriptionId: subscriptionOrProductId,
        token: purchaseToken,
      });

      const expiry = parseInt(subscription.data.expiryTimeMillis || '0', 10);
      const isValid = expiry > Date.now() && subscription.data.paymentState === 1;
      const tier = subscriptionOrProductId.includes('annual') ? 'PRO_ANNUAL' : 'PRO_MONTHLY';

      return {
        valid: isValid,
        tier: isValid ? tier : 'EXPIRED',
        isSubscribed: isValid,
        isLifetime: false,
        expiryTimeMillis: expiry,
        autoRenewing: subscription.data.autoRenewing ?? false,
      };
    }
  } catch (error) {
    console.error('Error verifying Google Play purchase:', error);
    return {
      valid: false,
      tier: 'EXPIRED',
      isSubscribed: false,
      isLifetime: false,
    };
  }
}

export async function checkOrUpdateUserEntitlement(
  userId: string,
  deviceIdHash: string
): Promise<{ freePdfsRemaining: number; isSubscribed: boolean; isLifetime: boolean }> {
  const db = admin.firestore();
  const userRef = db.collection('users').doc(userId);
  const doc = await userRef.get();

  if (!doc.exists) {
    const initialData = {
      uid: userId,
      deviceIdHash,
      freePdfsRemaining: 3,
      isSubscribed: false,
      isLifetime: false,
      createdAt: Date.now(),
    };
    await userRef.set(initialData);
    return { freePdfsRemaining: 3, isSubscribed: false, isLifetime: false };
  }

  const data = doc.data()!;
  return {
    freePdfsRemaining: data.freePdfsRemaining ?? 3,
    isSubscribed: data.isSubscribed ?? false,
    isLifetime: data.isLifetime ?? false,
  };
}
