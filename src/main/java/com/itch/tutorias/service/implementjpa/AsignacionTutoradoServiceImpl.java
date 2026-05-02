package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.repository.AsignacionTutoradoRepository;
import com.itch.tutorias.service.IAsignacionTutorado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AsignacionTutoradoServiceImpl implements IAsignacionTutorado {

    @Autowired
    private AsignacionTutoradoRepository asignacionTutoradoRepository;

    @Override
    public List<AsignacionTutorado> buscarPorAsignacion(Asignacion asignacion) {
        return asignacionTutoradoRepository.findByAsignacion(asignacion);
    }

    @Override
    public List<AsignacionTutorado> buscarPorTutorado(Tutorado tutorado) {
        return asignacionTutoradoRepository.findByTutorado(tutorado);
    }

    @Override
    public AsignacionTutorado guardar(AsignacionTutorado at) {
        return asignacionTutoradoRepository.save(at);
    }

    @Override
    public void eliminar(Integer id) {
        asignacionTutoradoRepository.deleteById(id);
    }

    @Override
    public boolean existeRelacion(Asignacion asignacion, Tutorado tutorado) {
        return asignacionTutoradoRepository.existsByAsignacionAndTutorado(asignacion, tutorado);
    }
}
