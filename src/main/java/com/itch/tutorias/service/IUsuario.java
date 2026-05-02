package com.itch.tutorias.service;

import com.itch.tutorias.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuario {
    List<Usuario> buscarTodos();
    List<Usuario> buscarPorPerfil(String nombrePerfil);
    List<Usuario> buscarPorPerfilYEstado(String nombrePerfil, Usuario.EstadoUsuario estado);
    Usuario buscarPorId(Integer id);
    Optional<Usuario> buscarPorNumeroIdentificacion(String numeroIdentificacion);
    Optional<Usuario> buscarPorCorreo(String correo);
    Usuario guardar(Usuario usuario);
    void desactivar(Integer id);
    boolean existeNumeroIdentificacion(String numeroId);
    boolean existeCorreo(String correo);
    void cambiarContrasena(Integer id, String nuevaContrasena);
}
