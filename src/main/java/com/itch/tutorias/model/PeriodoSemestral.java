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
    private EstatusPeriodo estatus = EstatusPeriodo.activo;

    public enum EstatusPeriodo {
        activo, cerrado
    }

    public PeriodoSemestral() {}

    //Getters
    public Integer getId() { return id; }
    public String getClave() { return clave; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public EstatusPeriodo getEstatus() { return estatus; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setClave(String clave) { this.clave = clave; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public void setEstatus(EstatusPeriodo estatus) { this.estatus = estatus; }

    @Override
    public String toString() {
        return "PeriodoSemestral{id=" + id + ", clave='" + clave + "', estatus=" + estatus + "}";
    }
}
