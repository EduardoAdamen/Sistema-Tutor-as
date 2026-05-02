package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.repository.TutorRepository;
import com.itch.tutorias.service.ITutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorServiceImpl implements ITutor {

    @Autowired
    private TutorRepository tutorRepository;

    @Override
    public List<Tutor> buscarTodos() {
        return tutorRepository.findAll();
    }

    @Override
    public Tutor buscarPorId(Integer id) {
        return tutorRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Tutor> buscarPorUsuario(Usuario usuario) {
        return tutorRepository.findByUsuario(usuario);
    }

    @Override
    public Tutor guardar(Tutor tutor) {
        return tutorRepository.save(tutor);
    }

    @Override
    public void eliminar(Integer id) {
        tutorRepository.deleteById(id);
    }
}
