# Micuota MVP

Micro plataforma de suscripciones MVP con Spring Boot, PostgreSQL y Mercado Pago.

## Requisitos

- Docker 20.10+
- Docker Compose 2.0+

## Despliegue con Docker

### Opción 1: Docker Compose (Recomendado)

```bash
# Construir y levantar los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener los servicios
docker-compose down

# Detener y eliminar volúmenes (resetear base de datos)
docker-compose down -v
```

### Opción 2: Docker Manual

```bash
# Construir la imagen
docker build -t micuota-app .

# Levantar PostgreSQL
docker run -d \
  --name micuota-postgres \
  -e POSTGRES_DB=micuota \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=root \
  -p 5432:5432 \
  postgres:15-alpine

# Ejecutar la aplicación
docker run -d \
  --name micuota-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/micuota \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=root \
  micuota-app
```

## Acceso

- **API**: http://localhost:8080
- **PostgreSQL**: localhost:5432
  - Database: micuota
  - Usuario: postgres
  - Password: root

## Frontend (landing + demo)

- **Landing**: http://localhost:3000  (archivo: `frontend-fastify/public/index.html`)
- **Demo UI**: http://localhost:3000/app.html  (archivo: `frontend-fastify/public/app.html`)

El frontend se sirve por el servicio `frontend` en `docker-compose.yml` (nginx). Si modificas archivos estáticos, reconstruye/recarga el servicio con:

```powershell
docker-compose up --build -d frontend
```

## Endpoints Principales

### Públicos (No requieren autenticación)
- `GET /health` - Health check de la API
- `GET /plans` - Listar todos los planes disponibles
- `GET /plans/{id}` - Obtener un plan específico
- `POST /auth/register` - Registro de usuario
- `POST /auth/login` - Login y obtención de token JWT
- `POST /webhooks/mercadopago` - Webhook de Mercado Pago

### Endpoints adicionales útiles para demo / desarrollo
- `POST /demo/seed` - Crea usuarios demo (profesor/alumno), y un plan de ejemplo. *Solo para desarrollo.*


### Protegidos (Requieren token JWT en header)
- `POST /plans` - Crear nuevo plan (requiere autenticación)
- `POST /subscriptions` - Crear suscripción (requiere autenticación)
- `GET /payments` - Listar pagos (requiere autenticación)

### Ejemplo de uso

```bash
# Verificar que la API funciona
curl http://localhost:8080/health

# Listar planes disponibles
curl http://localhost:8080/plans

# Registrar usuario
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Pérez","email":"juan@example.com","password":"password123","role":"PROFESOR"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@example.com","password":"password123"}'

# Usar token JWT para endpoints protegidos
curl -X POST http://localhost:8080/plans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{"title":"Plan Básico","price":"100","frequency":"MONTHLY"}'
```

## Seguridad

⚠️ **Importante**: En producción, cambiar:
- JWT_SECRET
- POSTGRES_PASSWORD
- Usar variables de entorno seguras

## Mocks y pruebas

Este repo incluye WireMock para mockear MercadoPago y facilitar demos sin claves reales.

- Servicio: `wiremock` en `docker-compose.yml` (monta `mocks/wiremock`)
- Endpoints mockeados: revisa `mocks/wiremock/mappings/*.json`

Para ejecutar los flujos automáticos (E2E) hay scripts PowerShell en `scripts/`:

- `scripts/run-e2e.ps1` — Ejecuta un flujo de prueba: health → register → login → crear plan → suscribirse → webhook.
- `scripts/demo-student-pay.ps1` — Flow rápido que usa `/demo/seed`, login de alumno y `payments/charge`.

Ejecuta (PowerShell):

```powershell
.\scripts\run-e2e.ps1 -BaseUrl "http://localhost:8080" -TimeoutSec 120
```

Si necesitas exponer la demo públicamente (ngrok), recuerda proteger `/demo/seed` (ver sección Hardening).

## CORS

El backend está configurado para permitir llamadas desde `http://localhost:3000` y patrones de ngrok (útil para demos). La configuración CORS está en `src/main/java/com/micuota/config/SecurityConfig.java`.

Si tienes problemas con preflight (OPTIONS), revisa la respuesta del servidor y asegúrate de que el header `Access-Control-Allow-Origin` incluya el origen de la página que hace la petición.

## Hardening / Producción

- Deshabilitar `POST /demo/seed` en entornos públicos. Se recomienda usar la variable de entorno `ALLOW_DEMO_SEED=false` por defecto y solo activarla en local.
- No exponer claves o secretos en imágenes públicas. Usa secretos del orquestador o variables de entorno (Docker secrets, Kubernetes secrets).
- Habilitar TLS y configurar dominios para el frontend y API.

## Quick start (resumen)

1. Levantar servicios:

```powershell
docker-compose up --build -d
```

2. Abrir landing:

http://localhost:3000

3. Abrir demo (si quieres probar el flujo):

http://localhost:3000/app.html

4. Ejecutar script E2E (opcional):

```powershell
.\scripts\run-e2e.ps1 -BaseUrl "http://localhost:8080" -TimeoutSec 120
```

## Contribuir

Si quieres mejorar el demo o la landing:

1. Crea una rama a partir de `main`.
2. Haz cambios en `frontend-fastify/public` y prueba reconstruyendo el servicio `frontend`.
3. Asegúrate de que `mvn clean package` compile si tocas código Java.

--

Si quieres, puedo añadir una sección de captura de emails en la landing (form simple que hace POST a `/leads`) o implementar la protección `ALLOW_DEMO_SEED` en backend.

## Colección de Postman

📁 **Archivo**: `Micuota_MVP.postman_collection.json`

Incluye todos los endpoints con ejemplos de planes SaaS (Starter, Pro, Enterprise) y casos de uso completos.

### Importar en Postman

1. Abre Postman (Desktop o Web)
2. Haz clic en **Import**
3. Selecciona el archivo `Micuota_MVP.postman_collection.json`
4. ¡Listo! Ya puedes probar todos los endpoints

### Características de la Colección

✅ **Scripts automáticos**: Login guarda el token JWT automáticamente  
✅ **Variables preconfiguradas**: base_url y jwt_token  
✅ **Ejemplos reales**: 6 planes SaaS listos para usar  
✅ **Webhooks**: Ejemplos de pagos aprobados/rechazados/pendientes  
✅ **Documentación completa**: Cada endpoint tiene su descripción  

📖 **Ver guía completa**: [POSTMAN_COLLECTION.md](POSTMAN_COLLECTION.md)

## Desarrollo Local

```bash
# Sin Docker
mvn clean package
java -jar target/micuota-mvp-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Ver logs de los contenedores

```bash
docker-compose logs -f app
docker-compose logs -f postgres
```

### Reconstruir imagen

```bash
docker-compose build --no-cache
docker-compose up -d
```

### Resetear base de datos

```bash
docker-compose down -v
docker-compose up -d
```


