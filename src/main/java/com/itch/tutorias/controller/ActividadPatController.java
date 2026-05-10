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
    private ISesionActividad sesionActividadService;

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

    @Autowired
    private IActividadPatCarrera actividadPatCarreraService;

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
        
        List<SesionActividad> actividades = new java.util.ArrayList<>();
        if (esAdmin) {
            actividades = sesionActividadService.buscarTodas();
        } else if (tutorReal != null) {
            actividades = sesionActividadService.buscarPorTutor(tutorReal.getId());
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
            actividades.removeIf(a -> !a.getActividadCarrera().getActividadGeneral().getTipoActividad().name().equals(tipoActividad));
        }

        model.addAttribute("periodos", periodoService.buscarTodos());
        model.addAttribute("tutores", esAdmin ? tutorService.buscarTodos() : null);
        model.addAttribute("tiposActividad", ActividadPatGeneral.TipoActividad.values());
        model.addAttribute("filtroPeriodoId", periodoId == null && periodoService.buscarActivo().isPresent() ? periodoService.buscarActivo().get().getId() : periodoId);
        model.addAttribute("filtroTutorId", tutorId);
        model.addAttribute("filtroTipoActividad", tipoActividad);

        model.addAttribute("actividades", actividades);
        model.addAttribute("tutor", esAdmin ? null : usuarioLogueado);
        return "pat/listaActividades";
    }

    @GetMapping("/pat/detalle/{id}")
    public String detalleActividad(@PathVariable Integer id, Model model) {
        SesionActividad sa = sesionActividadService.buscarPorId(id);
        if (sa == null) {
            return "redirect:/pat/actividades";
        }
        model.addAttribute("actividad", sa);
        return "pat/detalleActividad";
    }

    @GetMapping("/pat/editar-vinculacion/{id}")
    public String editarVinculacion(@PathVariable Integer id, Model model, RedirectAttributes attributes) {
        SesionActividad sa = sesionActividadService.buscarPorId(id);
        if (sa.getSesion().getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se puede editar vinculaciones de periodos cerrados.");
            return "redirect:/pat/actividades";
        }
        
        model.addAttribute("sesionActividad", sa);
        return "pat/formEditarVinculacion";
    }

    @PostMapping("/pat/guardar-vinculacion")
    public String guardarVinculacion(
            @ModelAttribute SesionActividad sesionActividad,
            @RequestParam("archivoEvidencia") MultipartFile archivoEvidencia,
            RedirectAttributes attributes) {
                
        SesionActividad existente = sesionActividadService.buscarPorId(sesionActividad.getId());
        sesionActividad.setSesion(existente.getSesion());
        sesionActividad.setActividadCarrera(existente.getActividadCarrera());
        sesionActividad.setFechaRegistro(existente.getFechaRegistro());
        
        if (!archivoEvidencia.isEmpty()) {
            String nombreArchivo = servicioAlmacenamiento.guardar(archivoEvidencia, "actividades");
            sesionActividad.setEvidencia(nombreArchivo);
        } else {
            sesionActividad.setEvidencia(existente.getEvidencia());
        }

        sesionActividadService.guardar(sesionActividad);
        attributes.addFlashAttribute("msg", "Vinculación de actividad actualizada exitosamente.");
        
        return "redirect:/pat/actividades";
    }

    @GetMapping("/pat/eliminar/{id}")
    public String eliminarActividad(@PathVariable Integer id, RedirectAttributes attributes) {
        SesionActividad sa = sesionActividadService.buscarPorId(id);
        
        if (sa.getSesion().getAsignacion().getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se puede eliminar vinculaciones de periodos cerrados.");
            return "redirect:/pat/actividades";
        }

        if (sa.getEvidencia() != null) {
            servicioAlmacenamiento.eliminar(sa.getEvidencia(), "actividades");
        }

        sesionActividadService.eliminar(id);
        attributes.addFlashAttribute("msg", "Vinculación de actividad PAT eliminada.");
        return "redirect:/pat/actividades";
    }
}
