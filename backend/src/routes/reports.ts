import { FastifyInstance, FastifyRequest, FastifyReply } from 'fastify';
import { z } from 'zod';
import { verifyAuth } from '../middleware/auth';
import { generateReportDraft } from '../services/gemini';

const GenerateReportSchema = z.object({
  jobTitle: z.string(),
  customerName: z.string(),
  typedNotes: z.string().optional(),
  mediaUris: z.array(z.string()).default([])
});

type GenerateReportRequest = z.infer<typeof GenerateReportSchema>;

// Simple in-memory cache for idempotency
const idempotencyCache = new Map<string, any>();

export async function reportRoutes(fastify: FastifyInstance) {
  fastify.post('/generate', { preHandler: [verifyAuth] }, async (request: FastifyRequest, reply: FastifyReply) => {
    try {
      const idempotencyKey = request.headers['x-idempotency-key'] as string;
      if (!idempotencyKey) {
        return reply.status(400).send({ error: 'Missing X-Idempotency-Key header' });
      }

      if (idempotencyCache.has(idempotencyKey)) {
        return reply.status(200).send(idempotencyCache.get(idempotencyKey));
      }

      const body = GenerateReportSchema.parse(request.body);

      request.log.info({ reportId: idempotencyKey }, "Generating report draft");

      const draft = await generateReportDraft({
        jobTitle: body.jobTitle,
        customerName: body.customerName,
        typedNotes: body.typedNotes,
        mediaUris: body.mediaUris
      });

      idempotencyCache.set(idempotencyKey, draft);

      return reply.status(200).send(draft);
    } catch (error) {
      if (error instanceof z.ZodError) {
        return reply.status(400).send({ error: 'Validation Error', details: error.errors });
      }
      
      request.log.error(error);
      return reply.status(500).send({ error: 'Internal Server Error' });
    }
  });
}
