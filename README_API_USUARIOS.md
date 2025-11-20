# API Usuarios - Cliente Backend

Este documento describe los endpoints disponibles en el backend para la gestión de usuarios, con ejemplos claros para que el frontend pueda consumirlas.

Base URL (ejemplo):

- http://{HOST}:{PORT}/api/usuarios

Notas generales:
- Todas las respuestas de error usan un JSON con { timestamp, status, error } o { timestamp, status, errors } para validaciones.
- Los campos sensibles como `password` y `token` no se exponen en las respuestas. El campo `password` puede enviarse en requests (write-only).

## Endpoints

### 1) Registro de usuario

- URL: /register
- Método: POST
- Código de éxito: 201 Created
- Request JSON:
  {
    "name": "Nombre Apellido",
    "email": "user@example.com",
    "password": "miPassword"
  }
- Response JSON (ejemplo):
  {
    "id": 0,
    "name": "Nombre Apellido",
    "email": "user@example.com",
    "verified": false
  }

Notas:
- `password` no será devuelto en la respuesta.
- Validaciones: `email` obligatorio y con formato válido.

### 2) Login

- URL: /login
- Método: POST
- Código de éxito: 200 OK
- Request JSON:
  {
    "email": "user@example.com",
    "password": "miPassword"
  }
- Response JSON (ejemplo):
  {
    "id": 0,
    "name": null,
    "email": "user@example.com",
    "verified": false
  }

Notas:
- Actualmente el backend devuelve un DTO con la información básica. En producción se debe devolver un JWT o token de sesión.

### 3) Recuperación de contraseña

- URL: /recover
- Método: POST
- Código de éxito: 200 OK
- Request JSON:
  {
    "email": "user@example.com"
  }
- Response: Texto plano dentro de JSON (string)
  "Correo de recuperación enviado correctamente a user@example.com"

Errores:
- 400 Bad Request si falta el email o no es válido.

### 4) Verificación vía token

- URL: /verify?token={TOKEN}
- Método: GET
- Código de éxito: 200 OK
- Response JSON (ejemplo):
  {
    "id": 0,
    "name": "Nombre",
    "email": "user@example.com",
    "verified": true
  }

Errores:
- 400 Bad Request si falta el token.
- 500 Internal Server Error si el token no es válido o hay un error en la verificación.

### 5) Firebase login (verificación con idToken)

- URL: /firebase-login
- Método: POST
- Headers:
  - Authorization: Bearer {idToken}
- Código de éxito: 200 OK
- Response JSON (ejemplo):
  {
    "uid": "firebase-uid-123",
    "email": "user@example.com"
  }

Errores:
- 400 Bad Request si el header Authorization no está presente o no tiene el formato "Bearer <token>".
- 401/500 según respuesta de Firebase (actualmente se maneja y mappea a 500 si ocurre excepción no esperada).

## Formato de errores

- Validaciones (400):
  {
    "timestamp": "2025-10-21T12:00:00Z",
    "status": 400,
    "errors": ["email: must not be blank", "password: must not be blank"]
  }

- Errores generales (500):
  {
    "timestamp": "2025-10-21T12:00:00Z",
    "status": 500,
    "error": "Ocurrió un error interno"
  }

## Recomendaciones para el Frontend

- En el registro y login enviar siempre `Content-Type: application/json`.
- No almacenar contraseñas en el cliente.
- Para el flujo de Firebase, obtener el `idToken` en el cliente con el SDK de Firebase y enviarlo en el header Authorization: `Bearer {idToken}`.
- Manejar códigos HTTP: 201 en registro, 200 OK en operaciones exitosas, 400 para validaciones y 500 para errores internos.

## Ejemplos con fetch (JS)

Registro:

```js
fetch('/api/usuarios/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name: 'Juan', email: 'juan@example.com', password: 'pwd' })
}).then(res => res.json()).then(console.log).catch(console.error);
```

Login:

```js
fetch('/api/usuarios/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'juan@example.com', password: 'pwd' })
}).then(res => res.json()).then(console.log).catch(console.error);
```

Recover:

```js
fetch('/api/usuarios/recover', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'juan@example.com' })
}).then(res => res.text()).then(console.log).catch(console.error);
```

Firebase login (ejemplo con idToken del cliente Firebase SDK):

```js
fetch('/api/usuarios/firebase-login', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer ' + idToken }
}).then(res => res.json()).then(console.log).catch(console.error);
```

---

Si quieres, puedo:
- Añadir tests unitarios para los controladores y el servicio (mockeando Firebase).
- Integrar la generación real de usuarios en Firebase o una base de datos.
- Añadir Swagger/OpenAPI para documentación interactiva.

