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

    //Relación ManyToOne hacia Asignacion, una asignación puede tener varios tutorados pero cada asignación_tutorado pertenece a una sola asignación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asig_tut_asignacion"))
    private Asignacion asignacion;

    //Relación ManyToOne hacia Usuario, un tutorado puede tener varias asignaciones pero cada asignación_tutorado pertenece a un solo tutorado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asig_tut_tutorado"))
    private Tutorado tutorado;

    public AsignacionTutorado() {}

    //Getters
    public Integer getId() { return id; }
    public Asignacion getAsignacion() { return asignacion; }
    public Tutorado getTutorado() { return tutorado; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
    public void setTutorado(Tutorado tutorado) { this.tutorado = tutorado; }

    @Override
    public String toString() {
        return "AsignacionTutorado{id=" + id + "}";
    }
}
