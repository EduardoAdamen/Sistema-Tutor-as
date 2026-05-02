package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.repository.UsuarioRepository;
import com.itch.tutorias.service.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> buscarPorPerfil(String nombrePerfil) {
        return usuarioRepository.findByPerfilesNombre(nombrePerfil);
    }

    @Override
    public List<Usuario> buscarPorPerfilYEstado(String nombrePerfil, Usuario.EstadoUsuario estado) {
        return usuarioRepository.findByPerfilesNombreAndEstado(nombrePerfil, estado);
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Override
    public Optional<Usuario> buscarPorNumeroIdentificacion(String numeroIdentificacion) {
        return usuarioRepository.findByNumeroIdentificacion(numeroIdentificacion);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuario.setEstado(Usuario.EstadoUsuario.inactivo);
        usuarioRepository.save(usuario);
    }

    @Override
    public boolean existeNumeroIdentificacion(String numeroId) {
        return usuarioRepository.existsByNumeroIdentificacion(numeroId);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public void cambiarContrasena(Integer id, String nuevaContrasena) {
        Usuario usuario = buscarPorId(id);
        usuario.setContrasenaHash(nuevaContrasena);
        usuarioRepository.save(usuario);
    }
}
