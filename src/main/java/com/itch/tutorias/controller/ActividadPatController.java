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

    @Autowired
    private ITutor tutorService;

    @GetMapping("/pat/actividades")
    public String listaActividades(
            @RequestParam(name = "periodoId", required = false) Integer periodoId,
            @RequestParam(name = "tutorId", required = false) Integer tutorId,
            @RequestParam(name = "tipoActividad", required = false) String tipoActividad,
            Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";
        
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        Usuario usuarioLogueado = usuarioOpt.orElse(null);
        
        Tutor tutorReal = null;
        boolean esAdmin = false;
        
        if (usuarioLogueado != null) {
             tutorReal = tutorService.buscarPorUsuario(usuarioLogueado).orElse(null);
             esAdmin = usuarioLogueado.getPerfiles().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase("ADMINISTRADOR"));
        }
        
        List<ActividadPat> actividades = new java.util.ArrayList<>();
        if (esAdmin) {
            actividades = actividadPatService.buscarTodas();
        } else if (tutorReal != null) {
            actividades = actividadPatService.buscarPorTutor(tutorReal.getId());
        }
        
        if (esAdmin || tutorReal != null) {
            Integer filterPeriodoId = periodoId;
            if (filterPeriodoId == null) {
                Optional<PeriodoSemestral> periodoOpt = periodoService.buscarActivo();
                if (periodoOpt.isPresent()) {
                    filterPeriodoId = periodoOpt.get().getId();
                }
            }
            
            Integer finalPeriodoId = filterPeriodoId;
            if (finalPeriodoId != null) {
                actividades.removeIf(a -> !a.getSesion().getAsignacion().getPeriodo().getId().equals(finalPeriodoId));
            } else {
                actividades.clear();
            }
        }

        if (tutorId != null && esAdmin) {
            actividades.removeIf(a -> !a.getSesion().getAsignacion().getTutor().getId().equals(tutorId));
        }

        if (tipoActividad != null && !tipoActividad.isEmpty()) {
            actividades.removeIf(a -> !a.getTipoActividad().name().equals(tipoActividad));
        }

        model.addAttribute("periodos", periodoService.buscarTodos());
        model.addAttribute("tutores", esAdmin ? tutorService.buscarTodos() : null);
        model.addAttribute("tiposActividad", ActividadPat.TipoActividad.values());
        model.addAttribute("filtroPeriodoId", periodoId == null && periodoService.buscarActivo().isPresent() ? periodoService.buscarActivo().get().getId() : periodoId);
        model.addAttribute("filtroTutorId", tutorId);
        model.addAttribute("filtroTipoActividad", tipoActividad);

        model.addAttribute("actividades", actividades);
        model.addAttribute("tutor", esAdmin ? null : usuarioLogueado);
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

    @GetMapping("/pat/detalle/{id}")
    public String detalleActividad(@PathVariable Integer id, Model model) {
        ActividadPat actividad = actividadPatService.buscarPorId(id);
        if (actividad == null) {
            return "redirect:/pat/actividades";
        }
        model.addAttribute("actividad", actividad);
        return "pat/detalleActividad";
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
