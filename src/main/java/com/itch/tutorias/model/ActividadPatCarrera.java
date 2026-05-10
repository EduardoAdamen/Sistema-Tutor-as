package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividad_pat_carrera",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_actividad_carrera",
            columnNames = {"actividad_general_id", "carrera_id"})
    })
public class ActividadPatCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_general_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_act_carrera_general"))
    private ActividadPatGeneral actividadGeneral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_act_carrera_carrera"))
    private Carrera carrera;

    @Column(name = "titulo_adaptado", nullable = false, length = 200)
    private String tituloAdaptado;

    @Column(name = "descripcion_adaptada", columnDefinition = "TEXT")
    private String descripcionAdaptada;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_adaptacion", nullable = false, updatable = false)
    private LocalDateTime fechaAdaptacion;

    public ActividadPatCarrera() {}

    //Getters
    public Integer getId() { return id; }
    public ActividadPatGeneral getActividadGeneral() { return actividadGeneral; }
    public Carrera getCarrera() { return carrera; }
    public String getTituloAdaptado() { return tituloAdaptado; }
    public String getDescripcionAdaptada() { return descripcionAdaptada; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getFechaAdaptacion() { return fechaAdaptacion; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setActividadGeneral(ActividadPatGeneral actividadGeneral) { this.actividadGeneral = actividadGeneral; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public void setTituloAdaptado(String tituloAdaptado) { this.tituloAdaptado = tituloAdaptado; }
    public void setDescripcionAdaptada(String descripcionAdaptada) { this.descripcionAdaptada = descripcionAdaptada; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setFechaAdaptacion(LocalDateTime fechaAdaptacion) { this.fechaAdaptacion = fechaAdaptacion; }

    @Override
    public String toString() {
        return "ActividadPatCarrera{id=" + id + ", tituloAdaptado='" + tituloAdaptado + "'}";
    }
}
