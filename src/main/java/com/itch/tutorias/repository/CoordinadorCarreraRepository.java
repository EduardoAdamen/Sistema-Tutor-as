package com.itch.tutorias.repository;

import com.itch.tutorias.model.CoordinadorCarrera;
import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoordinadorCarreraRepository extends JpaRepository<CoordinadorCarrera, Integer> {
    Optional<CoordinadorCarrera> findByUsuario(Usuario usuario);
    Optional<CoordinadorCarrera> findByCarrera(Carrera carrera);
}
