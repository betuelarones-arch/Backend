## 🚀 Uso de la API

### 1️⃣ Crear Nueva Conversación
```bash
 POST http://localhost:8080/api/chat/nueva-conversacion
```
**Respuesta:**
```json
{
  "conversacionId": "abc-123-xyz",
  "mensaje": "Nueva conversación creada exitosamente"
}
```

### 2️⃣ Enviar Mensaje
```bash
POST http://localhost:8080/api/chat/mensaje \
  -H "Content-Type: application/json" \
  -d '{
    "conversacionId": "abc-123-xyz",
    "mensaje": "¿Cómo puedo mejorar mi comprensión lectora?"
  }'
```
**Respuesta:**
```json
{
  "conversacionId": "abc-123-xyz",
  "respuesta": "Para mejorar tu comprensión lectora te recomiendo...",
  "timestamp": "2025-11-14T10:30:00"
}
```

### 3️⃣ Ver Historial
```bash
 GET http://localhost:8080/api/chat/historial/abc-123-xyz
```

### 4️⃣ Eliminar Conversación
```bash
 DELETE http://localhost:8080/api/chat/conversacion/abc-123-xyz
```

### 5️⃣ Health Check
```bash
  http://localhost:8080/api/chat/salud
```

## 🎯 Características

✅ **Contexto aislado**: Cada conversación tiene su propio contexto
✅ **Solo educación**: El chatbot rechaza temas no educativos
✅ **Historial completo**: Guarda todo el contexto de la conversación
✅ **OpenAI real**: Usa GPT-4o-mini
✅ **MVC pattern**: Sigue el patrón Modelo-Vista-Controlador

## 🔥 Flujo de Uso

1. Usuario crea nueva conversación → obtiene ID
2. Usuario envía preguntas con ese ID
3. El chatbot mantiene el contexto de ESA conversación
4. Al crear otra conversación nueva → contexto limpio
5. Cada conversación es independiente

## ⚠️ Notas Importantes

- Las conversaciones se guardan en memoria (ConcurrentHashMap)
- Si reinicias el servidor, se pierden las conversaciones
- Para producción considera usar Redis o base de datos
- El sistema valida que las preguntas sean educativas