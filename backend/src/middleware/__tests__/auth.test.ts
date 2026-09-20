import { describe, it, expect, vi, beforeEach } from 'vitest';
import { FastifyRequest, FastifyReply } from 'fastify';

// Mock firebase-admin
const mockVerifyIdToken = vi.fn();
vi.mock('firebase-admin', () => ({
  apps: [{}],
  initializeApp: vi.fn(),
  auth: () => ({
    verifyIdToken: mockVerifyIdToken
  })
}));

import { verifyAuth } from '../auth';

describe('Auth Middleware (verifyAuth)', () => {
  let mockRequest: Partial<FastifyRequest>;
  let mockReply: Partial<FastifyReply>;
  let statusMock: ReturnType<typeof vi.fn>;
  let sendMock: ReturnType<typeof vi.fn>;
  let errorMock: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    vi.clearAllMocks();
    sendMock = vi.fn();
    statusMock = vi.fn().mockReturnValue({ send: sendMock });
    errorMock = vi.fn();

    mockReply = {
      status: statusMock as any,
      send: sendMock as any
    };

    mockRequest = {
      headers: {},
      log: {
        error: errorMock
      } as any
    };
  });

  it('should return 401 if Authorization header is missing', async () => {
    mockRequest.headers = {};

    await verifyAuth(mockRequest as FastifyRequest, mockReply as FastifyReply);

    expect(statusMock).toHaveBeenCalledWith(401);
    expect(sendMock).toHaveBeenCalledWith({ error: 'Missing or invalid Authorization header' });
  });

  it('should return 401 if Authorization header does not start with Bearer ', async () => {
    mockRequest.headers = { authorization: 'Basic 12345' };

    await verifyAuth(mockRequest as FastifyRequest, mockReply as FastifyReply);

    expect(statusMock).toHaveBeenCalledWith(401);
    expect(sendMock).toHaveBeenCalledWith({ error: 'Missing or invalid Authorization header' });
  });

  it('should attach decoded token to request when verification succeeds', async () => {
    mockRequest.headers = { authorization: 'Bearer valid-firebase-token' };
    const decodedToken = { uid: 'user-123', email: 'technician@test.com' };
    mockVerifyIdToken.mockResolvedValueOnce(decodedToken);

    await verifyAuth(mockRequest as FastifyRequest, mockReply as FastifyReply);

    expect(mockVerifyIdToken).toHaveBeenCalledWith('valid-firebase-token');
    expect((mockRequest as any).user).toEqual(decodedToken);
    expect(statusMock).not.toHaveBeenCalled();
    expect(sendMock).not.toHaveBeenCalled();
  });

  it('should return 401 if token verification throws an error', async () => {
    mockRequest.headers = { authorization: 'Bearer expired-token' };
    mockVerifyIdToken.mockRejectedValueOnce(new Error('Token expired'));

    await verifyAuth(mockRequest as FastifyRequest, mockReply as FastifyReply);

    expect(statusMock).toHaveBeenCalledWith(401);
    expect(sendMock).toHaveBeenCalledWith({ error: 'Unauthorized' });
    expect(errorMock).toHaveBeenCalled();
  });
});
