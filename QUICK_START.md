# 🚀 Guía de Inicio Rápido - Micuota MVP

Guía rápida para poner en marcha y probar el MVP en minutos.

## ⚡ Inicio en 3 Pasos

### 1️⃣ Levantar la API

```bash
docker-compose up -d
```

Espera ~30 segundos a que se inicie todo.

### 2️⃣ Verificar que funciona

```bash
curl http://localhost:8080/health
```

Deberías ver: `{"status":"OK","service":"Micuota MVP","version":"0.0.1-SNAPSHOT"}`

### 3️⃣ Importar colección de Postman

1. Abre Postman
2. Import → Select File → `Micuota_MVP.postman_collection.json`
3. ¡Listo para probar!

## 📋 Flujo de Prueba Recomendado

### Paso 1: Autenticación

1. **Register - Profesor** → Crea un profesor
2. **Login - Profesor** → Inicia sesión (el token se guarda automáticamente ✅)

### Paso 2: Crear Planes

3. **Create Plan Starter** → Crea plan de $29.99/mes
4. **Get All Plans** → Lista los planes creados

### Paso 3: Suscribirse

5. **Login - Alumno** → Inicia sesión como alumno
6. **Create Subscription** → Suscríbete al plan Starter

### Paso 4: Ver Pagos

7. **Login - Profesor** → Vuelve como profesor
8. **Get Payments** → Ve los pagos recibidos

## 🎯 Casos de Uso Predefinidos

### Planes SaaS Incluidos

**Mensuales:**
- Starter: $29.99/mes
- Pro: $99.99/mes  
- Enterprise: $249.99/mes

**Anuales (Ahorro 2 meses):**
- Starter: $299.99/año
- Pro: $999.99/año
- Enterprise: $2,499.99/año

### Usuarios de Prueba

**Profesor:**
```
Email: profesor@example.com
Password: password123
```

**Alumno:**
```
Email: alumno@example.com
Password: password123
```

## 🔧 Comandos Útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Reiniciar todo
docker-compose restart

# Detener todo
docker-compose down

# Resetear base de datos
docker-compose down -v && docker-compose up -d
```

## 📊 Estado del Servicio

```bash
docker-compose ps
```

Deberías ver:
- ✅ micuota-app: Up
- ✅ micuota-postgres: Up (healthy)

## 🐛 Problemas Comunes

### La API no responde

```bash
# Verificar que está corriendo
docker-compose ps

# Ver logs de error
docker-compose logs app
```

### Puerto 8080 ocupado

Edita `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Cambia el puerto
```

### Error de base de datos

```bash
# Resetear completamente
docker-compose down -v
docker-compose up -d
```

## 📚 Más Información

- **Documentación completa**: [README.md](README.md)
- **Guía de Postman**: [POSTMAN_COLLECTION.md](POSTMAN_COLLECTION.md)
- **Variables de entorno**: Ver `docker-compose.yml`

## ✅ Checklist de Verificación

- [ ] Docker Desktop está corriendo
- [ ] Comando `docker-compose up -d` ejecutado
- [ ] Health check responde OK
- [ ] Colección de Postman importada
- [ ] Puedo hacer login como profesor
- [ ] Puedo crear un plan
- [ ] Puedo suscribirme como alumno

## 🎉 ¡Todo Listo!

Ya tienes el MVP funcionando. Explora los endpoints y prueba todos los casos de uso.

**Próximos pasos:**
1. Configura tus propias credenciales de Mercado Pago
2. Personaliza los planes según tu negocio
3. Despliega en producción

---

¿Preguntas? Revisa los logs con `docker-compose logs -f`

