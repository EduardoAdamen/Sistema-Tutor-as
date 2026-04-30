# Plan de Desarrollo — Sistema Web de Tutorías ITCH
### Spring Boot + Thymeleaf + MySQL · Proyecto Académico

---

## Contexto General del Sistema

El Sistema Web de Tutorías del Instituto Tecnológico de Chilpancingo (ITCH) busca digitalizar y automatizar el Programa de Tutorías del Departamento de Desarrollo Académico (DDA). Actualmente el proceso es completamente manual: listas físicas, carnets de asistencia en papel, cotejo manual y captura en Excel, lo que genera fraudes y errores.

### Actores del Sistema
| Clave | Actor | Descripción |
|-------|-------|-------------|
| ACT-01 | DDA (admin) | Administra todo el sistema: usuarios, carreras, periodos, asignaciones y reportes |
| ACT-02 | Tutor | Registra sesiones, asistencia y actividades PAT de su grupo asignado |
| ACT-03 | Tutorado | Consulta su asignación, historial de asistencia y porcentaje acumulado |

### Módulos del Sistema
- **MO_01** Control de Acceso (Login / Sesión / Roles)
- **MO_02** Gestión de Usuarios y Carreras
- **MO_03** Gestión de Periodos Semestrales y Asignaciones
- **MO_04** Registro de Sesiones y Asistencia
- **MO_05** Registro de Actividades PAT y Búsquedas

---

## Tecnología y Arquitectura

- **Backend:** Spring Boot (versión 3.x)
- **Vistas:** Thymeleaf (renderizado del lado del servidor)
- **Base de datos:** MySQL
- **ORM:** Spring Data JPA / Hibernate (crea las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`)
- **Seguridad:** Spring Security
- **Almacenamiento de archivos:** Disco local en `C:/Evidencias/` con subcarpetas por tipo
- **CSS Framework:** Bootstrap 5.3

### Estructura de Paquetes Java
```
com.itch.tutorias
├── config/
│   └── StorageConfig.java            ← Sirve imágenes/archivos desde el disco
│   └── SecurityConfig.java           ← Configuración Spring Security (Sprint 2)
├── controller/
│   ├── CarreraController.java
│   ├── UsuarioController.java
│   ├── PeriodoController.java
│   ├── AsignacionController.java
│   ├── SesionController.java
│   ├── AsistenciaController.java
│   ├── ActividadPatController.java
│   └── BusquedaController.java
├── model/
│   ├── Carrera.java
│   ├── Usuario.java
│   ├── PeriodoSemestral.java
│   ├── Asignacion.java
│   ├── AsignacionTutorado.java
│   ├── Sesion.java
│   ├── RegistroAsistencia.java
│   └── ActividadPat.java
├── repository/
│   ├── CarreraRepository.java
│   ├── UsuarioRepository.java
│   ├── PeriodoSemestralRepository.java
│   ├── AsignacionRepository.java
│   ├── AsignacionTutoradoRepository.java
│   ├── SesionRepository.java
│   ├── RegistroAsistenciaRepository.java
│   └── ActividadPatRepository.java
└── service/
    ├── ICarrera.java
    ├── IUsuario.java
    ├── IPeriodoSemestral.java
    ├── IAsignacion.java
    ├── IAsignacionTutorado.java
    ├── ISesion.java
    ├── IRegistroAsistencia.java
    ├── IActividadPat.java
    ├── IServicioAlmacenamiento.java
    └── implementjpa/
        ├── CarreraServiceImpl.java
        ├── UsuarioServiceImpl.java
        ├── PeriodoSemestralServiceImpl.java
        ├── AsignacionServiceImpl.java
        ├── AsignacionTutoradoServiceImpl.java
        ├── SesionServiceImpl.java
        ├── RegistroAsistenciaServiceImpl.java
        ├── ActividadPatServiceImpl.java
        └── ServicioAlmacenamientoImpl.java
```

### Estructura de Vistas Thymeleaf (`src/main/resources/templates/`)
```
templates/
├── fragments/
│   └── fragment.html          ← head, navbar, footer
├── auth/
│   └── login.html
├── carrera/
│   ├── listaCarreras.html
│   ├── formCarrera.html
│   └── detalleCarrera.html
├── usuario/
│   ├── listaUsuarios.html
│   ├── formUsuario.html
│   ├── detalleUsuario.html
│   └── perfil.html
├── periodo/
│   ├── listaPeriodos.html
│   ├── formPeriodo.html
│   └── detallePeriodo.html
├── asignacion/
│   ├── listaAsignaciones.html
│   ├── formAsignacion.html
│   └── detalleAsignacion.html
├── sesion/
│   ├── listaSesiones.html
│   ├── formSesion.html
│   └── detalleSesion.html
├── asistencia/
│   └── registrarAsistencia.html
├── pat/
│   ├── listaActividades.html
│   └── formActividad.html
├── busqueda/
│   ├── busquedaTutores.html
│   ├── busquedaTutorado.html
│   └── busquedaPAT.html
└── dashboard/
    ├── dashboardAdmin.html
    ├── dashboardTutor.html
    └── dashboardTutorado.html
```

### Archivos estáticos (`src/main/resources/static/`)
```
static/
├── css/
│   └── estilos.css
└── images/
    ├── logo-itch.png
    └── sin-imagen.png
```

### `application.properties` completo
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_tutorias?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate — crea/actualiza tablas automáticamente
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf
spring.thymeleaf.cache=false

# Multipart — tamaño máximo de archivos (5 MB)
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

# Almacenamiento en disco local
storage.location=C:/Evidencias

# Subcarpetas por tipo de evidencia
storage.folder.usuarios=usuarios
storage.folder.sesiones=sesiones
storage.folder.actividades=actividades
```

---

## Base de Datos

La base de datos **`sistema_tutorias`** en MySQL contiene las siguientes tablas (se crean automáticamente al ejecutar el proyecto gracias a Hibernate):

| Tabla | Descripción |
|-------|-------------|
| `carrera` | Catálogo de carreras del instituto |
| `usuario` | Usuarios del sistema (admin/tutor/tutorado) |
| `periodo_semestral` | Periodos semestrales del programa |
| `asignacion` | Asignación tutor → carrera/grupo/periodo |
| `asignacion_tutorado` | Relación M:N asignacion ↔ tutorado |
| `sesion` | Sesiones de tutoría registradas |
| `registro_asistencia` | Asistencia por tutorado por sesión |
| `actividad_pat` | Actividades del Plan de Acción Tutorial |

El usuario inicial `admin@itch.edu.mx` con contraseña `Admin2025` debe insertarse en la primera ejecución.

---

## Dependencias Maven (`pom.xml`)

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- Thymeleaf -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <!-- Thymeleaf + Spring Security extras -->
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    </dependency>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <!-- DevTools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <!-- BCrypt para contraseñas -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>
</dependencies>
```

---

# SPRINT 1 — Fundamentos: Entidades, Repositorios, Servicios, Configuración de Almacenamiento y Vistas CRUD de Catálogos

**Objetivo:** Construir toda la base del proyecto: entidades JPA con relaciones (que generen la BD en MySQL al ejecutar), interfaces de servicios, repositorios JPA, configuración de almacenamiento de archivos en disco y CRUD completo de las dos entidades catálogo independientes: **Carrera** y la entidad **PeriodoSemestral**. Sin login todavía.

---

## TAREA 1.1 — Configuración inicial del proyecto Spring Boot

Crear el proyecto Spring Boot con el nombre de grupo `com.itch.tutorias`, artefacto `sistema-tutorias`. Configurar el archivo `application.properties` exactamente como se indica arriba. Crear la base de datos `sistema_tutorias` en MySQL.

---

## TAREA 1.2 — Entidades JPA (`package com.itch.tutorias.model`)

Crear **todas** las entidades JPA de una sola vez con sus relaciones correctas. Todas deben tener la anotación `@Entity` y `@Table`. Cada entidad debe incluir explícitamente: constructor vacío, getters y setters para todos los campos, y método `toString()`. **No se usa Lombok**.

### 1.2.1 — `Carrera.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrera")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 120, unique = true)
    private String nombre;

    @Column(name = "clave", nullable = false, length = 20, unique = true)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCarrera estado = EstadoCarrera.activa;

    public enum EstadoCarrera {
        activa, inactiva
    }

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public Carrera() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public EstadoCarrera getEstado() { return estado; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setClave(String clave) { this.clave = clave; }
    public void setEstado(EstadoCarrera estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Carrera{id=" + id + ", nombre='" + nombre + "', clave='" + clave + "', estado=" + estado + "}";
    }
}
```

### 1.2.2 — `Usuario.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_identificacion", nullable = false, length = 30, unique = true)
    private String numeroIdentificacion;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "correo", nullable = false, length = 120, unique = true)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(name = "debe_cambiar_contrasena", nullable = false)
    private Boolean debeCambiarContrasena = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    /**
     * Relación ManyToOne hacia Carrera.
     * Un usuario puede pertenecer a una carrera (nullable para el admin).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_usuario_carrera"))
    private Carrera carrera;

    @Column(name = "foto_perfil", length = 255)
    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.activo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Rol {
        admin, tutor, tutorado
    }

    public enum EstadoUsuario {
        activo, inactivo
    }

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public Usuario() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public Boolean getDebeCambiarContrasena() { return debeCambiarContrasena; }
    public Rol getRol() { return rol; }
    public Carrera getCarrera() { return carrera; }
    public String getFotoPerfil() { return fotoPerfil; }
    public EstadoUsuario getEstado() { return estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }
    public void setDebeCambiarContrasena(Boolean debeCambiarContrasena) { this.debeCambiarContrasena = debeCambiarContrasena; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", numeroIdentificacion='" + numeroIdentificacion +
               "', nombreCompleto='" + nombreCompleto + "', rol=" + rol + ", estado=" + estado + "}";
    }
}
```

### 1.2.3 — `PeriodoSemestral.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "periodo_semestral")
public class PeriodoSemestral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave", nullable = false, length = 20, unique = true)
    private String clave;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPeriodo estado = EstadoPeriodo.activo;

    public enum EstadoPeriodo {
        activo, cerrado
    }

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public PeriodoSemestral() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public String getClave() { return clave; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public EstadoPeriodo getEstado() { return estado; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setClave(String clave) { this.clave = clave; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public void setEstado(EstadoPeriodo estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "PeriodoSemestral{id=" + id + ", clave='" + clave + "', estado=" + estado + "}";
    }
}
```

### 1.2.4 — `Asignacion.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "asignacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_asignacion",
            columnNames = {"tutor_id", "grupo", "hora_horario", "periodo_id"})
    })
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ManyToOne hacia Usuario (tutor).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_tutor"))
    private Usuario tutor;

    /**
     * ManyToOne hacia Carrera.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_carrera"))
    private Carrera carrera;

    /**
     * ManyToOne hacia PeriodoSemestral.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_periodo"))
    private PeriodoSemestral periodo;

    @Column(name = "grupo", nullable = false, length = 20)
    private String grupo;

    @Column(name = "aula", nullable = false, length = 30)
    private String aula;

    @Column(name = "dia_horario", nullable = false, length = 30)
    private String diaHorario;

    @Column(name = "hora_horario", nullable = false)
    private LocalTime horaHorario;

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public Asignacion() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public Usuario getTutor() { return tutor; }
    public Carrera getCarrera() { return carrera; }
    public PeriodoSemestral getPeriodo() { return periodo; }
    public String getGrupo() { return grupo; }
    public String getAula() { return aula; }
    public String getDiaHorario() { return diaHorario; }
    public LocalTime getHoraHorario() { return horaHorario; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setTutor(Usuario tutor) { this.tutor = tutor; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public void setPeriodo(PeriodoSemestral periodo) { this.periodo = periodo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public void setAula(String aula) { this.aula = aula; }
    public void setDiaHorario(String diaHorario) { this.diaHorario = diaHorario; }
    public void setHoraHorario(LocalTime horaHorario) { this.horaHorario = horaHorario; }

    @Override
    public String toString() {
        return "Asignacion{id=" + id + ", grupo='" + grupo + "', aula='" + aula + "'}";
    }
}
```

### 1.2.5 — `AsignacionTutorado.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asignacion_tutorado",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_asig_tutorado",
            columnNames = {"asignacion_id", "tutorado_id"})
    })
public class AsignacionTutorado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ManyToOne hacia Asignacion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asig_tut_asignacion"))
    private Asignacion asignacion;

    /**
     * ManyToOne hacia Usuario (tutorado).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asig_tut_tutorado"))
    private Usuario tutorado;

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public AsignacionTutorado() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public Usuario getTutorado() { return tutorado; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
    public void setTutorado(Usuario tutorado) { this.tutorado = tutorado; }

    @Override
    public String toString() {
        return "AsignacionTutorado{id=" + id + "}";
    }
}
```

### 1.2.6 — `Sesion.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sesion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_sesion",
            columnNames = {"asignacion_id", "fecha"})
    })
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ManyToOne hacia Asignacion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sesion_asignacion"))
    private Asignacion asignacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    /**
     * Ruta relativa del archivo de evidencia guardado en C:/Evidencias/sesiones/
     */
    @Column(name = "evidencia", length = 255)
    private String evidencia;

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public Sesion() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getEvidencia() { return evidencia; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setEvidencia(String evidencia) { this.evidencia = evidencia; }

    @Override
    public String toString() {
        return "Sesion{id=" + id + ", fecha=" + fecha + ", horaInicio=" + horaInicio + "}";
    }
}
```

### 1.2.7 — `RegistroAsistencia.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registro_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_registro_asistencia",
            columnNames = {"sesion_id", "tutorado_id"})
    })
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ManyToOne hacia Sesion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reg_asist_sesion"))
    private Sesion sesion;

    /**
     * ManyToOne hacia Usuario (tutorado).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reg_asist_tutorado"))
    private Usuario tutorado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", nullable = false)
    private EstadoAsistencia estadoAsistencia;

    public enum EstadoAsistencia {
        presente, ausente, justificado
    }

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public RegistroAsistencia() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public Sesion getSesion() { return sesion; }
    public Usuario getTutorado() { return tutorado; }
    public EstadoAsistencia getEstadoAsistencia() { return estadoAsistencia; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setSesion(Sesion sesion) { this.sesion = sesion; }
    public void setTutorado(Usuario tutorado) { this.tutorado = tutorado; }
    public void setEstadoAsistencia(EstadoAsistencia estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }

    @Override
    public String toString() {
        return "RegistroAsistencia{id=" + id + ", estadoAsistencia=" + estadoAsistencia + "}";
    }
}
```

### 1.2.8 — `ActividadPat.java`
```java
package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividad_pat")
public class ActividadPat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ManyToOne hacia Sesion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_actividad_sesion"))
    private Sesion sesion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actividad", nullable = false)
    private TipoActividad tipoActividad;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Ruta relativa del archivo de evidencia guardado en C:/Evidencias/actividades/
     */
    @Column(name = "evidencia", length = 255)
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public enum TipoActividad {
        taller, asesoria_academica, sesion_informativa, otra
    }

    // ── Constructor vacío requerido por JPA ──────────────────────────────
    public ActividadPat() {}

    // ── Getters ──────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public Sesion getSesion() { return sesion; }
    public TipoActividad getTipoActividad() { return tipoActividad; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getObservaciones() { return observaciones; }
    public String getEvidencia() { return evidencia; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    // ── Setters ──────────────────────────────────────────────────────────
    public void setId(Integer id) { this.id = id; }
    public void setSesion(Sesion sesion) { this.sesion = sesion; }
    public void setTipoActividad(TipoActividad tipoActividad) { this.tipoActividad = tipoActividad; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setEvidencia(String evidencia) { this.evidencia = evidencia; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "ActividadPat{id=" + id + ", titulo='" + titulo + "', tipoActividad=" + tipoActividad + "}";
    }
}
```

---

## TAREA 1.3 — Repositorios JPA (`package com.itch.tutorias.repository`)

Crear un repositorio por entidad extendiendo `JpaRepository`. Agregar métodos de consulta relevantes usando la convención de nombres de Spring Data.

### `CarreraRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Integer> {
    Optional<Carrera> findByNombre(String nombre);
    Optional<Carrera> findByClave(String clave);
    List<Carrera> findByEstado(Carrera.EstadoCarrera estado);
    boolean existsByNombre(String nombre);
    boolean existsByClave(String clave);
}
```

### `UsuarioRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);
    List<Usuario> findByRolAndEstado(Usuario.Rol rol, Usuario.EstadoUsuario estado);
    List<Usuario> findByCarrera(Carrera carrera);
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByCorreo(String correo);
}
```

### `PeriodoSemestralRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.PeriodoSemestral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PeriodoSemestralRepository extends JpaRepository<PeriodoSemestral, Integer> {
    Optional<PeriodoSemestral> findByClave(String clave);
    Optional<PeriodoSemestral> findByEstado(PeriodoSemestral.EstadoPeriodo estado);
    List<PeriodoSemestral> findAllByOrderByFechaInicioDesc();
    boolean existsByEstado(PeriodoSemestral.EstadoPeriodo estado);
}
```

### `AsignacionRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    List<Asignacion> findByPeriodo(PeriodoSemestral periodo);
    List<Asignacion> findByTutor(Usuario tutor);
    List<Asignacion> findByTutorAndPeriodo(Usuario tutor, PeriodoSemestral periodo);
    boolean existsByTutorAndGrupoAndHoraHorarioAndPeriodo(
        Usuario tutor, String grupo, java.time.LocalTime horaHorario, PeriodoSemestral periodo);
}
```

### `AsignacionTutoradoRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionTutoradoRepository extends JpaRepository<AsignacionTutorado, Integer> {
    List<AsignacionTutorado> findByAsignacion(Asignacion asignacion);
    List<AsignacionTutorado> findByTutorado(Usuario tutorado);
    Optional<AsignacionTutorado> findByAsignacionAndTutorado(Asignacion asignacion, Usuario tutorado);
    boolean existsByAsignacionAndTutorado(Asignacion asignacion, Usuario tutorado);
}
```

### `SesionRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {
    List<Sesion> findByAsignacionOrderByFechaDesc(Asignacion asignacion);
    Optional<Sesion> findByAsignacionAndFecha(Asignacion asignacion, LocalDate fecha);
    boolean existsByAsignacionAndFecha(Asignacion asignacion, LocalDate fecha);
    long countByAsignacion(Asignacion asignacion);
}
```

### `RegistroAsistenciaRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Integer> {
    List<RegistroAsistencia> findBySesion(Sesion sesion);
    List<RegistroAsistencia> findByTutorado(Usuario tutorado);
    Optional<RegistroAsistencia> findBySesionAndTutorado(Sesion sesion, Usuario tutorado);
    boolean existsBySesionAndTutorado(Sesion sesion, Usuario tutorado);

    /**
     * Cuenta registros con un estado determinado para un tutorado y una asignación específica.
     * Se usa para calcular el porcentaje de asistencia.
     */
    @Query("SELECT COUNT(ra) FROM RegistroAsistencia ra " +
           "JOIN ra.sesion s " +
           "WHERE s.asignacion.id = :asignacionId " +
           "AND ra.tutorado.id = :tutoradoId " +
           "AND ra.estadoAsistencia = :estado")
    long countByAsignacionIdAndTutoradoIdAndEstado(
        @Param("asignacionId") Integer asignacionId,
        @Param("tutoradoId") Integer tutoradoId,
        @Param("estado") RegistroAsistencia.EstadoAsistencia estado);
}
```

### `ActividadPatRepository.java`
```java
package com.itch.tutorias.repository;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ActividadPatRepository extends JpaRepository<ActividadPat, Integer> {
    List<ActividadPat> findBySesionOrderByFechaRegistroAsc(Sesion sesion);

    @Query("SELECT ap FROM ActividadPat ap " +
           "JOIN ap.sesion s " +
           "WHERE s.asignacion.tutor.id = :tutorId " +
           "ORDER BY s.fecha ASC")
    List<ActividadPat> findByTutorId(@Param("tutorId") Integer tutorId);

    @Query("SELECT ap FROM ActividadPat ap " +
           "JOIN ap.sesion s " +
           "WHERE s.fecha BETWEEN :inicio AND :fin")
    List<ActividadPat> findByFechaSesionBetween(
        @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
```

---

## TAREA 1.4 — Interfaces de Servicios (`package com.itch.tutorias.service`)

### `ICarrera.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.Carrera;
import java.util.List;

public interface ICarrera {
    List<Carrera> buscarTodas();
    List<Carrera> buscarActivas();
    Carrera buscarPorId(Integer id);
    Carrera guardar(Carrera carrera);
    void eliminar(Integer id);
    boolean existeNombre(String nombre);
    boolean existeClave(String clave);
}
```

### `IUsuario.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuario {
    List<Usuario> buscarTodos();
    List<Usuario> buscarPorRol(Usuario.Rol rol);
    List<Usuario> buscarPorRolYEstado(Usuario.Rol rol, Usuario.EstadoUsuario estado);
    Usuario buscarPorId(Integer id);
    Optional<Usuario> buscarPorNumeroIdentificacion(String numeroIdentificacion);
    Optional<Usuario> buscarPorCorreo(String correo);
    Usuario guardar(Usuario usuario);
    void desactivar(Integer id);
    boolean existeNumeroIdentificacion(String numeroId);
    boolean existeCorreo(String correo);
    void cambiarContrasena(Integer id, String nuevaContrasena);
}
```

### `IPeriodoSemestral.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.PeriodoSemestral;
import java.util.List;
import java.util.Optional;

public interface IPeriodoSemestral {
    List<PeriodoSemestral> buscarTodos();
    PeriodoSemestral buscarPorId(Integer id);
    Optional<PeriodoSemestral> buscarActivo();
    PeriodoSemestral guardar(PeriodoSemestral periodo);
    void cerrar(Integer id);
    boolean existePeriodoActivo();
}
```

### `IAsignacion.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Usuario;
import java.util.List;

public interface IAsignacion {
    List<Asignacion> buscarTodas();
    List<Asignacion> buscarPorPeriodo(PeriodoSemestral periodo);
    List<Asignacion> buscarPorTutor(Usuario tutor);
    Asignacion buscarPorId(Integer id);
    Asignacion guardar(Asignacion asignacion);
    void eliminar(Integer id);
    boolean existeDuplicado(Usuario tutor, String grupo, java.time.LocalTime hora, PeriodoSemestral periodo);
}
```

### `IAsignacionTutorado.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.Usuario;
import java.util.List;

public interface IAsignacionTutorado {
    List<AsignacionTutorado> buscarPorAsignacion(Asignacion asignacion);
    List<AsignacionTutorado> buscarPorTutorado(Usuario tutorado);
    AsignacionTutorado guardar(AsignacionTutorado at);
    void eliminar(Integer id);
    boolean existeRelacion(Asignacion asignacion, Usuario tutorado);
}
```

### `ISesion.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Sesion;
import java.time.LocalDate;
import java.util.List;

public interface ISesion {
    List<Sesion> buscarPorAsignacion(Asignacion asignacion);
    Sesion buscarPorId(Integer id);
    Sesion guardar(Sesion sesion);
    void eliminar(Integer id);
    boolean existeSesionEnFecha(Asignacion asignacion, LocalDate fecha);
    long contarSesionesPorAsignacion(Asignacion asignacion);
}
```

### `IRegistroAsistencia.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Usuario;
import java.util.List;

public interface IRegistroAsistencia {
    List<RegistroAsistencia> buscarPorSesion(Sesion sesion);
    List<RegistroAsistencia> buscarPorTutorado(Usuario tutorado);
    RegistroAsistencia guardar(RegistroAsistencia registro);
    boolean existeRegistro(Sesion sesion, Usuario tutorado);
    double calcularPorcentajeAsistencia(Integer asignacionId, Integer tutoradoId);
}
```

### `IActividadPat.java`
```java
package com.itch.tutorias.service;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import java.time.LocalDate;
import java.util.List;

public interface IActividadPat {
    List<ActividadPat> buscarPorSesion(Sesion sesion);
    List<ActividadPat> buscarPorTutor(Integer tutorId);
    List<ActividadPat> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
    ActividadPat buscarPorId(Integer id);
    ActividadPat guardar(ActividadPat actividad);
    void eliminar(Integer id);
}
```

### `IServicioAlmacenamiento.java`
```java
package com.itch.tutorias.service;

import org.springframework.web.multipart.MultipartFile;

public interface IServicioAlmacenamiento {
    /**
     * Guarda el archivo en C:/Evidencias/{subcarpeta}/ y retorna el nombre único generado.
     * @param archivo  archivo multipart recibido del formulario
     * @param subcarpeta  nombre de subcarpeta: "usuarios", "sesiones", "actividades"
     * @return nombre del archivo guardado (UUID + extensión original)
     */
    String guardar(MultipartFile archivo, String subcarpeta);

    /**
     * Elimina el archivo indicado de la subcarpeta.
     */
    void eliminar(String nombreArchivo, String subcarpeta);
}
```

---

## TAREA 1.5 — Implementaciones de Servicios (`package com.itch.tutorias.service.implementjpa`)

### `ServicioAlmacenamientoImpl.java`
```java
package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.service.IServicioAlmacenamiento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ServicioAlmacenamientoImpl implements IServicioAlmacenamiento {

    @Value("${storage.location}")
    private String storageLocation;

    @Override
    public String guardar(MultipartFile archivo, String subcarpeta) {
        try {
            Path dirDestino = Paths.get(storageLocation, subcarpeta);
            Files.createDirectories(dirDestino);

            String extension = "";
            String nombreOriginal = archivo.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreUnico = UUID.randomUUID().toString() + extension;
            Path destino = dirDestino.resolve(nombreUnico);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nombreUnico;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String nombreArchivo, String subcarpeta) {
        try {
            Path archivo = Paths.get(storageLocation, subcarpeta, nombreArchivo);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo: " + e.getMessage(), e);
        }
    }
}
```

### `CarreraServiceImpl.java`
```java
package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.repository.CarreraRepository;
import com.itch.tutorias.service.ICarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarreraServiceImpl implements ICarrera {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<Carrera> buscarTodas() {
        return carreraRepository.findAll();
    }

    @Override
    public List<Carrera> buscarActivas() {
        return carreraRepository.findByEstado(Carrera.EstadoCarrera.activa);
    }

    @Override
    public Carrera buscarPorId(Integer id) {
        return carreraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Carrera no encontrada con id: " + id));
    }

    @Override
    public Carrera guardar(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Override
    public void eliminar(Integer id) {
        carreraRepository.deleteById(id);
    }

    @Override
    public boolean existeNombre(String nombre) {
        return carreraRepository.existsByNombre(nombre);
    }

    @Override
    public boolean existeClave(String clave) {
        return carreraRepository.existsByClave(clave);
    }
}
```

### `PeriodoSemestralServiceImpl.java`
```java
package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.repository.PeriodoSemestralRepository;
import com.itch.tutorias.service.IPeriodoSemestral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PeriodoSemestralServiceImpl implements IPeriodoSemestral {

    @Autowired
    private PeriodoSemestralRepository periodoRepository;

    @Override
    public List<PeriodoSemestral> buscarTodos() {
        return periodoRepository.findAllByOrderByFechaInicioDesc();
    }

    @Override
    public PeriodoSemestral buscarPorId(Integer id) {
        return periodoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Periodo no encontrado con id: " + id));
    }

    @Override
    public Optional<PeriodoSemestral> buscarActivo() {
        return periodoRepository.findByEstado(PeriodoSemestral.EstadoPeriodo.activo);
    }

    @Override
    public PeriodoSemestral guardar(PeriodoSemestral periodo) {
        // RF-16: no puede haber más de un periodo activo
        if (periodo.getEstado() == PeriodoSemestral.EstadoPeriodo.activo
                && periodo.getId() == null
                && periodoRepository.existsByEstado(PeriodoSemestral.EstadoPeriodo.activo)) {
            throw new RuntimeException("Ya existe un periodo semestral activo. Ciérrelo antes de crear uno nuevo.");
        }
        return periodoRepository.save(periodo);
    }

    @Override
    public void cerrar(Integer id) {
        PeriodoSemestral periodo = buscarPorId(id);
        periodo.setEstado(PeriodoSemestral.EstadoPeriodo.cerrado);
        periodoRepository.save(periodo);
    }

    @Override
    public boolean existePeriodoActivo() {
        return periodoRepository.existsByEstado(PeriodoSemestral.EstadoPeriodo.activo);
    }
}
```

> **Nota para el agente:** Implementar de manera similar `UsuarioServiceImpl`, `AsignacionServiceImpl`, `AsignacionTutoradoServiceImpl`, `SesionServiceImpl`, `RegistroAsistenciaServiceImpl` y `ActividadPatServiceImpl` siguiendo el mismo patrón: inyectar el repositorio correspondiente, implementar todos los métodos de la interfaz, lanzar `RuntimeException` con mensaje descriptivo cuando no se encuentra un registro y aplicar las validaciones de negocio indicadas en los requisitos.

---

## TAREA 1.6 — Configuración de Almacenamiento (`package com.itch.tutorias.config`)

### `StorageConfig.java`
```java
package com.itch.tutorias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Expone los archivos guardados en C:/Evidencias/ bajo la URL /uploads/{subcarpeta}/**
 * Ejemplo: /uploads/sesiones/uuid-archivo.jpg  →  C:/Evidencias/sesiones/uuid-archivo.jpg
 */
@Configuration
public class StorageConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Carpeta de usuarios (fotos de perfil)
        registry.addResourceHandler("/uploads/usuarios/**")
                .addResourceLocations("file:///" + storageLocation + "/usuarios/");

        // Carpeta de evidencias de sesiones
        registry.addResourceHandler("/uploads/sesiones/**")
                .addResourceLocations("file:///" + storageLocation + "/sesiones/");

        // Carpeta de evidencias de actividades PAT
        registry.addResourceHandler("/uploads/actividades/**")
                .addResourceLocations("file:///" + storageLocation + "/actividades/");
    }
}
```

---

## TAREA 1.7 — Fragmentos Thymeleaf (`templates/fragments/fragment.html`)

Crear el archivo `fragment.html` con tres fragmentos reutilizables:

1. **`head`** — Incluye Bootstrap 5.3 CSS, Bootstrap Icons y el CSS personalizado `estilos.css`.
2. **`navbar`** — Barra de navegación responsiva con logo del ITCH, enlaces según rol (`th:if`) y botón de cerrar sesión. Muestra nombre completo y rol del usuario autenticado con `sec:authentication`.
3. **`footer`** — Pie de página con el nombre del sistema y año.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<!-- ===================== HEAD ===================== -->
<head th:fragment="head">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sistema de Tutorías · ITCH</title>

    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">
    <!-- Estilos propios -->
    <link th:href="@{/css/estilos.css}" rel="stylesheet">
</head>

<!-- ===================== NAVBAR ===================== -->
<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark mi-navbar">
    <div class="container-fluid">
        <a class="navbar-brand d-flex align-items-center gap-2" th:href="@{/dashboard}">
            <img th:src="@{/images/logo-itch.png}" alt="ITCH" height="36">
            <span class="fw-semibold">Tutorías ITCH</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">

                <!-- Enlace solo para ADMIN (DDA) -->
                <li class="nav-item" sec:authorize="hasRole('ROLE_admin')">
                    <a class="nav-link" th:href="@{/usuario/usuarios}">
                        <i class="bi bi-people"></i> Usuarios
                    </a>
                </li>
                <li class="nav-item" sec:authorize="hasRole('ROLE_admin')">
                    <a class="nav-link" th:href="@{/carrera/carreras}">
                        <i class="bi bi-mortarboard"></i> Carreras
                    </a>
                </li>
                <li class="nav-item" sec:authorize="hasRole('ROLE_admin')">
                    <a class="nav-link" th:href="@{/periodo/periodos}">
                        <i class="bi bi-calendar3"></i> Periodos
                    </a>
                </li>
                <li class="nav-item" sec:authorize="hasRole('ROLE_admin')">
                    <a class="nav-link" th:href="@{/asignacion/asignaciones}">
                        <i class="bi bi-diagram-3"></i> Asignaciones
                    </a>
                </li>

                <!-- Enlace para TUTOR -->
                <li class="nav-item" sec:authorize="hasRole('ROLE_tutor')">
                    <a class="nav-link" th:href="@{/sesion/mis-sesiones}">
                        <i class="bi bi-journal-check"></i> Mis Sesiones
                    </a>
                </li>

                <!-- Enlace para TUTORADO -->
                <li class="nav-item" sec:authorize="hasRole('ROLE_tutorado')">
                    <a class="nav-link" th:href="@{/tutorado/mi-asignacion}">
                        <i class="bi bi-person-check"></i> Mi Asignación
                    </a>
                </li>

                <!-- Búsquedas — todos los roles autenticados -->
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/busqueda}">
                        <i class="bi bi-search"></i> Búsquedas
                    </a>
                </li>
            </ul>

            <!-- Usuario autenticado + cerrar sesión -->
            <div class="d-flex align-items-center gap-3">
                <span class="text-white-50 small" sec:authentication="name"></span>
                <a th:href="@{/logout}" class="btn btn-sm btn-outline-light">
                    <i class="bi bi-box-arrow-right"></i> Salir
                </a>
            </div>
        </div>
    </div>
</nav>

<!-- ===================== FOOTER ===================== -->
<footer th:fragment="footer" class="footer mt-auto py-3 mi-footer">
    <div class="container text-center">
        <small class="text-muted">Sistema Web de Tutorías · Instituto Tecnológico de Chilpancingo · 2025</small>
    </div>
</footer>

</html>
```

---

## TAREA 1.8 — CSS personalizado (`static/css/estilos.css`)

Crear el archivo `estilos.css` con las variables de colores institucionales del ITCH (azul marino y rojo) y clases de utilidad usadas en las vistas. Las clases deben seguir la convención del ejemplo de código dado:

```css
:root {
    --color-primario: #003366;   /* Azul marino ITCH */
    --color-acento: #c0392b;     /* Rojo institucional */
    --color-claro: #f4f6fb;
    --color-texto: #2c3e50;
}

body {
    background-color: var(--color-claro);
    color: var(--color-texto);
    font-family: 'Segoe UI', sans-serif;
}

/* Navbar */
.mi-navbar {
    background-color: var(--color-primario);
}

/* Footer */
.mi-footer {
    background-color: var(--color-primario);
}

/* Títulos de sección */
.mi-titulo {
    color: var(--color-primario);
    font-weight: 700;
    border-left: 4px solid var(--color-acento);
    padding-left: 10px;
}

/* Encabezado de tabla */
.mi-etiqueta {
    background-color: var(--color-primario);
    color: #fff;
    font-size: 0.85rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

/* Tabla con bordes */
.bordes-fuertes {
    border: 2px solid var(--color-primario);
}
.bordes-fuertes td, .bordes-fuertes th {
    border: 1px solid #b0c4de;
}

/* Botón de encabezado (Agregar) */
.btn-encabezado {
    background-color: var(--color-acento);
    color: #fff;
    border: none;
    font-weight: 600;
    border-radius: 6px;
    padding: 0.4rem 1rem;
}
.btn-encabezado:hover {
    background-color: #96281b;
    color: #fff;
}

/* Botones de CRUD en tabla */
.btn-crud {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 6px;
    font-size: 0.95rem;
    text-decoration: none;
    transition: opacity 0.2s;
}
.btn-ver     { background-color: #17a2b8; color: #fff; }
.btn-editar  { background-color: #ffc107; color: #212529; }
.btn-eliminar{ background-color: #dc3545; color: #fff; }
.btn-crud:hover { opacity: 0.8; color: inherit; }

/* Miniatura de foto en tabla */
.thumb-foto {
    width: 48px;
    height: 48px;
    object-fit: cover;
    border-radius: 50%;
    border: 2px solid var(--color-primario);
}

/* Badge de estado de asistencia */
.badge-presente   { background-color: #28a745; }
.badge-ausente    { background-color: #dc3545; }
.badge-justificado{ background-color: #fd7e14; }
```

---

## TAREA 1.9 — CRUD de Carreras

Implementar CRUD completo de la entidad `Carrera` siguiendo el patrón del ejemplo provisto.

### `CarreraController.java`
```java
package com.itch.tutorias.controller;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.service.ICarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrera")
public class CarreraController {

    @Autowired
    private ICarrera carreraService;

    @GetMapping("/carreras")
    public String listar(Model model) {
        model.addAttribute("carreras", carreraService.buscarTodas());
        return "carrera/listaCarreras";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@ModelAttribute Carrera carrera) {
        return "carrera/formCarrera";
    }

    @PostMapping("/guardar")
    public String guardar(Carrera carrera, RedirectAttributes ra) {
        carreraService.guardar(carrera);
        ra.addFlashAttribute("msg", "Carrera guardada correctamente");
        return "redirect:/carrera/carreras";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        model.addAttribute("carrera", carreraService.buscarPorId(id));
        return "carrera/detalleCarrera";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("carrera", carreraService.buscarPorId(id));
        return "carrera/formCarrera";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        carreraService.eliminar(id);
        ra.addFlashAttribute("msg", "Carrera eliminada correctamente");
        return "redirect:/carrera/carreras";
    }
}
```

### Vistas de Carrera

**`templates/carrera/listaCarreras.html`** — Tabla con columnas: ID, Nombre, Clave, Estado, Acciones (ver/editar/eliminar). Seguir exactamente el patrón de ejemplo HTML dado con `th:each`, `th:text`, `th:href`, `th:if` para mensaje flash y fragmentos `head`, `navbar`, `footer`.

**`templates/carrera/formCarrera.html`** — Formulario Bootstrap con campos: nombre, clave, estado (select con opciones `activa`/`inactiva`). Botón guardar y enlace cancelar. Debe funcionar tanto para crear como para editar (si `carrera.id` no es nulo, la acción es editar).

**`templates/carrera/detalleCarrera.html`** — Vista de detalle con todos los campos de la carrera en un card Bootstrap. Botón editar y botón volver al listado.

---

## TAREA 1.10 — CRUD de Periodos Semestrales

Implementar CRUD completo de la entidad `PeriodoSemestral`.

### `PeriodoController.java`
Patrón igual al de `CarreraController` con rutas `/periodo/*`. Validar RF-16: al guardar un periodo con estado `activo`, llamar a `periodoService.existePeriodoActivo()` y si ya existe, agregar el mensaje de error al modelo y regresar al formulario sin guardar.

### Vistas de Periodo
**`templates/periodo/listaPeriodos.html`** — Tabla: ID, Clave, Fecha Inicio, Fecha Fin, Estado (badge verde/gris), Acciones. Botón "Cerrar Periodo" que llama a `/periodo/cerrar/{id}` solo si el periodo está activo.

**`templates/periodo/formPeriodo.html`** — Campos: clave, fecha_inicio (`type="date"`), fecha_fin (`type="date"`), estado (select). Validación de fechas en el frontend.

**`templates/periodo/detallePeriodo.html`** — Detalle del periodo con card y todos los campos.

---

## Resultado esperado al finalizar Sprint 1

Al ejecutar el proyecto Spring Boot con `spring.jpa.hibernate.ddl-auto=update`, MySQL debe crear automáticamente las 8 tablas (`carrera`, `usuario`, `periodo_semestral`, `asignacion`, `asignacion_tutorado`, `sesion`, `registro_asistencia`, `actividad_pat`) con todas sus relaciones de clave foránea.

El sistema debe:
- Arrancar sin errores en `http://localhost:8080`
- Tener CRUD funcional de Carreras (`/carrera/carreras`)
- Tener CRUD funcional de Periodos (`/periodo/periodos`)
- Servir archivos desde `C:/Evidencias/` en `/uploads/**`
- Tener todos los repositorios, servicios e interfaces listos para los sprints siguientes

---

# SPRINT 2 — Autenticación, Sesión y Control de Acceso por Rol

**Objetivo:** Implementar el módulo MO_01 completo: login con Spring Security, redirección por rol, control de acceso a rutas, visualización del usuario en navbar y cierre de sesión (RF-01 a RF-06).

---

## TAREA 2.1 — Configuración de Spring Security (`SecurityConfig.java`)

```java
package com.itch.tutorias.config;

import com.itch.tutorias.service.implementjpa.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y login — públicos
                .requestMatchers("/css/**", "/images/**", "/uploads/**", "/login", "/error").permitAll()
                // Rutas solo para admin (DDA)
                .requestMatchers("/usuario/**", "/carrera/**", "/periodo/**",
                                 "/asignacion/**").hasRole("admin")
                // Rutas para tutor
                .requestMatchers("/sesion/**", "/asistencia/**", "/pat/**").hasRole("tutor")
                // Rutas para tutorado
                .requestMatchers("/tutorado/**").hasRole("tutorado")
                // Búsquedas y dashboard — cualquier autenticado
                .requestMatchers("/busqueda/**", "/dashboard/**", "/perfil/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(new CustomLoginSuccessHandler())  // redirige según rol
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );
        return http.build();
    }
}
```

### `UserDetailsServiceImpl.java` (en `service/implementjpa`)
Implementar `UserDetailsService` de Spring Security. Buscar el usuario por `numeroIdentificacion` usando `UsuarioRepository`. Mapear el `Rol` del usuario al `GrantedAuthority` de Spring Security con el prefijo `ROLE_` (ej.: `ROLE_admin`, `ROLE_tutor`, `ROLE_tutorado`). Si el usuario no existe o está inactivo, lanzar `UsernameNotFoundException`.

### `CustomLoginSuccessHandler.java` (en `config`)
Implementar `AuthenticationSuccessHandler`. Según el rol del usuario autenticado:
- `ROLE_admin` → redirigir a `/dashboard/admin`
- `ROLE_tutor` → redirigir a `/dashboard/tutor`
- `ROLE_tutorado` → redirigir a `/dashboard/tutorado`

---

## TAREA 2.2 — Vista de Login

**`templates/auth/login.html`**

Vista de inicio de sesión Bootstrap con:
- Formulario `action="/login" method="post"` con token CSRF de Thymeleaf: `th:action="@{/login}"`
- Campo "Número de empleado o número de control" (name=`username`)
- Campo contraseña (name=`password`, type=`password`)
- Mensaje de error si `param.error` (credenciales incorrectas): `th:if="${param.error}"`
- Mensaje de sesión cerrada si `param.logout`
- Logo del ITCH centrado
- **No** incluir navbar ni footer (página pública)
- RNF-02: validación HTML5 `minlength="8"` en el campo de contraseña con mensaje de retroalimentación

---

## TAREA 2.3 — Dashboards por Rol

### `DashboardController.java`
```java
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/admin")
    public String dashboardAdmin(Model model) {
        // Pasar contadores al modelo: total usuarios, carreras, asignaciones activas, sesiones del periodo activo
        return "dashboard/dashboardAdmin";
    }

    @GetMapping("/tutor")
    public String dashboardTutor(Model model, Principal principal) {
        // Pasar al modelo: asignación activa del tutor, total tutorados, sesiones registradas, próxima sesión
        return "dashboard/dashboardTutor";
    }

    @GetMapping("/tutorado")
    public String dashboardTutorado(Model model, Principal principal) {
        // Pasar al modelo: tutor asignado, porcentaje de asistencia, últimas sesiones
        return "dashboard/dashboardTutorado";
    }
}
```

### Vistas de Dashboard
Cada dashboard debe mostrar cards informativos Bootstrap con íconos Bootstrap Icons relevantes. Usar clases CSS del `estilos.css` definido. Incluir fragmentos `head`, `navbar` y `footer`.

---

## TAREA 2.4 — Vista de Cambio de Contraseña (RF-11)

Agregar en `UsuarioController` el endpoint `GET /perfil/cambiar-contrasena` y `POST /perfil/cambiar-contrasena`. La vista `templates/usuario/perfil.html` debe:
- Mostrar el nombre completo y rol del usuario autenticado
- Formulario para cambiar contraseña con tres campos: contraseña actual, nueva contraseña, confirmar nueva contraseña
- Validar en el servicio que la contraseña actual sea correcta (usando `BCryptPasswordEncoder.matches`)
- Validar que nueva contraseña y confirmación coincidan
- Mostrar mensajes de éxito o error con alertas Bootstrap

---

## Resultado esperado al finalizar Sprint 2

- Login funcional en `http://localhost:8080/login`
- Redirección automática al dashboard según rol
- Navbar muestra nombre y rol del usuario autenticado
- Botón "Salir" funcional en todas las vistas
- Rutas protegidas por rol (sin login redirige a `/login`)
- El usuario `admin@itch.edu.mx` / `Admin2025` puede iniciar sesión

---

# SPRINT 3 — Gestión de Usuarios (MO_02)

**Objetivo:** CRUD completo de usuarios por parte del DDA (RF-07 a RF-14), incluyendo foto de perfil con carga de archivo.

---

## TAREA 3.1 — `UsuarioController.java`

Rutas CRUD completas bajo `/usuario/`:

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/usuario/usuarios` | GET | Listado con filtros por rol, carrera y estado |
| `/usuario/nuevo` | GET | Formulario nuevo usuario |
| `/usuario/guardar` | POST | Guardar (crear o editar) usuario con foto de perfil |
| `/usuario/ver/{id}` | GET | Detalle del usuario |
| `/usuario/editar/{id}` | GET | Formulario editar |
| `/usuario/desactivar/{id}` | GET | Desactivar cuenta (RF-10) |

### Lógica de guardado:
1. Si `archivoFoto` no está vacío, llamar `servicioAlmacenamiento.guardar(archivo, "usuarios")` y asignar el nombre a `usuario.fotoPerfil`.
2. Validar duplicados de `numeroIdentificacion` y `correo` antes de guardar (RF-12).
3. Al **crear** un usuario nuevo, generar contraseña temporal codificada con BCrypt y marcar `debeCambiarContrasena = true`.
4. La contraseña temporal sugerida es el mismo `numeroIdentificacion` del usuario.

### Filtros en el listado (RF-08):
El listado debe aceptar parámetros de consulta `?rol=&carrera=&estado=` para filtrar. Pasar las listas de carreras, roles y estados al modelo para poblar los selects de filtro.

---

## TAREA 3.2 — Vistas de Usuario

**`templates/usuario/listaUsuarios.html`**
- Barra de filtros (rol, carrera, estado) con botón "Filtrar"
- Tabla: foto de perfil (miniatura), nombre, número de identificación, correo, rol (badge de color), carrera, estado, acciones
- Foto con fallback a `sin-imagen.png` igual al ejemplo provisto

**`templates/usuario/formUsuario.html`**
- Campos: nombre completo, número de identificación, correo, rol (select), carrera (select, poblado desde BD), estado (select), foto de perfil (input file JPG/PNG)
- Para editar, mostrar foto actual si existe
- Validación HTML5 en campos requeridos

**`templates/usuario/detalleUsuario.html`**
- Card con foto grande, todos los datos del usuario
- Botones: editar, desactivar (con confirmación JS)

---

## Resultado esperado al finalizar Sprint 3

- DDA puede crear, editar, ver y desactivar usuarios
- Fotos de perfil se guardan en `C:/Evidencias/usuarios/` y se sirven en `/uploads/usuarios/`
- Validación de duplicados funcional con mensajes de error en vista
- Listado con filtros operativos

---

# SPRINT 4 — Gestión de Asignaciones (MO_03)

**Objetivo:** CRUD de asignaciones y gestión de tutorados por asignación (RF-15 a RF-24).

---

## TAREA 4.1 — `AsignacionController.java`

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/asignacion/asignaciones` | GET | Listado de todas las asignaciones |
| `/asignacion/nuevo` | GET | Formulario nueva asignación |
| `/asignacion/guardar` | POST | Guardar asignación (validar RF-19: sin duplicado tutor+grupo+hora+periodo) |
| `/asignacion/ver/{id}` | GET | Detalle de la asignación |
| `/asignacion/editar/{id}` | GET | Formulario editar (solo periodo activo — RF-24) |
| `/asignacion/eliminar/{id}` | GET | Eliminar asignación |
| `/asignacion/{id}/agregar-tutorado` | POST | Agregar tutorado a la asignación (RF-20) |
| `/asignacion/{id}/quitar-tutorado/{tutoradoId}` | GET | Quitar tutorado (RF-21: solo si no tiene asistencias) |

### Lógica en el controlador de detalle (RF-18):
Al cargar la vista de detalle de una asignación, calcular para cada tutorado de la lista su porcentaje de asistencia usando `registroAsistenciaService.calcularPorcentajeAsistencia(asignacionId, tutoradoId)`.

---

## TAREA 4.2 — Vistas de Asignación

**`templates/asignacion/listaAsignaciones.html`**
- Filtro por periodo (select con todos los periodos)
- Tabla: ID, Tutor, Carrera, Grupo, Aula, Día/Hora, Periodo, Acciones

**`templates/asignacion/formAsignacion.html`**
- Selects poblados: tutor (usuarios con rol tutor activos), carrera, periodo activo
- Campos: grupo (text), aula (text), día horario (select: Lunes…Viernes), hora horario (time)

**`templates/asignacion/detalleAsignacion.html`**
Vista más completa. Dividida en dos secciones:
1. **Información de la asignación:** card con tutor, carrera, periodo, grupo, aula, horario
2. **Lista de tutorados:** tabla con nombre, matrícula, porcentaje de asistencia (barra de progreso Bootstrap). Botón "Quitar" por cada tutorado. Formulario para agregar nuevo tutorado (campo número de control, botón buscar y agregar).
3. **Lista de sesiones registradas:** tabla con fecha, hora inicio/fin, evidencia (icono si existe), enlace "Ver detalle"

---

## TAREA 4.3 — Vista del Tutor (RF-22) y Vista del Tutorado (RF-23)

**Tutor:** En el dashboard del tutor, mostrar directamente su asignación activa con la lista de tutorados.

**Tutorado:** En `/tutorado/mi-asignacion`, mostrar nombre del tutor, carrera, grupo, aula, horario, y el porcentaje de asistencia propio del periodo activo.

---

## Resultado esperado al finalizar Sprint 4

- DDA puede crear, editar, ver y gestionar asignaciones
- Se valida que no haya duplicado tutor+grupo+hora+periodo
- DDA puede agregar y quitar tutorados de una asignación
- Tutor ve su lista de tutorados
- Tutorado ve su asignación activa con su porcentaje

---

# SPRINT 5 — Registro de Sesiones y Asistencia (MO_04)

**Objetivo:** El tutor puede registrar sesiones de tutoría con evidencia y marcar la asistencia de cada tutorado (RF-25 a RF-33).

---

## TAREA 5.1 — `SesionController.java`

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/sesion/mis-sesiones` | GET | Lista de sesiones del tutor autenticado en el periodo activo |
| `/sesion/nueva` | GET | Formulario nueva sesión |
| `/sesion/guardar` | POST | Guardar sesión con evidencia (RF-25, RF-26, RF-32) |
| `/sesion/ver/{id}` | GET | Detalle de sesión (RF-33) |
| `/sesion/editar/{id}` | GET | Editar sesión |
| `/sesion/eliminar/{id}` | GET | Eliminar sesión |

### Reglas de negocio:
- RF-26: al guardar, verificar que `sesion.fecha` no sea posterior a `periodo.fechaFin`. Si sí, agregar error al modelo.
- Si hay archivo de evidencia, guardarlo en `C:/Evidencias/sesiones/` y asignar el nombre a `sesion.evidencia`.
- La sesión se asocia automáticamente a la asignación activa del tutor.

---

## TAREA 5.2 — `AsistenciaController.java`

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/asistencia/registrar/{sesionId}` | GET | Formulario de asistencia para la sesión indicada |
| `/asistencia/guardar/{sesionId}` | POST | Guardar todos los registros de asistencia de la sesión |

### Lógica:
- El formulario carga la lista completa de tutorados de la asignación de la sesión.
- Por cada tutorado se muestra un radio group: `presente` / `ausente` / `justificado` (RF-27).
- Al guardar, verificar RF-29: si ya existe registro para ese tutorado en esa sesión, actualizar en vez de insertar.
- RF-28: después de guardar, el porcentaje de asistencia se recalcula automáticamente en la vista de detalle de asignación.

---

## TAREA 5.3 — Vistas de Sesiones y Asistencia

**`templates/sesion/listaSesiones.html`**
Tabla: fecha, hora inicio, hora fin, evidencia (ícono de archivo si existe), total tutorados, acciones (ver/editar/registrar asistencia/eliminar).

**`templates/sesion/formSesion.html`**
Campos: fecha (date), hora inicio (time), hora fin (time), input file para evidencia (JPG, PNG, PDF hasta 5 MB). Si se edita, mostrar evidencia actual con enlace.

**`templates/sesion/detalleSesion.html`** (RF-33)
Dividido en secciones:
1. Datos de la sesión (fecha, horario, evidencia con visualización/enlace)
2. Registro de asistencia: tabla con nombre del tutorado, estado de asistencia (badge de color), porcentaje individual acumulado
3. Actividades PAT registradas en esta sesión (lista con tipo, título, descripción)
4. Botones: registrar/editar asistencia, agregar actividad PAT

**`templates/asistencia/registrarAsistencia.html`**
Tabla con todos los tutorados. Por cada fila, un conjunto de radio buttons Bootstrap (presente/ausente/justificado). Botón "Guardar Asistencia".

**Vista del Tutorado (RF-30):** En `/tutorado/mi-asistencia`, tabla con todas las sesiones del periodo activo, estado de asistencia en cada una, y barra de progreso con el porcentaje acumulado. El tutorado NO puede modificar ningún registro.

---

## Resultado esperado al finalizar Sprint 5

- Tutor puede registrar sesiones con fechas válidas dentro del periodo activo
- Tutor puede subir evidencias (imagen o PDF) asociadas a cada sesión
- Tutor puede marcar y actualizar asistencia de cada tutorado
- Porcentaje de asistencia se calcula y muestra en tiempo real
- Tutorado puede consultar su historial de asistencia

---

# SPRINT 6 — Actividades PAT y Búsquedas (MO_05)

**Objetivo:** El tutor registra actividades del Plan de Acción Tutorial por sesión. El DDA y otros roles pueden realizar búsquedas avanzadas (RF-34 a RF-39).

---

## TAREA 6.1 — `ActividadPatController.java`

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/pat/actividades` | GET | Lista de actividades del tutor autenticado en el periodo activo |
| `/pat/nueva/{sesionId}` | GET | Formulario nueva actividad para la sesión indicada |
| `/pat/guardar` | POST | Guardar actividad PAT |
| `/pat/editar/{id}` | GET | Editar actividad (solo del periodo activo — RF-35) |
| `/pat/eliminar/{id}` | GET | Eliminar con confirmación (RF-36) |

### Lógica de guardado:
- Si se adjunta evidencia, guardar en `C:/Evidencias/actividades/` con `servicioAlmacenamiento.guardar(archivo, "actividades")`.
- Verificar que la sesión a la que pertenece la actividad corresponda al periodo activo antes de permitir editar o eliminar.

---

## TAREA 6.2 — `BusquedaController.java`

| Ruta | Descripción | RF |
|------|-------------|-----|
| `GET /busqueda/tutores` | Busca tutores activos de un periodo (parámetro `?periodoId=`) | RF-37 |
| `GET /busqueda/tutorado` | Busca todas las asignaciones de un tutorado por número de control (`?numeroControl=`) | RF-38 |
| `GET /busqueda/pat` | Busca actividades PAT por rango de fechas, tipo, carrera y tutor | RF-39 |

---

## TAREA 6.3 — Vistas de Actividades PAT

**`templates/pat/listaActividades.html`**
Tabla ordenada cronológicamente: sesión (fecha), tipo de actividad (badge de color por tipo), título, descripción (truncada), evidencia (ícono). Acciones: editar/eliminar.

**`templates/pat/formActividad.html`**
Campos: tipo de actividad (select con opciones del Enum), título (text), descripción (textarea), observaciones (textarea), archivo de evidencia (file: JPG, PNG, PDF). El campo `sesionId` va como campo oculto.

---

## TAREA 6.4 — Vistas de Búsqueda

**`templates/busqueda/busquedaTutores.html`** (RF-37)
- Select de periodos semestrales + botón buscar
- Tabla resultante: nombre tutor, carrera, grupo, aula, día, hora

**`templates/busqueda/busquedaTutorado.html`** (RF-38)
- Campo de texto para número de control + botón buscar
- Tabla resultante: periodo, tutor, carrera, grupo, porcentaje de asistencia

**`templates/busqueda/busquedaPAT.html`** (RF-39)
- Filtros: fecha inicio (date), fecha fin (date), tipo de actividad (select), carrera (select), tutor (select dinámico)
- Tabla resultante: fecha sesión, tutor, carrera, tipo, título, descripción, evidencia (enlace si existe)

---

## Resultado esperado al finalizar Sprint 6

- Tutor puede registrar, editar y eliminar actividades PAT por sesión
- Tutor puede adjuntar evidencias a las actividades
- Las tres búsquedas avanzadas funcionan con los filtros indicados
- El DDA puede supervisar el cumplimiento del PAT en todas las carreras

---

# SPRINT 7 — Pulido, Validaciones y Datos Iniciales

**Objetivo:** Completar detalles de UX, validaciones faltantes, datos de prueba y asegurar que el sistema esté listo para entrega académica.

---

## TAREA 7.1 — Manejo de errores global

Crear `GlobalExceptionHandler.java` con `@ControllerAdvice` para:
- Capturar `RuntimeException` genérica y mostrar una vista de error amigable
- Crear `templates/error/error.html` con mensaje descriptivo y botón para regresar al inicio

## TAREA 7.2 — Vista 403 (Acceso denegado)

Crear `templates/error/403.html` con mensaje "No tienes permiso para acceder a esta sección" y enlace al dashboard correspondiente.

## TAREA 7.3 — Datos iniciales de prueba (`DataInitializer.java`)

Crear una clase con `@Component` que implemente `CommandLineRunner`. Al arrancar la aplicación:
1. Si no existe el usuario `ADMIN001`, insertar el administrador con contraseña `Admin2025` hasheada con BCrypt.
2. Insertar 3 carreras de ejemplo: `Ingeniería en Sistemas Computacionales`, `Ingeniería Industrial`, `Ingeniería Civil`.

## TAREA 7.4 — Validaciones de formularios

Revisar todos los formularios y agregar las siguientes mejoras:
- Atributos `required` en campos obligatorios
- Mensajes de error Bootstrap (`is-invalid` + `invalid-feedback`) para validaciones del lado servidor pasadas como atributos del modelo
- Confirmaciones JavaScript (`confirm(...)`) antes de eliminar o desactivar
- Límite de tamaño de archivos con mensajes de error en vista (5 MB para evidencias, 2 MB para fotos de perfil)

## TAREA 7.5 — Paginación (opcional / recomendada)

Si los listados de usuarios y asignaciones son extensos, implementar paginación usando `PagingAndSortingRepository` y `Pageable`. Mostrar controles de paginación Bootstrap en las tablas.

## TAREA 7.6 — Revisión de vistas del Tutorado

Verificar que el tutorado autenticado solo pueda:
- Ver su propia asignación activa (`/tutorado/mi-asignacion`) — RF-23
- Ver su propio historial de asistencia (`/tutorado/mi-asistencia`) — RF-30
- Cambiar su contraseña desde su perfil — RF-11
- NO pueda acceder a rutas de otros roles (Spring Security ya lo bloquea, pero verificar)

## TAREA 7.7 — Revisión final de estilos y responsive

- Verificar que todas las vistas sean responsivas con Bootstrap 5
- Que el navbar colapse correctamente en móvil
- Que las tablas tengan `table-responsive` para scroll horizontal en pantallas pequeñas

---

## Resultado esperado al finalizar Sprint 7 — Sistema completo

El sistema estará completamente funcional con:

| Módulo | Estado |
|--------|--------|
| MO_01 — Control de acceso | ✅ Login, roles, redirección, cierre de sesión |
| MO_02 — Gestión de usuarios | ✅ CRUD usuarios, fotos de perfil, carreras |
| MO_03 — Periodos y asignaciones | ✅ CRUD periodos, asignaciones, gestión de tutorados |
| MO_04 — Sesiones y asistencia | ✅ Registro de sesiones, asistencia, evidencias, porcentajes |
| MO_05 — PAT y búsquedas | ✅ Actividades PAT, evidencias, 3 búsquedas avanzadas |

---

## Notas Finales para el Agente

1. **Siempre** usar el fragmento `th:replace="~{fragments/fragment :: head}"` al inicio de cada vista y los fragmentos `navbar` y `footer` en el body.
2. **Todas** las rutas que involucren operaciones de escritura (POST) deben incluir el token CSRF de Thymeleaf: usar `th:action="@{/ruta}"` en el `<form>` (Thymeleaf lo inyecta automáticamente).
3. Los **selects dinámicos** (tutor, carrera, periodo) deben poblarse siempre desde el controlador pasando la lista al modelo.
4. El campo `Principal principal` en los controladores de tutor y tutorado permite obtener el username (número de identificación) del usuario autenticado para filtrar sus propios datos.
5. **Nunca** usar `spring.jpa.hibernate.ddl-auto=create` en producción. Usar `update` para el desarrollo académico.
6. Las **rutas de almacenamiento** (`C:/Evidencias/`) deben existir físicamente o el servicio las crea con `Files.createDirectories(...)`.
7. Para mostrar el **porcentaje de asistencia**, calcularlo así: `(sesionesPresentes + sesionesJustificadas) * 100 / totalSesiones`. Redondear a 2 decimales.
8. El sistema **no necesita API REST** ni frontend separado. Todo se resuelve con controladores Spring MVC que devuelven nombres de vistas Thymeleaf.
9. Revisar que el `pom.xml` incluya la dependencia `thymeleaf-extras-springsecurity6` para poder usar `sec:authorize` y `sec:authentication` en las vistas.
10. La contraseña del admin inicial en el SQL es BCrypt. Para generarla correctamente en `DataInitializer.java`, usar `passwordEncoder.encode("Admin2025")`.
