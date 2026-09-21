import { describe, it, expect, vi, beforeEach } from 'vitest';
import Fastify from 'fastify';
import { subscriptionRoutes } from '../subscriptions.js';

// Mock firebase-admin
vi.mock('firebase-admin', () => {
  const mockUpdate = vi.fn().mockResolvedValue({});
  const mockSet = vi.fn().mockResolvedValue({});
  const mockGet = vi.fn().mockImplementation(() => {
    return Promise.resolve({
      exists: true,
      data: () => ({
        freePdfsRemaining: 3,
        isSubscribed: false,
        isLifetime: false,
      }),
    });
  });

  const mockDoc = vi.fn().mockReturnValue({
    get: mockGet,
    update: mockUpdate,
    set: mockSet,
  });

  const mockCollection = vi.fn().mockReturnValue({
    doc: mockDoc,
  });

  const mockAuth = () => ({
    verifyIdToken: vi.fn().mockImplementation(async (token: string) => {
      if (token === 'valid_test_token') {
        return { uid: 'test-user-123' };
      }
      throw new Error('Invalid token');
    }),
  });

  const mockFirestore = () => ({
    collection: mockCollection,
  });

  return {
    auth: mockAuth,
    firestore: mockFirestore,
    default: {
      auth: mockAuth,
      firestore: mockFirestore,
    },
  };
});

describe('Subscription & Entitlement Routes', () => {
  let app: ReturnType<typeof Fastify>;

  beforeEach(async () => {
    app = Fastify();
    await app.register(subscriptionRoutes);
    await app.ready();
  });

  it('rejects unauthenticated requests to verify-export', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/v1/reports/verify-export',
      payload: { reportId: 'rep-1' },
    });
    expect(response.statusCode).toBe(401);
  });

  it('approves verify-export when user has free trials remaining', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/v1/reports/verify-export',
      headers: { authorization: 'Bearer valid_test_token' },
      payload: { reportId: 'rep-1', deviceIdHash: 'device_123' },
    });
    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.body);
    expect(body.status).toBe('APPROVED');
    expect(body.freePdfsRemaining).toBe(2);
  });

  it('verifies purchase tokens for subscriptions and lifetime passes', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/v1/subscriptions/verify',
      headers: { authorization: 'Bearer valid_test_token' },
      payload: {
        subscriptionOrProductId: 'field_report_ai_pro_monthly',
        purchaseToken: 'mock_token_abc123',
        isLifetime: false,
      },
    });
    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.body);
    expect(body.success).toBe(true);
    expect(body.status).toBe('VERIFIED');
    expect(body.entitlement.isSubscribed).toBe(true);
  });
});
