# Migración de Contraseñas y Almacenamiento de Outputs en BD

## Resumen

El backend Java ha sido actualizado para:
1. **Almacenar contraseñas en BCrypt** en la base de datos (en lugar de texto plano)
2. **Guardar PPT, audio y recursos como blobs** en columnas `BYTEA` de PostgreSQL
3. **Proporcionar endpoints REST** para subir y descargar estos archivos binarios

## 📋 Cambios en la Base de Datos

### Tabla `core.usuarios`
- Añadida columna `verified BOOLEAN` para marcar usuarios verificados
- Todas las contraseñas deben estar en formato **BCrypt** (detectadas por prefijo `$2a$`, `$2b$`, `$2y$`)

### Tabla `core.lecciones`
- Añadida columna `ppt_blob BYTEA` para almacenar archivos PowerPoint binarios

### Tabla `core.recursos`
- Añadida columna `file_blob BYTEA` para almacenar archivos de recursos

### Tabla `core.audios_ia`
- Añadida columna `audio_blob BYTEA` para almacenar archivos de audio generados

---

## 🔐 Migración de Contraseñas a BCrypt

### Requisitos Previos
- PostgreSQL disponible y corriendo
- Acceso de lectura/escritura a la tabla `core.usuarios`
- **Backup de la base de datos (OBLIGATORIO)**

### Opción A: Ejecutar la Migración Automática (Recomendado)

#### Paso 1: Hacer Backup
```powershell
$fecha = Get-Date -Format "yyyyMMdd_HHmmss"
pg_dump -h localhost -p 5432 -U postgres -F c -b -v -f "backup_usuarios_$fecha.dump" mi_basedatos
```
Cuando se pida password, ingresa la contraseña del usuario PostgreSQL.

#### Paso 2: Compilar el Módulo
```powershell
cd "C:\PI-4C\2025-2-4c24-pi-pi-1c\Cliente\Backend\cliente_app"
mvn -DskipTests clean package dependency:copy-dependencies
```

#### Paso 3: Ejecutar la Migración
```powershell
$env:CLASSPATH = "target\classes;target\dependency\*"
java `
  -Djdbc.url="jdbc:postgresql://localhost:5432/mi_basedatos" `
  -Djdbc.user="postgres" `
  -Djdbc.pass="tu_contraseña_postgres" `
  com.learning.cliente_app.migration.HashPasswordsMigration
```

**Salida esperada:**
```
Connecting to: jdbc:postgresql://localhost:5432/mi_basedatos
Updated users: 5
[1, 2, 3, 4, 5]
```

### Características de la Migración
- ✅ **Idempotente**: Si una contraseña ya está en BCrypt (prefijo `$2a$`, `$2b$`, `$2y$`), no la vuelve a hashear
- ✅ **Segura**: Usa transacción (rollback automático si hay error)
- ✅ **Log detallado**: Imprime IDs de usuarios actualizados

### Opción B: Migración Manual
Si prefieres migrar manualmente o solo algunos usuarios:

```sql
-- Ver contraseñas actuales (NO ejecutar en producción sin cuidado)
SELECT id_usuario, contrasena FROM core.usuarios WHERE contrasena NOT LIKE '$2%' LIMIT 10;

-- Actualizar a BCrypt (requiere generar hash primero en tu aplicación)
-- Por ejemplo, para usuario id=1, si la contraseña original era "test123":
UPDATE core.usuarios 
SET contrasena = '$2a$10$...(hash generado)...' 
WHERE id_usuario = 1;
```

---

## 📤 Almacenamiento de Outputs en Base de Datos

### Nuevos Endpoints REST para Subida de Archivos

#### 1. Subir PPT a Lección
```
POST /api/lecciones/{leccionId}/ppt-upload
Content-Type: multipart/form-data

file: <archivo.pptx>
```

**Respuesta (201):**
```json
{
  "message": "PPT blob guardado exitosamente",
  "entityId": 5,
  "fileSize": 1024000,
  "contentType": "application/vnd.ms-powerpoint"
}
```

#### 2. Descargar PPT de Lección
```
GET /api/lecciones/{leccionId}/ppt-download
```
Descarga el archivo binario PPT.

#### 3. Subir Audio IA
```
POST /api/audios-ia/{audioId}/audio-upload
Content-Type: multipart/form-data

file: <archivo.mp3>
```

#### 4. Descargar Audio IA
```
GET /api/audios-ia/{audioId}/audio-download
```

#### 5. Subir Archivo de Recurso
```
POST /api/recursos/{recursoId}/file-upload
Content-Type: multipart/form-data

file: <archivo.pdf>
```

#### 6. Descargar Archivo de Recurso
```
GET /api/recursos/{recursoId}/file-download
```

---

## 🔗 Integración del Orquestador Python

El servicio `Infra/services/ai-suite/app/main.py` debe ser actualizado para:

### Antes (URL almacenada):
```python
# AI Suite generaba audio/PPT y guardaba la URL en DB
url_audio = f"http://storage.example.com/audio_{id}.mp3"
db.update(audios_ia, {'id': id, 'url_audio': url_audio})
```

### Después (Blob almacenado):
```python
import requests

# 1. Generar audio/PPT con OpenAI, local TTS, etc
audio_bytes = generate_audio(script)  # devuelve bytes

# 2. Crear registro en BD (sin blob aún)
response = db_create_audio_ia(leccion_id, texto_input, modelo)
audio_id = response['id_audio']

# 3. POST del archivo binario a Java API
files = {'file': ('audio.mp3', audio_bytes, 'audio/mpeg')}
response = requests.post(
    f"http://localhost:8080/api/audios-ia/{audio_id}/audio-upload",
    files=files
)

if response.status_code == 200:
    print("Audio guardado en BD exitosamente")
else:
    print(f"Error: {response.text}")
```

---

## ✅ Verificación Post-Migración

### 1. Verificar Contraseñas Migranas
```sql
SELECT id_usuario, correo, SUBSTR(contrasena, 1, 10) as hash_prefix
FROM core.usuarios
WHERE contrasena LIKE '$2%';
```
Debería mostrar todos los usuarios con prefijo BCrypt.

### 2. Probar Login con Usuario Migrante
```bash
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"correo": "usuario@example.com", "contrasena": "su_password_original"}'
```
Debe retornar **200 OK** con token.

### 3. Probar Subida de PPT
```bash
curl -X POST http://localhost:8080/api/lecciones/1/ppt-upload \
  -F "file=@mi_presentacion.pptx"
```
Debe retornar **200 OK** con confirmación.

### 4. Probar Descarga de PPT
```bash
curl -X GET http://localhost:8080/api/lecciones/1/ppt-download \
  -o descargado.pptx
```
Debe descargar el archivo binario correctamente.

---

## 🐛 Troubleshooting

| Problema | Causa | Solución |
|----------|-------|----------|
| `java.sql.SQLException: Schema "CORE" not found` | Entidades mapeadas a esquema que no existe | Confirmar que `init.sql` se ejecutó en BD; crear schema manualmente si es necesario |
| `IllegalArgumentException: Could not resolve placeholder 'openai.api.key'` | Propiedad no definida en `application.properties` | Añadir `openai.api.key=<tu_clave>` en `src/main/resources/application.properties` |
| Tests fallan por H2 schema | H2 en-memoria no crea esquema CORE automáticamente | Usar `INIT=CREATE SCHEMA IF NOT EXISTS CORE` en URL de H2 (ya configurado en test properties) |
| `NoClassDefFoundError: org.springframework.mock.web` | Dependencia de test en main code | Ya solucionado (reemplazado con ByteArrayMultipartFile interno) |

---

## 📝 Archivo de Utilidad de Migración

**Ruta:** `Cliente/Backend/cliente_app/src/main/java/com/learning/cliente_app/migration/HashPasswordsMigration.java`

- Implementación: JDBC + BCryptPasswordEncoder (Spring Security)
- Detecta contraseñas ya hasheadas (prefijo `$2`)
- Transacción atómica: todas las filas o ninguna
- **Parámetros del sistema:**
  - `jdbc.url` (default: `jdbc:postgresql://localhost:5432/mi_basedatos`)
  - `jdbc.user` (default: `postgres`)
  - `jdbc.pass` (default: `postgres`)

---

## 📦 Próximos Pasos

1. ✅ **Ejecutar migración de contraseñas** (hacer backup antes)
2. ✅ **Desplegar cambios** (`mvn -DskipTests package`)
3. ✅ **Validar endpoints** de subida/descarga
4. ✅ **Actualizar ai-suite** para POST de binarios
5. ✅ **Ejecutar tests** en tu entorno local (los tests aquí tienen issue de schema H2)

---

**Última actualización:** 2025-11-16
**Versiones:** Java 21, Spring Boot 3.3.3, PostgreSQL 12+
