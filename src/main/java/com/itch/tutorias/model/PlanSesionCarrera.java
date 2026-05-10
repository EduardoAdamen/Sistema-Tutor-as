package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_sesion_carrera",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_plan_sesion_carrera",
            columnNames = {"carrera_id", "numero_sesion"})
    })
public class PlanSesionCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_sesion_carrera"))
    private Carrera carrera;

    @Column(name = "numero_sesion", nullable = false)
    private Integer numeroSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_sesion_actividad"))
    private ActividadPatCarrera actividadCarrera;

    public PlanSesionCarrera() {}

    //Getters
    public Integer getId() { return id; }
    public Carrera getCarrera() { return carrera; }
    public Integer getNumeroSesion() { return numeroSesion; }
    public ActividadPatCarrera getActividadCarrera() { return actividadCarrera; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public void setNumeroSesion(Integer numeroSesion) { this.numeroSesion = numeroSesion; }
    public void setActividadCarrera(ActividadPatCarrera actividadCarrera) { this.actividadCarrera = actividadCarrera; }

    @Override
    public String toString() {
        return "PlanSesionCarrera{id=" + id + ", sesion=" + numeroSesion + "}";
    }
}
