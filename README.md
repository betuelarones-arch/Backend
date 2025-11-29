# 🎓 Cliente App - Learning Platform Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

Backend REST API para plataforma de aprendizaje con IA, desarrollada con Spring Boot. Incluye gestión de usuarios, generación de contenido educativo con IA, chatbot, y más.

---

## 📋 Tabla de Contenidos
- [Características Principales](#-características-principales)
- [Tecnologías](#-tecnologías)
- [Inicio Rápido](#-inicio-rápido)
- [API Endpoints](#-api-endpoints)
- [Despliegue](#-despliegue)
- [Documentación](#-documentación)
- [Contribuir](#-contribuir)

---

## ✨ Características Principales

### 👥 Gestión de Usuarios
- ✅ Registro y autenticación de usuarios
- ✅ Login con Firebase
- ✅ Recuperación de contraseña
- ✅ **NUEVO:** Actualización de perfil
- ✅ **NUEVO:** Subida de foto de perfil
- ✅ **NUEVO:** Cambio de contraseña
- ✅ **NUEVO:** Gestión de sesiones activas
- ✅ **NUEVO:** Eliminación de cuenta

### 🤖 Inteligencia Artificial
- 🧠 Generación de preguntas a partir de archivos
- 💬 Chatbot educativo con Gemini AI
- 📝 Resumen automático de textos y archivos
- 🎙️ Generación de audio con IA

### 🏫 Gestión Educativa
- 📚 Creación y gestión de clases
- 👨‍🎓 Sistema de unión a clases con QR
- 📊 Recursos educativos
- 📄 Gestión de lecciones con PPT

### 🔐 Seguridad
- 🛡️ Autenticación con Firebase
- 🔑 Hashing de contraseñas con BCrypt
- 🔒 Gestión de sesiones con tokens
- 🚪 Logout real con invalidación de tokens

---

## 🛠️ Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - ORM y acceso a datos
- **PostgreSQL** - Base de datos relacional
- **Maven** - Gestión de dependencias

### Inteligencia Artificial
- **Gemini AI** - Generación de contenido y chatbot
- **Firebase** - Autenticación

### DevOps
- **Docker** - Containerización
- **Docker Compose** - Orquestación de servicios

---

## 🚀 Inicio Rápido

### Prerequisitos
- Java 21+
- Docker y Docker Compose (para desarrollo local)
- Git

### Desarrollo Local

#### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd cliente_app
```

#### 2. Configurar variables de entorno
```bash
# Copiar archivo de ejemplo
cp .env.dev .env

# Editar y configurar GEMINI_API_KEY
nano .env
```

#### 3. Desplegar con Docker

**Desarrollo (con pgAdmin):**
```bash
# Linux/Mac
chmod +x deploy-dev.sh
./deploy-dev.sh

# Windows
.\deploy-dev.ps1
```

**Manual:**
```bash
docker-compose --profile dev up -d --build
```

#### 4. Verificar
```bash
# Ver estado
docker-compose ps

# Health check
curl http://localhost:8080/actuator/health
```

La aplicación estará disponible en:
- **Backend:** http://localhost:8080
- **pgAdmin:** http://localhost:5050 (dev: admin@localhost.com / admin123)
- **PostgreSQL:** localhost:5432

### 🌐 Despliegue en Render.com (Producción)

**¡Despliegue en la nube con 1 click!**

#### Opción 1: Con Blueprint (Automático) ⭐ Recomendado

1. **Push el código a GitHub:**
   ```bash
   git add .
   git commit -m "Ready for Render"
   git push origin main
   ```

2. **En Render:**
   - Ir a [render.com](https://render.com)
   - **Blueprints** > **New Blueprint Instance**
   - Conectar repositorio
   - Render detectará `render.yaml` y creará:
     - ✅ Web Service (Spring Boot)
     - ✅ PostgreSQL Database

3. **Configurar variable de entorno:**
   - En Dashboard > Web Service > Environment
   - Agregar: `GEMINI_API_KEY=tu_api_key`

4. **¡Listo!** Tu app está en: `https://tu-app.onrender.com`

#### Opción 2: Manual

Ver [Guía Completa de Render](RENDER_DEPLOYMENT.md) para instrucciones detalladas.

**Tiempo estimado:** 5-10 minutos

⚠️ **Nota:** Free tier tiene limitaciones (ver documentación)

---

## 📚 API Endpoints

### 👤 Usuarios (`/api/usuarios`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/register` | Registrar nuevo usuario |
| POST | `/login` | Iniciar sesión |
| POST | `/recover` | Recuperar contraseña |
| GET | `/verify` | Verificar usuario vía token |
| POST | `/firebase-login` | Login con Firebase |
| POST | `/support` | Reportar error o soporte |
| **PUT** | **`/perfil`** | **⭐ Actualizar perfil** |
| **POST** | **`/foto`** | **⭐ Subir foto de perfil** |
| **DELETE** | **`/cuenta`** | **⭐ Eliminar cuenta** |

### 🔐 Autenticación (`/api/auth`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **POST** | **`/cambiar-password`** | **⭐ Cambiar contraseña** |
| **GET** | **`/sesiones`** | **⭐ Listar sesiones activas** |
| **POST** | **`/logout`** | **⭐ Cerrar sesión (logout real)** |
| **DELETE** | **`/sesiones/{id}`** | **⭐ Cerrar sesión remota** |

### 📂 Recursos (`/api/recursos`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/file-upload` | Subir archivo de recurso |
| GET | `/{id}/file-download` | Descargar archivo |

### ❓ Preguntas (`/api/preguntas`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/subir` | Generar preguntas desde archivo |
| GET | `/limites` | Límites de generación |
| GET | `/` | Obtener todas las preguntas |
| GET | `/archivo/{nombre}` | Preguntas por archivo |
| DELETE | `/archivo/{nombre}` | Eliminar preguntas |
| PUT | `/{id}` | Actualizar pregunta |
| DELETE | `/{id}` | Eliminar pregunta |

### 🤖 Chatbot (`/api/chat`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/nueva-conversacion` | Iniciar conversación |
| POST | `/mensaje` | Enviar mensaje |
| GET | `/historial/{id}` | Historial de chat |
| DELETE | `/conversacion/{id}` | Eliminar conversación |
| GET | `/salud` | Health check |

### 📝 Resumen (`/api/resumen`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/text` | Resumir texto |
| POST | `/file` | Resumir archivo |

### 🔊 Audio IA (`/api/audios-ia`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/audio-upload` | Subir audio generado |
| GET | `/{id}/audio-download` | Descargar audio |

### 📄 Lecciones (`/api/lecciones`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/{id}/ppt-upload` | Subir PPT |
| GET | `/{id}/ppt-download` | Descargar PPT |

### 🏫 Classroom (`/api/classroom`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/clase/crear` | Crear clase |
| GET | `/clases/mis-clases` | Mis clases |
| GET | `/clase/{codigo}/qr` | QR de clase |
| GET | `/clase/{codigo}` | Info de clase |
| POST | `/unirse` | Unirse a clase |
| GET | `/unirse/{codigo}` | Unirse vía link/QR |

⭐ **Nuevo en v1.1.0**

---

## 🐳 Despliegue

### Desarrollo
```bash
# Script automatizado
./deploy-dev.sh  # Linux/Mac
.\deploy-dev.ps1 # Windows

# O manual
docker-compose --profile dev up -d
```

### Producción
```bash
# 1. Configurar .env.prod
cp .env.prod.template .env.prod
nano .env.prod

# 2. Desplegar
./deploy-prod.sh  # Linux/Mac
.\deploy-prod.ps1 # Windows
```

Ver [Guía de Despliegue Completa](DEPLOYMENT_GUIDE.md) para más detalles.

---

## 📖 Documentación

### Guías Disponibles
- 🌐 [**Despliegue en Render**](RENDER_DEPLOYMENT.md) - ⭐ Guía paso a paso para Render.com
- 📘 [**Guía de Despliegue General**](DEPLOYMENT_GUIDE.md) - Docker, ambientes, scripts
- 📗 [**Documentación de API**](ENDPOINTS_DOCUMENTATION.md) - Endpoints detallados
- 📙 [**Walkthrough**](walkthrough.md) - Implementación de nuevas features

### Despliegue

**Render.com (Recomendado para producción):**
- ✅ Gratis para empezar
- ✅ Deploy automático desde Git
- ✅ PostgreSQL incluido
- ✅ SSL automático
- Ver [RENDER_DEPLOYMENT.md](RENDER_DEPLOYMENT.md)

**Docker Local (Desarrollo):**
- Scripts automatizados: `deploy-dev.sh` / `deploy-dev.ps1`
- Ver [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

### Perfiles de Spring

El proyecto soporta múltiples perfiles para diferentes ambientes:

- **`dev`** - Desarrollo (logging verbose, CORS permisivo)
- **`prod`** - Producción (logging mínimo, seguridad reforzada)

Cambiar perfil:
```bash
# Via environment variable
export SPRING_PROFILES_ACTIVE=prod

# Via application.properties
spring.profiles.active=prod

# Via Docker
SPRING_PROFILES=prod docker-compose up
```

### Variables de Entorno

Archivo | Propósito
--------|----------
`.env.dev` | Desarrollo (incluido en repo)
`.env.prod.template` | Template para producción
`.env.prod` | Producción (**NO commitear**)

Variables críticas:
- `GEMINI_API_KEY` - API key de Google Gemini
- `DB_PASSWORD` - Contraseña de PostgreSQL
- `APP_BASE_URL` - URL base de la aplicación

---

## 🗄️ Base de Datos

### Schema Principal

```sql
-- Usuarios
usuarios (id_usuario, nombre, apellido, correo, contrasena, rol, verified, fecha_creacion, foto_perfil_url)

-- Sesiones (nueva)
sesiones (id_sesion, id_usuario, token_hash, dispositivo, ip_address, fecha_creacion, ultima_actividad, activa)

-- Clases
clases (...)

-- Recursos
recursos (...)
```

### Migraciones

Ejecutar migraciones manualmente:
```bash
docker-compose exec -T db psql -U $DB_USER -d $DB_NAME < migration_sesiones.sql
```

Para producción, usar herramientas como:
- Flyway
- Liquibase

---

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=UserServiceTest
```

### Colección de Postman

Importar colecciones disponibles:
- `Postman_Collection.json` - Endpoints básicos
- `API_VIDEO_POSTMAN_COLLECTION.json` - Endpoints de video

---

## 🔧 Desarrollo

### Estructura del Proyecto
```
cliente_app/
├── src/
│   ├── main/
│   │   ├── java/com/learning/cliente_app/
│   │   │   ├── user/        # Gestión de usuarios
│   │   │   ├── chatbot/     # Chatbot con IA
│   │   │   ├── classroom/   # Sistema de clases
│   │   │   ├── lecciones/   # Lecciones
│   │   │   ├── recursos/    # Recursos
│   │   │   └── config/      # Configuración
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
├── Dockerfile
├── docker-compose.yml
├── deploy-dev.sh
├── deploy-prod.sh
└── README.md
```

### Agregar Nuevos Endpoints

1. Crear DTOs en `dto/`
2. Crear entidad en `model/`
3. Crear repositorio en `repository/`
4. Crear servicio en `service/`
5. Crear controlador en `controller/`
6. Actualizar documentación

---

## 🤝 Contribuir

### Workflow
1. Fork el repositorio
2. Crear rama feature (`git checkout -b feature/nueva-feature`)
3. Commit cambios (`git commit -m 'Add nueva feature'`)
4. Push a la rama (`git push origin feature/nueva-feature`)
5. Crear Pull Request

### Estándares de Código
- Seguir convenciones de Java
- Usar nombres descriptivos
- Documentar métodos públicos
- Escribir tests para nuevas features
- Actualizar documentación

---

## 📝 Changelog

### v1.1.0 (2025-11-28)
**Nuevas Features:**
- ✨ Actualización de perfil de usuario
- ✨ Subida de foto de perfil
- ✨ Cambio de contraseña con validación
- ✨ Gestión de sesiones activas
- ✨ Logout real con invalidación de tokens
- ✨ Eliminación de cuenta
- 🐳 Configuración completa de Docker
- 📚 Documentación exhaustiva de despliegue

**Mejoras:**
- 🔒 Seguridad mejorada con hashing de tokens
- 🗄️ Nueva tabla de sesiones
- 📦 Perfiles de Spring (dev/prod)
- 🚀 Scripts automatizados de despliegue

### v1.0.0
**Features Iniciales:**
- Sistema de usuarios y autenticación
- Integración con Gemini AI
- Chatbot educativo
- Generación de preguntas
- Sistema de clases
- Gestión de recursos

---

## 📄 Licencia

[Especificar licencia]

---

## 👥 Equipo

[Información del equipo]

---

## 🆘 Soporte

Si encuentras problemas:
1. Revisa la [Guía de Despliegue](DEPLOYMENT_GUIDE.md)
2. Consulta [Issues](link-a-issues) existentes
3. Crea un nuevo Issue con detalles

---

## 🔗 Enlaces Útiles

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Docker Docs](https://docs.docker.com/)
- [Gemini AI](https://ai.google.dev/)

---

**⭐ Si te resulta útil este proyecto, considera darle una estrella!**
