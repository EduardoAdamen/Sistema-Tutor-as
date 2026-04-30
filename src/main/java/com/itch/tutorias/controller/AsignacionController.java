package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import java.util.List;
import java.util.Optional;

@Controller
public class AsignacionController {

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private ICarrera carreraService;

    @Autowired
    private IUsuario usuarioService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private ISesion sesionService;

    @Autowired
    private IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/asignacion/asignaciones")
    public String listaAsignaciones(@RequestParam(required = false) Integer periodoId, Model model) {
        List<PeriodoSemestral> periodos = periodoService.buscarTodos();
        List<Asignacion> asignaciones;

        if (periodoId != null) {
            PeriodoSemestral periodo = periodoService.buscarPorId(periodoId);
            asignaciones = asignacionService.buscarPorPeriodo(periodo);
        } else {
            Optional<PeriodoSemestral> activoOpt = periodoService.buscarActivo();
            if (activoOpt.isPresent()) {
                asignaciones = asignacionService.buscarPorPeriodo(activoOpt.get());
                periodoId = activoOpt.get().getId();
            } else {
                asignaciones = asignacionService.buscarTodas();
            }
        }

        model.addAttribute("periodos", periodos);
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("filtroPeriodoId", periodoId);

        return "asignacion/listaAsignaciones";
    }

    @GetMapping("/asignacion/nuevo")
    public String nuevaAsignacion(Model model, RedirectAttributes attributes) {
        Optional<PeriodoSemestral> periodoActivoOpt = periodoService.buscarActivo();
        if (periodoActivoOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No hay un periodo activo para crear asignaciones.");
            return "redirect:/asignacion/asignaciones";
        }

        model.addAttribute("asignacion", new Asignacion());
        model.addAttribute("tutores", usuarioService.buscarPorRolYEstado(Usuario.Rol.tutor, Usuario.EstadoUsuario.activo));
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("periodoActivo", periodoActivoOpt.get());

        return "asignacion/formAsignacion";
    }

    @PostMapping("/asignacion/guardar")
    public String guardarAsignacion(@ModelAttribute Asignacion asignacion, RedirectAttributes attributes) {
        PeriodoSemestral periodoActivoOpt = periodoService.buscarActivo().orElse(null);
        if (periodoActivoOpt == null) {
            attributes.addFlashAttribute("error", "No hay un periodo activo.");
            return "redirect:/asignacion/asignaciones";
        }
        
        asignacion.setPeriodo(periodoActivoOpt);

        if (asignacion.getId() == null) {
            // Es nuevo, validación RF-19
            if (asignacionService.existeDuplicado(
                    asignacion.getTutor(),
                    asignacion.getGrupo(),
                    asignacion.getHoraHorario(),
                    asignacion.getPeriodo())) {
                attributes.addFlashAttribute("error", "Ya existe una asignación para ese tutor, en el mismo grupo y horario en el periodo actual.");
                return "redirect:/asignacion/nuevo";
            }
        } else {
            Asignacion existente = asignacionService.buscarPorId(asignacion.getId());
            if (existente.getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
                attributes.addFlashAttribute("error", "No se puede editar una asignación de un periodo cerrado.");
                return "redirect:/asignacion/asignaciones";
            }
        }

        asignacionService.guardar(asignacion);
        attributes.addFlashAttribute("msg", "Asignación guardada exitosamente.");
        return "redirect:/asignacion/asignaciones";
    }

    @GetMapping("/asignacion/ver/{id}")
    public String detalleAsignacion(@PathVariable Integer id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        Usuario usuarioLogueado = usuarioService.buscarPorNumeroIdentificacion(principal.getName()).orElse(null);

        Asignacion asignacion = asignacionService.buscarPorId(id);
        List<AsignacionTutorado> tutorados = asignacionTutoradoService.buscarPorAsignacion(asignacion);
        List<Sesion> sesiones = sesionService.buscarPorAsignacion(asignacion);

        java.util.Map<Integer, Double> porcentajesAsistencia = new java.util.HashMap<>();
        for (AsignacionTutorado at : tutorados) {
            double porcentaje = registroAsistenciaService.calcularPorcentajeAsistencia(asignacion.getId(), at.getTutorado().getId());
            porcentajesAsistencia.put(at.getTutorado().getId(), porcentaje);
        }

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("asignacion", asignacion);
        model.addAttribute("asigTutorados", tutorados);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("porcentajes", porcentajesAsistencia);

        return "asignacion/detalleAsignacion";
    }

    @GetMapping("/asignacion/editar/{id}")
    public String editarAsignacion(@PathVariable Integer id, Model model, RedirectAttributes attributes) {
        Asignacion asignacion = asignacionService.buscarPorId(id);
        
        if (asignacion.getPeriodo().getEstatus() != PeriodoSemestral.EstatusPeriodo.activo) {
            attributes.addFlashAttribute("error", "Solo se pueden editar asignaciones del periodo activo.");
            return "redirect:/asignacion/asignaciones";
        }

        model.addAttribute("asignacion", asignacion);
        model.addAttribute("tutores", usuarioService.buscarPorRolYEstado(Usuario.Rol.tutor, Usuario.EstadoUsuario.activo));
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("periodoActivo", asignacion.getPeriodo());

        return "asignacion/formAsignacion";
    }

    @GetMapping("/asignacion/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable Integer id, RedirectAttributes attributes) {
        // Validación posible: Que no tenga tutorados o sesiones
        asignacionService.eliminar(id);
        attributes.addFlashAttribute("msg", "Asignación eliminada.");
        return "redirect:/asignacion/asignaciones";
    }

    @PostMapping("/asignacion/{id}/agregar-tutorado")
    public String agregarTutorado(@PathVariable Integer id, @RequestParam("numeroIdentificacion") String numId, RedirectAttributes attributes) {
        Asignacion asignacion = asignacionService.buscarPorId(id);
        Optional<Usuario> tutoradoOpt = usuarioService.buscarPorNumeroIdentificacion(numId);

        if (tutoradoOpt.isEmpty() || tutoradoOpt.get().getRol() != Usuario.Rol.tutorado || tutoradoOpt.get().getEstado() != Usuario.EstadoUsuario.activo) {
            attributes.addFlashAttribute("error", "El alumno con número de control no fue encontrado, no es tutorado o está inactivo.");
            return "redirect:/asignacion/ver/" + id;
        }

        Usuario tutorado = tutoradoOpt.get();
        if (asignacionTutoradoService.existeRelacion(asignacion, tutorado)) {
            attributes.addFlashAttribute("error", "El alumno ya se encuentra asignado a este grupo.");
            return "redirect:/asignacion/ver/" + id;
        }

        AsignacionTutorado relacion = new AsignacionTutorado();
        relacion.setAsignacion(asignacion);
        relacion.setTutorado(tutorado);
        asignacionTutoradoService.guardar(relacion);
        
        attributes.addFlashAttribute("msg", "Tutorado agregado exitosamente.");
        return "redirect:/asignacion/ver/" + id;
    }

    @GetMapping("/asignacion/{id}/quitar-tutorado/{tutoradoId}")
    public String quitarTutorado(@PathVariable Integer id, @PathVariable Integer tutoradoId, RedirectAttributes attributes) {
        Asignacion asignacion = asignacionService.buscarPorId(id);
        Usuario tutorado = usuarioService.buscarPorId(tutoradoId);
        
        // RF-21: Solo si no tiene asistencias asociadas a esta asignacion
        List<Sesion> sesiones = sesionService.buscarPorAsignacion(asignacion);
        boolean tieneAsistencias = false;
        
        for (Sesion s : sesiones) {
            if (registroAsistenciaService.existeRegistro(s, tutorado)) {
                tieneAsistencias = true;
                break;
            }
        }
        
        if (tieneAsistencias) {
            attributes.addFlashAttribute("error", "No se puede eliminar al tutorado porque ya tiene registros de asistencia en esta asignación.");
            return "redirect:/asignacion/ver/" + id;
        }

        // Buscar la relacion exacta
        List<AsignacionTutorado> relacionados = asignacionTutoradoService.buscarPorTutorado(tutorado);
        for (AsignacionTutorado at : relacionados) {
            if (at.getAsignacion().getId().equals(id)) {
                asignacionTutoradoService.eliminar(at.getId());
                break;
            }
        }

        attributes.addFlashAttribute("msg", "Tutorado retirado del grupo.");
        return "redirect:/asignacion/ver/" + id;
    }
}
