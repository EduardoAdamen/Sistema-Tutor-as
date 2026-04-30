package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.repository.CarreraRepository;
import com.itch.tutorias.service.ICarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarreraServiceImpl implements ICarrera {

    @Autowired
    private CarreraRepository carreraRepository;

    @Override
    public List<Carrera> buscarTodas() {
        return carreraRepository.findAll();
    }

    @Override
    public List<Carrera> buscarActivas() {
        return carreraRepository.findByEstado(Carrera.EstadoCarrera.activa);
    }

    @Override
    public Carrera buscarPorId(Integer id) {
        return carreraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Carrera no encontrada con id: " + id));
    }

    @Override
    public Carrera guardar(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Override
    public void eliminar(Integer id) {
        carreraRepository.deleteById(id);
    }

    @Override
    public boolean existeNombre(String nombre) {
        return carreraRepository.existsByNombre(nombre);
    }

    @Override
    public boolean existeClave(String clave) {
        return carreraRepository.existsByClave(clave);
    }
}
