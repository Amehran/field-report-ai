import { FastifyRequest, FastifyReply } from 'fastify';
import * as admin from 'firebase-admin';

// Initialize Firebase Admin
// This uses default credentials automatically in Google Cloud Run
// For local testing, ensure GOOGLE_APPLICATION_CREDENTIALS is set
if (!admin.apps.length) {
  admin.initializeApp();
}

export const verifyAuth = async (request: FastifyRequest, reply: FastifyReply) => {
  const authHeader = request.headers.authorization;
  if (!authHeader?.startsWith('Bearer ')) {
    reply.status(401).send({ error: 'Missing or invalid Authorization header' });
    return;
  }

  const idToken = authHeader.split('Bearer ')[1];
  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    // Attach decoded token to request for downstream use
    (request as any).user = decodedToken;
  } catch (error) {
    request.log.error('Firebase token verification failed:', error);
    reply.status(401).send({ error: 'Unauthorized' });
  }
};
