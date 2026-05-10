package com.itch.tutorias.repository;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.PlanSesionCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanSesionCarreraRepository extends JpaRepository<PlanSesionCarrera, Integer> {
    List<PlanSesionCarrera> findByCarreraOrderByNumeroSesionAsc(Carrera carrera);
    Optional<PlanSesionCarrera> findByCarreraAndNumeroSesion(Carrera carrera, Integer numeroSesion);
    void deleteByCarreraAndNumeroSesion(Carrera carrera, Integer numeroSesion);
}
