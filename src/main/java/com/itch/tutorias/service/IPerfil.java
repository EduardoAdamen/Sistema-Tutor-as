package com.itch.tutorias.service;

import com.itch.tutorias.model.Perfil;
import java.util.List;
import java.util.Optional;

public interface IPerfil {
    List<Perfil> buscarTodos();
    Optional<Perfil> buscarPorNombre(String nombre);
    Perfil guardar(Perfil perfil);
}
