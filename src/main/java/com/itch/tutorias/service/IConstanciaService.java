package com.itch.tutorias.service;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Tutor;
import java.io.ByteArrayOutputStream;

public interface IConstanciaService {
    
    /**
     * Genera un PDF de constancia para un tutorado si cumple el 80% de asistencia.
     */
    byte[] generarConstanciaTutorado(Asignacion asignacion, Tutorado tutorado, double porcentaje) throws Exception;
    
    /**
     * Genera un PDF de constancia para el tutor que impartió la asignación.
     */
    byte[] generarConstanciaTutor(Asignacion asignacion, Tutor tutor) throws Exception;
}
