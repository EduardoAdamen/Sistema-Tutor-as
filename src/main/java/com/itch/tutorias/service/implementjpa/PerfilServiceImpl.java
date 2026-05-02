package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Perfil;
import com.itch.tutorias.repository.PerfilRepository;
import com.itch.tutorias.service.IPerfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilServiceImpl implements IPerfil {

    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public List<Perfil> buscarTodos() {
        return perfilRepository.findAll();
    }

    @Override
    public Optional<Perfil> buscarPorNombre(String nombre) {
        return perfilRepository.findByNombre(nombre);
    }

    @Override
    public Perfil guardar(Perfil perfil) {
        return perfilRepository.save(perfil);
    }
}
