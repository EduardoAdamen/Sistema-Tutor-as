package com.itch.tutorias.repository;

import com.itch.tutorias.model.PeriodoSemestral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PeriodoSemestralRepository extends JpaRepository<PeriodoSemestral, Integer> {
    Optional<PeriodoSemestral> findByClave(String clave);
    Optional<PeriodoSemestral> findByEstatus(PeriodoSemestral.EstatusPeriodo estatus);
    List<PeriodoSemestral> findAllByOrderByFechaInicioDesc();
    boolean existsByEstatus(PeriodoSemestral.EstatusPeriodo estatus);
}
