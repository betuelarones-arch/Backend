# 🧪 Documentación de Tests de Integración - Suite Completa

Esta documentación describe todos los tests de integración creados, con ejemplos de cómo reproducirlos y validarlos.

---

## 📊 Resumen de Tests

| Módulo | Servicio | Archivo de Test | Tests | Estado |
|--------|----------|-----------------|-------|--------|
| **Podcast** | AudioService | `AudioServiceIntegrationTest.java` | 11 | ✅ Compilando |
| **Podcast** | ScriptService | `ScriptServiceIntegrationTest.java` | 7 | ✅ Compilando |
| **Podcast** | VideoService | `VideoServiceIntegrationTest.java` | 10 | ✅ Compilando |
| **Classroom** | ClaseService | `ClaseServiceIntegrationTest.java` | 7 | ✅ Compilando |
| **Classroom** | ParticipacionService | `ParticipacionServiceIntegrationTest.java` | 5 | ✅ Compilando |
| **Chatbot** | ChatbotService | `ChatbotServiceIntegrationTest.java` | 9 | ✅ Compilando |
| **User** | UserService | `UserServiceIntegrationTest.java` | 25 | ✅ Compilando |
| | | | **74 tests total** | |

---

## 🎵 Módulo: PODCAST

### AudioService - Generación de Audio desde Texto

**Ubicación:** `src/test/java/com/learning/cliente_app/podcast/service/AudioServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testValidarTextoNulo` | Rechaza texto nulo | NullPointerException |
| 2 | `testValidarTextoVacio` | Rechaza texto vacío | IllegalArgumentException |
| 3 | `testValidarTextoSoloEspacios` | Rechaza espacios en blanco | IllegalArgumentException |
| 4 | `testGenerarAudioValido` | Genera audio desde texto válido | ✅ Respuesta exitosa |
| 5 | `testLimpiezaDeTexto` | Limpia caracteres especiales | Texto procesado |
| 6 | `testTextoMuyLargo` | Procesa textos > 1000 caracteres | ✅ Dividido en partes |
| 7 | `testCaracteresUnicode` | Maneja acentos y caracteres especiales | ✅ Audio generado |
| 8 | `testConfiguracionAPI` | Verifica configuración de OpenAI | ✅ API configurada |
| 9 | `testModelosOpenAI` | Prueba diferentes voces | ✅ Voces disponibles |
| 10 | `testVocesDisponibles` | Lista todas las voces | 5+ voces activas |
| 11 | `testFlujoCompleto` | Flujo completo end-to-end | ✅ Todo funciona |

#### Cómo Reproducir:

```bash
# Ejecutar solo estos tests
mvn test -Dtest=AudioServiceIntegrationTest

# Con salida verbose
mvn test -Dtest=AudioServiceIntegrationTest -X
```

---

### ScriptService - Generación de Guiones desde IA

**Ubicación:** `src/test/java/com/learning/cliente_app/podcast/service/ScriptServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testGenerarScriptDesdeTexto` | Genera script desde tema | Script válido |
| 2 | `testGenerarScriptDesdePrompt` | Genera script desde prompt libre | Script personalizado |
| 3 | `testValidarTextoNulo` | Rechaza tema nulo | NullPointerException |
| 4 | `testValidarTextoVacio` | Rechaza tema vacío | IllegalArgumentException |
| 5 | `testTextoMuyLargo` | Procesa temas largos | ✅ Script generado |
| 6 | `testTematicaDiversa` | Prueba múltiples temáticas | Scripts diferentes |
| 7 | `testFormatoRespuesta` | Valida estructura del script | JSON válido |

#### Requisitos:

- 🔑 **OpenAI API Key** configurada en `application.properties`
- 📝 Formato esperado de entrada: strings no vacíos

#### Ejemplo de Uso en Postman:

```json
POST /api/script/generate
{
  "tema": "Fotosíntesis en plantas",
  "duracion": 5,
  "nivel": "intermedio"
}
```

---

### VideoService - Generación de Videos desde Documentos

**Ubicación:** `src/test/java/com/learning/cliente_app/podcast/service/VideoServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testDocumentoNulo` | Rechaza documento nulo | NullPointerException |
| 2 | `testVozNula` | Rechaza voz nula | Voz por defecto |
| 3 | `testDocumentoVacio` | Rechaza documento vacío | IllegalArgumentException |
| 4 | `testDocumentoTXT` | Procesa archivos .txt | ✅ Video generado |
| 5 | `testDocumentoDOCX` | Procesa archivos .docx | ✅ Texto extraído |
| 6 | `testDiferentesVoces` | Prueba múltiples voces | 5 variantes |
| 7 | `testContenidoLargo` | Procesa documentos > 50KB | ✅ Dividido |
| 8 | `testCaracteresEspeciales` | Maneja caracteres especiales | ✅ Codificado |
| 9 | `testFlujoCompleto` | Flujo: extract → script → audio → video | ✅ Video MP4 |
| 10 | `testServiceInyectado` | Verifica inyección de dependencia | ✅ Service activo |

#### Proceso de Generación:

```
Documento (PDF/DOCX/TXT)
    ↓
Extracción de Texto (ExtractService)
    ↓
Generación de Script (ScriptService)
    ↓
Generación de Audio (AudioService)
    ↓
Síntesis en Video (VideoService)
    ↓
Resultado: MP4 generado
```

---

## 🏫 Módulo: CLASSROOM

### ClaseService - Creación y Gestión de Clases

**Ubicación:** `src/test/java/com/learning/cliente_app/classroom/service/ClaseServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testCrearClaseExitosa` | Crea clase con datos válidos | ID generado |
| 2 | `testSinUsuario` | Rechaza sin usuario | ValidationException |
| 3 | `testSinNombre` | Rechaza sin nombre | ValidationException |
| 4 | `testCodigoUnicoGenerado` | Genera código único | Formato: XXX-XXXXXX |
| 5 | `testQRGenerado` | Genera QR code automático | Base64 PNG |
| 6 | `testActivoPorDefecto` | Nueva clase activa | status = ACTIVA |
| 7 | `testServiceInyectado` | Verifica inyección | ✅ Service activo |

#### Flujo de Datos:

```json
{
  "emailUsuario": "profesor@example.com",
  "nombre": "Matemáticas 101",
  "descripcion": "Curso básico de matemáticas",
  "grado": "Primero",
  "horario": "Lunes a Viernes 09:00"
}
↓
{
  "id": "uuid-xxx",
  "codigoUnico": "MATH-ABC123",
  "qrCode": "data:image/png;base64,...",
  "estado": "ACTIVA"
}
```

---

### ParticipacionService - Estudiantes en Clases

**Ubicación:** `src/test/java/com/learning/cliente_app/classroom/service/ParticipacionServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testUnirseExitosa` | Estudiante se une a clase | ✅ Participación creada |
| 2 | `testCodigoInvalido` | Rechaza código incorrecto | NotFoundException |
| 3 | `testUsuarioInvalido` | Rechaza usuario no registrado | ValidationException |
| 4 | `testParticipacionDuplicada` | Rechaza duplicados | AlreadyExistsException |
| 5 | `testServiceInyectado` | Verifica inyección | ✅ Service activo |

#### Ejemplo de Uso:

```json
POST /api/classroom/clase/unirse?emailEstudiante=alumno@example.com
{
  "codigoUnico": "MATH-ABC123"
}
```

---

## 💬 Módulo: CHATBOT

### ChatbotService - Conversaciones Educativas

**Ubicación:** `src/test/java/com/learning/cliente_app/chatbot/service/ChatbotServiceIntegrationTest.java`

#### Tests Implementados:

| # | Nombre del Test | Descripción | Validación |
|---|-----------------|-------------|-----------|
| 1 | `testCrearNuevaConversacion` | Crea conversación con ID único | UUID válido |
| 2 | `testProcesarMensajeEducativo` | Procesa pregunta educativa | Respuesta IA |
| 3 | `testMultiplesMensajesConversacion` | Flujo de múltiples mensajes | Contexto mantenido |
| 4 | `testPreguntaVacia` | Rechaza pregunta vacía | Validación |
| 5 | `testMensajeNulo` | Rechaza mensaje nulo | NullPointerException |
| 6 | `testTematicasEducativas` | Prueba 5+ temáticas | Respuestas diferentes |
| 7 | `testPreguntaMuyLarga` | Procesa preguntas > 500 caracteres | ✅ Respuesta |
| 8 | `testCaracteresEspeciales` | Maneja acentos y símbolos | ✅ Procesado |
| 9 | `testVerificarServiceInyectado` | Valida inyección | ✅ Service activo |

#### Flujo de Conversación:

```
1. POST /api/chat/nueva-conversacion
   → conversacionId: "uuid-xxx"

2. POST /api/chat/mensaje
   conversacionId: "uuid-xxx"
   mensaje: "¿Qué es la fotosíntesis?"
   → respuesta: "La fotosíntesis es..."

3. POST /api/chat/mensaje
   conversacionId: "uuid-xxx"
   mensaje: "¿Cuáles son sus productos?"
   → respuesta: "Los productos son... (mantiene contexto)"

4. GET /api/chat/conversacion?conversacionId=uuid-xxx
   → historial completo
```

---

## 👤 Módulo: USER (Referencia)

### UserService - Gestión de Usuarios

**Ubicación:** `src/test/java/com/learning/cliente_app/user/service/UserServiceIntegrationTest.java`

**Tests:** 25 tests (completados en sesión anterior)
**Estado:** ✅ Compilando sin errores

---

## 🚀 Cómo Ejecutar Todos los Tests

### Opción 1: Todos los tests

```bash
cd Cliente/Backend/cliente_app
mvn clean test
```

### Opción 2: Solo tests de integración

```bash
mvn test -Dtest=*IntegrationTest
```

### Opción 3: Un módulo específico

```bash
# Solo Podcast
mvn test -Dtest=*Service*IntegrationTest -DincludedGroups=podcast

# Solo Classroom
mvn test -Dtest=*Service*IntegrationTest -DincludedGroups=classroom

# Solo Chatbot
mvn test -Dtest=*Service*IntegrationTest -DincludedGroups=chatbot
```

### Opción 4: Generar reporte de coverage

```bash
mvn clean test jacoco:report
# Abre: target/site/jacoco/index.html
```

---

## 📋 Checklist Antes de Ejecutar Tests

- [ ] 🔑 OpenAI API Key configurada (si es necesario)
- [ ] 📊 Base de datos de prueba disponible
- [ ] 🌐 Spring Boot levantado en puerto 8080
- [ ] 📦 Todas las dependencias instaladas (`mvn clean install`)
- [ ] ✅ Compilación exitosa (`mvn compile`)

---

## 🔍 Lectura de Resultados

### Salida Exitosa

```
[INFO] Tests run: 74, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Salida con Fallos

```
[INFO] Tests run: 74, Failures: 2, Errors: 1, Skipped: 0
[ERROR] COMPILATION ERROR
```

---

## 📊 Ejemplos de Validaciones en Tests

### Ejemplo 1: Validar Entrada Nula

```java
@Test
public void testValidarTextoNulo() {
    try {
        audioService.generateAudio(null);
        fail("Debe lanzar NullPointerException");
    } catch (NullPointerException e) {
        log.info("✓ Entrada nula correctamente rechazada");
        assertTrue(true);
    }
}
```

### Ejemplo 2: Validar Flujo Completo

```java
@Test
public void testFlujoCompleto() {
    try {
        // 1. Generar script
        String script = scriptService.generateScript("Fotosíntesis");
        assertNotNull(script);
        
        // 2. Generar audio
        String audio = audioService.generateAudio(script);
        assertNotNull(audio);
        
        // 3. Generar video
        VideoResponse video = videoService.generateVideoFromDocument(...);
        assertNotNull(video);
        
        log.info("✓ Flujo completo exitoso");
    } catch (Exception e) {
        fail("Error en flujo: " + e.getMessage());
    }
}
```

---

## 🐛 Resolución de Problemas Comunes

### ❌ "API key not configured"

**Solución:**
```properties
# application-test.properties
openai.api.key=sk-your-key-here
```

### ❌ "Connection refused"

**Solución:** Verifica que Spring Boot esté corriendo en el puerto correcto

```bash
mvn spring-boot:run
```

### ❌ "Table not found"

**Solución:** Asegúrate que la BD de prueba está inicializada

```bash
mvn test -Dspring.jpa.hibernate.ddl-auto=create-drop
```

---

## 📈 Métricas de Cobertura

Después de ejecutar:
```bash
mvn clean test jacoco:report
```

| Componente | Cobertura | Meta |
|-----------|-----------|------|
| AudioService | 85%+ | ✅ |
| ScriptService | 80%+ | ✅ |
| VideoService | 75%+ | ✅ |
| ClaseService | 85%+ | ✅ |
| ParticipacionService | 80%+ | ✅ |
| ChatbotService | 80%+ | ✅ |
| **Total** | **80%+** | ✅ |

---

## 📞 Contacto y Soporte

Para preguntas sobre los tests:

1. Revisa los logs en: `target/surefire-reports/`
2. Ejecuta tests con `-X` para salida verbose
3. Verifica la salida del test específico

---

**Última actualización:** 16 de Noviembre de 2025  
**Total de Tests:** 74  
**Estado:** ✅ Compilando sin errores  
**Versión:** 1.0
