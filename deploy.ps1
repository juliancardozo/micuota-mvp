# Script de despliegue para Micuota MVP (PowerShell)

Write-Host "🚀 Desplegando Micuota MVP..." -ForegroundColor Cyan
Write-Host ""

# Verificar que Docker esté instalado
try {
    $dockerVersion = docker --version
    Write-Host "✅ Docker encontrado: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker no está instalado. Por favor instala Docker Desktop primero." -ForegroundColor Red
    exit 1
}

# Verificar que Docker Compose esté instalado
try {
    $composeVersion = docker-compose --version
    Write-Host "✅ Docker Compose encontrado: $composeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose no está instalado." -ForegroundColor Red
    exit 1
}

Write-Host ""

# Detener contenedores existentes si los hay
Write-Host "🛑 Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose down

# Construir y levantar los servicios
Write-Host "🔨 Construyendo imágenes..." -ForegroundColor Yellow
docker-compose build

Write-Host "🎯 Levantando servicios..." -ForegroundColor Yellow
docker-compose up -d

# Esperar a que los servicios estén listos
Write-Host "⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar estado
Write-Host ""
Write-Host "📊 Estado de los contenedores:" -ForegroundColor Cyan
docker-compose ps

Write-Host ""
Write-Host "✅ ¡Despliegue completado!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 La API está disponible en: http://localhost:8080" -ForegroundColor Cyan
Write-Host "🗄️  PostgreSQL está disponible en: localhost:5432" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para ver los logs, ejecuta: docker-compose logs -f" -ForegroundColor Yellow
Write-Host "Para detener los servicios, ejecuta: docker-compose down" -ForegroundColor Yellow


