@echo off
echo ========================================
echo   DESPLIEGUE DE MICUOTA MVP
echo ========================================
echo.

echo [1/3] Deteniendo contenedores existentes...
docker-compose down

echo.
echo [2/3] Construyendo y levantando servicios...
docker-compose up -d --build

echo.
echo [3/3] Esperando a que los servicios inicien...
timeout /t 15 /nobreak >nul 2>&1

echo.
echo ========================================
echo   DESPLIEGUE COMPLETADO
echo ========================================
echo.
echo La aplicacion esta disponible en:
echo   - API: http://localhost:8080
echo   - Health: http://localhost:8080/health
echo.
echo Para ver los logs: docker-compose logs -f
echo Para detener: docker-compose down
echo.
pause

