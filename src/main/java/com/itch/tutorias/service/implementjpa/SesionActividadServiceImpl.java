package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.repository.SesionActividadRepository;
import com.itch.tutorias.service.ISesionActividad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SesionActividadServiceImpl implements ISesionActividad {

    @Autowired
    private SesionActividadRepository sesionActividadRepository;

    @Override
    public List<SesionActividad> buscarTodas() {
        return sesionActividadRepository.findAll();
    }

    @Override
    public List<SesionActividad> buscarPorSesion(Sesion sesion) {
        return sesionActividadRepository.findBySesionOrderByFechaRegistroAsc(sesion);
    }

    @Override
    public List<SesionActividad> buscarPorTutor(Integer tutorId) {
        return sesionActividadRepository.findByTutorId(tutorId);
    }

    @Override
    public List<SesionActividad> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return sesionActividadRepository.findByFechaSesionBetween(inicio, fin);
    }

    @Override
    public SesionActividad buscarPorId(Integer id) {
        return sesionActividadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sesión-Actividad no encontrada con id: " + id));
    }

    @Override
    public SesionActividad guardar(SesionActividad sesionActividad) {
        return sesionActividadRepository.save(sesionActividad);
    }

    @Override
    public void eliminar(Integer id) {
        sesionActividadRepository.deleteById(id);
    }
}
