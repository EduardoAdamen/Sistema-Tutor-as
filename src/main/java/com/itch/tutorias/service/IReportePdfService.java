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

    /**
     * Genera un reporte en PDF del catálogo de actividades PAT generales.
     */
    byte[] generarReporteActividadesGenerales(List<com.itch.tutorias.model.ActividadPatGeneral> actividades) throws Exception;
}
