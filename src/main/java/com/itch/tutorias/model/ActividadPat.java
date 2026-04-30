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

    //Relación ManyToOne hacia Sesion, una actividad PAT pertenece a una sesión
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

    //Ruta relativa del archivo de evidencia guardado en C:/Evidencias/actividades_pat/
    @Column(name = "evidencia", length = 255)
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public enum TipoActividad {
        taller, asesoria_academica, sesion_informativa, otra
    }

 
    public ActividadPat() {}

    //Getters
    public Integer getId() { return id; }
    public Sesion getSesion() { return sesion; }
    public TipoActividad getTipoActividad() { return tipoActividad; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getObservaciones() { return observaciones; }
    public String getEvidencia() { return evidencia; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    //Setters
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
