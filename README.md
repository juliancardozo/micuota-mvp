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

## Endpoints Principales

### Públicos (No requieren autenticación)
- `GET /health` - Health check de la API
- `GET /plans` - Listar todos los planes disponibles
- `GET /plans/{id}` - Obtener un plan específico
- `POST /auth/register` - Registro de usuario
- `POST /auth/login` - Login y obtención de token JWT
- `POST /webhooks/mercadopago` - Webhook de Mercado Pago

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


