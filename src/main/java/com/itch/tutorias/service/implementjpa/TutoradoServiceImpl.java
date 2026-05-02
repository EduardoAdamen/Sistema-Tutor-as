package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.repository.TutoradoRepository;
import com.itch.tutorias.service.ITutorado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutoradoServiceImpl implements ITutorado {

    @Autowired
    private TutoradoRepository tutoradoRepository;

    @Override
    public List<Tutorado> buscarTodos() {
        return tutoradoRepository.findAll();
    }

    @Override
    public Tutorado buscarPorId(Integer id) {
        return tutoradoRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Tutorado> buscarPorUsuario(Usuario usuario) {
        return tutoradoRepository.findByUsuario(usuario);
    }

    @Override
    public List<Tutorado> buscarPorCarrera(Carrera carrera) {
        return tutoradoRepository.findByCarrera(carrera);
    }

    @Override
    public Tutorado guardar(Tutorado tutorado) {
        return tutoradoRepository.save(tutorado);
    }

    @Override
    public void eliminar(Integer id) {
        tutoradoRepository.deleteById(id);
    }
}
