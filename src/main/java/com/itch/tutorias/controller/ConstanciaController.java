package com.itch.tutorias.controller;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.service.IAsignacion;
import com.itch.tutorias.service.IConstanciaService;
import com.itch.tutorias.service.IRegistroAsistencia;
import com.itch.tutorias.service.ITutor;
import com.itch.tutorias.service.ITutorado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/constancia")
public class ConstanciaController {

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private ITutorado tutoradoService;

    @Autowired
    private IRegistroAsistencia registroAsistenciaService;

    @Autowired
    private IConstanciaService constanciaService;

    @Autowired
    private com.itch.tutorias.service.ISesion sesionService;

    @GetMapping("/tutorado/{asignacionId}/{tutoradoId}")
    public ResponseEntity<byte[]> descargarConstanciaTutorado(@PathVariable Integer asignacionId, @PathVariable Integer tutoradoId) {
        try {
            Asignacion asignacion = asignacionService.buscarPorId(asignacionId);
            java.util.List<com.itch.tutorias.model.Sesion> sesiones = sesionService.buscarPorAsignacion(asignacion);
            if (sesiones.size() < 10) {
                throw new Exception("Se requieren al menos 10 sesiones registradas para generar la constancia.");
            }
            Tutorado tutorado = tutoradoService.buscarPorId(tutoradoId);
            double porcentaje = registroAsistenciaService.calcularPorcentajeAsistencia(asignacionId, tutoradoId);
            
            byte[] pdfBytes = constanciaService.generarConstanciaTutorado(asignacion, tutorado, porcentaje);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Constancia_Tutorado_" + tutorado.getUsuario().getNumeroIdentificacion() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling, could return a simple HTML page or redirect
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().getBytes());
        }
    }

    @GetMapping("/tutor/{asignacionId}")
    public ResponseEntity<byte[]> descargarConstanciaTutor(@PathVariable Integer asignacionId) {
        try {
            Asignacion asignacion = asignacionService.buscarPorId(asignacionId);
            java.util.List<com.itch.tutorias.model.Sesion> sesiones = sesionService.buscarPorAsignacion(asignacion);
            if (sesiones.size() < 10) {
                throw new Exception("El Tutor debe tener al menos 10 sesiones registradas para generar su constancia.");
            }
            Tutor tutor = asignacion.getTutor();
            
            byte[] pdfBytes = constanciaService.generarConstanciaTutor(asignacion, tutor);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Constancia_Tutor_" + tutor.getUsuario().getNumeroIdentificacion() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().getBytes());
        }
    }
}
