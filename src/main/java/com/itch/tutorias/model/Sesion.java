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

    //Relación ManyToOne hacia Asignacion, una asignación puede tener varias sesiones pero cada sesión pertenece a una sola asignación
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

    @Column(name = "horas_formativas")
    private Double horasFormativas;

    //Ruta del archivo de evidencia guardado en C:/Evidencias/sesiones/
    @Column(name = "evidencia", length = 255)
    private String evidencia;

    public Sesion() {}

    //Getters
    public Integer getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public Double getHorasFormativas() { return horasFormativas; }
    public String getEvidencia() { return evidencia; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setHorasFormativas(Double horasFormativas) { this.horasFormativas = horasFormativas; }
    public void setEvidencia(String evidencia) { this.evidencia = evidencia; }

    @Override
    public String toString() {
        return "Sesion{id=" + id + ", fecha=" + fecha + ", horaInicio=" + horaInicio + ", horasFormativas=" + horasFormativas + "}";
    }
}
