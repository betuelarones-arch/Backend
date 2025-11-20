# 📱 Guía de Pruebas con Postman - API Educativa

Este documento proporciona ejemplos de requests listos para copiar y pegar en Postman. **No contiene rutas específicas** para que funcione en cualquier entorno.

---

## 🔧 Configuración Base en Postman

### 1. Crear Variable de Entorno

En Postman, ve a **Environments** → **Create New** y agrega:

```
BASE_URL = http://localhost:8080
```

Luego, en todos los ejemplos, reemplaza `http://localhost:8080` con `{{BASE_URL}}`

---

## 📹 Pruebas de Podcast (Audio, Script, Video)

### 1️⃣ Generar Audio desde Texto

**Método:** `POST`  
**URL:** `http://localhost:8080/api/audio/generate`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "texto": "La fotosíntesis es el proceso mediante el cual las plantas convierten la luz solar en energía química.",
  "voz": "alloy",
  "velocidad": 1.0
}
```

**Respuestas esperadas:**
- ✅ **200 OK**: Audio generado exitosamente
- ❌ **400 Bad Request**: Texto vacío o formato inválido
- ❌ **500 Internal Server Error**: Error de API de OpenAI

---

### 2️⃣ Generar Script desde Texto

**Método:** `POST`  
**URL:** `http://localhost:8080/api/script/generate`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "tema": "Derivadas en Cálculo",
  "duracion": 5,
  "nivel": "intermedio"
}
```

**Respuestas esperadas:**
- ✅ **200 OK**: Script generado con éxito
- ❌ **400 Bad Request**: Tema vacío
- ❌ **500 Internal Server Error**: Error de IA

---

### 3️⃣ Generar Script desde Prompt

**Método:** `POST`  
**URL:** `http://localhost:8080/api/script/generate-from-prompt`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "prompt": "Crea un script educativo sobre los planetas del sistema solar para estudiantes de primaria",
  "duracion": 10,
  "lenguaje": "es"
}
```

---

### 4️⃣ Generar Video desde Documento

**Método:** `POST`  
**URL:** `http://localhost:8080/api/video/generate-from-document`

**Headers:**
```
(Automático - form-data)
```

**Body (form-data):**
- **Key:** `document` | **Type:** `File` | **Value:** Selecciona un PDF, DOC, DOCX o TXT
- **Key:** `voice` | **Type:** `Text` | **Value:** `alloy` (o `echo`, `fable`, `onyx`, `nova`, `shimmer`)

**Respuestas esperadas:**
- ✅ **200 OK**: Video generado
- ❌ **400 Bad Request**: Documento no válido
- ❌ **500 Internal Server Error**: Error en procesamiento

---

### 5️⃣ Generar Video desde Prompt

**Método:** `POST`  
**URL:** `http://localhost:8080/api/video/generate-from-prompt`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "prompt": "Crea un video educativo sobre la Primera Guerra Mundial",
  "voz": "alloy",
  "velocidad": 1.0
}
```

---

## 🏫 Pruebas de Classroom (Aulas)

### 1️⃣ Crear una Clase

**Método:** `POST`  
**URL:** `http://localhost:8080/api/classroom/clase/crear`

**Headers:**
```
Content-Type: application/json
```

**Query Parameters:**
```
emailUsuario = docente@example.com
```

**Body (raw JSON):**
```json
{
  "nombre": "Matemáticas Básicas 101",
  "descripcion": "Curso introductorio de matemáticas para principiantes",
  "grado": "Primero",
  "horario": "Lunes a Viernes 09:00 - 10:00"
}
```

**Respuestas esperadas:**
- ✅ **201 Created**: Clase creada exitosamente
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Matemáticas Básicas 101",
    "codigoUnico": "MATH-ABC123",
    "qrCode": "data:image/png;base64,iVBORw0KGgo...",
    "fechaCreacion": "2025-11-16T10:30:00",
    "estado": "ACTIVA"
  }
  ```
- ❌ **400 Bad Request**: Datos incompletos

---

### 2️⃣ Obtener Mis Clases

**Método:** `GET`  
**URL:** `http://localhost:8080/api/classroom/clases/mis-clases`

**Query Parameters:**
```
emailUsuario = docente@example.com
```

**Respuestas esperadas:**
- ✅ **200 OK**: Lista de clases del usuario
  ```json
  [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "nombre": "Matemáticas Básicas 101",
      "codigoUnico": "MATH-ABC123",
      "totalEstudiantes": 25,
      "estado": "ACTIVA"
    }
  ]
  ```

---

### 3️⃣ Unirse a una Clase

**Método:** `POST`  
**URL:** `http://localhost:8080/api/classroom/clase/unirse`

**Headers:**
```
Content-Type: application/json
```

**Query Parameters:**
```
emailEstudiante = estudiante@example.com
```

**Body (raw JSON):**
```json
{
  "codigoUnico": "MATH-ABC123"
}
```

**Respuestas esperadas:**
- ✅ **200 OK**: Estudiante agregado a la clase
- ❌ **400 Bad Request**: Código inválido o estudiante ya en la clase
- ❌ **404 Not Found**: Clase no encontrada

---

### 4️⃣ Generar Código QR para Clase

**Método:** `POST`  
**URL:** `http://localhost:8080/api/classroom/qr/generar`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "claseId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Respuesta (base64 de imagen PNG):**
- ✅ **200 OK**: QR code generado

---

### 5️⃣ Obtener Participantes de una Clase

**Método:** `GET`  
**URL:** `http://localhost:8080/api/classroom/clase/participantes`

**Query Parameters:**
```
claseId = 550e8400-e29b-41d4-a716-446655440000
```

**Respuestas esperadas:**
- ✅ **200 OK**: Lista de participantes
  ```json
  [
    {
      "id": "uuid-1",
      "nombre": "Juan García",
      "email": "juan@example.com",
      "rol": "ESTUDIANTE",
      "fechaUnion": "2025-11-15T14:30:00"
    }
  ]
  ```

---

## 💬 Pruebas de Chatbot

### 1️⃣ Crear Nueva Conversación

**Método:** `POST`  
**URL:** `http://localhost:8080/api/chat/nueva-conversacion`

**Respuestas esperadas:**
- ✅ **201 Created**: Conversación creada
  ```json
  {
    "conversacionId": "conv-550e8400-e29b-41d4-a716",
    "mensaje": "Nueva conversación creada exitosamente"
  }
  ```

---

### 2️⃣ Enviar Mensaje al Chatbot

**Método:** `POST`  
**URL:** `http://localhost:8080/api/chat/mensaje`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "conversacionId": "conv-550e8400-e29b-41d4-a716",
  "mensaje": "¿Cuál es la derivada de x al cuadrado?"
}
```

**Respuestas esperadas:**
- ✅ **200 OK**: Respuesta del chatbot educativo
  ```json
  {
    "conversacionId": "conv-550e8400-e29b-41d4-a716",
    "respuesta": "La derivada de x² es 2x. Esta es una de las reglas básicas del cálculo diferencial...",
    "timestamp": "2025-11-16T11:45:00"
  }
  ```
- ❌ **400 Bad Request**: Mensaje vacío o conversación inválida

---

### 3️⃣ Obtener Historial de Conversación

**Método:** `GET`  
**URL:** `http://localhost:8080/api/chat/conversacion`

**Query Parameters:**
```
conversacionId = conv-550e8400-e29b-41d4-a716
```

**Respuestas esperadas:**
- ✅ **200 OK**: Historial de mensajes
  ```json
  {
    "conversacionId": "conv-550e8400-e29b-41d4-a716",
    "mensajes": [
      {
        "rol": "user",
        "contenido": "¿Cuál es la derivada de x²?",
        "timestamp": "2025-11-16T11:45:00"
      },
      {
        "rol": "assistant",
        "contenido": "La derivada de x² es 2x...",
        "timestamp": "2025-11-16T11:45:05"
      }
    ]
  }
  ```

---

## 👤 Pruebas de Usuarios

### 1️⃣ Registrar Usuario

**Método:** `POST`  
**URL:** `http://localhost:8080/api/users/registro`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Juan García",
  "email": "juan@example.com",
  "password": "SecurePass123!",
  "rol": "ESTUDIANTE"
}
```

**Respuestas esperadas:**
- ✅ **201 Created**: Usuario creado
- ❌ **400 Bad Request**: Email duplicado o formato inválido

---

### 2️⃣ Login de Usuario

**Método:** `POST`  
**URL:** `http://localhost:8080/api/users/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "juan@example.com",
  "password": "SecurePass123!"
}
```

**Respuestas esperadas:**
- ✅ **200 OK**: Token JWT generado
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "juan@example.com",
    "rol": "ESTUDIANTE"
  }
  ```

---

## 🧪 Flujos de Prueba Completos

### Flujo 1: Crear Clase y Agregarme como Estudiante

```
1. POST /api/chat/nueva-conversacion
   → Guarda el conversacionId

2. POST /api/classroom/clase/crear
   emailUsuario = docente@example.com
   Body: { nombre: "Matemáticas", ... }
   → Guarda el claseId y codigoUnico

3. POST /api/classroom/clase/unirse
   emailEstudiante = estudiante@example.com
   Body: { codigoUnico: "MATH-ABC123" }
   → Confirmación de unión

4. GET /api/classroom/clases/mis-clases
   emailUsuario = estudiante@example.com
   → Verifica que la clase aparece
```

### Flujo 2: Generar Contenido Educativo

```
1. POST /api/script/generate
   Body: { tema: "Fotosíntesis", ... }
   → Obtiene script generado

2. POST /api/audio/generate
   Body: { texto: "<script>", voz: "alloy" }
   → Obtiene audio

3. POST /api/video/generate-from-prompt
   Body: { prompt: "Video sobre fotosíntesis", ... }
   → Obtiene video
```

### Flujo 3: Interacción Chatbot Educativo

```
1. POST /api/chat/nueva-conversacion
   → Obtiene conversacionId

2. POST /api/chat/mensaje (x5)
   Body: { conversacionId: "...", mensaje: "Preguntas educativas" }
   → Respuestas progresivas

3. GET /api/chat/conversacion
   conversacionId = "..."
   → Historial completo de conversación
```

---

## 📊 Códigos de Respuesta

| Código | Significado |
|--------|-------------|
| **200** | ✅ Exitoso |
| **201** | ✅ Creado exitosamente |
| **400** | ❌ Solicitud inválida |
| **401** | ❌ No autorizado |
| **404** | ❌ No encontrado |
| **500** | ❌ Error del servidor |

---

## 🔐 Autenticación (si es requerida)

Si el backend requiere autenticación, agrega este header a todas las requests:

```
Authorization: Bearer {{TOKEN}}
```

Donde `{{TOKEN}}` es el JWT obtenido del endpoint `/api/users/login`

---

## 💡 Tips para Postman

1. **Guardar Requests en Colecciones**: Organiza todas las requests en carpetas
2. **Usar Variables**: Guarda `BASE_URL`, `TOKEN`, `conversacionId`, etc. como variables
3. **Tests Automáticos**: Agrega scripts de prueba en la pestaña "Tests"
4. **Mock Server**: Crea un mock server para simular respuestas

---

## 📝 Notas Importantes

- 🔑 Reemplaza todos los `email` y `id` con valores reales de tu sistema
- ⏱️ Algunos endpoints requieren claves de API (OpenAI, etc.) configuradas en el backend
- 📱 Los requests de `form-data` requieren un archivo real (PDF, DOC, etc.)
- 🌐 Todos los ejemplos asumen `http://localhost:8080` como base

---

**Última actualización:** 16 de Noviembre de 2025  
**Version:** 1.0
