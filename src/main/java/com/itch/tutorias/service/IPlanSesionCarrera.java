package com.itch.tutorias.service;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.PlanSesionCarrera;
import java.util.List;
import java.util.Optional;

public interface IPlanSesionCarrera {
    List<PlanSesionCarrera> buscarPorCarrera(Carrera carrera);
    Optional<PlanSesionCarrera> buscarPorCarreraYSesion(Carrera carrera, Integer numeroSesion);
    PlanSesionCarrera guardar(PlanSesionCarrera plan);
    void eliminarPorCarreraYSesion(Carrera carrera, Integer numeroSesion);
}
