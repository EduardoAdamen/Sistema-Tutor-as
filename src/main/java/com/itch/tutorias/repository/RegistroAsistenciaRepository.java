package com.itch.tutorias.repository;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Tutorado;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Integer> {
    List<RegistroAsistencia> findBySesion(Sesion sesion);
    List<RegistroAsistencia> findByTutorado(Tutorado tutorado);
    Optional<RegistroAsistencia> findBySesionAndTutorado(Sesion sesion, Tutorado tutorado);
    boolean existsBySesionAndTutorado(Sesion sesion, Tutorado tutorado);

    //Consulta para contar registros de asistencia por asignación, tutorado y estatus
    long countBySesion_Asignacion_IdAndTutorado_IdAndEstatusAsistencia(
        Integer asignacionId,
        Integer tutoradoId,
        RegistroAsistencia.EstatusAsistencia estatus);
}
