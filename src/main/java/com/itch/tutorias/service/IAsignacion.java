package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Grupo;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Tutor;

import java.time.LocalTime;
import java.util.List;

public interface IAsignacion {
    List<Asignacion> buscarTodas();
    List<Asignacion> buscarPorPeriodo(PeriodoSemestral periodo);
    List<Asignacion> buscarPorTutor(Tutor tutor);
    Asignacion buscarPorId(Integer id);
    Asignacion guardar(Asignacion asignacion);
    void eliminar(Integer id);
    boolean existeDuplicado(Tutor tutor, Grupo grupo, LocalTime hora, PeriodoSemestral periodo);
}
