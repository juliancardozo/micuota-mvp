#!/bin/bash

# Script de despliegue para Micuota MVP

echo "🚀 Desplegando Micuota MVP..."
echo ""

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar que Docker Compose esté instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

# Detener contenedores existentes si los hay
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir y levantar los servicios
echo "🔨 Construyendo imágenes..."
docker-compose build

echo "🎯 Levantando servicios..."
docker-compose up -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios estén listos..."
sleep 10

# Verificar estado
echo ""
echo "📊 Estado de los contenedores:"
docker-compose ps

echo ""
echo "✅ ¡Despliegue completado!"
echo ""
echo "🌐 La API está disponible en: http://localhost:8080"
echo "🗄️  PostgreSQL está disponible en: localhost:5432"
echo ""
echo "Para ver los logs, ejecuta: docker-compose logs -f"
echo "Para detener los servicios, ejecuta: docker-compose down"


