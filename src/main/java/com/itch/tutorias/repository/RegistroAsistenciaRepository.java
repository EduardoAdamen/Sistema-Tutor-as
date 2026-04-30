package com.itch.tutorias.repository;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Integer> {
    List<RegistroAsistencia> findBySesion(Sesion sesion);
    List<RegistroAsistencia> findByTutorado(Usuario tutorado);
    Optional<RegistroAsistencia> findBySesionAndTutorado(Sesion sesion, Usuario tutorado);
    boolean existsBySesionAndTutorado(Sesion sesion, Usuario tutorado);

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
