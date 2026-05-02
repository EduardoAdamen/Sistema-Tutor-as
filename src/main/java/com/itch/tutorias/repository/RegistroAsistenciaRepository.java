package com.itch.tutorias.repository;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Tutorado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Integer> {
    List<RegistroAsistencia> findBySesion(Sesion sesion);
    List<RegistroAsistencia> findByTutorado(Tutorado tutorado);
    Optional<RegistroAsistencia> findBySesionAndTutorado(Sesion sesion, Tutorado tutorado);
    boolean existsBySesionAndTutorado(Sesion sesion, Tutorado tutorado);

    //Consulta para contar registros de asistencia por asignación, tutorado y estatus
    @Query("SELECT COUNT(ra) FROM RegistroAsistencia ra " +
           "JOIN ra.sesion s " +
           "WHERE s.asignacion.id = :asignacionId " +
           "AND ra.tutorado.id = :tutoradoId " +
           "AND ra.estatusAsistencia = :estatus")
    long countByAsignacionIdAndTutoradoIdAndEstatus(
        @Param("asignacionId") Integer asignacionId,
        @Param("tutoradoId") Integer tutoradoId,
        @Param("estatus") RegistroAsistencia.EstatusAsistencia estatus);
}
