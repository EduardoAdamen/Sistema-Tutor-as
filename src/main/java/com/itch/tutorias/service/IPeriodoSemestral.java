package com.itch.tutorias.service;

import com.itch.tutorias.model.PeriodoSemestral;
import java.util.List;
import java.util.Optional;

public interface IPeriodoSemestral {
    List<PeriodoSemestral> buscarTodos();
    PeriodoSemestral buscarPorId(Integer id);
    Optional<PeriodoSemestral> buscarActivo();
    PeriodoSemestral guardar(PeriodoSemestral periodo);
    void cerrar(Integer id);
    boolean existePeriodoActivo();
}
