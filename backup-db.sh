#!/bin/bash

# ============================================
# Script de Backup de Base de Datos
# ============================================

BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=7

# Cargar variables de entorno
if [ -f .env.prod ]; then
    export $(cat .env.prod | grep -v '^#' | xargs)
elif [ -f .env.dev ]; then
    export $(cat .env.dev | grep -v '^#' | xargs)
else
    echo "❌ No se encontró archivo de variables de entorno"
    exit 1
fi

# Crear directorio de backups
mkdir -p $BACKUP_DIR

echo "💾 Iniciando backup de base de datos..."
echo "   Base de datos: $DB_NAME"
echo "   Fecha: $DATE"

# Hacer backup de la base de datos
docker-compose exec -T db pg_dump -U $DB_USER $DB_NAME > "${BACKUP_DIR}/db_${DATE}.sql"

if [ $? -eq 0 ]; then
    echo "✅ Backup de base de datos completado"
    echo "   Archivo: ${BACKUP_DIR}/db_${DATE}.sql"
else
    echo "❌ Error al hacer backup de base de datos"
    exit 1
fi

# Backup de fotos de perfil
echo ""
echo "📸 Backup de fotos de perfil..."
docker run --rm \
    -v cliente_app_profile-photos:/data \
    -v $(pwd)/backups:/backup \
    alpine tar czf /backup/photos_${DATE}.tar.gz -C /data . 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Backup de fotos completado"
    echo "   Archivo: ${BACKUP_DIR}/photos_${DATE}.tar.gz"
else
    echo "⚠️  No se pudo hacer backup de fotos (puede ser que no existan)"
fi

# Comprimir backup de BD
echo ""
echo "🗜️  Comprimiendo backup de base de datos..."
gzip "${BACKUP_DIR}/db_${DATE}.sql"
echo "✅ Compresión completada"

# Limpiar backups antiguos
echo ""
echo "🧹 Limpiando backups antiguos (más de ${RETENTION_DAYS} días)..."
find $BACKUP_DIR -type f -mtime +${RETENTION_DAYS} -delete
echo "✅ Limpieza completada"

# Mostrar backups disponibles
echo ""
echo "📋 Backups disponibles:"
ls -lh $BACKUP_DIR

echo ""
echo "✅ Proceso de backup completado!"
echo ""
echo "📝 Para restaurar:"
echo "   gzip -d backups/db_${DATE}.sql.gz"
echo "   docker-compose exec -T db psql -U $DB_USER -d $DB_NAME < backups/db_${DATE}.sql"
