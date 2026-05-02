package com.itch.tutorias.repository;

import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Integer> {
    Optional<Tutor> findByUsuario(Usuario usuario);
}
