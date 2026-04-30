package com.itch.tutorias.repository;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionTutoradoRepository extends JpaRepository<AsignacionTutorado, Integer> {
    List<AsignacionTutorado> findByAsignacion(Asignacion asignacion);
    List<AsignacionTutorado> findByTutorado(Usuario tutorado);
    Optional<AsignacionTutorado> findByAsignacionAndTutorado(Asignacion asignacion, Usuario tutorado);
    boolean existsByAsignacionAndTutorado(Asignacion asignacion, Usuario tutorado);
}
