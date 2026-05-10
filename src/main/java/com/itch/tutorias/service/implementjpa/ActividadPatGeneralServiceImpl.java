package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.ActividadPatGeneral;
import com.itch.tutorias.repository.ActividadPatGeneralRepository;
import com.itch.tutorias.service.IActividadPatGeneral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActividadPatGeneralServiceImpl implements IActividadPatGeneral {

    @Autowired
    private ActividadPatGeneralRepository actividadPatGeneralRepository;

    @Override
    public List<ActividadPatGeneral> buscarTodas() {
        return actividadPatGeneralRepository.findAll();
    }

    @Override
    public ActividadPatGeneral buscarPorId(Integer id) {
        return actividadPatGeneralRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Actividad PAT General no encontrada con id: " + id));
    }

    @Override
    public ActividadPatGeneral guardar(ActividadPatGeneral actividad) {
        return actividadPatGeneralRepository.save(actividad);
    }

    @Override
    public void eliminar(Integer id) {
        actividadPatGeneralRepository.deleteById(id);
    }
}
