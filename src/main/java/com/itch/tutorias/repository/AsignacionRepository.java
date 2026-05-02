package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    List<Asignacion> findByPeriodo(PeriodoSemestral periodo);
    List<Asignacion> findByTutor(Tutor tutor);
    List<Asignacion> findByTutorAndPeriodo(Tutor tutor, PeriodoSemestral periodo);
    boolean existsByTutorAndGrupoAndHoraHorarioAndPeriodo(
        Tutor tutor, com.itch.tutorias.model.Grupo grupo, java.time.LocalTime horaHorario, PeriodoSemestral periodo);
}
