package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sesion")
public class SesionController {

    @Autowired
    private ISesion sesionService;

    @Autowired
    private ISesionActividad sesionActividadService;

    @Autowired
    private IActividadPatCarrera actividadPatCarreraService;

    @Autowired
    private IPlanSesionCarrera planSesionCarreraService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private IRegistroAsistencia asistenciaService;

    @Autowired
    private IUsuario usuarioService;

    @Autowired
    private ITutor tutorService;

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private IServicioAlmacenamiento servicioAlmacenamiento;

    @GetMapping("/grupo/{asignacionId}")
    public String sesionesPorGrupo(@PathVariable Integer asignacionId, Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";
        
        Asignacion asignacion = asignacionService.buscarPorId(asignacionId);
        if (asignacion == null) {
            attributes.addFlashAttribute("error", "Grupo no encontrado.");
            return "redirect:/tutor/mis-grupos";
        }

        List<Sesion> sesiones = sesionService.buscarPorAsignacion(asignacion);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("asignacion", asignacion);
        return "sesion/listaSesiones";
    }

    @GetMapping("/nueva/{asignacionId}")
    public String nuevaSesion(@PathVariable Integer asignacionId, Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        Asignacion asignacionActiva = asignacionService.buscarPorId(asignacionId);
        if (asignacionActiva == null || asignacionActiva.getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "No se pueden crear sesiones para este grupo o periodo cerrado.");
            return "redirect:/tutor/mis-grupos";
        }

        List<Sesion> sesionesActuales = sesionService.buscarPorAsignacion(asignacionActiva);
        if (sesionesActuales.size() >= 10) {
            attributes.addFlashAttribute("error", "Se ha alcanzado el límite de 10 sesiones para este grupo.");
            return "redirect:/sesion/grupo/" + asignacionActiva.getId();
        }

        Sesion sesion = new Sesion();
        sesion.setAsignacion(asignacionActiva);

        // Auto-llenar hora inicio y fin desde la asignación
        sesion.setHoraInicio(asignacionActiva.getHoraHorario());
        sesion.setHoraFin(asignacionActiva.getHoraFinHorario());

        // Determinar número de sesión (siguiente)
        int siguienteNumSesion = sesionesActuales.size() + 1;

        // Buscar actividad planeada por el coordinador para esta sesión
        Carrera carreraGrupo = asignacionActiva.getGrupo().getCarrera();
        Optional<PlanSesionCarrera> planOpt = planSesionCarreraService.buscarPorCarreraYSesion(carreraGrupo, siguienteNumSesion);

        model.addAttribute("sesion", sesion);
        model.addAttribute("numSesion", siguienteNumSesion);
        model.addAttribute("actividadPlanificada", planOpt.orElse(null));
        return "sesion/formSesion";
    }

    @PostMapping("/guardar")
    public String guardarSesion(@ModelAttribute Sesion sesion,
                                @RequestParam("archivoEvidencia") MultipartFile archivoEvidencia,
                                @RequestParam(value = "actividadCarreraId", required = false) Integer actividadCarreraId,
                                Principal principal,
                                RedirectAttributes attributes) {
                                
        if (principal == null) return "redirect:/login";
        Asignacion asignacionActiva = asignacionService.buscarPorId(sesion.getAsignacion().getId());

        if (asignacionActiva == null) {
            return "redirect:/tutor/mis-grupos";
        }

        // Validar límite de 10 sesiones 
        if (sesion.getId() == null) {
            List<Sesion> sesionesActuales = sesionService.buscarPorAsignacion(asignacionActiva);
            if (sesionesActuales.size() >= 10) {
                attributes.addFlashAttribute("error", "Se ha alcanzado el límite de 10 sesiones para este grupo.");
                return "redirect:/sesion/grupo/" + asignacionActiva.getId();
            }
        }

        // Validar la fecha de la sesión dentro del periodo
        if (sesion.getFecha().isAfter(asignacionActiva.getPeriodo().getFechaFin()) || sesion.getFecha().isBefore(asignacionActiva.getPeriodo().getFechaInicio())) {
            attributes.addFlashAttribute("error", "La fecha de la sesión debe estar dentro del periodo escolar activo (" 
                    + asignacionActiva.getPeriodo().getFechaInicio() + " a " + asignacionActiva.getPeriodo().getFechaFin() + ").");
            return "redirect:/sesion/nueva/" + asignacionActiva.getId();
        }

        sesion.setAsignacion(asignacionActiva);

        // Auto-llenar horas desde la asignación (siempre)
        sesion.setHoraInicio(asignacionActiva.getHoraHorario());
        sesion.setHoraFin(asignacionActiva.getHoraFinHorario());

        if (!archivoEvidencia.isEmpty()) {
            String nombreArchivo = servicioAlmacenamiento.guardar(archivoEvidencia, "sesiones");
            sesion.setEvidencia(nombreArchivo);
        } else if (sesion.getId() != null) {
            Sesion actual = sesionService.buscarPorId(sesion.getId());
            sesion.setEvidencia(actual.getEvidencia());
        }

        Sesion sesionGuardada = sesionService.guardar(sesion);

        // Vincular la actividad del plan del coordinador (solo sesiones nuevas)
        if (sesion.getId() == null && actividadCarreraId != null) {
            ActividadPatCarrera actCarrera = actividadPatCarreraService.buscarPorId(actividadCarreraId);
            if (actCarrera != null) {
                SesionActividad sa = new SesionActividad();
                sa.setSesion(sesionGuardada);
                sa.setActividadCarrera(actCarrera);
                sesionActividadService.guardar(sa);
            }
        }

        attributes.addFlashAttribute("msg", "Sesión guardada exitosamente.");
        return "redirect:/sesion/grupo/" + asignacionActiva.getId();
    }

    @GetMapping("/editar/{id}")
    public String editarSesion(@PathVariable Integer id, Model model, Principal principal, RedirectAttributes attributes) {
        Sesion sesion = sesionService.buscarPorId(id);
        if (sesion == null) {
            attributes.addFlashAttribute("error", "Sesión no encontrada.");
            return "redirect:/sesion/mis-sesiones";
        }
        model.addAttribute("sesion", sesion);

        // Determinar número de sesión para mostrar actividad vinculada
        List<Sesion> todas = sesionService.buscarPorAsignacion(sesion.getAsignacion());
        int numSesion = 0;
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getId().equals(sesion.getId())) {
                numSesion = i + 1;
                break;
            }
        }

        List<SesionActividad> actividadesVinculadas = sesionActividadService.buscarPorSesion(sesion);
        model.addAttribute("numSesion", numSesion);
        model.addAttribute("actividadPlanificada", actividadesVinculadas.isEmpty() ? null : actividadesVinculadas.get(0));

        return "sesion/formSesion";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarSesion(@PathVariable Integer id, RedirectAttributes attributes) {
        Sesion sesion = sesionService.buscarPorId(id);
        Integer asignacionId = null;
        if (sesion != null) {
            asignacionId = sesion.getAsignacion().getId();
            if (sesion.getEvidencia() != null && !sesion.getEvidencia().isEmpty()) {
                servicioAlmacenamiento.eliminar(sesion.getEvidencia(), "sesiones");
            }
            sesionService.eliminar(id);
            attributes.addFlashAttribute("msg", "Sesión eliminada correctamente.");
        }
        return asignacionId != null ? "redirect:/sesion/grupo/" + asignacionId : "redirect:/tutor/mis-grupos";
    }

    @GetMapping("/ver/{id}")
    public String verSesion(@PathVariable Integer id, Model model, Principal principal, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";
        Usuario usuarioLogueado = usuarioService.buscarPorNumeroIdentificacion(principal.getName()).orElse(null);
        
        Sesion sesion = sesionService.buscarPorId(id);
        if (sesion == null) {
            attributes.addFlashAttribute("error", "Sesión no encontrada.");
            return "redirect:/";
        }
        
        List<SesionActividad> actividades = sesionActividadService.buscarPorSesion(sesion);
        List<AsignacionTutorado> tutorados = asignacionTutoradoService.buscarPorAsignacion(sesion.getAsignacion());
        List<RegistroAsistencia> asistencias = asistenciaService.buscarPorSesion(sesion);

        // Determinar número de sesión
        List<Sesion> todas = sesionService.buscarPorAsignacion(sesion.getAsignacion());
        int numSesion = 0;
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getId().equals(sesion.getId())) {
                numSesion = i + 1;
                break;
            }
        }

        model.addAttribute("sesion", sesion);
        model.addAttribute("numSesion", numSesion);
        model.addAttribute("actividades", actividades);
        model.addAttribute("tutorados", tutorados);
        model.addAttribute("asistencias", asistencias);
        return "sesion/detalleSesion";
    }
}
