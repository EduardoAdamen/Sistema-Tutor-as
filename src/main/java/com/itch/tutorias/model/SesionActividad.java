package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_actividad",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_sesion_actividad",
            columnNames = {"sesion_id", "actividad_carrera_id"})
    })
public class SesionActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sesact_sesion"))
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sesact_actividad"))
    private ActividadPatCarrera actividadCarrera;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "evidencia", length = 255)
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public SesionActividad() {}

    //Getters
    public Integer getId() { return id; }
    public Sesion getSesion() { return sesion; }
    public ActividadPatCarrera getActividadCarrera() { return actividadCarrera; }
    public String getObservaciones() { return observaciones; }
    public String getEvidencia() { return evidencia; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setSesion(Sesion sesion) { this.sesion = sesion; }
    public void setActividadCarrera(ActividadPatCarrera actividadCarrera) { this.actividadCarrera = actividadCarrera; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setEvidencia(String evidencia) { this.evidencia = evidencia; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "SesionActividad{id=" + id + "}";
    }
}
