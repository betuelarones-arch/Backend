package com.learning.cliente_app.classroom.web;

import com.learning.cliente_app.classroom.domain.Participacion;
import com.learning.cliente_app.classroom.dto.ClaseDTO;
import com.learning.cliente_app.classroom.dto.CrearClaseRequest;
import com.learning.cliente_app.classroom.dto.UnirseClaseRequest;
import com.learning.cliente_app.classroom.service.ClaseService;
import com.learning.cliente_app.classroom.service.ParticipacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {

    @Autowired
    private ClaseService claseService;

    @Autowired
    private ParticipacionService participacionService;

    @Autowired
    private com.learning.cliente_app.classroom.service.QRCodeService qrCodeService;

    /**
     * Crea una nueva clase (cualquier usuario logueado puede crear)
     * POST /api/classroom/clase/crear
     */
    @PostMapping("/clase/crear")
    public ResponseEntity<?> crearClase(
            @RequestParam String emailUsuario,
            @RequestBody CrearClaseRequest request) {
        try {
            ClaseDTO clase = claseService.crearClase(emailUsuario, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(clase);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al generar QR code: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene todas las clases creadas por un usuario
     * GET /api/classroom/clases/mis-clases
     */
    @GetMapping("/clases/mis-clases")
    public ResponseEntity<?> obtenerMisClases(@RequestParam String emailUsuario) {
        try {
            List<ClaseDTO> clases = claseService.obtenerClasesPorUsuario(emailUsuario);
            return ResponseEntity.ok(clases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene el QR code de una clase
     * GET /api/classroom/clase/{codigoUnico}/qr
     */
    @GetMapping(value = "/clase/{codigoUnico}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> obtenerQRCode(@PathVariable String codigoUnico) {
        try {
            ClaseDTO clase = claseService.obtenerClasePorCodigo(codigoUnico);
            
            // Generar QR code si no existe
            if (clase.getQrCode() == null) {
                byte[] qrCode = qrCodeService.generarQRCodeParaClase(codigoUnico);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCode);
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(clase.getQrCode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene información de una clase por código
     * GET /api/classroom/clase/{codigoUnico}
     */
    @GetMapping("/clase/{codigoUnico}")
    public ResponseEntity<?> obtenerClase(@PathVariable String codigoUnico) {
        try {
            ClaseDTO clase = claseService.obtenerClasePorCodigo(codigoUnico);
            return ResponseEntity.ok(clase);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Un usuario se une a una clase usando código único
     * POST /api/classroom/unirse
     */
    @PostMapping("/unirse")
    public ResponseEntity<?> unirseAClase(@RequestBody UnirseClaseRequest request) {
        try {
            if (request.getCodigoUnico() == null || request.getCodigoUnico().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El código único es requerido"));
            }

            if (request.getEmailEstudiante() == null || request.getEmailEstudiante().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El email del usuario es requerido"));
            }

            Participacion participacion = participacionService.unirseAClase(
                    request.getCodigoUnico(),
                    request.getEmailEstudiante());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Te has unido exitosamente a la clase");
            response.put("claseId", participacion.getClase().getId());
            response.put("claseNombre", participacion.getClase().getNombre());
            response.put("fechaUnion", participacion.getFechaUnion());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint para unirse usando solo el código (GET para facilitar acceso desde QR)
     * GET /api/classroom/unirse/{codigoUnico}?email=usuario@example.com
     */
    @GetMapping("/unirse/{codigoUnico}")
    public ResponseEntity<?> unirseAClasePorCodigo(
            @PathVariable String codigoUnico,
            @RequestParam(required = false) String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                // Retornar página HTML simple para ingresar email
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(generarPaginaUnirse(codigoUnico));
            }

            Participacion participacion = participacionService.unirseAClase(codigoUnico, email);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generarPaginaExito(participacion.getClase().getNombre()));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generarPaginaError(e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }

    private String generarPaginaUnirse(String codigoUnico) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Unirse a Clase</title>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { font-family: Arial, sans-serif; max-width: 500px; margin: 50px auto; padding: 20px; }
                    .container { background: #f5f5f5; padding: 30px; border-radius: 10px; }
                    input { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px; }
                    button { width: 100%; padding: 12px; background: #4CAF50; color: white; border: none; border-radius: 5px; cursor: pointer; }
                    button:hover { background: #45a049; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Unirse a la Clase</h2>
                    <p>Código: <strong>%s</strong></p>
                    <form method="GET">
                        <input type="email" name="email" placeholder="Ingresa tu email" required>
                        <button type="submit">Unirse</button>
                    </form>
                </div>
            </body>
            </html>
            """.formatted(codigoUnico);
    }

    private String generarPaginaExito(String nombreClase) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>¡Unido Exitosamente!</title>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; max-width: 500px; margin: 50px auto; padding: 20px; text-align: center; }
                    .success { background: #d4edda; color: #155724; padding: 20px; border-radius: 10px; }
                </style>
            </head>
            <body>
                <div class="success">
                    <h2>✓ ¡Te has unido exitosamente!</h2>
                    <p>Clase: <strong>%s</strong></p>
                </div>
            </body>
            </html>
            """.formatted(nombreClase);
    }

    private String generarPaginaError(String mensaje) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Error</title>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; max-width: 500px; margin: 50px auto; padding: 20px; text-align: center; }
                    .error { background: #f8d7da; color: #721c24; padding: 20px; border-radius: 10px; }
                </style>
            </head>
            <body>
                <div class="error">
                    <h2>✗ Error</h2>
                    <p>%s</p>
                </div>
            </body>
            </html>
            """.formatted(mensaje);
    }
}

