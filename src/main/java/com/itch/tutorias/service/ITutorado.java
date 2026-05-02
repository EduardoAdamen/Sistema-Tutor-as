package com.itch.tutorias.service;

import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import java.util.List;
import java.util.Optional;

public interface ITutorado {
    List<Tutorado> buscarTodos();
    Tutorado buscarPorId(Integer id);
    Optional<Tutorado> buscarPorUsuario(Usuario usuario);
    List<Tutorado> buscarPorCarrera(Carrera carrera);
    Tutorado guardar(Tutorado tutorado);
    void eliminar(Integer id);
}
