package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.ActividadPatCarrera;
import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.repository.ActividadPatCarreraRepository;
import com.itch.tutorias.service.IActividadPatCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActividadPatCarreraServiceImpl implements IActividadPatCarrera {

    @Autowired
    private ActividadPatCarreraRepository actividadPatCarreraRepository;

    @Override
    public List<ActividadPatCarrera> buscarTodas() {
        return actividadPatCarreraRepository.findAll();
    }

    @Override
    public List<ActividadPatCarrera> buscarPorCarrera(Carrera carrera) {
        return actividadPatCarreraRepository.findByCarrera(carrera);
    }

    @Override
    public ActividadPatCarrera buscarPorId(Integer id) {
        return actividadPatCarreraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Actividad PAT Carrera no encontrada con id: " + id));
    }

    @Override
    public ActividadPatCarrera guardar(ActividadPatCarrera actividad) {
        return actividadPatCarreraRepository.save(actividad);
    }

    @Override
    public void eliminar(Integer id) {
        actividadPatCarreraRepository.deleteById(id);
    }

    @Override
    public boolean existeAdaptacion(Integer actividadGeneralId, Integer carreraId) {
        return actividadPatCarreraRepository.existsByActividadGeneralIdAndCarreraId(actividadGeneralId, carreraId);
    }
}
