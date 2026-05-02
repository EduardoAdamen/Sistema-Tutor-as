package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Grupo;
import com.itch.tutorias.repository.GrupoRepository;
import com.itch.tutorias.service.IGrupo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoServiceImpl implements IGrupo {

    @Autowired
    private GrupoRepository repo;

    @Override
    public List<Grupo> buscarTodos() {
        return repo.findAll();
    }

    @Override
    public List<Grupo> buscarPorCarrera(Carrera carrera) {
        return repo.findByCarrera(carrera);
    }

    @Override
    public Optional<Grupo> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Grupo guardar(Grupo grupo) {
        return repo.save(grupo);
    }

    @Override
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
