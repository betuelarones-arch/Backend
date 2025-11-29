#!/bin/bash

# ============================================
# Script de Despliegue - Desarrollo
# ============================================

echo "🚀 Iniciando despliegue en modo DESARROLLO..."

# Verificar que Docker esté corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    exit 1
fi

# Cargar variables de entorno
if [ -f .env.dev ]; then
    echo "📝 Cargando variables de entorno de desarrollo..."
    export $(cat .env.dev | grep -v '^#' | xargs)
else
    echo "⚠️  Advertencia: No se encontró .env.dev, usando valores por defecto"
fi

# Detener contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose --profile dev down

# Eliminar volúmenes antiguos (opcional, comentar si no se desea)
# echo "🗑️  Eliminando volúmenes antiguos..."
# docker-compose down -v

# Construir imagen
echo "🔨 Construyendo imagen de Docker..."
docker-compose build --no-cache

# Iniciar servicios con perfil de desarrollo
echo "▶️  Iniciando servicios..."
docker-compose --profile dev up -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios inicien..."
sleep 10

# Verificar estado de los servicios
echo ""
echo "📊 Estado de los servicios:"
docker-compose ps

# Mostrar logs
echo ""
echo "📋 Logs iniciales:"
docker-compose logs --tail=50

echo ""
echo "✅ Despliegue completado!"
echo ""
echo "📍 Servicios disponibles:"
echo "   - Backend: http://localhost:${BACKEND_PORT:-8080}"
echo "   - PostgreSQL: localhost:${DB_PORT:-5432}"
echo "   - pgAdmin: http://localhost:${PGADMIN_PORT:-5050}"
echo ""
echo "📝 Comandos útiles:"
echo "   - Ver logs: docker-compose logs -f"
echo "   - Detener: docker-compose --profile dev down"
echo "   - Reiniciar: docker-compose restart"
echo ""
