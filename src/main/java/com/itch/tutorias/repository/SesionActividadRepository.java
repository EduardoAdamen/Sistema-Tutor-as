package com.itch.tutorias.repository;

import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SesionActividadRepository extends JpaRepository<SesionActividad, Integer> {
    List<SesionActividad> findBySesionOrderByFechaRegistroAsc(Sesion sesion);

    @Query("SELECT sa FROM SesionActividad sa " +
           "JOIN sa.sesion s " +
           "WHERE s.asignacion.tutor.id = :tutorId " +
           "ORDER BY s.fecha ASC")
    List<SesionActividad> findByTutorId(@Param("tutorId") Integer tutorId);

    @Query("SELECT sa FROM SesionActividad sa " +
           "JOIN sa.sesion s " +
           "WHERE s.fecha BETWEEN :inicio AND :fin")
    List<SesionActividad> findByFechaSesionBetween(
        @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
