package com.itch.tutorias.repository;

import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Integer> {
    Optional<Carrera> findByNombre(String nombre);
    Optional<Carrera> findByClave(String clave);
    List<Carrera> findByEstado(Carrera.EstadoCarrera estado);
    boolean existsByNombre(String nombre);
    boolean existsByClave(String clave);
}
