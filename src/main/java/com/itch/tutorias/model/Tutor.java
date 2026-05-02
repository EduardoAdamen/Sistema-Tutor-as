package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tutor")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_tutor_usuario"))
    private Usuario usuario;

    public Tutor() {}

    public Tutor(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
