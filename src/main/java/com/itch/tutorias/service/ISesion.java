package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Sesion;
import java.time.LocalDate;
import java.util.List;

public interface ISesion {
    List<Sesion> buscarPorAsignacion(Asignacion asignacion);
    Sesion buscarPorId(Integer id);
    Sesion guardar(Sesion sesion);
    void eliminar(Integer id);
    boolean existeSesionEnFecha(Asignacion asignacion, LocalDate fecha);
    long contarSesionesPorAsignacion(Asignacion asignacion);
}
