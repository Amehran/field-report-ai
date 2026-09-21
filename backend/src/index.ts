import Fastify from 'fastify';
import { reportRoutes } from './routes/reports.js';
import { subscriptionRoutes } from './routes/subscriptions.js';

const fastify = Fastify({
  logger: true
});

fastify.register(reportRoutes);
fastify.register(subscriptionRoutes);

// Health check endpoint
fastify.get('/health', async () => {
  return { status: 'ok' };
});

const start = async () => {
  try {
    const port = parseInt(process.env.PORT || '8080', 10);
    await fastify.listen({ port, host: '0.0.0.0' });
    console.log(`Server listening on port ${port}`);
  } catch (err) {
    fastify.log.error(err);
    process.exit(1);
  }
};

start();
