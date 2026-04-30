package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {
    List<Sesion> findByAsignacionOrderByFechaDesc(Asignacion asignacion);
    Optional<Sesion> findByAsignacionAndFecha(Asignacion asignacion, LocalDate fecha);
    boolean existsByAsignacionAndFecha(Asignacion asignacion, LocalDate fecha);
    long countByAsignacion(Asignacion asignacion);
}
