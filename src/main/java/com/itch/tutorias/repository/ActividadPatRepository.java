package com.itch.tutorias.repository;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ActividadPatRepository extends JpaRepository<ActividadPat, Integer> {
    List<ActividadPat> findBySesionOrderByFechaRegistroAsc(Sesion sesion);
    //Consulta para obtener actividades por tutor ordenadas por fecha de sesión
    @Query("SELECT ap FROM ActividadPat ap " +
           "JOIN ap.sesion s " +
           "WHERE s.asignacion.tutor.id = :tutorId " +
           "ORDER BY s.fecha ASC")
    List<ActividadPat> findByTutorId(@Param("tutorId") Integer tutorId);
    
    //Consulta para obtener actividades por rango de fechas de sesión
    @Query("SELECT ap FROM ActividadPat ap " +
           "JOIN ap.sesion s " +
           "WHERE s.fecha BETWEEN :inicio AND :fin")
    List<ActividadPat> findByFechaSesionBetween(
        @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
