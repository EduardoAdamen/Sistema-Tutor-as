package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.repository.ActividadPatRepository;
import com.itch.tutorias.service.IActividadPat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ActividadPatServiceImpl implements IActividadPat {

    @Autowired
    private ActividadPatRepository actividadPatRepository;

    @Override
    public List<ActividadPat> buscarTodas() {
        return actividadPatRepository.findAll();
    }

    @Override
    public List<ActividadPat> buscarPorSesion(Sesion sesion) {
        return actividadPatRepository.findBySesionOrderByFechaRegistroAsc(sesion);
    }

    @Override
    public List<ActividadPat> buscarPorTutor(Integer tutorId) {
        return actividadPatRepository.findByTutorId(tutorId);
    }

    @Override
    public List<ActividadPat> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return actividadPatRepository.findByFechaSesionBetween(inicio, fin);
    }

    @Override
    public ActividadPat buscarPorId(Integer id) {
        return actividadPatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Actividad PAT no encontrada con id: " + id));
    }

    @Override
    public ActividadPat guardar(ActividadPat actividad) {
        return actividadPatRepository.save(actividad);
    }

    @Override
    public void eliminar(Integer id) {
        actividadPatRepository.deleteById(id);
    }
}
