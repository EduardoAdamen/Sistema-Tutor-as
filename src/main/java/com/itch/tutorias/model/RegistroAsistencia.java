package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registro_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_registro_asistencia",
            columnNames = {"sesion_id", "tutorado_id"})
    })
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Relación ManyToOne hacia Sesion, una sesión puede tener varios registros de asistencia pero cada registro de asistencia pertenece a una sola sesión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reg_asist_sesion"))
    private Sesion sesion;

    //Relación ManyToOne hacia Usuario, un tutorado puede tener varios registros de asistencia pero cada registro de asistencia pertenece a un solo tutorado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reg_asist_tutorado"))
    private Usuario tutorado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", nullable = false)
    private EstatusAsistencia estatusAsistencia;

    public enum EstatusAsistencia {
        presente, ausente, justificado
    }


    public RegistroAsistencia() {}

    //Getters
    public Integer getId() { return id; }
    public Sesion getSesion() { return sesion; }
    public Usuario getTutorado() { return tutorado; }
    public EstatusAsistencia getEstatusAsistencia() { return estatusAsistencia; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setSesion(Sesion sesion) { this.sesion = sesion; }
    public void setTutorado(Usuario tutorado) { this.tutorado = tutorado; }
    public void setEstatusAsistencia(EstatusAsistencia estatusAsistencia) { this.estatusAsistencia = estatusAsistencia; }

    @Override
    public String toString() {
        return "RegistroAsistencia{id=" + id + ", estatusAsistencia=" + estatusAsistencia + "}";
    }
}
