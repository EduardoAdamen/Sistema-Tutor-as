package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coordinador_carrera")
public class CoordinadorCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_coordinador_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_coordinador_carrera"))
    private Carrera carrera;

    public CoordinadorCarrera() {}

    public CoordinadorCarrera(Usuario usuario, Carrera carrera) {
        this.usuario = usuario;
        this.carrera = carrera;
    }

    //Getters
    public Integer getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Carrera getCarrera() { return carrera; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    @Override
    public String toString() {
        return "CoordinadorCarrera{id=" + id + "}";
    }
}
