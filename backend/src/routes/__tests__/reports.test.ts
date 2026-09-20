import { describe, it, expect, vi, beforeEach } from 'vitest';
import Fastify from 'fastify';
import { reportRoutes } from '../reports';

// Mock dependencies
vi.mock('../../services/gemini', () => ({
  generateReportDraft: vi.fn().mockResolvedValue({
    workCompletedJson: "Completed task A",
    findingsJson: "Found issue B",
    recommendationsJson: "Fix issue B"
  })
}));

vi.mock('../../middleware/auth', () => ({
  verifyAuth: vi.fn().mockImplementation(async (request, reply) => {
    if (request.headers.authorization !== 'Bearer valid-token') {
      reply.status(401).send({ error: 'Unauthorized' });
      return;
    }
  })
}));

describe('Report Routes', () => {
  let fastify: any;

  beforeEach(() => {
    vi.clearAllMocks();
    fastify = Fastify({ logger: false });
    fastify.register(reportRoutes);
  });

  it('should return 401 if unauthorized', async () => {
    const response = await fastify.inject({
      method: 'POST',
      url: '/generate',
      payload: {}
    });

    expect(response.statusCode).toBe(401);
  });

  it('should return 400 if idempotency key is missing', async () => {
    const response = await fastify.inject({
      method: 'POST',
      url: '/generate',
      headers: { authorization: 'Bearer valid-token' },
      payload: {
        jobTitle: "Test Job",
        customerName: "Jane Doe"
      }
    });

    expect(response.statusCode).toBe(400);
    expect(JSON.parse(response.payload).error).toBe('Missing X-Idempotency-Key header');
  });

  it('should return 400 if body validation fails', async () => {
    const response = await fastify.inject({
      method: 'POST',
      url: '/generate',
      headers: { 
        authorization: 'Bearer valid-token',
        'x-idempotency-key': 'key-123'
      },
      payload: {
        jobTitle: "Test Job" // Missing customerName
      }
    });

    expect(response.statusCode).toBe(400);
    expect(JSON.parse(response.payload).error).toBe('Validation Error');
  });

  it('should return 200 and draft if successful', async () => {
    const response = await fastify.inject({
      method: 'POST',
      url: '/generate',
      headers: { 
        authorization: 'Bearer valid-token',
        'x-idempotency-key': 'key-123'
      },
      payload: {
        jobTitle: "Test Job",
        customerName: "Jane Doe"
      }
    });

    expect(response.statusCode).toBe(200);
    const data = JSON.parse(response.payload);
    expect(data.workCompletedJson).toBe("Completed task A");
  });

  it('should return cached draft if idempotency key is reused', async () => {
    // Make first request
    await fastify.inject({
      method: 'POST',
      url: '/generate',
      headers: { authorization: 'Bearer valid-token', 'x-idempotency-key': 'key-124' },
      payload: { jobTitle: "Job 1", customerName: "Jane" }
    });

    // Make second request with the same key
    const response2 = await fastify.inject({
      method: 'POST',
      url: '/generate',
      headers: { authorization: 'Bearer valid-token', 'x-idempotency-key': 'key-124' },
      payload: { jobTitle: "Job 2", customerName: "John" } // Payload shouldn't matter due to cache
    });

    expect(response2.statusCode).toBe(200);
    // The mock always returns the same payload currently, but we can verify that the service wasn't called twice
    const { generateReportDraft } = await import('../../services/gemini');
    expect(generateReportDraft).toHaveBeenCalledTimes(1);
  });
});
