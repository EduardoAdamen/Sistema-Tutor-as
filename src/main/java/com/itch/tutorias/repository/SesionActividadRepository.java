package com.itch.tutorias.repository;

import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SesionActividadRepository extends JpaRepository<SesionActividad, Integer> {
    List<SesionActividad> findBySesionOrderByFechaRegistroAsc(Sesion sesion);

    List<SesionActividad> findBySesion_Asignacion_Tutor_IdOrderBySesion_FechaAsc(Integer tutorId);

    List<SesionActividad> findBySesion_FechaBetween(LocalDate inicio, LocalDate fin);
}
