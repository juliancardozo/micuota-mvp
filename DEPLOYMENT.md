# Despliegue y exposición pública de Micuota MVP

Este documento explica dos formas sencillas de "disponibilizar" la API en Internet:

A) Exposición temporal y rápida con ngrok (ideal para demos)
B) Despliegue en producción usando Docker Compose + Caddy (TLS automático) en un VPS

---

A) Exposición rápida con ngrok (recomendada para pruebas y demos)

Requisitos:
- ngrok instalado y autenticado (https://ngrok.com)
- La app corriendo localmente en `http://localhost:8080` (puedes usar `docker-compose up --build -d`)

Pasos:
1. Arranca la app localmente (si aún no está corriendo):

```powershell
cd C:\Users\julia\Desktop\micuota\micuota-mvp
docker-compose up --build -d
```

2. Ejecuta el script incluido para exponer el puerto 8080 mediante ngrok:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run-public-ngrok.ps1
```

El script mostrará la URL pública (https) que redirige a tu API. Comparte esa URL para que otros puedan consumir los endpoints.

Limitaciones:
- ngrok URLs son temporales (a menos que uses una cuenta paga y dominios reservados).
- No es ideal para producción.

---

B) Despliegue en producción en un VPS con Docker Compose + Caddy (TLS automático)

Resumen de la arquitectura propuesta:
- Un servidor VPS (DigitalOcean, AWS EC2, Linode, etc.) con Docker y docker-compose instalados.
- `docker-compose.prod.yml` en el servidor que levanta: Postgres, la app (jar), y Caddy como reverse proxy y ACME (Let's Encrypt) para certificados TLS.
- Dominio apuntando al IP público del VPS.

Pasos (alto nivel):
1. Reservar un dominio y apuntar el A record al IP del VPS.
2. Subir el repo o copiar `docker-compose.prod.yml` al VPS.
3. Crear un archivo `.env.prod` con variables críticas (no subirlo a git):

```
DOMAIN=api.tudominio.com
JWT_SECRET=tu_jwt_secret_seguro
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/micuota
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_password_seguro
SPRING_PROFILES_ACTIVE=prod
```

4. En el VPS, en la carpeta con `docker-compose.prod.yml` ejecutar:

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d --build
```

5. Caddy hará el provisioning de certificados automáticamente si el dominio apunta al VPS.

Seguridad y recomendaciones:
- Nunca comprometas `JWT_SECRET` ni las credenciales DB en Git.
- Configura firewall (ufw) para permitir sólo puertos 80/443 y SSH.
- Considera usar un servicio de secretos (Vault, AWS Secrets Manager) para producción.

---

Si quieres que haga:
- Ejecutar el script ngrok ahora y te devuelva la URL pública, o
- Preparar el VPS (crear `docker-compose.prod.yml` y/o `.env.prod` y guiar paso a paso),
- Automatizar CI/CD (GitHub Actions) para desplegar en tu VPS o en un service (ECS, Azure App Service),

indica cuál prefieres y lo pongo en marcha.
