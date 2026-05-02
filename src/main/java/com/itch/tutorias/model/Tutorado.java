package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tutorado")
public class Tutorado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_tutorado_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tutorado_carrera"))
    private Carrera carrera;

    @Column(name = "semestre", nullable = false)
    private Integer semestre = 1;

    public Tutorado() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
}
