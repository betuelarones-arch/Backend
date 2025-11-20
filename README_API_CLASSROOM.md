# API Classroom - Sistema de Clases

Este documento describe los endpoints para el sistema de clases donde cualquier usuario puede crear clases y otros usuarios pueden unirse usando un código único o QR.

**Base URL:** `http://localhost:8080/api/classroom`

---

## 🔐 Autenticación

Todos los usuarios usan el sistema de autenticación normal (`/api/usuarios/login`). No hay distinción entre profesores y estudiantes - **cualquier usuario puede crear clases y unirse a ellas**.

---

## Endpoints

### 1. Crear una Clase

Cualquier usuario logueado puede crear una clase. El sistema genera automáticamente:
- Un código único de 6 caracteres
- Un link para unirse
- Un código QR

**URL:** `POST /api/classroom/clase/crear`  
**Parámetros:**
- `emailUsuario` (query param): Email del usuario que crea la clase

**Request Body (JSON):**
```json
{
  "nombre": "Matemáticas Avanzadas",
  "descripcion": "Clase sobre álgebra y cálculo",
  "fechaInicio": "2025-11-06T10:00:00",
  "fechaFin": "2025-11-06T12:00:00"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nombre": "Matemáticas Avanzadas",
  "descripcion": "Clase sobre álgebra y cálculo",
  "codigoUnico": "ABC123",
  "urlUnirse": "http://localhost:8080/api/classroom/unirse/ABC123",
  "qrCode": [bytes en base64],
  "fechaCreacion": "2025-11-05T20:00:00",
  "fechaInicio": "2025-11-06T10:00:00",
  "fechaFin": "2025-11-06T12:00:00",
  "activa": true,
  "profesorId": 1,
  "profesorNombre": "Juan Pérez",
  "cantidadEstudiantes": 0
}
```

---

### 2. Obtener Mis Clases

Obtiene todas las clases creadas por un usuario.

**URL:** `GET /api/classroom/clases/mis-clases?emailUsuario=usuario@example.com`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Matemáticas Avanzadas",
    "codigoUnico": "ABC123",
    "urlUnirse": "http://localhost:8080/api/classroom/unirse/ABC123",
    "cantidadEstudiantes": 5
  }
]
```

---

### 3. Obtener Información de una Clase

Obtiene información de una clase usando su código único.

**URL:** `GET /api/classroom/clase/{codigoUnico}`

**Ejemplo:**
```
GET /api/classroom/clase/ABC123
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Matemáticas Avanzadas",
  "descripcion": "Clase sobre álgebra y cálculo",
  "codigoUnico": "ABC123",
  "urlUnirse": "http://localhost:8080/api/classroom/unirse/ABC123",
  "profesorNombre": "Juan Pérez",
  "cantidadEstudiantes": 5
}
```

---

### 4. Obtener QR Code de una Clase

Obtiene el código QR como imagen PNG para una clase.

**URL:** `GET /api/classroom/clase/{codigoUnico}/qr`

**Ejemplo:**
```
GET /api/classroom/clase/ABC123/qr
```

**Response (200 OK):**
- Content-Type: `image/png`
- Body: Imagen PNG del código QR

---

### 5. Unirse a una Clase (POST)

Un usuario se une a una clase usando el código único.

**URL:** `POST /api/classroom/unirse`

**Request Body (JSON):**
```json
{
  "codigoUnico": "ABC123",
  "emailEstudiante": "alumno@example.com"
}
```

**Response (200 OK):**
```json
{
  "mensaje": "Te has unido exitosamente a la clase",
  "claseId": 1,
  "claseNombre": "Matemáticas Avanzadas",
  "fechaUnion": "2025-11-05T21:00:00"
}
```

---

### 6. Unirse a una Clase (GET - Para QR Code)

Permite unirse usando solo el código desde un link o QR code. Si no se proporciona email, muestra un formulario HTML.

**URL:** `GET /api/classroom/unirse/{codigoUnico}?email=usuario@example.com`

**Ejemplo:**
```
GET /api/classroom/unirse/ABC123?email=alumno@example.com
```

**Sin email (muestra formulario):**
```
GET /api/classroom/unirse/ABC123
```

**Response (200 OK - HTML):**
- Si tiene email: Página de éxito
- Si no tiene email: Formulario para ingresar email

---

## Flujo de Uso

### Para el Creador de la Clase:

1. **Usuario se loguea** → `/api/usuarios/login`
2. **Crea una clase** → `POST /api/classroom/clase/crear?emailUsuario=profesor@example.com`
3. **Recibe código único y QR** en la respuesta
4. **Comparte el código o QR** con los alumnos

### Para los Alumnos:

1. **Usuario se registra/loguea** → `/api/usuarios/register` o `/api/usuarios/login`
2. **Escanea QR o ingresa código** → `GET /api/classroom/unirse/{codigoUnico}`
3. **Ingresa su email** (si no está logueado)
4. **Se une automáticamente** a la clase

---

## Ejemplos de Uso

### Ejemplo 1: Crear Clase (JavaScript)

```javascript
const crearClase = async (emailUsuario, nombreClase) => {
  const response = await fetch(`http://localhost:8080/api/classroom/clase/crear?emailUsuario=${emailUsuario}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      nombre: nombreClase,
      descripcion: "Descripción de la clase",
      fechaInicio: new Date().toISOString()
    })
  });

  const clase = await response.json();
  console.log('Código único:', clase.codigoUnico);
  console.log('URL para unirse:', clase.urlUnirse);
  
  // Mostrar QR code
  if (clase.qrCode) {
    const qrImage = document.createElement('img');
    qrImage.src = `data:image/png;base64,${btoa(String.fromCharCode(...clase.qrCode))}`;
    document.body.appendChild(qrImage);
  }
};
```

### Ejemplo 2: Unirse a Clase (cURL)

```bash
curl -X POST http://localhost:8080/api/classroom/unirse \
  -H "Content-Type: application/json" \
  -d '{
    "codigoUnico": "ABC123",
    "emailEstudiante": "alumno@example.com"
  }'
```

### Ejemplo 3: Obtener QR Code

```bash
curl http://localhost:8080/api/classroom/clase/ABC123/qr --output qr-code.png
```

---

## Características

✅ **Código único de 6 caracteres** - Fácil de compartir  
✅ **QR Code automático** - Escaneable directamente  
✅ **Link directo** - Compartible por cualquier medio  
✅ **Sin roles** - Cualquier usuario puede crear y unirse  
✅ **Validaciones** - Previene unirse dos veces a la misma clase  
✅ **Página HTML** - Formulario automático si se accede sin email  

---

## Notas Importantes

- El código único es **case-sensitive** (distingue mayúsculas/minúsculas)
- Los usuarios deben estar **registrados** antes de unirse a una clase
- No se puede unir dos veces a la misma clase
- El QR code apunta a: `http://localhost:8080/api/classroom/unirse/{codigoUnico}`
- La URL base se configura en `application.properties` con `app.base-url`

---

## Estructura de Base de Datos

- **clases**: Almacena las clases creadas
- **participaciones**: Relación many-to-many entre usuarios y clases
- **users**: Usuarios del sistema (ya existente)

---

¿Necesitas ayuda con algo más? 🚀

