package com.itch.tutorias.service;

import com.itch.tutorias.model.CoordinadorCarrera;
import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface ICoordinadorCarrera {
    List<CoordinadorCarrera> buscarTodos();
    Optional<CoordinadorCarrera> buscarPorUsuario(Usuario usuario);
    Optional<CoordinadorCarrera> buscarPorCarrera(Carrera carrera);
    CoordinadorCarrera buscarPorId(Integer id);
    CoordinadorCarrera guardar(CoordinadorCarrera coordinador);
    void eliminar(Integer id);
}
