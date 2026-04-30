package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    List<Asignacion> findByPeriodo(PeriodoSemestral periodo);
    List<Asignacion> findByTutor(Usuario tutor);
    List<Asignacion> findByTutorAndPeriodo(Usuario tutor, PeriodoSemestral periodo);
    boolean existsByTutorAndGrupoAndHoraHorarioAndPeriodo(
        Usuario tutor, String grupo, java.time.LocalTime horaHorario, PeriodoSemestral periodo);
}
