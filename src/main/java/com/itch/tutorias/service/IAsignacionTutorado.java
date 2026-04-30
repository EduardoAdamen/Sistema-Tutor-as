package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.Usuario;
import java.util.List;

public interface IAsignacionTutorado {
    List<AsignacionTutorado> buscarPorAsignacion(Asignacion asignacion);
    List<AsignacionTutorado> buscarPorTutorado(Usuario tutorado);
    AsignacionTutorado guardar(AsignacionTutorado at);
    void eliminar(Integer id);
    boolean existeRelacion(Asignacion asignacion, Usuario tutorado);
}
