package com.itch.tutorias.repository;

import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);
    List<Usuario> findByRolAndEstado(Usuario.Rol rol, Usuario.EstadoUsuario estado);
    List<Usuario> findByCarrera(Carrera carrera);
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByCorreo(String correo);
}
