package com.itch.tutorias.service;

import com.itch.tutorias.model.RegistroAsistencia;
import com.itch.tutorias.model.Sesion;
import com.itch.tutorias.model.Usuario;
import java.util.List;

public interface IRegistroAsistencia {
    List<RegistroAsistencia> buscarPorSesion(Sesion sesion);
    List<RegistroAsistencia> buscarPorTutorado(Usuario tutorado);
    RegistroAsistencia guardar(RegistroAsistencia registro);
    boolean existeRegistro(Sesion sesion, Usuario tutorado);
    double calcularPorcentajeAsistencia(Integer asignacionId, Integer tutoradoId);
}
