# 🔗 Mapeando Tests a Requests Postman

Esta guía muestra la equivalencia entre cada test de integración y cómo probarlo manualmente con Postman.

---

## 🎵 PODCAST - AudioService

### Test 1: Validar Texto Nulo
**Test:** `testValidarTextoNulo()`  
**En Postman:**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": null,
  "voz": "alloy"
}
```
**Resultado esperado:** ❌ 400 Bad Request  
**Mensaje:** `"El texto es requerido"`

---

### Test 2: Validar Texto Vacío
**Test:** `testValidarTextoVacio()`  
**En Postman:**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": "",
  "voz": "alloy"
}
```
**Resultado esperado:** ❌ 400 Bad Request  
**Mensaje:** `"El texto no puede estar vacío"`

---

### Test 3: Generar Audio Válido
**Test:** `testGenerarAudioValido()`  
**En Postman:**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": "La fotosíntesis es el proceso mediante el cual las plantas convierten la luz solar en energía química.",
  "voz": "alloy",
  "velocidad": 1.0
}
```
**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "audioUrl": "https://...",
  "duracion": 8.5,
  "voz": "alloy",
  "formato": "mp3"
}
```

---

### Test 4: Texto Muy Largo
**Test:** `testTextoMuyLargo()`  
**En Postman:**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": "Lorem ipsum dolor sit amet... (>1000 caracteres)",
  "voz": "alloy"
}
```
**Resultado esperado:** ✅ 200 OK  
**Nota:** Sistema automáticamente divide en segmentos

---

### Test 5: Caracteres Unicode
**Test:** `testCaracteresUnicode()`  
**En Postman:**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": "¿Qué es la ecuación de Schrödinger? Explica: α, β, γ, ñ, é, ü",
  "voz": "nova"
}
```
**Resultado esperado:** ✅ 200 OK  
**Verificar:** Audio genera correctamente con acentos

---

### Test 6: Pruebas de Voces
**Test:** `testVocesDisponibles()`  
**En Postman (ejecutar x5):**
```
POST http://localhost:8080/api/audio/generate
Content-Type: application/json

{
  "texto": "Hola, ¿cómo estás?",
  "voz": "{{VOICE}}"
}
```

Donde `{{VOICE}}` puede ser:
- `alloy`
- `echo`
- `fable`
- `onyx`
- `shimmer`

**Resultado esperado:** ✅ 200 OK (en todas)

---

## 📝 PODCAST - ScriptService

### Test 1: Generar Script desde Tema
**Test:** `testGenerarScriptDesdeTexto()`  
**En Postman:**
```
POST http://localhost:8080/api/script/generate
Content-Type: application/json

{
  "tema": "La Revolución Francesa",
  "duracion": 5,
  "nivel": "intermedio"
}
```
**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "script": "En el año 1789...",
  "tema": "La Revolución Francesa",
  "duracion": 5,
  "palabras": 850
}
```

---

### Test 2: Generar Script desde Prompt
**Test:** `testGenerarScriptDesdePrompt()`  
**En Postman:**
```
POST http://localhost:8080/api/script/generate-from-prompt
Content-Type: application/json

{
  "prompt": "Crea un script educativo sobre cómo funciona el ciclo del agua para estudiantes de primaria",
  "duracion": 5,
  "lenguaje": "es"
}
```
**Resultado esperado:** ✅ 200 OK

---

### Test 3: Tema Nulo
**Test:** `testValidarTextoNulo()`  
**En Postman:**
```
POST http://localhost:8080/api/script/generate
Content-Type: application/json

{
  "tema": null,
  "duracion": 5
}
```
**Resultado esperado:** ❌ 400 Bad Request

---

## 🎥 PODCAST - VideoService

### Test 1: Generar Video desde Documento
**Test:** `testDocumentoTXT()`  
**En Postman:**
1. Click en "Body"
2. Selecciona "form-data"
3. Key: `document` | Type: `File` | Selecciona archivo .txt
4. Key: `voice` | Type: `Text` | Value: `alloy`

```
POST http://localhost:8080/api/video/generate-from-document
```

**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "id": "video-xyz",
  "videoUrl": "https://...",
  "script": "Contenido generado...",
  "audioUrl": "https://...",
  "duracion": 12.5
}
```

---

### Test 2: Diferentes Voces
**Test:** `testDiferentesVoces()`  
**En Postman:** Repetir "Test 1" con diferentes voces:

```
voice = alloy / echo / fable / onyx / shimmer
```

**Resultado esperado:** ✅ 200 OK (con sonidos diferentes)

---

### Test 3: Documento Vacío
**Test:** `testDocumentoVacio()`  
**En Postman:**
1. Crea un archivo .txt vacío
2. Sube como `document`

**Resultado esperado:** ❌ 400 Bad Request  
**Mensaje:** `"El documento no puede estar vacío"`

---

## 🏫 CLASSROOM - ClaseService

### Test 1: Crear Clase Exitosa
**Test:** `testCrearClaseExitosa()`  
**En Postman:**
```
POST http://localhost:8080/api/classroom/clase/crear?emailUsuario=profesor@example.com
Content-Type: application/json

{
  "nombre": "Matemáticas Básicas 101",
  "descripcion": "Curso introductorio de matemáticas",
  "grado": "Primero",
  "horario": "Lunes a Viernes 09:00 - 10:00"
}
```
**Resultado esperado:** ✅ 201 Created  
**Respuesta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Matemáticas Básicas 101",
  "codigoUnico": "MATH-ABC123",
  "qrCode": "data:image/png;base64,iVBORw0KGgo...",
  "estado": "ACTIVA"
}
```
**Guardar:** El `codigoUnico` para tests posteriores

---

### Test 2: Sin Usuario
**Test:** `testSinUsuario()`  
**En Postman:**
```
POST http://localhost:8080/api/classroom/clase/crear?emailUsuario=
Content-Type: application/json

{
  "nombre": "Clase"
}
```
**Resultado esperado:** ❌ 400 Bad Request

---

### Test 3: Sin Nombre
**Test:** `testSinNombre()`  
**En Postman:**
```
POST http://localhost:8080/api/classroom/clase/crear?emailUsuario=profesor@example.com
Content-Type: application/json

{
  "nombre": ""
}
```
**Resultado esperado:** ❌ 400 Bad Request

---

## 🏫 CLASSROOM - ParticipacionService

### Test 1: Unirse a Clase Exitoso
**Test:** `testUnirseExitosa()`  
**En Postman:**
```
POST http://localhost:8080/api/classroom/clase/unirse?emailEstudiante=alumno@example.com
Content-Type: application/json

{
  "codigoUnico": "MATH-ABC123"
}
```
**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "mensaje": "Te has unido exitosamente a la clase",
  "claseNombre": "Matemáticas Básicas 101",
  "fechaUnion": "2025-11-16T14:30:00"
}
```

---

### Test 2: Código Inválido
**Test:** `testCodigoInvalido()`  
**En Postman:**
```
POST http://localhost:8080/api/classroom/clase/unirse?emailEstudiante=alumno@example.com
Content-Type: application/json

{
  "codigoUnico": "CODIGO-INVALIDO"
}
```
**Resultado esperado:** ❌ 404 Not Found  
**Mensaje:** `"Clase no encontrada"`

---

### Test 3: Participación Duplicada
**Test:** `testParticipacionDuplicada()`  
**En Postman:**
1. Ejecuta "Test 1" (unirse a clase)
2. Ejecuta "Test 1" nuevamente con el mismo email y código

**Resultado esperado:** ❌ 400 Bad Request  
**Mensaje:** `"El usuario ya participa en esta clase"`

---

## 💬 CHATBOT - ChatbotService

### Test 1: Crear Nueva Conversación
**Test:** `testCrearNuevaConversacion()`  
**En Postman:**
```
POST http://localhost:8080/api/chat/nueva-conversacion
```
**Resultado esperado:** ✅ 201 Created  
**Respuesta:**
```json
{
  "conversacionId": "conv-550e8400-e29b-41d4-a716",
  "mensaje": "Nueva conversación creada exitosamente"
}
```
**Guardar:** El `conversacionId` para los siguientes tests

---

### Test 2: Enviar Mensaje Educativo
**Test:** `testProcesarMensajeEducativo()`  
**En Postman:**
```
POST http://localhost:8080/api/chat/mensaje
Content-Type: application/json

{
  "conversacionId": "conv-550e8400-e29b-41d4-a716",
  "mensaje": "¿Cuál es la derivada de x al cuadrado?"
}
```
**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "conversacionId": "conv-550e8400-e29b-41d4-a716",
  "respuesta": "La derivada de x² es 2x. Esta es una de las reglas básicas del cálculo diferencial...",
  "timestamp": "2025-11-16T11:45:00"
}
```

---

### Test 3: Múltiples Mensajes en Secuencia
**Test:** `testMultiplesMensajesConversacion()`  
**En Postman:** Ejecuta en orden:

**Mensaje 1:**
```json
{
  "conversacionId": "conv-xxx",
  "mensaje": "¿Qué es la fotosíntesis?"
}
```

**Mensaje 2:**
```json
{
  "conversacionId": "conv-xxx",
  "mensaje": "¿Cuáles son los productos finales?"
}
```

**Mensaje 3:**
```json
{
  "conversacionId": "conv-xxx",
  "mensaje": "¿Cuál es la importancia ecológica?"
}
```

**Resultado esperado:** ✅ 200 OK (en todos)  
**Nota:** Las respuestas mantienen contexto de mensajes anteriores

---

### Test 4: Pregunta Vacía
**Test:** `testPreguntaVacia()`  
**En Postman:**
```
POST http://localhost:8080/api/chat/mensaje
Content-Type: application/json

{
  "conversacionId": "conv-xxx",
  "mensaje": ""
}
```
**Resultado esperado:** ❌ 400 Bad Request  
**Mensaje:** `"El mensaje no puede estar vacío"`

---

### Test 5: Caracteres Especiales
**Test:** `testCaracteresEspeciales()`  
**En Postman:**
```
POST http://localhost:8080/api/chat/mensaje
Content-Type: application/json

{
  "conversacionId": "conv-xxx",
  "mensaje": "¿Qué es 2+2? ¿Cómo se escribe: año, día? Símbolos: @#$%^&*()"
}
```
**Resultado esperado:** ✅ 200 OK

---

### Test 6: Obtener Historial
**Test:** (Para verificar contexto)  
**En Postman:**
```
GET http://localhost:8080/api/chat/conversacion?conversacionId=conv-xxx
```
**Resultado esperado:** ✅ 200 OK  
**Respuesta:**
```json
{
  "conversacionId": "conv-xxx",
  "mensajes": [
    {
      "rol": "user",
      "contenido": "¿Qué es la fotosíntesis?",
      "timestamp": "2025-11-16T11:45:00"
    },
    {
      "rol": "assistant",
      "contenido": "La fotosíntesis es...",
      "timestamp": "2025-11-16T11:45:05"
    }
  ]
}
```

---

## 🔄 Flujo Completo: Escenario Real

### Paso 1: Crear Conversación de Chatbot
```
POST /api/chat/nueva-conversacion
→ Guardar conversacionId
```

### Paso 2: Crear Clase
```
POST /api/classroom/clase/crear?emailUsuario=profesor@example.com
Body: { nombre: "Biología", ... }
→ Guardar codigoUnico
```

### Paso 3: Unirse a Clase
```
POST /api/classroom/clase/unirse?emailEstudiante=alumno@example.com
Body: { codigoUnico: "BIO-XYZ" }
→ Confirmación
```

### Paso 4: Generar Contenido
```
POST /api/script/generate
Body: { tema: "Fotosíntesis", ... }
→ Script generado

POST /api/audio/generate
Body: { texto: "<script>", voz: "alloy" }
→ Audio generado
```

### Paso 5: Interactuar con Chatbot
```
POST /api/chat/mensaje
Body: { conversacionId: "...", mensaje: "¿Qué es la fotosíntesis?" }
→ Respuesta del chatbot
```

---

## 📊 Tabla de Códigos HTTP Esperados

| Endpoint | Método | Éxito | Error |
|----------|--------|-------|-------|
| `/api/audio/generate` | POST | 200 | 400, 500 |
| `/api/script/generate` | POST | 200 | 400, 500 |
| `/api/video/generate-from-document` | POST | 200 | 400, 500 |
| `/api/classroom/clase/crear` | POST | 201 | 400, 500 |
| `/api/classroom/clase/unirse` | POST | 200 | 400, 404 |
| `/api/chat/nueva-conversacion` | POST | 201 | 500 |
| `/api/chat/mensaje` | POST | 200 | 400, 500 |
| `/api/chat/conversacion` | GET | 200 | 404 |

---

## 💡 Tips para Postman

1. **Guardar Variables:** Después de cada request exitoso, copia el valor en una variable
   - `conversacionId`: De `/api/chat/nueva-conversacion`
   - `codigoUnico`: De `/api/classroom/clase/crear`
   - `claseId`: De los endpoints de classroom

2. **Reutilizar en Próximas Requests:**
   ```
   Selecciona el valor → Click derecho → "Set as variable"
   ```

3. **Tests Automáticos en Postman:**
   En la pestaña "Tests", agrega:
   ```javascript
   pm.test("Status es 200", () => {
     pm.response.to.have.status(200);
   });

   pm.test("Response tiene data", () => {
     pm.expect(pm.response.json()).to.have.property("data");
   });
   ```

---

**Última actualización:** 16 de Noviembre de 2025  
**Versión:** 1.0
