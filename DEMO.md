# Demo rápida (MVP)

Pasos para levantar el MVP localmente y ejecutar la demo E2E.

1) Variables/precauciones
   - La base de datos será inicializada vacía si borraste volúmenes.
   - Si necesitas conservar datos, crea un dump antes de `docker-compose down --volumes`.

2) Levantar servicios (backend + postgres + frontend estático)

   Desde la raíz del proyecto:

   ```powershell
   docker-compose up --build -d
   ```

   - Backend en: http://localhost:8080
   - Frontend estático en: http://localhost:3000

3) Ejecutar E2E (rápido)

   ```powershell
   .\scripts\run-e2e.ps1 -BaseUrl "http://localhost:8080" -TimeoutSec 120
   ```

4) Exponer públicamente (opcional)

   - Inicia ngrok y apunta a 8080 (puedes usar `scripts/run-public-ngrok.ps1` si lo tienes preparado).

5) Limpieza

   ```powershell
   docker-compose down --volumes
   ```
