package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividad_pat_general")
public class ActividadPatGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actividad", nullable = false)
    private TipoActividad tipoActividad;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public enum TipoActividad {
        taller, asesoria_academica, sesion_informativa, otra
    }

    public ActividadPatGeneral() {}

    //Getters
    public Integer getId() { return id; }
    public TipoActividad getTipoActividad() { return tipoActividad; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setTipoActividad(TipoActividad tipoActividad) { this.tipoActividad = tipoActividad; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "ActividadPatGeneral{id=" + id + ", titulo='" + titulo + "', tipoActividad=" + tipoActividad + "}";
    }
}
