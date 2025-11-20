# Solución de Problemas - Cliente App

Este documento contiene soluciones para los problemas comunes al ejecutar la aplicación.

## ❌ Problema: "release version 21 not supported"

**Error:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.0:compile (default-compile) on project cliente_app: Fatal error compiling: error: release version 21 not supported
```

**Causa:**
El proyecto está configurado para Java 21, pero el sistema tiene una versión diferente de Java instalada.

**Solución:**
Asegúrate de tener Java 21 instalado y configurado. Verifica tu versión de Java:
```bash
java -version
javac -version
```

Para usar Java 21:
1. Instala Java 21 desde [Adoptium](https://adoptium.net/) o [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21)
2. Configura `JAVA_HOME` para apuntar a Java 21
3. Verifica con `java -version` que muestre versión 21

---

## ❌ Problema: Error de dependencia duplicada

**Error:**
```
[WARNING] 'dependencies.dependency.(groupId:artifactId:type:classifier)' must be unique: com.google.firebase:firebase-admin:jar -> duplicate declaration
```

**Causa:**
La dependencia `firebase-admin` estaba declarada dos veces en el `pom.xml`.

**Solución:**
Ya se ha eliminado la dependencia duplicada.

---

## ❌ Problema: No se puede conectar a la base de datos

**Error:**
```
Could not connect to database: Connection refused
```

**Causa:**
PostgreSQL no está ejecutándose o la configuración es incorrecta.

**Solución:**
1. Verifica que PostgreSQL esté ejecutándose:
   ```bash
   # Windows
   Get-Service postgresql*
   
   # Linux/Mac
   sudo systemctl status postgresql
   ```

2. Verifica la configuración en `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/mi_basedatos
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. Crea la base de datos si no existe:
   ```sql
   CREATE DATABASE mi_basedatos;
   ```

---

## ❌ Problema: Firebase serviceAccountKey.json no encontrado

**Error:**
```
FileNotFoundException: config/serviceAccountKey.json
```

**Causa:**
Falta el archivo de credenciales de Firebase.

**Solución:**
1. Descarga el archivo `serviceAccountKey.json` desde Firebase Console
2. Colócalo en: `src/main/resources/config/serviceAccountKey.json`
3. Asegúrate de que el archivo esté en `.gitignore` para no subirlo al repositorio

---

## ❌ Problema: Puerto 8080 ya en uso

**Error:**
```
Port 8080 is already in use
```

**Solución:**
1. Cambia el puerto en `application.properties`:
   ```properties
   server.port=8081
   ```

2. O detén el proceso que está usando el puerto:
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   
   # Linux/Mac
   lsof -ti:8080 | xargs kill
   ```

---

## ❌ Problema: Errores de compilación en el IDE

**Error:**
El IDE muestra errores rojos pero el código parece correcto.

**Solución:**
1. **Actualiza el proyecto Maven:**
   - Eclipse: Click derecho en proyecto → Maven → Update Project
   - IntelliJ: Click derecho en `pom.xml` → Maven → Reload Project
   - VS Code: Cmd/Ctrl + Shift + P → "Java: Clean Java Language Server Workspace"

2. **Limpia y recompila:**
   ```bash
   mvn clean compile
   ```

3. **Verifica que el IDE esté usando la versión correcta de Java:**
   - Asegúrate de que el proyecto use Java 17 (o la versión instalada)

---

## ✅ Cómo ejecutar la aplicación

### Opción 1: Desde Maven
```bash
cd Cliente/Backend/cliente_app
mvn spring-boot:run
```

### Opción 2: Desde el IDE
1. Abre `ClienteAppApplication.java`
2. Ejecuta el método `main()`

### Opción 3: JAR ejecutable
```bash
mvn clean package
java -jar target/cliente_app-0.0.1-SNAPSHOT.jar
```

---

## ✅ Verificar que la aplicación está funcionando

Una vez que la aplicación esté ejecutándose, puedes verificar:

1. **Verificar que el servidor está activo:**
   ```bash
   curl http://localhost:8080/api/video/status/test
   ```

2. **Ver los logs:**
   Deberías ver algo como:
   ```
   Started ClienteAppApplication in X.XXX seconds
   ```

3. **Probar un endpoint:**
   ```bash
   curl -X GET http://localhost:8080/api/video/status/test
   ```

---

## 🔧 Configuración recomendada

### Variables de entorno (opcional)

Puedes mover las API keys a variables de entorno para mayor seguridad:

```bash
# Windows PowerShell
$env:OPENAI_API_KEY="tu-api-key"
$env:CLOUDMERSIVE_API_KEY="tu-api-key"

# Linux/Mac
export OPENAI_API_KEY="tu-api-key"
export CLOUDMERSIVE_API_KEY="tu-api-key"
```

Y en `application.properties`:
```properties
openai.api.key=${OPENAI_API_KEY}
cloudmersive.api.key=${CLOUDMERSIVE_API_KEY}
```

---

## 📝 Checklist antes de ejecutar

- [ ] Java 17+ instalado y configurado
- [ ] Maven instalado
- [ ] PostgreSQL ejecutándose
- [ ] Base de datos `mi_basedatos` creada
- [ ] `serviceAccountKey.json` en `src/main/resources/config/`
- [ ] Puerto 8080 disponible
- [ ] API keys configuradas en `application.properties`

---

## 🆘 Si aún no funciona

1. **Revisa los logs completos:**
   ```bash
   mvn spring-boot:run > logs.txt 2>&1
   ```

2. **Verifica todas las dependencias:**
   ```bash
   mvn dependency:tree
   ```

3. **Limpia el proyecto completamente:**
   ```bash
   mvn clean
   rm -rf target/
   mvn compile
   ```

4. **Verifica la configuración de Spring Boot:**
   - Asegúrate de que `@SpringBootApplication` esté en la clase principal
   - Verifica que todos los paquetes estén bajo `com.learning.cliente_app`

---

## 📞 Información útil para debugging

**Versiones:**
- Java: 21
- Spring Boot: 3.5.6
- Maven: (verificar con `mvn -version`)

**Rutas importantes:**
- Configuración: `src/main/resources/application.properties`
- Clase principal: `src/main/java/com/learning/cliente_app/ClienteAppApplication.java`
- Logs: En la consola donde ejecutas la aplicación

