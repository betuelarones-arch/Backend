# GUÍA DE EJECUCIÓN - USER SERVICE TESTS

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Estructura de Tests](#estructura-de-tests)
3. [Ejecución en Terminal](#ejecución-en-terminal)
4. [Interpretación de Resultados](#interpretación-de-resultados)
5. [Troubleshooting](#troubleshooting)

---

## Requisitos Previos

### Software Requerido

```bash
# Verificar Java 21
java -version

# Verificar Maven
mvn -version

# PostgreSQL debe estar corriendo (o H2 en memoria para tests)
```

### Variables de Entorno

```powershell
# En PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:MAVEN_HOME = "C:\apache-maven-3.8.8"
$env:PATH = "$env:PATH;$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin"
```

### Dependencias Maven

El proyecto debe tener en `pom.xml`:

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## Estructura de Tests

### Ubicación de Archivos

```
Cliente/Backend/cliente_app/
├── src/test/java/com/learning/cliente_app/user/service/
│   ├── UserServiceIntegrationTest.java    ← 12 tests de integración
│   └── UserServiceUnitTest.java           ← 13 tests unitarios
├── src/main/java/com/learning/cliente_app/
│   └── user/
│       ├── service/
│       │   ├── UserService.java           ← Interface
│       │   └── UserServiceImpl.java        ← Implementación
│       ├── repository/
│       │   └── UserRepository.java        ← JPA Repository
│       ├── dto/
│       │   └── UsuarioDTO.java            ← DTO
│       └── model/
│           └── UserEntity.java            ← Entity JPA
├── TEST_SUITE_README.md                   ← Documentación
└── pom.xml                                ← Maven config
```

---

## Ejecución en Terminal

### Paso 1: Navegar al Directorio del Proyecto

```powershell
# Cambiar al directorio correcto
cd "c:\PI-4C\2025-2-4c24-pi-pi-1c\Cliente\Backend\cliente_app"

# Verificar que estamos en el lugar correcto
Get-ChildItem

# Debería mostrar:
# - src/
# - target/
# - pom.xml
# - README.md
```

### Paso 2: Limpiar y Compilar

```powershell
# Limpiar builds anteriores
mvn clean

# Compilar el proyecto
mvn compile

# Compilar tests
mvn test-compile
```

### Paso 3: Ejecutar Tests

#### Opción A: Ejecutar TODOS los tests

```powershell
# Básico
mvn test

# Con salida detallada
mvn test -e

# Con salida aún más detallada
mvn test -X

# Sin parar en primer error
mvn test --fail-at-end
```

**Salida esperada:**
```
[INFO] Running com.learning.cliente_app.user.service.UserServiceIntegrationTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Running com.learning.cliente_app.user.service.UserServiceUnitTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

#### Opción B: Solo Tests de Integración

```powershell
# Ejecutar solo integración
mvn test -Dtest=UserServiceIntegrationTest

# Con salida
mvn test -Dtest=UserServiceIntegrationTest -e
```

#### Opción C: Solo Unit Tests

```powershell
# Ejecutar solo unitarios
mvn test -Dtest=UserServiceUnitTest

# Con salida
mvn test -Dtest=UserServiceUnitTest -e
```

#### Opción D: Test Individual Específico

```powershell
# Un test específico de integración
mvn test -Dtest=UserServiceIntegrationTest#testRegistrarUsuario

# Un test específico unitario
mvn test -Dtest=UserServiceUnitTest#testRegistrarUsuarioExitoso
```

#### Opción E: Con Cobertura (si está configurado)

```powershell
# Con JaCoCo
mvn clean test jacoco:report

# El reporte estará en: target/site/jacoco/index.html
```

---

## Interpretación de Resultados

### Salida Exitosa

```
[INFO] --- maven-surefire-plugin:2.22.2:test (default-test) @ cliente_app ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.learning.cliente_app.user.service.UserServiceIntegrationTest

[INFO] USUARIO REGISTRADO: ID=1, Email=newuser@example.com, Name=Juan
[INFO] LOGIN EXITOSO para: Email=login@example.com, Name=María
[INFO] CONTRASEÑA VERIFICADA EN BCRYPT: $2a$10$...

[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s
[INFO] 
[INFO] Running com.learning.cliente_app.user.service.UserServiceUnitTest

[INFO] REGISTRO EXITOSO: ID=1, Email=juan@example.com
[INFO] PASSWORD ENCODED: $2a$10$...

[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.234 s
[INFO] 
[INFO] BUILD SUCCESS
```

### Errores Comunes

#### Error 1: "Package not found"

```
[ERROR] COMPILATION ERROR
[ERROR] /path/to/UserServiceIntegrationTest.java:[8,1] package com.learning.cliente_app.user.dto does not exist
```

**Solución:**
```powershell
# Asegurar que UserService.java existe
Test-Path "src/main/java/com/learning/cliente_app/user/service/UserService.java"

# Si no existe, ejecutar:
mvn clean install

# O verificar que los imports en el test sean correctos
```

#### Error 2: "Cannot find symbol"

```
[ERROR] /path/to/UserServiceIntegrationTest.java:[50,1] cannot find symbol
[ERROR] symbol: class UsuarioDTO
```

**Solución:**
```powershell
# Verificar que UsuarioDTO existe
Test-Path "src/main/java/com/learning/cliente_app/user/dto/UsuarioDTO.java"

# Verificar nombre exacto (case-sensitive)
Get-ChildItem "src/main/java/com/learning/cliente_app/user/dto/"
```

#### Error 3: "Test execution interrupted"

```
[ERROR] Tests run: 5, Failures: 2, Errors: 3
```

**Solución:**
```powershell
# Ver logs completos
mvn test -e | Out-String -Stream

# Ver logs de BD
mvn test -DdbPassword=xxxx -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

# Verificar BD está corriendo
ping localhost
```

#### Error 4: "Database connection refused"

```
[ERROR] java.sql.SQLException: Conexión rechazada
```

**Solución:**
```powershell
# Verificar PostgreSQL está activo
Get-Service postgresql* | Select-Object Status

# O usar H2 en memoria (por defecto en tests)
# Verificar: application-test.properties existe
Test-Path "src/test/resources/application-test.properties"
```

---

## Logs de Ejecución

### Ver Logs en Tiempo Real

```powershell
# Ejecutar con stream de logs
mvn test -e | Tee-Object -FilePath test_log.txt

# Después de ejecución, ver archivo
Get-Content test_log.txt
```

### Buscar Logs Específicos

```powershell
# Buscar logs de éxito
mvn test -e | Select-String "USUARIO REGISTRADO"

# Buscar logs de error
mvn test -e | Select-String "Error"

# Buscar logs de BCrypt
mvn test -e | Select-String "BCRYPT"

# Guardar en archivo
mvn test -e | Out-File "full_log.txt"
```

---

## Troubleshooting Avanzado

### Limpiar Caché Maven

```powershell
# Eliminar caché local
Remove-Item "$env:USERPROFILE\.m2\repository" -Recurse -Force

# Reintentar descarga
mvn clean install
```

### Compilar con Debug

```powershell
# Modo debug de Maven
mvn -X test -Dtest=UserServiceIntegrationTest#testRegistrarUsuario
```

### Timeout en Tests

```powershell
# Aumentar timeout
mvn test -Dorg.junit.jupiter.execution.parallel.enabled=false

# O establecer en pom.xml:
# <maven.surefire.plugin.version>2.22.2</maven.surefire.plugin.version>
```

### Verificar Configuración

```powershell
# Ver java version usada por Maven
mvn --version

# Ver todas las propiedades
mvn help:active-profiles

# Listar dependencias
mvn dependency:tree
```

---

## Ejemplo Completo de Ejecución

```powershell
# 1. Navegar al proyecto
cd "c:\PI-4C\2025-2-4c24-pi-pi-1c\Cliente\Backend\cliente_app"

# 2. Limpiar y compilar
mvn clean compile test-compile

# 3. Ejecutar tests
mvn test

# 4. Ver resultados
# Buscar "BUILD SUCCESS" o "BUILD FAILURE"

# 5. Si hay errores, ver detalles
mvn test -e | Out-File "error_log.txt"
Get-Content "error_log.txt"

# 6. Ejecutar un test específico para verificar
mvn test -Dtest=UserServiceIntegrationTest#testRegistrarUsuario

# 7. Generar reporte (opcional)
mvn clean test jacoco:report

# 8. Ver logs de test
Get-Content "target/surefire-reports/TEST-*.txt"
```

---

## Verificación Final

Después de ejecutar, verificar:

✅ **Compilación sin errores**
```powershell
# Debe mostrar: [INFO] BUILD SUCCESS
```

✅ **25 tests ejecutados**
```powershell
# Debe mostrar: Tests run: 25, Failures: 0, Errors: 0
```

✅ **Logs de éxito presentes**
```powershell
# Buscar en salida:
# - USUARIO REGISTRADO
# - LOGIN EXITOSO
# - CONTRASEÑA VERIFICADA EN BCRYPT
```

✅ **No hay excepciones no capturadas**
```powershell
# No debe haber [ERROR] o [FAILURE] fuera de try-catch
```

---

## Próximos Pasos

Una vez que todos los tests pasen:

1. **Migrar Passwords en BD**
   ```bash
   cd ../
   java -jar HashPasswordsMigration.jar
   ```

2. **Ejecutar aplicación**
   ```bash
   mvn spring-boot:run
   ```

3. **Probar endpoints**
   ```powershell
   curl -X POST http://localhost:8080/api/usuarios/register `
     -H "Content-Type: application/json" `
     -d '{"name":"Test","email":"test@example.com","password":"Pass123"}'
   ```

4. **Ejecutar blob endpoints**
   ```powershell
   # Ver: IMPLEMENTATION_SUMMARY.md
   # Para endpoints POST /api/lecciones/{id}/ppt-upload, etc.
   ```

---

## Soporte

Si hay problemas:

1. **Verificar compilación**: `mvn compile`
2. **Verificar tests**: `mvn test-compile`
3. **Ver logs completos**: `mvn test -e`
4. **Consultar documentación**: `TEST_SUITE_README.md`
5. **Verificar archivos**:
   - `UserServiceIntegrationTest.java` existe
   - `UserServiceUnitTest.java` existe
   - `UserService.java` implementa métodos
   - `UsuarioDTO.java` tiene campos correctos

