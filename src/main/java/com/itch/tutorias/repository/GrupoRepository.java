package com.itch.tutorias.repository;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    List<Grupo> findByCarrera(Carrera carrera);
}
