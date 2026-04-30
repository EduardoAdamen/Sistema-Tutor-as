package com.itch.tutorias.service;

import com.itch.tutorias.model.ActividadPat;
import com.itch.tutorias.model.Sesion;
import java.time.LocalDate;
import java.util.List;

public interface IActividadPat {
    List<ActividadPat> buscarTodas();
    List<ActividadPat> buscarPorSesion(Sesion sesion);
    List<ActividadPat> buscarPorTutor(Integer tutorId);
    List<ActividadPat> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
    ActividadPat buscarPorId(Integer id);
    ActividadPat guardar(ActividadPat actividad);
    void eliminar(Integer id);
}
