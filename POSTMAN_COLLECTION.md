# Colección de Postman - Micuota MVP

Colección completa de endpoints para probar toda la funcionalidad del MVP de Micuota.

## 📋 Contenido

### 1. Health Check
- **GET /health** - Verificar estado de la API

### 2. Authentication
- **POST /auth/register** - Registro de Profesor
- **POST /auth/register** - Registro de Alumno  
- **POST /auth/login** - Login de Profesor (guarda token automáticamente)
- **POST /auth/login** - Login de Alumno (guarda token automáticamente)

### 3. Plans (Planes SaaS)

#### Planes Mensuales
1. **Starter - Plan Mensual** - $29.99/mes
   - Ideal para emprendedores
   - Características básicas
   - Hasta 100 usuarios
   - Soporte por email
   - Almacenamiento de 10GB

2. **Pro - Plan Mensual** - $99.99/mes
   - Perfecto para equipos en crecimiento
   - Todas las características del Starter
   - Hasta 1,000 usuarios
   - Soporte prioritario
   - Almacenamiento de 100GB
   - Analytics avanzados

3. **Enterprise - Plan Mensual** - $249.99/mes
   - Para grandes organizaciones
   - Todo lo del Pro
   - Usuarios ilimitados
   - Soporte 24/7 dedicado
   - Almacenamiento ilimitado
   - API personalizada
   - SLAs garantizados
   - Account manager dedicado

#### Planes Anuales (Ahorro de 2 meses)
4. **Starter - Plan Anual** - $299.99/año
5. **Pro - Plan Anual** - $999.99/año
6. **Enterprise - Plan Anual** - $2,499.99/año

#### Otros endpoints
- **GET /plans** - Listar todos los planes
- **GET /plans/{id}** - Obtener plan específico
- **POST /plans** - Crear nuevo plan (requiere autenticación de profesor)

### 4. Subscriptions
- **POST /subscriptions** - Crear suscripción a un plan (requiere autenticación de alumno)

### 5. Payments
- **GET /payments** - Listar pagos del profesor autenticado

### 6. Webhooks
- **POST /webhooks/mercadopago** - Webhook de MercadoPago (Payment Approved)
- **POST /webhooks/mercadopago** - Webhook de MercadoPago (Payment Rejected)
- **POST /webhooks/mercadopago** - Webhook de MercadoPago (Payment Pending)

## 🚀 Instalación

### Opción 1: Importar en Postman Desktop

1. Abre Postman Desktop
2. Haz clic en **Import** (botón en la esquina superior izquierda)
3. Selecciona el archivo `Micuota_MVP.postman_collection.json`
4. La colección aparecerá en tu workspace

### Opción 2: Importar en Postman Web

1. Ve a [app.getpostman.com](https://app.getpostman.com)
2. Haz clic en **Import** 
3. Selecciona el archivo `Micuota_MVP.postman_collection.json`
4. La colección aparecerá en tu workspace

### Opción 3: Importar por URL (después de subir a repo)

1. En Postman, haz clic en **Import**
2. Selecciona la pestaña **Link**
3. Pega la URL del archivo en GitHub/GitLab
4. Haz clic en **Import**

## ⚙️ Configuración

### Variables de Entorno

La colección incluye dos variables:

- **base_url**: `http://localhost:8080` (por defecto)
- **jwt_token**: Se guarda automáticamente al hacer login

### Cambiar la URL Base

Si tu API está en otro servidor:

1. Haz clic derecho en la colección
2. Selecciona **Edit**
3. Ve a la pestaña **Variables**
4. Cambia el valor de `base_url`
5. Guarda los cambios

## 📝 Guía de Uso

### Flujo Completo: Registrar, Login y Crear Plan

1. **Registrar Profesor**
   - Ejecuta: `Authentication > Register - Profesor`
   - Observa la respuesta con el usuario creado

2. **Login Profesor**
   - Ejecuta: `Authentication > Login - Profesor`
   - El token JWT se guarda automáticamente en `{{jwt_token}}`
   - Verifica el mensaje en la consola: "Token guardado en variable jwt_token"

3. **Crear Plan**
   - Ejecuta cualquier plan en `Plans > 01-06`
   - Ejemplo: `Plans > 01 - Create Plan Starter (Mensual)`
   - El token se envía automáticamente

### Flujo Completo: Alumno se Suscribe

1. **Registrar Alumno**
   - Ejecuta: `Authentication > Register - Alumno`
   - Cambia el email si ya existe

2. **Login Alumno**
   - Ejecuta: `Authentication > Login - Alumno`
   - El token se guarda automáticamente

3. **Ver Planes Disponibles**
   - Ejecuta: `Plans > Get All Plans`
   - Identifica el ID del plan deseado

4. **Crear Suscripción**
   - Ejecuta: `Subscriptions > Create Subscription`
   - Cambia el `planId` en el body por el ID del plan

5. **Ver Pagos** (como profesor)
   - Haz login como profesor
   - Ejecuta: `Payments > Get Payments`

### Probar Webhooks

1. **Simular Pago Aprobado**
   - Ejecuta: `Webhooks > MercadoPago Webhook - Payment`
   - Cambia `subscription_id` por un ID real de suscripción

2. **Simular Pago Rechazado**
   - Ejecuta: `Webhooks > MercadoPago Webhook - Payment Rejected`

3. **Simular Pago Pendiente**
   - Ejecuta: `Webhooks > MercadoPago Webhook - Payment Pending`

## 🔒 Seguridad

### Autenticación Bearer Token

Todos los endpoints protegidos usan autenticación Bearer Token:

```
Authorization: Bearer {{jwt_token}}
```

El token se obtiene automáticamente al hacer login y se guarda en la variable de entorno.

### Endpoints Públicos (No requieren autenticación)

- GET /health
- GET /plans
- GET /plans/{id}
- POST /auth/register
- POST /auth/login
- POST /webhooks/mercadopago

### Endpoints Protegidos (Requieren autenticación)

- POST /plans
- POST /subscriptions
- GET /payments

## 🐛 Troubleshooting

### Error 401 Unauthorized

**Problema**: No estás autenticado

**Solución**:
1. Asegúrate de haber hecho login primero
2. Verifica que el token se guardó correctamente
3. Mira la consola de Postman para ver si dice "Token guardado"

### Error 403 Forbidden

**Problema**: No tienes permisos para este endpoint

**Solución**:
- Los profesores solo pueden crear planes
- Los alumnos solo pueden crear suscripciones
- Los profesores solo pueden ver sus pagos

### Error 500 Internal Server Error

**Problema**: Error en el servidor o datos inválidos

**Solución**:
1. Verifica que la API esté corriendo
2. Revisa los logs: `docker-compose logs -f app`
3. Verifica que los datos del request sean correctos

### Token no se guarda automáticamente

**Problema**: El script de test no se ejecuta

**Solución**:
1. Verifica que estés usando el login correcto (el que tiene el script)
2. Ve a la pestaña "Tests" del request para ver si hay errores
3. Ejecuta manualmente el login y copia el token a la variable

### No veo los planes después de crearlos

**Problema**: El plan se creó pero no aparece en la lista

**Solución**:
1. Verifica que hiciste login antes de crear el plan
2. Verifica los logs del servidor
3. Prueba con `GET /plans` para listar todos

## 📊 Ejemplos de Respuestas

### Register Success

```json
{
    "id": 1,
    "name": "Profesor Juan Pérez",
    "email": "profesor@example.com",
    "role": "PROFESOR",
    "password": "$2a$10$...",
    "mpUserId": null
}
```

### Login Success

```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Plan Created

```json
{
    "id": 1,
    "title": "Starter - Plan Mensual",
    "price": 29.99,
    "frequency": "MONTHLY",
    "mpPlanId": "2c9380848...",
    "createdAt": "2025-11-03T16:00:00"
}
```

### Webhook Success

```json
"received"
```

## 🎯 Próximos Pasos

1. Importa la colección en tu Postman
2. Configura `base_url` si es necesario
3. Sigue el flujo completo de usuario
4. Prueba todos los endpoints
5. Explora las respuestas y adapta los datos según necesites

## 📞 Soporte

Para más información:
- Revisa los logs: `docker-compose logs -f app`
- Consulta el README.md del proyecto
- Verifica que todos los servicios estén corriendo: `docker-compose ps`

