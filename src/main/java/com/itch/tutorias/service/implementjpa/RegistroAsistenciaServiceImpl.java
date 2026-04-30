package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.repository.RegistroAsistenciaRepository;
import com.itch.tutorias.repository.SesionRepository;
import com.itch.tutorias.service.IRegistroAsistencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegistroAsistenciaServiceImpl implements IRegistroAsistencia {

    @Autowired
    private RegistroAsistenciaRepository registroRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Override
    public List<RegistroAsistencia> buscarPorSesion(Sesion sesion) {
        return registroRepository.findBySesion(sesion);
    }

    @Override
    public List<RegistroAsistencia> buscarPorTutorado(Usuario tutorado) {
        return registroRepository.findByTutorado(tutorado);
    }

    @Override
    public RegistroAsistencia guardar(RegistroAsistencia registro) {
        return registroRepository.save(registro);
    }

    @Override
    public boolean existeRegistro(Sesion sesion, Usuario tutorado) {
        return registroRepository.existsBySesionAndTutorado(sesion, tutorado);
    }

    @Override
    public double calcularPorcentajeAsistencia(Integer asignacionId, Integer tutoradoId) {
        Sesion ejemplo = new Sesion();
        //Contar total de sesiones de la asignación
        com.itch.tutorias.model.Asignacion asig = new com.itch.tutorias.model.Asignacion();
        asig.setId(asignacionId);
        long totalSesiones = sesionRepository.countByAsignacion(asig);

        if (totalSesiones == 0) return 0.0;

        long presentes = registroRepository.countByAsignacionIdAndTutoradoIdAndEstatus(
            asignacionId, tutoradoId, RegistroAsistencia.EstatusAsistencia.presente);
        long justificados = registroRepository.countByAsignacionIdAndTutoradoIdAndEstatus(
            asignacionId, tutoradoId, RegistroAsistencia.EstatusAsistencia.justificado);

        return Math.round(((presentes + justificados) * 100.0 / totalSesiones) * 100.0) / 100.0;
    }
}
