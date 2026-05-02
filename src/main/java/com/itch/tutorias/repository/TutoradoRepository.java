package com.itch.tutorias.repository;

import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutoradoRepository extends JpaRepository<Tutorado, Integer> {
    Optional<Tutorado> findByUsuario(Usuario usuario);
    List<Tutorado> findByCarrera(Carrera carrera);
}
