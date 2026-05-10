package com.itch.tutorias.service;

import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Sesion;
import java.time.LocalDate;
import java.util.List;

public interface ISesionActividad {
    List<SesionActividad> buscarTodas();
    List<SesionActividad> buscarPorSesion(Sesion sesion);
    List<SesionActividad> buscarPorTutor(Integer tutorId);
    List<SesionActividad> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
    SesionActividad buscarPorId(Integer id);
    SesionActividad guardar(SesionActividad sesionActividad);
    void eliminar(Integer id);
}
