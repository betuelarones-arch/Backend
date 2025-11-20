# ⚡ GUÍA RÁPIDA - Entity Mappings Actualizados

**Versión**: 1.0 | **Fecha**: 16 Nov 2025  
**Estado**: ✅ LISTO PARA USAR

---

## 📌 LO QUE CAMBIÓ

### Clase Entity
```diff
- @JoinColumn(name = "creador_id")     // ❌ Incorrecto
+ @JoinColumn(name = "id_creador")     // ✅ Correcto

- private Long id;  // Sin @Column
+ @Column(name = "id_clase")
+ private Long id;  // ✅ Correcto
```

### Participacion Entity
```diff
- @JoinColumn(name = "clase_id")       // ❌ Incorrecto
+ @JoinColumn(name = "id_clase")       // ✅ Correcto

- @JoinColumn(name = "usuario_id")     // ❌ Incorrecto
+ @JoinColumn(name = "id_estudiante")  // ✅ Correcto

- private boolean activa;  // ❌ NO EXISTE EN BD
+ // Removido
```

---

## 🗄️ MAPEO CORRECTO (Clase)

| Campo Java | @Column(name=) | Tipo SQL | Nullable |
|-----------|----------------|----------|----------|
| id | `id_clase` | BIGSERIAL | NO |
| nombre | `nombre` | TEXT | NO |
| descripcion | `descripcion` | TEXT | SI |
| creador | `id_creador` (FK) | BIGINT | NO |
| codigoUnico | `codigo_unico` | TEXT | NO |
| fechaCreacion | `fecha_creacion` | TIMESTAMPTZ | SI |
| fechaInicio | `fecha_inicio` | TIMESTAMPTZ | NO |
| fechaFin | `fecha_fin` | TIMESTAMPTZ | SI |
| activa | `activa` | BOOLEAN | NO |

---

## 🗄️ MAPEO CORRECTO (Participacion)

| Campo Java | @Column(name=) | Tipo SQL | Nullable |
|-----------|----------------|----------|----------|
| id | `id_participacion` | BIGSERIAL | NO |
| clase (FK) | `id_clase` | BIGINT | NO |
| usuario (FK) | `id_estudiante` | BIGINT | NO |
| fechaUnion | `fecha_union` | TIMESTAMPTZ | NO |

---

## ✅ VALIDAR DESPUÉS DE COMPILAR

```bash
# 1. Compilar
mvn clean compile

# 2. Ejecutar tests
mvn test

# 3. Ver si hay errores de mappeo
# Buscar en logs: "SQL Error" o "violates foreign key"

# 4. Si todo OK:
mvn clean package -DskipTests
```

---

## 🚀 ENDPOINTS AFECTADOS

```
POST /api/classroom/clase/crear
  → Crea Clase con id_creador → usuarios.id_usuario

POST /api/classroom/unirse
  → Crea Participacion:
    - id_clase → clases.id_clase
    - id_estudiante → usuarios.id_usuario

GET /api/classroom/clases/mis-clases
  → SELECT * FROM clases WHERE id_creador = ?

GET /api/classroom/clase/{codigo}/qr
  → SELECT * FROM clases WHERE codigo_unico = ?
```

---

## ⚠️ PROBLEMAS COMUNES

### Error: "violates foreign key constraint"
```
❌ Causa: Código está intentando usar id_creador como usuario_id
✅ Solución: Verificar @JoinColumn(name="id_creador")
```

### Error: "column \"xxx\" does not exist"
```
❌ Causa: Hibernate buscando "creadorId" en BD (camelCase)
✅ Solución: Usar @Column(name="id_creador") explícitamente
```

### Error: "unknown column 'activa'"
```
❌ Causa: Campo activa en entity pero NO en BD
✅ Solución: Removido de Participacion entity
```

---

## 📝 REFERENCIA RÁPIDA

**Antes**: `creador_id`, `clase_id`, `usuario_id`, columna `activa` innecesaria  
**Después**: `id_creador`, `id_clase`, `id_estudiante`, sin `activa`

**Resultado**: ✅ Entity mappings 100% coinciden con SQL schema

---

