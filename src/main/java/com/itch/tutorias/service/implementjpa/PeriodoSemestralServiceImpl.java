package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.repository.PeriodoSemestralRepository;
import com.itch.tutorias.service.IPeriodoSemestral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PeriodoSemestralServiceImpl implements IPeriodoSemestral {

    @Autowired
    private PeriodoSemestralRepository periodoRepository;

    @Override
    public List<PeriodoSemestral> buscarTodos() {
        return periodoRepository.findAllByOrderByFechaInicioDesc();
    }

    @Override
    public PeriodoSemestral buscarPorId(Integer id) {
        return periodoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Periodo no encontrado con id: " + id));
    }

    @Override
    public Optional<PeriodoSemestral> buscarActivo() {
        return periodoRepository.findByEstatus(PeriodoSemestral.EstatusPeriodo.activo);
    }

    @Override
    public PeriodoSemestral guardar(PeriodoSemestral periodo) {
        // RF-16: no puede haber más de un periodo activo
        if (periodo.getEstatus() == PeriodoSemestral.EstatusPeriodo.activo
                && periodo.getId() == null
                && periodoRepository.existsByEstatus(PeriodoSemestral.EstatusPeriodo.activo)) {
            throw new RuntimeException("Ya existe un periodo semestral activo. Ciérrelo antes de crear uno nuevo.");
        }
        return periodoRepository.save(periodo);
    }

    @Override
    public void cerrar(Integer id) {
        PeriodoSemestral periodo = buscarPorId(id);
        periodo.setEstatus(PeriodoSemestral.EstatusPeriodo.cerrado);
        periodoRepository.save(periodo);
    }

    @Override
    public boolean existePeriodoActivo() {
        return periodoRepository.existsByEstatus(PeriodoSemestral.EstatusPeriodo.activo);
    }
}
