package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.repository.SesionRepository;
import com.itch.tutorias.service.ISesion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SesionServiceImpl implements ISesion {

    @Autowired
    private SesionRepository sesionRepository;

    @Override
    public List<Sesion> buscarPorAsignacion(Asignacion asignacion) {
        return sesionRepository.findByAsignacionOrderByFechaDesc(asignacion);
    }

    @Override
    public Sesion buscarPorId(Integer id) {
        return sesionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + id));
    }

    @Override
    public Sesion guardar(Sesion sesion) {
        return sesionRepository.save(sesion);
    }

    @Override
    public void eliminar(Integer id) {
        sesionRepository.deleteById(id);
    }

    @Override
    public boolean existeSesionEnFecha(Asignacion asignacion, LocalDate fecha) {
        return sesionRepository.existsByAsignacionAndFecha(asignacion, fecha);
    }

    @Override
    public long contarSesionesPorAsignacion(Asignacion asignacion) {
        return sesionRepository.countByAsignacion(asignacion);
    }
}
