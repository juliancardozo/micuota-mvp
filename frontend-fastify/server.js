const path = require('path')
const Fastify = require('fastify')
const fastifyStatic = require('fastify-static')

const PORT = process.env.PORT || 3000
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8080'

const fastify = Fastify({ logger: true })

// Serve static files from ./public
fastify.register(fastifyStatic, {
  root: path.join(__dirname, 'public'),
  prefix: '/',
})

// Simple endpoint to expose backend url to client-side JS
fastify.get('/config', async (request, reply) => {
  return { backendUrl: BACKEND_URL }
})

const start = async () => {
  try {
    await fastify.listen({ port: PORT, host: '0.0.0.0' })
    console.log(`Frontend server listening on http://localhost:${PORT}`)
  } catch (err) {
    fastify.log.error(err)
    process.exit(1)
  }
}

start()
