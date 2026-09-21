import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import * as admin from 'firebase-admin';
import { verifyGooglePlayPurchase, checkOrUpdateUserEntitlement } from '../services/billing.js';

const verifyExportSchema = z.object({
  reportId: z.string().min(1),
  deviceIdHash: z.string().optional(),
});

const verifyPurchaseSchema = z.object({
  subscriptionOrProductId: z.string().min(1),
  purchaseToken: z.string().min(1),
  isLifetime: z.boolean().optional().default(false),
});

export async function subscriptionRoutes(fastify: FastifyInstance) {
  // Pre-handler hook: verify Firebase Auth token
  fastify.addHook('onRequest', async (request, reply) => {
    try {
      const authHeader = request.headers.authorization;
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return reply.status(401).send({ error: 'Unauthorized: Missing or invalid token format' });
      }
      const idToken = authHeader.split('Bearer ')[1];
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      (request as any).user = decodedToken;
    } catch (err) {
      return reply.status(401).send({ error: 'Unauthorized: Invalid ID token' });
    }
  });

  // POST /v1/reports/verify-export (Check/decrement PDF export trial counter)
  fastify.post('/v1/reports/verify-export', async (request, reply) => {
    const userId = (request as any).user?.uid;
    const bodyResult = verifyExportSchema.safeParse(request.body);
    
    if (!bodyResult.success) {
      return reply.status(400).send({ error: 'Invalid request body', details: bodyResult.error.format() });
    }

    const { deviceIdHash = 'default_device' } = bodyResult.data;
    const db = admin.firestore();
    const userRef = db.collection('users').doc(userId);
    const userDoc = await userRef.get();

    let freePdfsRemaining = 3;
    let isSubscribed = false;
    let isLifetime = false;

    if (!userDoc.exists) {
      await userRef.set({
        uid: userId,
        deviceIdHash,
        freePdfsRemaining: 3,
        isSubscribed: false,
        isLifetime: false,
        createdAt: Date.now(),
      });
    } else {
      const data = userDoc.data()!;
      freePdfsRemaining = data.freePdfsRemaining ?? 3;
      isSubscribed = data.isSubscribed ?? false;
      isLifetime = data.isLifetime ?? false;
    }

    if (isSubscribed || isLifetime) {
      return reply.send({
        status: 'APPROVED',
        isSubscribed,
        isLifetime,
        freePdfsRemaining,
      });
    }

    if (freePdfsRemaining > 0) {
      const newRemaining = freePdfsRemaining - 1;
      await userRef.update({
        freePdfsRemaining: newRemaining,
        updatedAt: Date.now(),
      });
      return reply.send({
        status: 'APPROVED',
        isSubscribed: false,
        isLifetime: false,
        freePdfsRemaining: newRemaining,
      });
    }

    return reply.status(403).send({
      status: 'DENIED',
      reason: 'TRIAL_EXPIRED',
      freePdfsRemaining: 0,
      message: 'Free trial exports depleted. Please upgrade to Pro to generate unlimited PDFs.',
    });
  });

  // POST /v1/subscriptions/verify (Verify Google Play Purchase Token)
  fastify.post('/v1/subscriptions/verify', async (request, reply) => {
    const userId = (request as any).user?.uid;
    const bodyResult = verifyPurchaseSchema.safeParse(request.body);

    if (!bodyResult.success) {
      return reply.status(400).send({ error: 'Invalid purchase token body', details: bodyResult.error.format() });
    }

    const { subscriptionOrProductId, purchaseToken, isLifetime } = bodyResult.data;
    const verification = await verifyGooglePlayPurchase(subscriptionOrProductId, purchaseToken, isLifetime);

    if (!verification.valid) {
      return reply.status(400).send({
        error: 'Invalid or expired purchase token',
        verification,
      });
    }

    const db = admin.firestore();
    const userRef = db.collection('users').doc(userId);
    const updateData: any = {
      isSubscribed: verification.isSubscribed,
      isLifetime: verification.isLifetime,
      subscriptionTier: verification.tier,
      purchaseToken,
      updatedAt: Date.now(),
    };

    if (verification.expiryTimeMillis) {
      updateData.subscriptionExpiry = verification.expiryTimeMillis;
    }

    await userRef.set(updateData, { merge: true });

    return reply.send({
      success: true,
      status: 'VERIFIED',
      entitlement: verification,
    });
  });
}
