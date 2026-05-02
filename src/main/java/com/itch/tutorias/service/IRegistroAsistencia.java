package com.itch.tutorias.service;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Tutorado;
import java.util.List;

public interface IRegistroAsistencia {
    List<RegistroAsistencia> buscarPorSesion(Sesion sesion);
    List<RegistroAsistencia> buscarPorTutorado(Tutorado tutorado);
    RegistroAsistencia guardar(RegistroAsistencia registro);
    boolean existeRegistro(Sesion sesion, Tutorado tutorado);
    double calcularPorcentajeAsistencia(Integer asignacionId, Integer tutoradoId);
}
