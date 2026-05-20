package com.itch.tutorias.repository;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ActividadPatRepository extends JpaRepository<ActividadPat, Integer> {
    List<ActividadPat> findBySesionOrderByFechaRegistroAsc(Sesion sesion);
    //Consulta para obtener actividades por tutor ordenadas por fecha de sesión
    List<ActividadPat> findBySesion_Asignacion_Tutor_IdOrderBySesion_FechaAsc(Integer tutorId);
    
    //Consulta para obtener actividades por rango de fechas de sesión
    List<ActividadPat> findBySesion_FechaBetween(LocalDate inicio, LocalDate fin);
}
