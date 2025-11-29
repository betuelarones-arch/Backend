# ============================================
# Script de Despliegue - Producción (Windows)
# ============================================

Write-Host "🚀 Iniciando despliegue en modo PRODUCCIÓN..." -ForegroundColor Green

# Verificar Docker
$dockerRunning = docker info 2>$null
if (-not $dockerRunning) {
    Write-Host "❌ Error: Docker no está corriendo" -ForegroundColor Red
    exit 1
}

# Verificar archivo .env.prod
if (-not (Test-Path .env.prod)) {
    Write-Host "❌ Error: No se encontró .env.prod" -ForegroundColor Red
    Write-Host "   Copia .env.prod.template a .env.prod y configura las variables"
    exit 1
}

# Cargar variables de entorno
Write-Host "📝 Cargando variables de entorno de producción..." -ForegroundColor Cyan
Get-Content .env.prod | ForEach-Object {
    if ($_ -match '^([^#].+?)=(.+)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
    }
}

# Verificar variables críticas
$requiredVars = @("GEMINI_API_KEY", "DB_PASSWORD")
foreach ($var in $requiredVars) {
    $value = [Environment]::GetEnvironmentVariable($var)
    if ([string]::IsNullOrEmpty($value) -or $value.StartsWith("CAMBIAR")) {
        Write-Host "❌ Error: Variable $var no está configurada correctamente" -ForegroundColor Red
        exit 1
    }
}

# Confirmación
Write-Host "`n⚠️  ADVERTENCIA: Vas a desplegar en PRODUCCIÓN" -ForegroundColor Yellow
$confirm = Read-Host "¿Estás seguro? (yes/no)"
if ($confirm -ne "yes") {
    Write-Host "❌ Despliegue cancelado" -ForegroundColor Red
    exit 0
}

# Crear backup
Write-Host "💾 Creando backup de base de datos..." -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path backups | Out-Null
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
docker-compose exec -T db pg_dump -U $env:DB_USER $env:DB_NAME > "backups/backup_$timestamp.sql" 2>$null

# Detener contenedores
Write-Host "🛑 Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose down

# Construir imagen
Write-Host "🔨 Construyendo imagen de producción..." -ForegroundColor Cyan
docker-compose build --no-cache

# Iniciar servicios
Write-Host "▶️  Iniciando servicios de producción..." -ForegroundColor Green
docker-compose up -d

# Esperar
Write-Host "⏳ Esperando a que los servicios inicien..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Verificar estado
Write-Host "`n📊 Estado de los servicios:" -ForegroundColor Cyan
docker-compose ps

# Logs
Write-Host "`n📋 Logs iniciales:" -ForegroundColor Cyan
docker-compose logs --tail=30

# Verificar conectividad
Write-Host "`n🧪 Verificando conectividad..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:$($env:BACKEND_PORT ?? '8080')/actuator/health" -TimeoutSec 5
    Write-Host "✅ Backend respondiendo correctamente" -ForegroundColor Green
} catch {
    Write-Host "❌ Backend no responde - revisar logs" -ForegroundColor Red
}

Write-Host "`n✅ Despliegue en producción completado!" -ForegroundColor Green
Write-Host "`n📍 URL del servicio: $env:APP_BASE_URL" -ForegroundColor Cyan
Write-Host "`n📝 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   - Ver logs: docker-compose logs -f backend"
Write-Host "   - Ver estado: docker-compose ps"
Write-Host "   - Detener: docker-compose down"
Write-Host "`n🔒 RECORDATORIO: Verifica la seguridad de tu configuración" -ForegroundColor Yellow
