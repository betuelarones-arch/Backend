# Cliente App - API Documentation

This repository contains the backend application for the learning platform. Below is a comprehensive list of all available API endpoints.

## 📚 API Endpoints

### 👤 Users (`/api/usuarios`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register a new user |
| POST | `/login` | User login |
| POST | `/recover` | Password recovery |
| GET | `/verify` | Verify user via token |
| POST | `/firebase-login` | Login with Firebase token |

### 📂 Resources (`/api/recursos`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/{id}/file-upload` | Upload resource file (blob) |
| GET | `/{id}/file-download` | Download resource file (blob) |**Note:** The video generation feature has been removed to reduce API costs.


### ❓ Questions (`/api/preguntas`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/subir` | Upload file and generate questions |
| GET | `/limites` | Get question generation limits |
| GET | `/` | Get all questions |
| GET | `/archivo/{nombreArchivo}` | Get questions by filename |
| DELETE | `/archivo/{nombreArchivo}` | Delete questions by filename |
| PUT | `/{id}` | Update a question |
| DELETE | `/{id}` | Delete a question |

### 🔊 AI Audio (`/api/audios-ia`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/{id}/audio-upload` | Upload AI generated audio |
| GET | `/{id}/audio-download` | Download AI generated audio |

### 📝 Lessons (`/api/lecciones`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/{id}/ppt-upload` | Upload PPT file for lesson |
| GET | `/{id}/ppt-download` | Download PPT file from lesson |

### 🏫 Classroom (`/api/classroom`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/clase/crear` | Create a new class |
| GET | `/clases/mis-clases` | Get classes for a user |
| GET | `/clase/{codigoUnico}/qr` | Get QR code for a class |
| GET | `/clase/{codigoUnico}` | Get class info by code |
| POST | `/unirse` | Join a class (authenticated) |
| GET | `/unirse/{codigoUnico}` | Join a class via link/QR |

### 🤖 Chatbot (`/api/chat`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/nueva-conversacion` | Start a new conversation |
| POST | `/mensaje` | Send message to chatbot |
| GET | `/historial/{conversacionId}` | Get conversation history |
| DELETE | `/conversacion/{conversacionId}` | Delete conversation |
| GET | `/salud` | Service health check |

### 📄 Summary (`/api/resumen`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/text` | Summarize raw text |
| POST | `/file` | Summarize uploaded file |
