package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.CoordinadorCarrera;
import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.repository.CoordinadorCarreraRepository;
import com.itch.tutorias.service.ICoordinadorCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordinadorCarreraServiceImpl implements ICoordinadorCarrera {

    @Autowired
    private CoordinadorCarreraRepository coordinadorRepository;

    @Override
    public List<CoordinadorCarrera> buscarTodos() {
        return coordinadorRepository.findAll();
    }

    @Override
    public Optional<CoordinadorCarrera> buscarPorUsuario(Usuario usuario) {
        return coordinadorRepository.findByUsuario(usuario);
    }

    @Override
    public Optional<CoordinadorCarrera> buscarPorCarrera(Carrera carrera) {
        return coordinadorRepository.findByCarrera(carrera);
    }

    @Override
    public CoordinadorCarrera buscarPorId(Integer id) {
        return coordinadorRepository.findById(id).orElse(null);
    }

    @Override
    public CoordinadorCarrera guardar(CoordinadorCarrera coordinador) {
        return coordinadorRepository.save(coordinador);
    }

    @Override
    public void eliminar(Integer id) {
        coordinadorRepository.deleteById(id);
    }
}
