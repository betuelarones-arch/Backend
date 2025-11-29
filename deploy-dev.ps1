# ============================================
# Script de Despliegue - Desarrollo (Windows)
# ============================================

Write-Host "🚀 Iniciando despliegue en modo DESARROLLO..." -ForegroundColor Green

# Verificar que Docker está corriendo
$dockerRunning = docker info 2>$null
if (-not $dockerRunning) {
    Write-Host "❌ Error: Docker no está corriendo" -ForegroundColor Red
    exit 1
}

# Cargar variables de entorno
if (Test-Path .env.dev) {
    Write-Host "📝 Cargando variables de entorno de desarrollo..." -ForegroundColor Cyan
    Get-Content .env.dev | ForEach-Object {
        if ($_ -match '^([^#].+?)=(.+)$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
        }
    }
} else {
    Write-Host "⚠️  Advertencia: No se encontró .env.dev" -ForegroundColor Yellow
}

# Detener contenedores existentes
Write-Host "🛑 Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose --profile dev down

# Construir imagen
Write-Host "🔨 Construyendo imagen de Docker..." -ForegroundColor Cyan
docker-compose build --no-cache

# Iniciar servicios
Write-Host "▶️  Iniciando servicios..." -ForegroundColor Green
docker-compose --profile dev up -d

# Esperar a que los servicios inicien
Write-Host "⏳ Esperando a que los servicios inicien..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar estado
Write-Host "`n📊 Estado de los servicios:" -ForegroundColor Cyan
docker-compose ps

# Mostrar logs
Write-Host "`n📋 Logs iniciales:" -ForegroundColor Cyan
docker-compose logs --tail=50

Write-Host "`n✅ Despliegue completado!" -ForegroundColor Green
Write-Host "`n📍 Servicios disponibles:" -ForegroundColor Cyan
Write-Host "   - Backend: http://localhost:$($env:BACKEND_PORT ?? '8080')"
Write-Host "   - PostgreSQL: localhost:$($env:DB_PORT ?? '5432')"
Write-Host "   - pgAdmin: http://localhost:$($env:PGADMIN_PORT ?? '5050')"
Write-Host "`n📝 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   - Ver logs: docker-compose logs -f"
Write-Host "   - Detener: docker-compose --profile dev down"
Write-Host "   - Reiniciar: docker-compose restart"
