package com.itch.tutorias.service;

import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Tutor;

import java.util.List;

public interface IReportePdfService {
    
    /**
     * Genera un reporte en PDF de las actividades PAT de un tutor en un periodo específico.
     */
    byte[] generarReporteActividadesPat(Tutor tutor, PeriodoSemestral periodo, List<SesionActividad> actividades) throws Exception;
}
