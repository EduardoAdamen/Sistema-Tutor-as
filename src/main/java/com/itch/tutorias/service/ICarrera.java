package com.itch.tutorias.service;

import com.itch.tutorias.model.Carrera;
import java.util.List;

public interface ICarrera {
    List<Carrera> buscarTodas();
    List<Carrera> buscarActivas();
    Carrera buscarPorId(Integer id);
    Carrera guardar(Carrera carrera);
    void eliminar(Integer id);
    boolean existeNombre(String nombre);
    boolean existeClave(String clave);
}
