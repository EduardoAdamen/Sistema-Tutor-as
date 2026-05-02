package com.itch.tutorias.service;

import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface ITutor {
    List<Tutor> buscarTodos();
    Tutor buscarPorId(Integer id);
    Optional<Tutor> buscarPorUsuario(Usuario usuario);
    Tutor guardar(Tutor tutor);
    void eliminar(Integer id);
}
