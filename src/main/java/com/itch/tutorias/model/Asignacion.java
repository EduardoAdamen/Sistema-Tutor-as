package com.itch.tutorias.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "asignacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_asignacion",
            columnNames = {"tutor_id", "grupo", "hora_horario", "periodo_id"})
    })
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Relación ManyToOne hacia Usuario, un tutor puede tener varias asignaciones pero cada asignación tiene un solo tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_tutor"))
    private Usuario tutor;

    //Relación ManyToOne hacia Carrera, una carrera puede tener varias asignaciones pero cada asignación pertenece a una sola carrera
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_carrera"))
    private Carrera carrera;

    //Relación ManyToOne hacia PeriodoSemestral, un periodo puede tener varias asignaciones pero cada asignación pertenece a un solo periodo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_periodo"))
    private PeriodoSemestral periodo;

    @Column(name = "grupo", nullable = false, length = 20)
    private String grupo;

    @Column(name = "aula", nullable = false, length = 30)
    private String aula;

    @Column(name = "dia_horario", nullable = false, length = 30)
    private String diaHorario;

    @Column(name = "hora_horario", nullable = false)
    private LocalTime horaHorario;

    public Asignacion() {}

    //Getters
    public Integer getId() { return id; }
    public Usuario getTutor() { return tutor; }
    public Carrera getCarrera() { return carrera; }
    public PeriodoSemestral getPeriodo() { return periodo; }
    public String getGrupo() { return grupo; }
    public String getAula() { return aula; }
    public String getDiaHorario() { return diaHorario; }
    public LocalTime getHoraHorario() { return horaHorario; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setTutor(Usuario tutor) { this.tutor = tutor; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public void setPeriodo(PeriodoSemestral periodo) { this.periodo = periodo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public void setAula(String aula) { this.aula = aula; }
    public void setDiaHorario(String diaHorario) { this.diaHorario = diaHorario; }
    public void setHoraHorario(LocalTime horaHorario) { this.horaHorario = horaHorario; }

    @Override
    public String toString() {
        return "Asignacion{id=" + id + ", grupo='" + grupo + "', aula='" + aula + "'}";
    }
}
