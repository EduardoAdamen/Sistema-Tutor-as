package com.itch.tutorias.repository;

import com.itch.tutorias.model.ActividadPatCarrera;
import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadPatCarreraRepository extends JpaRepository<ActividadPatCarrera, Integer> {
    List<ActividadPatCarrera> findByCarrera(Carrera carrera);
    boolean existsByActividadGeneralIdAndCarreraId(Integer actividadGeneralId, Integer carreraId);
}
