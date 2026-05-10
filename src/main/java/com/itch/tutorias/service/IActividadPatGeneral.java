package com.itch.tutorias.service;

import com.itch.tutorias.model.ActividadPatGeneral;
import java.util.List;

public interface IActividadPatGeneral {
    List<ActividadPatGeneral> buscarTodas();
    ActividadPatGeneral buscarPorId(Integer id);
    ActividadPatGeneral guardar(ActividadPatGeneral actividad);
    void eliminar(Integer id);
}
