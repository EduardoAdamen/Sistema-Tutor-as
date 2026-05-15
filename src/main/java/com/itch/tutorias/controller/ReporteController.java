package com.itch.tutorias.controller;

import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.SesionActividad;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.IPeriodoSemestral;
import com.itch.tutorias.service.IReportePdfService;
import com.itch.tutorias.service.ISesionActividad;
import com.itch.tutorias.service.ITutor;
import com.itch.tutorias.service.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reporte")
public class ReporteController {

    @Autowired
    private IReportePdfService reportePdfService;

    @Autowired
    private ISesionActividad sesionActividadService;

    @Autowired
    private IUsuario usuarioService;

    @Autowired
    private ITutor tutorService;

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private com.itch.tutorias.service.IActividadPatGeneral actividadPatGeneralService;

    @GetMapping("/actividades-generales")
    public ResponseEntity<byte[]> descargarReporteActividadesGenerales() {
        try {
            List<com.itch.tutorias.model.ActividadPatGeneral> actividades = actividadPatGeneralService.buscarTodas();
            byte[] pdfBytes = reportePdfService.generarReporteActividadesGenerales(actividades);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Actividades_PAT_Generales_Catalogo.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().getBytes());
        }
    }

    @GetMapping("/actividades")
    public ResponseEntity<byte[]> descargarReporteActividades(
            @RequestParam(name = "periodoId", required = false) Integer periodoId,
            @RequestParam(name = "tutorId", required = false) Integer tutorId,
            Principal principal) {
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
            Usuario usuarioLogueado = usuarioOpt.orElseThrow(() -> new Exception("Usuario no encontrado"));
            
            Tutor tutor = null;
            boolean esAdmin = usuarioLogueado.getPerfiles().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase("ADMINISTRADOR"));
            
            if (esAdmin) {
                if (tutorId != null) {
                    tutor = tutorService.buscarPorId(tutorId);
                }
            } else {
                tutor = tutorService.buscarPorUsuario(usuarioLogueado).orElseThrow(() -> new Exception("Tutor no encontrado"));
            }
            
            PeriodoSemestral periodo;
            if (periodoId != null) {
                periodo = periodoService.buscarPorId(periodoId);
            } else {
                periodo = periodoService.buscarActivo().orElseThrow(() -> new Exception("No hay periodo activo"));
            }
            
            List<SesionActividad> actividades;
            if (tutor != null) {
                actividades = sesionActividadService.buscarPorTutor(tutor.getId());
            } else {
                actividades = sesionActividadService.buscarTodas();
            }
            final Integer pId = periodo.getId();
            actividades.removeIf(a -> !a.getSesion().getAsignacion().getPeriodo().getId().equals(pId));
            
            byte[] pdfBytes = reportePdfService.generarReporteActividadesPat(tutor, periodo, actividades);

            String filename = tutor != null ? "Actividades_PAT_" + tutor.getUsuario().getNumeroIdentificacion() : "Actividades_PAT_Generales";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", filename + "_" + periodo.getClave() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().getBytes());
        }
    }
}
