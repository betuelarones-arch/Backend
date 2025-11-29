#!/bin/bash

# ============================================
# Script de Despliegue - Producción
# ============================================

echo "🚀 Iniciando despliegue en modo PRODUCCIÓN..."

# Verificar que Docker esté corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    exit 1
fi

# Verificar que existe archivo .env.prod
if [ ! -f .env.prod ]; then
    echo "❌ Error: No se encontró .env.prod"
    echo "   Copia .env.prod.template a .env.prod y configura las variables"
    exit 1
fi

# Cargar variables de entorno
echo "📝 Cargando variables de entorno de producción..."
export $(cat .env.prod | grep -v '^#' | xargs)

# Verificar variables críticas
REQUIRED_VARS=("GEMINI_API_KEY" "DB_PASSWORD")
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ] || [ "${!var}" == "CAMBIAR"* ]; then
        echo "❌ Error: Variable $var no está configurada correctamente"
        exit 1
    fi
done

# Confirmación de usuario
echo ""
echo "⚠️  ADVERTENCIA: Vas a desplegar en PRODUCCIÓN"
read -p "¿Estás seguro? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "❌ Despliegue cancelado"
    exit 0
fi

# Hacer backup de la base de datos (si existe)
echo "💾 Creando backup de base de datos..."
mkdir -p backups
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
docker-compose exec -T db pg_dump -U ${DB_USER} ${DB_NAME} > "backups/backup_${TIMESTAMP}.sql" 2>/dev/null || echo "⚠️  No se pudo crear backup (posiblemente primera ejecución)"

# Detener contenedores existentes (sin eliminar volúmenes)
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir imagen de producción
echo "🔨 Construyendo imagen de producción..."
docker-compose build --no-cache

# Iniciar servicios
echo "▶️  Iniciando servicios de producción..."
docker-compose up -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios inicien..."
sleep 20

# Verificar health checks
echo "🏥 Verificando health de los servicios..."
for i in {1..30}; do
    if docker-compose ps | grep -q "healthy"; then
        echo "✅ Servicios saludables"
        break
    fi
    echo "   Esperando... ($i/30)"
    sleep 2
done

# Verificar estado de los servicios
echo ""
echo "📊 Estado de los servicios:"
docker-compose ps

# Mostrar logs recientes
echo ""
echo "📋 Logs iniciales:"
docker-compose logs --tail=30

# Verificar conectividad
echo ""
echo "🧪 Verificando conectividad..."
if curl -f http://localhost:${BACKEND_PORT:-8080}/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend respondiendo correctamente"
else
    echo "❌ Backend no responde - revisar logs"
fi

echo ""
echo "✅ Despliegue en producción completado!"
echo ""
echo "📍 URL del servicio: ${APP_BASE_URL}"
echo ""
echo "📝 Comandos útiles:"
echo "   - Ver logs: docker-compose logs -f backend"
echo "   - Ver estado: docker-compose ps"
echo "   - Backup DB: ./scripts/backup-db.sh"
echo "   - Detener: docker-compose down"
echo ""
echo "🔒 RECORDATORIO: Verifica la seguridad de tu configuración"
echo ""
