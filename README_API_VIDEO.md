# API Video - Cliente Backend

Este documento describe los endpoints disponibles en el backend para la generación de videos con IA, con ejemplos claros para que el frontend pueda consumirlos.

**Base URL (ejemplo):**
- `http://{HOST}:{PORT}/api/video`

**Notas generales:**
- Todos los endpoints de generación de video son asíncronos: primero devuelven un ID de video y luego puedes consultar el estado.
- Las respuestas de error usan un JSON con `{ error, timestamp }`.
- El tamaño máximo de archivos es 50MB (configurable en `application.properties`).

---

## Endpoints

### 1) Generar video desde documento

Genera un video automáticamente desde un documento (PDF, DOC, DOCX, TXT). El sistema:
1. Extrae el texto del documento usando Cloudmersive
2. Genera un guion profesional con OpenAI
3. Crea el video con JSON2Video

**URL:** `/generate-from-document`  
**Método:** `POST`  
**Content-Type:** `multipart/form-data`  
**Código de éxito:** `200 OK`

**Parámetros:**
- `document` (requerido): Archivo PDF, DOC, DOCX o TXT
- `voice` (opcional): Nombre de la voz a usar (ej: "es-ES", "es-MX", "en-US")

**Request (multipart/form-data):**
```
document: [archivo PDF/DOC/DOCX/TXT]
voice: "es-ES" (opcional)
```

**Response JSON (ejemplo):**
```json
{
  "id": "video-123456",
  "status": "processing",
  "videoUrl": null
}
```

**Errores:**
- `400 Bad Request`: Si falta el documento o está vacío
- `500 Internal Server Error`: Si hay error al procesar el documento o generar el video

---

### 2) Generar video desde prompt

Genera un video desde un prompt de texto. El sistema:
1. Genera un guion profesional con OpenAI desde el prompt
2. Crea el video con JSON2Video (opcionalmente con una imagen)

**URL:** `/generate-from-prompt`  
**Método:** `POST`  
**Content-Type:** `multipart/form-data`  
**Código de éxito:** `200 OK`

**Parámetros:**
- `prompt` (requerido): Texto o tema del video (ej: "Explica qué es la inteligencia artificial")
- `image` (opcional): Archivo de imagen para el video
- `voice` (opcional): Nombre de la voz a usar

**Request (multipart/form-data):**
```
prompt: "Explica qué es la inteligencia artificial y sus aplicaciones"
image: [archivo de imagen] (opcional)
voice: "es-ES" (opcional)
```

**Response JSON (ejemplo):**
```json
{
  "id": "video-789012",
  "status": "processing",
  "videoUrl": null
}
```

**Errores:**
- `400 Bad Request`: Si falta el prompt o está vacío
- `500 Internal Server Error`: Si hay error al generar el guion o el video

---

### 3) Generar video con guion proporcionado

Genera un video usando un guion ya escrito. Útil cuando ya tienes el guion preparado.

**URL:** `/generate`  
**Método:** `POST`  
**Content-Type:** `multipart/form-data`  
**Código de éxito:** `200 OK`

**Parámetros:**
- `script` (requerido): Texto del guion para el video
- `file` (opcional): Archivo de imagen o video para el video
- `voice` (opcional): Nombre de la voz a usar

**Request (multipart/form-data):**
```
script: "Bienvenidos a este video educativo sobre inteligencia artificial..."
file: [archivo de imagen] (opcional)
voice: "es-ES" (opcional)
```

**Response JSON (ejemplo):**
```json
{
  "id": "video-345678",
  "status": "processing",
  "videoUrl": null
}
```

**Errores:**
- `400 Bad Request`: Si falta el guion o está vacío
- `500 Internal Server Error`: Si hay error al generar el video

---

### 4) Verificar estado del video

Consulta el estado de un video que está en proceso de generación.

**URL:** `/status/{id}`  
**Método:** `GET`  
**Código de éxito:** `200 OK`

**Parámetros:**
- `id` (path): ID del video obtenido en la respuesta de generación

**Request:**
```
GET /api/video/status/video-123456
```

**Response JSON (ejemplo - procesando):**
```json
{
  "id": "video-123456",
  "status": "processing",
  "videoUrl": null
}
```

**Response JSON (ejemplo - completado):**
```json
{
  "id": "video-123456",
  "status": "completed",
  "videoUrl": "https://api.json2video.com/videos/video-123456.mp4"
}
```

**Errores:**
- `400 Bad Request`: Si falta el ID del video
- `500 Internal Server Error`: Si hay error al consultar el estado

---

## Formato de errores

**Errores de validación (400):**
```json
{
  "error": "El documento es requerido",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Errores generales (500):**
```json
{
  "error": "Error al generar video desde documento: No se pudo extraer texto del documento",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## Ejemplos de uso

### Ejemplo 1: Generar video desde PDF con JavaScript (Fetch API)

```javascript
const formData = new FormData();
formData.append('document', documentFile); // File object
formData.append('voice', 'es-ES');

fetch('http://localhost:8080/api/video/generate-from-document', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('Video ID:', data.id);
  console.log('Status:', data.status);
  
  // Consultar estado periódicamente
  const checkStatus = setInterval(() => {
    fetch(`http://localhost:8080/api/video/status/${data.id}`)
      .then(res => res.json())
      .then(status => {
        console.log('Status:', status.status);
        if (status.status === 'completed') {
          clearInterval(checkStatus);
          console.log('Video URL:', status.videoUrl);
        }
      });
  }, 5000); // Consultar cada 5 segundos
})
.catch(error => console.error('Error:', error));
```

### Ejemplo 2: Generar video desde prompt con cURL

```bash
curl -X POST http://localhost:8080/api/video/generate-from-prompt \
  -F "prompt=Explica qué es la inteligencia artificial y sus aplicaciones principales" \
  -F "voice=es-ES"
```

### Ejemplo 3: Generar video con guion usando Axios

```javascript
import axios from 'axios';

const formData = new FormData();
formData.append('script', 'Este es mi guion para el video...');
formData.append('voice', 'es-ES');

axios.post('http://localhost:8080/api/video/generate', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})
.then(response => {
  console.log('Video iniciado:', response.data);
})
.catch(error => {
  console.error('Error:', error.response.data);
});
```

### Ejemplo 4: Consultar estado con Python

```python
import requests
import time

video_id = "video-123456"
base_url = "http://localhost:8080/api/video"

while True:
    response = requests.get(f"{base_url}/status/{video_id}")
    data = response.json()
    
    print(f"Status: {data['status']}")
    
    if data['status'] == 'completed':
        print(f"Video URL: {data['videoUrl']}")
        break
    elif data['status'] == 'failed':
        print("Error: La generación del video falló")
        break
    
    time.sleep(5)  # Esperar 5 segundos antes de consultar de nuevo
```

### Ejemplo 5: Generar video desde documento con React

```jsx
import React, { useState } from 'react';

function VideoGenerator() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [videoId, setVideoId] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData();
    formData.append('document', file);
    formData.append('voice', 'es-ES');

    try {
      const response = await fetch('http://localhost:8080/api/video/generate-from-document', {
        method: 'POST',
        body: formData
      });

      const data = await response.json();
      setVideoId(data.id);
      
      // Iniciar polling para verificar estado
      pollVideoStatus(data.id);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  const pollVideoStatus = async (id) => {
    const interval = setInterval(async () => {
      const response = await fetch(`http://localhost:8080/api/video/status/${id}`);
      const data = await response.json();
      
      if (data.status === 'completed') {
        clearInterval(interval);
        alert(`Video listo: ${data.videoUrl}`);
      }
    }, 5000);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="file" 
        accept=".pdf,.doc,.docx,.txt"
        onChange={(e) => setFile(e.target.files[0])} 
      />
      <button type="submit" disabled={loading || !file}>
        {loading ? 'Generando...' : 'Generar Video'}
      </button>
    </form>
  );
}
```

---

## Formatos de archivo soportados

### Documentos:
- **PDF** (.pdf)
- **Word** (.doc, .docx)
- **Texto plano** (.txt)

### Imágenes (opcional para prompt):
- **JPG/JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- Otros formatos soportados por JSON2Video

---

## Flujo recomendado

1. **Enviar solicitud de generación** → Obtener `videoId`
2. **Iniciar polling** → Consultar `/status/{videoId}` cada 5-10 segundos
3. **Verificar estado**:
   - `processing`: El video aún se está generando
   - `completed`: El video está listo, usar `videoUrl`
   - `failed`: Hubo un error, revisar logs

---

## Notas importantes

- ⚠️ **Tiempo de procesamiento**: La generación de videos puede tardar varios minutos dependiendo de la duración y complejidad.
- ⚠️ **Límites de tamaño**: El tamaño máximo de archivos es 50MB por defecto.
- ⚠️ **Rate limiting**: Respeta los límites de las APIs externas (OpenAI, Cloudmersive, JSON2Video).
- ⚠️ **Costos**: Cada llamada consume recursos de las APIs externas, ten en cuenta los costos.

---

## Recomendaciones para el Frontend

1. **Mostrar progreso**: Implementa una barra de progreso mientras se genera el video.
2. **Polling inteligente**: No consultes el estado más de una vez cada 5 segundos.
3. **Manejo de errores**: Captura y muestra mensajes de error amigables al usuario.
4. **Validación de archivos**: Valida el tipo y tamaño de archivo antes de enviarlo.
5. **Timeout**: Implementa un timeout máximo (ej: 10 minutos) para evitar polling infinito.

---

## Troubleshooting

### Error: "No se pudo extraer texto del documento"
- Verifica que el archivo no esté corrupto
- Asegúrate de que el formato sea soportado (PDF, DOC, DOCX, TXT)
- Verifica que el archivo no esté protegido con contraseña

### Error: "No se pudo generar el guion"
- Verifica que la API key de OpenAI esté configurada correctamente
- Revisa que tengas créditos disponibles en tu cuenta de OpenAI

### Error: "Error al comunicarse con JSON2Video API"
- Verifica que la API key de JSON2Video esté configurada
- Revisa que el endpoint de JSON2Video esté disponible
- Verifica que tengas créditos disponibles en JSON2Video

---

Si quieres, puedo:
- Añadir tests de integración para los endpoints
- Implementar WebSocket para notificaciones en tiempo real
- Agregar autenticación y autorización a los endpoints
- Implementar cola de procesamiento para videos largos

