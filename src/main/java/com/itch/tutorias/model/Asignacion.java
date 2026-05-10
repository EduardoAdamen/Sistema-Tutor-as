package com.itch.tutorias.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "asignacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_asignacion",
            columnNames = {"tutor_id", "grupo_id", "hora_horario", "periodo_id"})
    })
public class Asignacion {

    public enum DiaSemana {
        Viernes
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_tutor"))
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_grupo"))
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_asignacion_periodo"))
    private PeriodoSemestral periodo;

    @Column(name = "aula", nullable = false, length = 30)
    private String aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_horario", nullable = false, columnDefinition = "ENUM('Viernes') DEFAULT 'Viernes'")
    private DiaSemana diaHorario = DiaSemana.Viernes;

    @Column(name = "hora_horario", nullable = false)
    private LocalTime horaHorario;

    @Column(name = "hora_fin_horario", nullable = false)
    private LocalTime horaFinHorario;

    public Asignacion() {}

    //Getters
    public Integer getId() { return id; }
    public Tutor getTutor() { return tutor; }
    public Grupo getGrupo() { return grupo; }
    public PeriodoSemestral getPeriodo() { return periodo; }
    public String getAula() { return aula; }
    public DiaSemana getDiaHorario() { return diaHorario; }
    public LocalTime getHoraHorario() { return horaHorario; }
    public LocalTime getHoraFinHorario() { return horaFinHorario; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public void setPeriodo(PeriodoSemestral periodo) { this.periodo = periodo; }
    public void setAula(String aula) { this.aula = aula; }
    public void setDiaHorario(DiaSemana diaHorario) { this.diaHorario = diaHorario; }
    public void setHoraHorario(LocalTime horaHorario) { this.horaHorario = horaHorario; }
    public void setHoraFinHorario(LocalTime horaFinHorario) { this.horaFinHorario = horaFinHorario; }

    @Override
    public String toString() {
        return "Asignacion{id=" + id + ", aula='" + aula + "'}";
    }
}

