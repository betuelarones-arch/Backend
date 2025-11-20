# 📚 Documentación Completa - API Educativa

Este directorio contiene la documentación completa para probar y entender la API educativa sin necesidad de especificar rutas locales. Todo está diseñado para funcionar en **cualquier entorno**.

---

## 📖 Archivos de Documentación

### 1. 📱 `POSTMAN_API_TESTING.md`
**Para:** Usuarios que quieren probar endpoints manualmente  
**Contiene:**
- ✅ Ejemplos de requests para todos los endpoints
- ✅ Parámetros esperados y respuestas
- ✅ Códigos HTTP y errores comunes
- ✅ Flujos completos de prueba
- ✅ Tips para organizar en Postman

**Cuándo usar:** Cuando quieres entender qué hace cada endpoint y cómo probarlo

---

### 2. 🧪 `TESTS_INTEGRATION_GUIDE.md`
**Para:** Desarrolladores que quieren entender los tests de integración  
**Contiene:**
- ✅ Descripción de todos los 74 tests
- ✅ Módulos: Podcast, Classroom, Chatbot, User
- ✅ Cómo ejecutar tests (Maven)
- ✅ Lectura de resultados
- ✅ Resolución de problemas

**Cuándo usar:** Cuando trabajas en el código y quieres ejecutar tests

---

### 3. 🔗 `POSTMAN_TESTS_MAPPING.md`
**Para:** Entender la equivalencia entre tests y requests Postman  
**Contiene:**
- ✅ Cada test de integración mapeado a un request Postman
- ✅ Parámetros exactos de entrada/salida
- ✅ Valores esperados para cada test
- ✅ Flujos completos paso a paso

**Cuándo usar:** Cuando quieres validar manualmente lo que hace un test

---

### 4. 📦 `Postman_Collection.json`
**Para:** Importar directamente en Postman  
**Contiene:**
- ✅ Colección lista para importar
- ✅ Todos los endpoints organizados por módulo
- ✅ Requests pre-configuradas
- ✅ Solo requiere reemplazar `localhost:8080` si es necesario

**Cómo usar:**
1. Abre Postman
2. Click en "Import"
3. Selecciona este archivo JSON
4. ¡Listo! Todos los requests estarán disponibles

---

## 🚀 Guía Rápida de Inicio

### Opción A: Probar endpoints con Postman

```
1. Abre el archivo POSTMAN_API_TESTING.md
2. Copia un ejemplo de request
3. Abre Postman
4. Pega el request
5. Reemplaza valores de prueba (emails, IDs)
6. Click en "Send"
```

### Opción B: Ejecutar todos los tests

```bash
cd Cliente/Backend/cliente_app
mvn clean test
```

### Opción C: Importar colección Postman

```
1. Abre Postman
2. Import → Postman_Collection.json
3. Click en cada request
4. Click en "Send"
```

---

## 📊 Estructura de Módulos

```
🎵 PODCAST
├── AudioService (11 tests)
│   └── Genera audio desde texto usando OpenAI TTS
├── ScriptService (7 tests)
│   └── Genera scripts educativos con IA
└── VideoService (10 tests)
    └── Genera videos desde documentos

🏫 CLASSROOM
├── ClaseService (7 tests)
│   └── Crea y gestiona clases
└── ParticipacionService (5 tests)
    └── Maneja estudiantes en clases

💬 CHATBOT
└── ChatbotService (9 tests)
    └── Chatbot educativo con IA

👤 USERS
└── UserService (25 tests)
    └── Gestión de usuarios
```

**Total: 74 tests de integración** ✅

---

## 🔄 Flujos Comunes de Prueba

### Flujo 1: Crear Clase y Agregarme

```
1. POST /api/classroom/clase/crear
   → Crea una clase de matemáticas

2. GET /api/classroom/clases/mis-clases
   → Obtiene la clase creada

3. POST /api/classroom/clase/unirse
   → Te unes con otro email

4. GET /api/classroom/clase/participantes
   → Verifica que estés en la lista
```

**Archivo referencia:** `POSTMAN_TESTS_MAPPING.md` (Sección: Flujo Completo)

---

### Flujo 2: Generar Contenido Educativo

```
1. POST /api/script/generate
   → Genera script educativo

2. POST /api/audio/generate
   → Convierte script a audio

3. POST /api/video/generate-from-document
   → Genera video desde documento

4. GET /api/video/resultado
   → Obtiene video generado
```

---

### Flujo 3: Chat Educativo Completo

```
1. POST /api/chat/nueva-conversacion
   → Crea conversación con ID único

2. POST /api/chat/mensaje (x5)
   → Envía múltiples preguntas educativas

3. GET /api/chat/conversacion
   → Obtiene historial completo
```

---

## 💡 Variables Importantes para Postman

Crea estas variables en Postman (Environment):

```
BASE_URL = http://localhost:8080
TOKEN = (obtén del endpoint /api/users/login)
CONVERSATION_ID = (obtén de /api/chat/nueva-conversacion)
CLASS_CODE = (obtén de /api/classroom/clase/crear)
AUDIO_VOICE = alloy | echo | fable | onyx | shimmer
```

Uso en requests:
```
POST {{BASE_URL}}/api/chat/mensaje
Header: Authorization: Bearer {{TOKEN}}
```

---

## 🔐 Autenticación

Si el API requiere autenticación:

1. Ejecuta:
   ```
   POST /api/users/login
   Body: { email: "...", password: "..." }
   ```

2. Guarda el token retornado

3. En todos los requests posteriores, agrega:
   ```
   Header: Authorization: Bearer {token}
   ```

---

## 📋 Checklist de Validación

Para verificar que todo funciona:

- [ ] ✅ AudioService genera audio desde texto
- [ ] ✅ ScriptService genera scripts
- [ ] ✅ VideoService genera videos desde documentos
- [ ] ✅ ClaseService crea clases con QR
- [ ] ✅ ParticipacionService agrega estudiantes
- [ ] ✅ ChatbotService responde preguntas educativas
- [ ] ✅ UserService maneja usuarios

---

## 🐛 Troubleshooting

### ❌ "Connection refused"
```
→ Asegúrate que Spring Boot está corriendo en puerto 8080
$ mvn spring-boot:run
```

### ❌ "API key not configured"
```
→ Configura OpenAI API key en application.properties
openai.api.key=sk-...
```

### ❌ "Table not found"
```
→ La BD de prueba no está inicializada
$ mvn test -Dspring.jpa.hibernate.ddl-auto=create-drop
```

### ❌ "Request returns 400"
```
→ Verifica el formato JSON y valores requeridos
→ Revisa POSTMAN_TESTS_MAPPING.md para valores exactos
```

---

## 📈 Métricas

| Métrica | Valor |
|---------|-------|
| Total Tests | 74 |
| Estado | ✅ Compilando sin errores |
| Módulos | 5 (Podcast, Classroom, Chatbot, User, Lecciones) |
| Endpoints Documentados | 15+ |
| Ejemplos Postman | 20+ |
| Coverage Meta | 80%+ |

---

## 🔄 Ciclo de Prueba Recomendado

### Día 1: Setup
- [ ] Importar colección Postman
- [ ] Probar 3 endpoints básicos
- [ ] Verificar Spring Boot levantado

### Día 2: Validación Manual
- [ ] Seguir flujo 1 (Classroom)
- [ ] Seguir flujo 2 (Podcast)
- [ ] Seguir flujo 3 (Chatbot)

### Día 3: Automatización
- [ ] Ejecutar `mvn test`
- [ ] Revisar reporte de coverage
- [ ] Validar 74 tests pasando

---

## 📞 Preguntas Frecuentes

**P: ¿Cuál es la diferencia entre tests e9 y Postman?**  
R: Los tests ejecutan lógica interna. Postman prueba los endpoints HTTP. Ambos son válidos.

**P: ¿Puedo ejecutar un test específico?**  
R: Sí, con `mvn test -Dtest=AudioServiceIntegrationTest`

**P: ¿Necesito todos los archivos?**  
R: No. Elige según necesidad:
- Solo pruebas manuales → `POSTMAN_API_TESTING.md`
- Solo tests automáticos → `TESTS_INTEGRATION_GUIDE.md`
- Ambos → Usa los 3 archivos MD

**P: ¿Los ejemplos funcionan sin cambios?**  
R: Casi. Solo reemplaza:
- `localhost:8080` → tu servidor
- `ejemplo@example.com` → tu email
- IDs dinámicos → los que obtengas

---

## 🎓 Aprendizaje Recomendado

1. **Principiante:** Lee `POSTMAN_API_TESTING.md` (30 min)
2. **Intermedio:** Importa `Postman_Collection.json` y prueba (1 hora)
3. **Avanzado:** Lee `TESTS_INTEGRATION_GUIDE.md` y ejecuta tests (2 horas)
4. **Experto:** Usa `POSTMAN_TESTS_MAPPING.md` para entender correlación (1 hora)

---

## 📝 Notas Finales

- ✅ **Sin rutas específicas:** Todos los ejemplos funcionan en cualquier máquina
- ✅ **Genérico:** Los valores de prueba son ejemplos, úsalos como base
- ✅ **Escalable:** Fácil de extender para nuevos servicios
- ✅ **Documentado:** Cada endpoint tiene ejemplo con entrada/salida

---

## 📂 Archivos en Este Directorio

```
Cliente/Backend/cliente_app/
├── POSTMAN_API_TESTING.md          ← Guía de endpoints
├── TESTS_INTEGRATION_GUIDE.md       ← Guía de tests
├── POSTMAN_TESTS_MAPPING.md        ← Mapeo tests ↔ Postman
├── Postman_Collection.json          ← Colección para importar
└── TESTS_README.md                  ← Este archivo
```

---

**Última actualización:** 16 de Noviembre de 2025  
**Versión:** 1.0  
**Estado:** ✅ Listo para usar
