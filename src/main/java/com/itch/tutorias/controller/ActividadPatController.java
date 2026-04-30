package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import java.util.List;
import java.util.Optional;

@Controller
public class ActividadPatController {

    @Autowired
    private IActividadPat actividadPatService;

    @Autowired
    private ISesion sesionService;

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private IServicioAlmacenamiento servicioAlmacenamiento;

    @Autowired
    private IUsuario usuarioService;

    @GetMapping("/pat/actividades")
    public String listaActividades(Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";
        
        Optional<Usuario> tutorOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        Usuario tutor = tutorOpt.orElse(null);
        
        List<ActividadPat> actividades = new java.util.ArrayList<>();
        if (tutor != null) {
            actividades = actividadPatService.buscarPorTutor(tutor.getId());
            
            // Filtramos para asegurar que sean actividades del periodo activo
            Optional<PeriodoSemestral> periodoOpt = periodoService.buscarActivo();
            if (periodoOpt.isPresent()) {
                PeriodoSemestral activo = periodoOpt.get();
                actividades.removeIf(a -> !a.getSesion().getAsignacion().getPeriodo().getId().equals(activo.getId()));
            } else {
                actividades.clear();
            }
        }

        model.addAttribute("actividades", actividades);
        model.addAttribute("tutor", tutor);
        return "pat/listaActividades";
    }

    @GetMapping("/pat/nueva/{sesionId}")
    public String nuevaActividad(@PathVariable Integer sesionId, Model model, RedirectAttributes attributes) {
        Sesion sesion = sesionService.buscarPorId(sesionId);
        if (sesion.getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se puede añadir actividades a sesiones de periodos cerrados.");
            return "redirect:/asignacion/ver/" + sesion.getAsignacion().getId();
        }

        ActividadPat actividad = new ActividadPat();
        actividad.setSesion(sesion);
        model.addAttribute("actividad", actividad);
        model.addAttribute("tiposActividad", ActividadPat.TipoActividad.values());
        return "pat/formActividad";
    }

    @PostMapping("/pat/guardar")
    public String guardarActividad(
            @ModelAttribute ActividadPat actividad,
            @RequestParam("archivoEvidencia") MultipartFile archivoEvidencia,
            RedirectAttributes attributes) {
                
        boolean esNuevo = (actividad.getId() == null);
        
        Sesion sesion = sesionService.buscarPorId(actividad.getSesion().getId());
        actividad.setSesion(sesion); // Asegurar el objeto completo
        
        if (sesion.getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
             attributes.addFlashAttribute("error", "Solo se puede registrar o editar en un periodo activo.");
             return "redirect:/asignacion/ver/" + sesion.getAsignacion().getId();
        }

        if (!esNuevo) {
             ActividadPat existente = actividadPatService.buscarPorId(actividad.getId());
             actividad.setFechaRegistro(existente.getFechaRegistro());
             if (archivoEvidencia.isEmpty()) {
                 actividad.setEvidencia(existente.getEvidencia());
             }
        }

        if (!archivoEvidencia.isEmpty()) {
            String nombreArchivo = servicioAlmacenamiento.guardar(archivoEvidencia, "actividades");
            actividad.setEvidencia(nombreArchivo);
        }

        actividadPatService.guardar(actividad);
        attributes.addFlashAttribute("msg", "Actividad del PAT guardada exitosamente.");
        
        return "redirect:/pat/actividades";
    }

    @GetMapping("/pat/editar/{id}")
    public String editarActividad(@PathVariable Integer id, Model model, RedirectAttributes attributes) {
        ActividadPat actividad = actividadPatService.buscarPorId(id);
        if (actividad.getSesion().getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se puede editar actividades de periodos cerrados.");
            return "redirect:/pat/actividades";
        }
        
        model.addAttribute("actividad", actividad);
        model.addAttribute("tiposActividad", ActividadPat.TipoActividad.values());
        return "pat/formActividad";
    }

    @GetMapping("/pat/eliminar/{id}")
    public String eliminarActividad(@PathVariable Integer id, RedirectAttributes attributes) {
        ActividadPat actividad = actividadPatService.buscarPorId(id);
        
        if (actividad.getSesion().getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se puede eliminar actividades de periodos cerrados.");
            return "redirect:/pat/actividades";
        }

        if (actividad.getEvidencia() != null) {
            servicioAlmacenamiento.eliminar(actividad.getEvidencia(), "actividades");
        }

        actividadPatService.eliminar(id);
        attributes.addFlashAttribute("msg", "Actividad PAT eliminada.");
        return "redirect:/pat/actividades";
    }
}
