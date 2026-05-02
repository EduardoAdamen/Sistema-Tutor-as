package com.itch.tutorias.repository;

import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByPerfilesNombre(String nombrePerfil);
    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);
    List<Usuario> findByPerfilesNombreAndEstado(String nombrePerfil, Usuario.EstadoUsuario estado);
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByCorreo(String correo);
}
