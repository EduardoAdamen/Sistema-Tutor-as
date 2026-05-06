package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Grupo;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.repository.AsignacionRepository;
import com.itch.tutorias.service.IAsignacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsignacionServiceImpl implements IAsignacion {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Override
    public List<Asignacion> buscarTodas() {
        return asignacionRepository.findAll();
    }

    @Override
    public List<Asignacion> buscarPorPeriodo(PeriodoSemestral periodo) {
        return asignacionRepository.findByPeriodo(periodo);
    }

    @Override
    public List<Asignacion> buscarPorTutor(Tutor tutor) {
        return asignacionRepository.findByTutor(tutor);
    }

    @Override
    public Asignacion buscarPorId(Integer id) {
        return asignacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada con id: " + id));
    }

    @Override
    public Asignacion guardar(Asignacion asignacion) {
        return asignacionRepository.save(asignacion);
    }

    @Override
    public void eliminar(Integer id) {
        asignacionRepository.deleteById(id);
    }

    @Override
    public boolean existeDuplicado(Tutor tutor, Grupo grupo, LocalTime hora, PeriodoSemestral periodo) {
        return asignacionRepository.existsByTutorAndGrupoAndHoraHorarioAndPeriodo(tutor, grupo, hora, periodo);
    }
}
