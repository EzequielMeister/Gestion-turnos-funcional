# Sistema de Gestión de Turnos Médicos

## 📋 Descripción

Sistema de gestión de turnos médicos desarrollado para el **Centro Médico Salud**. Permite a los pacientes solicitar turnos online, a los médicos gestionar su agenda y a los administradores supervisar el sistema completo.

## 👥 Roles de Usuario

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **ADMIN** | Administrador del sistema | Gestionar todos los turnos, ver reportes |
| **MEDICO** | Profesional médico | Ver y gestionar sus propios turnos |
| **PACIENTE** | Paciente del centro médico | Solicitar y cancelar sus turnos |

## 🛠️ Tecnologías

| Componente | Tecnología | Versión |
|------------|------------|---------|
| Backend | Spring Boot 3 | 3.5.4 |
| Lenguaje | Java | 17+ |
| Persistencia | Spring Data JPA | - |
| Base de Datos | SQL Server / H2 (dev) | - |
| Seguridad | Spring Security | - |
| Frontend | HTML5 + Bootstrap 5 | 5.3.3 |
| API Docs | SpringDoc OpenAPI | 2.3.0 |
| Testing | JUnit 5 + Mockito | - |

## 📁 Estructura del Proyecto

```
gestion-turnos/
├── src/main/java/com/ezequiel/gestion_turnos/
│   ├── config/              # Configuraciones de seguridad, CORS, OpenAPI
│   ├── controller/          # Controladores REST (TurnoController, UserController)
│   ├── dto/                 # Data Transfer Objects para API
│   ├── exception/           # Manejo centralizado de excepciones
│   ├── model/               # Entidades JPA (User, Role, Turno, Especialidad)
│   ├── repository/          # Repositorios JPA
│   └── service/             # Lógica de negocio (Services + Impl)
├── src/main/resources/
│   ├── application.yml      # Configuración principal
│   ├── application-dev.yml  # Perfil desarrollo (H2)
│   ├── application-prod.yml # Perfil producción (SQL Server)
│   └── static/              # Archivos HTML del frontend
├── src/test/                # Tests unitarios
└── pom.xml                  # Dependencias Maven
```

## 🚀 Instalación y Ejecución

### Requisitos Previos
- JDK 17+
- Maven 3.8+
- SQL Server (producción) o H2 (desarrollo)

### Desarrollo (con H2 en memoria)
```bash
./mvnw spring-boot:run
```
Acceso: http://localhost:8080

### Producción (con SQL Server)
```bash
# Configurar variables de entorno
export DB_HOST=localhost
export DB_PORT=1433
export DB_NAME=gestion_turnos
export DB_USER=eze
export DB_PASS=passpass

./mvnw spring-boot:run -Pprod
```

## 🔐 Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | adminpass | ADMIN |
| medico | medicopass | MEDICO |
| paciente | pacientepass | PACIENTE |

## 📡 API REST

### Autenticación
```
POST /login          - Iniciar sesión
GET  /logout         - Cerrar sesión
GET  /api/me         - Obtener usuario actual
```

### Turnos
```
GET    /api/turnos              - Listar turnos (filtrado por rol)
GET    /api/turnos/{id}         - Obtener turno por ID
POST   /api/turnos              - Crear nuevo turno
PUT    /api/turnos/{id}         - Actualizar turno
DELETE /api/turnos/{id}         - Eliminar turno
```

### Especialidades
```
GET    /api/especialidades      - Listar especialidades
POST   /api/especialidades      - Crear especialidad (solo admin)
```

### Documentación Interactiva
Acceder a http://localhost:8080/swagger-ui.html para ver la documentación completa de la API.

## 📋 Estados de Turno

| Estado | Descripción | Transiciones |
|--------|-------------|--------------|
| `PENDIENTE` | Turno solicitado, sin confirmar | → CONFIRMADO, CANCELADO |
| `CONFIRMADO` | Turno confirmado por el paciente | → COMPLETADO, CANCELADO |
| `COMPLETADO` | Turno ya atendido | (final) |
| `CANCELADO` | Turno cancelado | (final) |

## ⏰ Reglas de Negocio

1. **BR-01**: Un médico no puede tener más de un turno en la misma fecha y hora
2. **BR-02**: Los turnos deben programarse dentro del horario de atención (08:00 - 18:00)
3. **BR-03**: Los turnos cancelados liberan inmediatamente el horario
4. **BR-04**: Solo usuarios autenticados pueden solicitar turnos
5. **BR-05**: Los pacientes solo pueden cancelar sus propios turnos
6. **BR-06**: Los turnos con más de 24h de anticipación pueden cancelarse sin penalización

## 🧪 Testing

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar con cobertura
./mvnw test jacoco:report
```

## 📊 Métricas del Proyecto

- **Líneas de código**: ~1,500+ (Java + HTML)
- **Cobertura objetivo**: >70%
- **Endpoints API**: 12+
- **Entidades JPA**: 4

## 👤 Autor

**Ezequiel Meister** - Analista de Sistemas

## 📄 Licencia

Este proyecto es parte del portfolio profesional y demuestra competencias en:
- Diseño de arquitecturas Java/Spring
- Modelado de datos y reglas de negocio
- Seguridad y autenticación
- APIs RESTful
- Testing y calidad de código
