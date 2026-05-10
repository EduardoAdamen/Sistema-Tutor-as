package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.PlanSesionCarrera;
import com.itch.tutorias.repository.PlanSesionCarreraRepository;
import com.itch.tutorias.service.IPlanSesionCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlanSesionCarreraServiceImpl implements IPlanSesionCarrera {

    @Autowired
    private PlanSesionCarreraRepository planRepo;

    @Override
    public List<PlanSesionCarrera> buscarPorCarrera(Carrera carrera) {
        return planRepo.findByCarreraOrderByNumeroSesionAsc(carrera);
    }

    @Override
    public Optional<PlanSesionCarrera> buscarPorCarreraYSesion(Carrera carrera, Integer numeroSesion) {
        return planRepo.findByCarreraAndNumeroSesion(carrera, numeroSesion);
    }

    @Override
    public PlanSesionCarrera guardar(PlanSesionCarrera plan) {
        return planRepo.save(plan);
    }

    @Override
    @Transactional
    public void eliminarPorCarreraYSesion(Carrera carrera, Integer numeroSesion) {
        planRepo.deleteByCarreraAndNumeroSesion(carrera, numeroSesion);
    }
}
