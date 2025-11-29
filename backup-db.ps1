# ============================================
# Script de Backup de Base de Datos (Windows)
# ============================================

$BACKUP_DIR = ".\backups"
$DATE = Get-Date -Format "yyyyMMdd_HHmmss"
$RETENTION_DAYS = 7

# Cargar variables de entorno
if (Test-Path .env.prod) {
    Get-Content .env.prod | ForEach-Object {
        if ($_ -match '^([^#].+?)=(.+)$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
        }
    }
} elseif (Test-Path .env.dev) {
    Get-Content .env.dev | ForEach-Object {
        if ($_ -match '^([^#].+?)=(.+)$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
        }
    }
} else {
    Write-Host "❌ No se encontró archivo de variables de entorno" -ForegroundColor Red
    exit 1
}

# Crear directorio de backups
New-Item -ItemType Directory -Force -Path $BACKUP_DIR | Out-Null

Write-Host "💾 Iniciando backup de base de datos..." -ForegroundColor Green
Write-Host "   Base de datos: $env:DB_NAME"
Write-Host "   Fecha: $DATE"

# Hacer backup de la base de datos
docker-compose exec -T db pg_dump -U $env:DB_USER $env:DB_NAME > "$BACKUP_DIR\db_$DATE.sql"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backup de base de datos completado" -ForegroundColor Green
    Write-Host "   Archivo: $BACKUP_DIR\db_$DATE.sql"
} else {
    Write-Host "❌ Error al hacer backup de base de datos" -ForegroundColor Red
    exit 1
}

# Backup de fotos de perfil
Write-Host "`n📸 Backup de fotos de perfil..." -ForegroundColor Cyan
docker run --rm `
    -v cliente_app_profile-photos:/data `
    -v ${PWD}/backups:/backup `
    alpine tar czf /backup/photos_$DATE.tar.gz -C /data . 2>$null

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backup de fotos completado" -ForegroundColor Green
    Write-Host "   Archivo: $BACKUP_DIR\photos_$DATE.tar.gz"
} else {
    Write-Host "⚠️  No se pudo hacer backup de fotos (puede ser que no existan)" -ForegroundColor Yellow
}

# Comprimir backup de BD (si está disponible 7-Zip o similar)
if (Get-Command 7z -ErrorAction SilentlyContinue) {
    Write-Host "`n🗜️  Comprimiendo backup de base de datos..." -ForegroundColor Cyan
    7z a "$BACKUP_DIR\db_$DATE.sql.zip" "$BACKUP_DIR\db_$DATE.sql" | Out-Null
    Remove-Item "$BACKUP_DIR\db_$DATE.sql"
    Write-Host "✅ Compresión completada" -ForegroundColor Green
}

# Limpiar backups antiguos
Write-Host "`n🧹 Limpiando backups antiguos (más de $RETENTION_DAYS días)..." -ForegroundColor Cyan
$cutoffDate = (Get-Date).AddDays(-$RETENTION_DAYS)
Get-ChildItem -Path $BACKUP_DIR -File | Where-Object { $_.LastWriteTime -lt $cutoffDate } | Remove-Item
Write-Host "✅ Limpieza completada" -ForegroundColor Green

# Mostrar backups disponibles
Write-Host "`n📋 Backups disponibles:" -ForegroundColor Cyan
Get-ChildItem -Path $BACKUP_DIR | Format-Table Name, Length, LastWriteTime

Write-Host "`n✅ Proceso de backup completado!" -ForegroundColor Green
Write-Host "`n📝 Para restaurar:" -ForegroundColor Cyan
Write-Host "   docker-compose exec -T db psql -U $env:DB_USER -d $env:DB_NAME < backups\db_$DATE.sql"
