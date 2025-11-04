# Micuota Frontend (Fastify)

Pequeño frontend servido por Fastify que permite probar los endpoints principales del backend (registro, login, crear plan).

Requisitos:
- Node.js 18+ y npm
- Backend Micuota corriendo (por defecto en http://localhost:8080). Si usas ngrok, exporta la URL con `BACKEND_URL`.

Instalación y ejecución (PowerShell):

```powershell
cd frontend-fastify
npm install
# Opcional: set BACKEND_URL for tests; in PowerShell:
$env:BACKEND_URL = 'https://e0ec3891d12a.ngrok-free.app'
npm start
```

Luego abre http://localhost:3000 en tu navegador.

Notas:
- El servidor Fastify expone `/config` que el frontend usa para conocer `BACKEND_URL`.
- Si no se configura `BACKEND_URL`, el frontend usará `http://localhost:8080`.
- Este proyecto es minimal para pruebas y demos. Puedes mejorar la UX y seguridad según necesites.
