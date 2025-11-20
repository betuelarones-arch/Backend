# TEST SUITE - UserService

## Resumen General

Se han creado **dos archivos de test** comprehensivos siguiendo el patrón **PetServiceTest** solicitado:

1. **UserServiceIntegrationTest.java** - Pruebas de Integración (12 tests)
2. **UserServiceUnitTest.java** - Unit Tests con Mockito (13 tests)

**Total: 25 tests** cobriendo toda la funcionalidad de UserService

---

## 1. UserServiceIntegrationTest (Integración)

**Ubicación:** `src/test/java/com/learning/cliente_app/user/service/`

**Características:**
- `@SpringBootTest` - Carga el contexto completo de Spring
- `@Slf4j` - Logging automático
- `@Autowired` - Inyección de dependencias reales
- Estructura: try-catch + assertions explícitos
- Base de datos real (PostgreSQL en producción, H2 en test)

### Tests de Registro (3)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testRegistrarUsuario()` | Registra usuario exitosamente | ID generado, email y nombre coinciden |
| `testRegistrarUsuarioDuplicado()` | Rechaza correo duplicado | Lanza `IllegalArgumentException` |
| `testRegistrarUsuarioSinEmail()` | Rechaza email vacío | Lanza excepción con "email" |

### Tests de Login (5)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testLoginExitoso()` | Login con credenciales correctas | Usuario retornado coincide |
| `testLoginContraseñaIncorrecta()` | Login con password incorrecta | Lanza excepción "credenciales" |
| `testLoginUsuarioNoExistente()` | Login con email inexistente | Lanza excepción "no encontrado" |
| `testLoginSinEmail()` | Login sin email | Lanza `IllegalArgumentException` |
| `testLoginSinPassword()` | Login sin password | Lanza `IllegalArgumentException` |

### Tests de Verificación (1)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testVerifyPasswordIsBCrypted()` | Verifica BCrypt en BD | Password empieza con `$2`, coincide con encoder |

### Tests de Recuperación (2)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testRecuperarContrasena()` | Solicita recuperación exitosamente | Sin excepciones |
| `testRecuperarContraseñaEmailNoExistente()` | Recuperación para email no existente | Manejo seguro |

### Tests de Verificación Token (1)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testVerificarUsuarioConToken()` | Verifica usuario con token | Usuario verificado o manejado |

### Tests de Flujo Completo (2)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testCompleteUserFlow()` | Flujo: Registrar → Verificar BD → Login → Recuperación → BCrypt | Todo exitoso |
| `testMultipleUsersSimultaneously()` | Registra 3 usuarios y verifica todos | Todos creados y logueables |

---

## 2. UserServiceUnitTest (Unit Tests con Mockito)

**Ubicación:** `src/test/java/com/learning/cliente_app/user/service/`

**Características:**
- `@ExtendWith(MockitoExtension.class)` - Mockito configuration
- `@Mock` - Mocks de UserRepository y PasswordEncoder
- `@BeforeEach` - Setup de UserServiceImpl con mocks
- Estructura: Setup → Ejecutar → Validar con mocks

### Tests de Registro (3)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testRegistrarUsuarioExitoso()` | Registro exitoso con mocks | `save()` llamado, usuario retornado correcto |
| `testRegistrarUsuarioDuplicadoFalla()` | Email duplicado | `save()` nunca llamado, excepción lanzada |
| `testRegistrarSinEmailFalla()` | Email vacío | `save()` nunca llamado, excepción lanzada |

### Tests de Login (5)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testLoginExitoso()` | Login con credenciales correctas | `findByEmail()` y `matches()` llamados, usuario retornado |
| `testLoginContraseñaIncorrectaFalla()` | Password incorrecta | `matches()` retorna false, excepción lanzada |
| `testLoginUsuarioNoEncontradoFalla()` | Usuario no encontrado | `findByEmail()` retorna empty, excepción lanzada |
| `testLoginSinEmailFalla()` | Email vacío en login | `findByEmail()` nunca llamado |
| `testLoginSinPasswordFalla()` | Password vacía en login | `findByEmail()` nunca llamado |

### Tests de Validación (2)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testPasswordEncodingCorrectamente()` | Encoding de password | Resultado empieza con `$2a$10$` o `$2y$10$` |
| `testPasswordMatchingCorrectamente()` | Password matching | `matches()` retorna true, verify llamado |

### Tests de Interacción Repository (2)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testRepositoryInteractionVerified()` | Interacción con repository | `verify()` confirma llamadas |
| `testNoSaveOnDuplicateEmail()` | `save()` no llamado en email duplicado | `verify(repo, never()).save()` |

### Tests de Comportamiento (1)

| Test | Descripción | Validación |
|------|-------------|-----------|
| `testFindByEmailCalledOnLogin()` | `findByEmail()` llamado en login | `verify()` confirma interacción |

---

## Patrones Utilizados

### 1. Estructura PetServiceTest

```java
@SpringBootTest          // o @ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceIntegrationTest {
    
    @Autowired               // o @Mock
    private UserService userService;
    
    @Test
    public void testMethod() {
        try {
            // Arrange: Preparar datos
            // Act: Ejecutar
            UsuarioDTO result = userService.registrarUsuario(dto);
            
            // Assert: Validar
            assertEquals(expected, result);
            log.info("TEST EXITOSO");
            
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }
}
```

### 2. Assertions Utilizados

```java
assertNotNull(value)           // Verifica no nulo
assertEquals(expected, actual)  // Igualdad
assertTrue(condition)          // Condición true
fail(message)                  // Falla explícita
```

### 3. Logging con @Slf4j

```java
log.info("USUARIO REGISTRADO: ID={}, Email={}", id, email);
log.error("Error: {}", e.getMessage(), e);
log.warn("Advertencia");
```

### 4. Mockito Patterns

```java
@Mock private UserRepository userRepository;

when(userRepository.findByEmail(EMAIL))
    .thenReturn(Optional.of(user));

verify(userRepository).findByEmail(EMAIL);
verify(userRepository, never()).save(any());
```

---

## Ejecución de Tests

### Ejecutar TODOS los tests
```bash
cd Cliente/Backend/cliente_app
mvn clean test
```

### Ejecutar solo tests de integración
```bash
mvn test -Dtest=UserServiceIntegrationTest
```

### Ejecutar solo tests unitarios
```bash
mvn test -Dtest=UserServiceUnitTest
```

### Con reporte de cobertura
```bash
mvn clean test jacoco:report
```

---

## Cobertura

| Componente | Método | Tests |
|------------|--------|-------|
| **Registro** | `registrarUsuario()` | 6 (3 int + 3 unit) |
| **Login** | `loginUsuario()` | 10 (5 int + 5 unit) |
| **Recuperación** | `recuperarContrasena()` | 2 (2 int) |
| **Verificación** | `verificarUsuario()` | 1 (1 int) |
| **Validaciones** | Password BCrypt, encoding | 5 (1 int + 2 unit + 2 verify) |
| **Flujos** | Completos, múltiples usuarios | 2 (2 int) |

**Total: 25 tests** cubriendo:
- ✅ Casos exitosos
- ✅ Casos de error
- ✅ Validaciones de entrada
- ✅ Interacción con BD
- ✅ BCrypt password handling
- ✅ Flujos complejos

---

## Verificación de Compilación

✅ **UserServiceIntegrationTest.java** - Sin errores de compilación
✅ **UserServiceUnitTest.java** - Sin errores de compilación

Ambos archivos están listos para ejecutar.

---

## Próximos Pasos

1. **Ejecutar tests**: `mvn clean test`
2. **Revisar logs**: Buscar `USUARIO REGISTRADO`, `LOGIN EXITOSO`, etc.
3. **Verificar BCrypt**: En logs debe ver contraseña con `$2a$10$...`
4. **Agregar más tests** si se requiere cobertura adicional de:
   - Controllers (UserControllerIntegrationTest)
   - Repositorios (UserRepositoryTest)
   - Casos edge (null checks, SQL injection, etc.)

