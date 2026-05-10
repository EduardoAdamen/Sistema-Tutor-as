package com.itch.tutorias.service;

import com.itch.tutorias.model.ActividadPatCarrera;
import com.itch.tutorias.model.Carrera;
import java.util.List;

public interface IActividadPatCarrera {
    List<ActividadPatCarrera> buscarTodas();
    List<ActividadPatCarrera> buscarPorCarrera(Carrera carrera);
    ActividadPatCarrera buscarPorId(Integer id);
    ActividadPatCarrera guardar(ActividadPatCarrera actividad);
    void eliminar(Integer id);
    boolean existeAdaptacion(Integer actividadGeneralId, Integer carreraId);
}
