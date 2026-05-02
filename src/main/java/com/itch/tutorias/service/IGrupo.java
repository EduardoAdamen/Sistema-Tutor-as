package com.itch.tutorias.service;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Grupo;

import java.util.List;
import java.util.Optional;

public interface IGrupo {
    List<Grupo> buscarTodos();
    List<Grupo> buscarPorCarrera(Carrera carrera);
    Optional<Grupo> buscarPorId(Integer id);
    Grupo guardar(Grupo grupo);
    void eliminar(Integer id);
}
