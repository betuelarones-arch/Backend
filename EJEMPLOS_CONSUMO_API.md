# Ejemplos de Consumo de la API de Video

Este archivo contiene ejemplos prácticos de cómo consumir los endpoints de la API de video desde diferentes tecnologías.

## Base URL
```
http://localhost:8080/api/video
```

---

## 1. JavaScript/TypeScript (Fetch API)

### Generar video desde documento

```javascript
async function generarVideoDesdeDocumento(archivo, voz = 'es-ES') {
  const formData = new FormData();
  formData.append('document', archivo);
  formData.append('voice', voz);

  try {
    const response = await fetch('http://localhost:8080/api/video/generate-from-document', {
      method: 'POST',
      body: formData
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Error al generar video');
    }

    const data = await response.json();
    console.log('Video ID:', data.id);
    console.log('Status:', data.status);
    
    return data.id;
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
}

// Uso
const inputFile = document.querySelector('input[type="file"]');
inputFile.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (file) {
    const videoId = await generarVideoDesdeDocumento(file, 'es-ES');
    consultarEstadoVideo(videoId);
  }
});
```

### Consultar estado del video

```javascript
async function consultarEstadoVideo(videoId) {
  try {
    const response = await fetch(`http://localhost:8080/api/video/status/${videoId}`);
    const data = await response.json();
    
    console.log('Estado:', data.status);
    if (data.status === 'completed') {
      console.log('Video URL:', data.videoUrl);
      return data.videoUrl;
    }
    
    return null;
  } catch (error) {
    console.error('Error al consultar estado:', error);
    throw error;
  }
}

// Polling para verificar estado
function pollVideoStatus(videoId, onComplete, onError, maxAttempts = 60) {
  let attempts = 0;
  
  const interval = setInterval(async () => {
    attempts++;
    
    try {
      const videoUrl = await consultarEstadoVideo(videoId);
      
      if (videoUrl) {
        clearInterval(interval);
        onComplete(videoUrl);
      } else if (attempts >= maxAttempts) {
        clearInterval(interval);
        onError(new Error('Timeout: El video tardó demasiado en generarse'));
      }
    } catch (error) {
      clearInterval(interval);
      onError(error);
    }
  }, 5000); // Consultar cada 5 segundos
}

// Uso
pollVideoStatus(
  videoId,
  (videoUrl) => console.log('Video listo:', videoUrl),
  (error) => console.error('Error:', error)
);
```

### Generar video desde prompt

```javascript
async function generarVideoDesdePrompt(prompt, imagen = null, voz = 'es-ES') {
  const formData = new FormData();
  formData.append('prompt', prompt);
  if (imagen) {
    formData.append('image', imagen);
  }
  formData.append('voice', voz);

  const response = await fetch('http://localhost:8080/api/video/generate-from-prompt', {
    method: 'POST',
    body: formData
  });

  const data = await response.json();
  return data.id;
}
```

---

## 2. React Hook Personalizado

```jsx
import { useState } from 'react';

function useVideoGenerator() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [videoId, setVideoId] = useState(null);
  const [videoUrl, setVideoUrl] = useState(null);

  const generarDesdeDocumento = async (archivo, voz = 'es-ES') => {
    setLoading(true);
    setError(null);
    
    const formData = new FormData();
    formData.append('document', archivo);
    formData.append('voice', voz);

    try {
      const response = await fetch('http://localhost:8080/api/video/generate-from-document', {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al generar video');
      }

      const data = await response.json();
      setVideoId(data.id);
      
      // Iniciar polling
      pollStatus(data.id);
      
      return data.id;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const pollStatus = async (id) => {
    const interval = setInterval(async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/video/status/${id}`);
        const data = await response.json();
        
        if (data.status === 'completed') {
          clearInterval(interval);
          setVideoUrl(data.videoUrl);
          setLoading(false);
        } else if (data.status === 'failed') {
          clearInterval(interval);
          setError('La generación del video falló');
          setLoading(false);
        }
      } catch (err) {
        clearInterval(interval);
        setError(err.message);
        setLoading(false);
      }
    }, 5000);
  };

  return {
    loading,
    error,
    videoId,
    videoUrl,
    generarDesdeDocumento
  };
}

// Uso en componente
function VideoUploader() {
  const { loading, error, videoUrl, generarDesdeDocumento } = useVideoGenerator();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const file = e.target.document.files[0];
    if (file) {
      await generarDesdeDocumento(file);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="file" name="document" accept=".pdf,.doc,.docx,.txt" />
      <button type="submit" disabled={loading}>
        {loading ? 'Generando...' : 'Generar Video'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {videoUrl && (
        <video controls src={videoUrl} style={{ width: '100%', marginTop: '20px' }} />
      )}
    </form>
  );
}
```

---

## 3. Python (requests)

```python
import requests
import time
from typing import Optional

BASE_URL = "http://localhost:8080/api/video"

def generar_video_desde_documento(ruta_archivo: str, voz: str = "es-ES") -> Optional[str]:
    """Genera un video desde un documento y retorna el ID del video."""
    url = f"{BASE_URL}/generate-from-document"
    
    with open(ruta_archivo, 'rb') as archivo:
        files = {'document': archivo}
        data = {'voice': voz}
        
        response = requests.post(url, files=files, data=data)
        response.raise_for_status()
        
        resultado = response.json()
        return resultado.get('id')

def consultar_estado(video_id: str) -> dict:
    """Consulta el estado de un video."""
    url = f"{BASE_URL}/status/{video_id}"
    response = requests.get(url)
    response.raise_for_status()
    return response.json()

def esperar_video_completado(video_id: str, timeout: int = 600) -> Optional[str]:
    """Espera hasta que el video esté completado y retorna la URL."""
    inicio = time.time()
    
    while time.time() - inicio < timeout:
        estado = consultar_estado(video_id)
        
        if estado.get('status') == 'completed':
            return estado.get('videoUrl')
        elif estado.get('status') == 'failed':
            raise Exception("La generación del video falló")
        
        time.sleep(5)  # Esperar 5 segundos
    
    raise TimeoutError("Timeout: El video tardó demasiado en generarse")

# Uso
try:
    video_id = generar_video_desde_documento("documento.pdf", "es-ES")
    print(f"Video ID: {video_id}")
    
    video_url = esperar_video_completado(video_id)
    print(f"Video URL: {video_url}")
except Exception as e:
    print(f"Error: {e}")
```

---

## 4. cURL

### Generar video desde documento

```bash
curl -X POST http://localhost:8080/api/video/generate-from-document \
  -F "document=@/ruta/al/documento.pdf" \
  -F "voice=es-ES"
```

### Generar video desde prompt

```bash
curl -X POST http://localhost:8080/api/video/generate-from-prompt \
  -F "prompt=Explica qué es la inteligencia artificial" \
  -F "voice=es-ES"
```

### Consultar estado

```bash
curl -X GET http://localhost:8080/api/video/status/video-123456
```

---

## 5. Axios (JavaScript)

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/video';

// Generar video desde documento
async function generarVideoDesdeDocumento(archivo, voz = 'es-ES') {
  const formData = new FormData();
  formData.append('document', archivo);
  formData.append('voice', voz);

  const response = await axios.post(
    `${API_BASE_URL}/generate-from-document`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
  );

  return response.data.id;
}

// Consultar estado
async function consultarEstado(videoId) {
  const response = await axios.get(`${API_BASE_URL}/status/${videoId}`);
  return response.data;
}

// Uso
const videoId = await generarVideoDesdeDocumento(archivo);
console.log('Video ID:', videoId);

// Polling
const interval = setInterval(async () => {
  const estado = await consultarEstado(videoId);
  console.log('Estado:', estado.status);
  
  if (estado.status === 'completed') {
    clearInterval(interval);
    console.log('Video URL:', estado.videoUrl);
  }
}, 5000);
```

---

## 6. Node.js (sin librerías externas)

```javascript
const http = require('http');
const fs = require('fs');
const FormData = require('form-data');

function generarVideoDesdeDocumento(rutaArchivo, voz = 'es-ES') {
  return new Promise((resolve, reject) => {
    const form = new FormData();
    form.append('document', fs.createReadStream(rutaArchivo));
    form.append('voice', voz);

    const options = {
      hostname: 'localhost',
      port: 8080,
      path: '/api/video/generate-from-document',
      method: 'POST',
      headers: form.getHeaders()
    };

    const req = http.request(options, (res) => {
      let data = '';
      
      res.on('data', (chunk) => {
        data += chunk;
      });
      
      res.on('end', () => {
        if (res.statusCode === 200) {
          const resultado = JSON.parse(data);
          resolve(resultado.id);
        } else {
          reject(new Error(`Error ${res.statusCode}: ${data}`));
        }
      });
    });

    req.on('error', (error) => {
      reject(error);
    });

    form.pipe(req);
  });
}

// Uso
generarVideoDesdeDocumento('./documento.pdf', 'es-ES')
  .then(videoId => console.log('Video ID:', videoId))
  .catch(error => console.error('Error:', error));
```

---

## 7. Ejemplo Completo con Manejo de Errores

```javascript
class VideoGeneratorClient {
  constructor(baseUrl = 'http://localhost:8080/api/video') {
    this.baseUrl = baseUrl;
  }

  async generarDesdeDocumento(archivo, voz = 'es-ES') {
    const formData = new FormData();
    formData.append('document', archivo);
    formData.append('voice', voz);

    try {
      const response = await fetch(`${this.baseUrl}/generate-from-document`, {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || `HTTP ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      if (error instanceof TypeError) {
        throw new Error('Error de conexión: No se pudo conectar al servidor');
      }
      throw error;
    }
  }

  async consultarEstado(videoId) {
    try {
      const response = await fetch(`${this.baseUrl}/status/${videoId}`);
      
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || `HTTP ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      throw new Error(`Error al consultar estado: ${error.message}`);
    }
  }

  async esperarCompletado(videoId, intervalo = 5000, maxIntentos = 120) {
    return new Promise((resolve, reject) => {
      let intentos = 0;

      const interval = setInterval(async () => {
        intentos++;

        try {
          const estado = await this.consultarEstado(videoId);

          if (estado.status === 'completed') {
            clearInterval(interval);
            resolve(estado.videoUrl);
          } else if (estado.status === 'failed') {
            clearInterval(interval);
            reject(new Error('La generación del video falló'));
          } else if (intentos >= maxIntentos) {
            clearInterval(interval);
            reject(new Error('Timeout: El video tardó demasiado en generarse'));
          }
        } catch (error) {
          clearInterval(interval);
          reject(error);
        }
      }, intervalo);
    });
  }
}

// Uso
const cliente = new VideoGeneratorClient();

async function procesarVideo(archivo) {
  try {
    // Generar video
    const resultado = await cliente.generarDesdeDocumento(archivo, 'es-ES');
    console.log('Video iniciado:', resultado.id);

    // Esperar completado
    const videoUrl = await cliente.esperarCompletado(resultado.id);
    console.log('Video listo:', videoUrl);
    
    return videoUrl;
  } catch (error) {
    console.error('Error:', error.message);
    throw error;
  }
}
```

---

## Notas Importantes

1. **Content-Type**: No establezcas manualmente `Content-Type: multipart/form-data` cuando uses `FormData`, el navegador lo hará automáticamente con el boundary correcto.

2. **CORS**: Si consumes desde un navegador, asegúrate de que el backend tenga CORS configurado correctamente.

3. **Tamaño de archivos**: Los archivos grandes pueden tardar más en subir. Considera mostrar un indicador de progreso.

4. **Polling**: No consultes el estado más frecuentemente que cada 5 segundos para evitar sobrecargar el servidor.

5. **Errores**: Siempre maneja los errores y muestra mensajes amigables al usuario.

