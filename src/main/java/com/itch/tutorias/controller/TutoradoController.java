package com.itch.tutorias.controller;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.IAsignacionTutorado;
import com.itch.tutorias.service.IPeriodoSemestral;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.itch.tutorias.service.ITutorado;
import com.itch.tutorias.model.Tutorado;

@Controller
@RequestMapping("/tutorado")
public class TutoradoController {

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private ITutorado tutoradoService;
    
    @Autowired
    private com.itch.tutorias.service.IUsuario usuarioService;

    @Autowired
    private com.itch.tutorias.service.ISesion sesionService;

    @Autowired
    private com.itch.tutorias.service.IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/mi-tutoria")
    public String miTutoria(Principal principal, org.springframework.ui.Model model, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        Optional<Usuario> tutoradoOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (tutoradoOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No se pudo identificar tu cuenta de tutorado.");
            return "redirect:/";
        }
        
        Usuario tutorado = tutoradoOpt.get();

        PeriodoSemestral periodoActivo = periodoService.buscarActivo().orElse(null);
        if (periodoActivo == null) {
            attributes.addFlashAttribute("error", "No hay un periodo escolar activo en este momento.");
            return "redirect:/";
        }

        List<AsignacionTutorado> relaciones = new java.util.ArrayList<>();
        if (tutorado != null) {
            Optional<Tutorado> tOpt = tutoradoService.buscarPorUsuario(tutorado);
            if (tOpt.isPresent()) {
                relaciones = asignacionTutoradoService.buscarPorTutorado(tOpt.get());
            }
        }
        Asignacion asignacionActiva = relaciones.stream()
                .map(AsignacionTutorado::getAsignacion)
                .filter(a -> a.getPeriodo().getId().equals(periodoActivo.getId()))
                .findFirst()
                .orElse(null);

        if (asignacionActiva == null) {
            attributes.addFlashAttribute("error", "No tienes grupo de tutoría asignado en el periodo actual.");
            return "redirect:/";
        }

        List<com.itch.tutorias.model.Sesion> sesiones = sesionService.buscarPorAsignacion(asignacionActiva);
        java.util.Map<Integer, String> estadosAsistencia = new java.util.HashMap<>();
        
        for (com.itch.tutorias.model.Sesion s : sesiones) {
            java.util.List<com.itch.tutorias.model.RegistroAsistencia> asistenciasSesion = registroAsistenciaService.buscarPorSesion(s);
            for (com.itch.tutorias.model.RegistroAsistencia ra : asistenciasSesion) {
                if (ra.getTutorado().getId().equals(tutoradoOpt.flatMap(u -> tutoradoService.buscarPorUsuario(u)).get().getId())) {
                    estadosAsistencia.put(s.getId(), ra.getEstatusAsistencia().name());
                }
            }
        }
        
        double porcentaje = registroAsistenciaService.calcularPorcentajeAsistencia(asignacionActiva.getId(), tutoradoOpt.flatMap(u -> tutoradoService.buscarPorUsuario(u)).get().getId());

        model.addAttribute("asignacion", asignacionActiva);
        model.addAttribute("tutorado", tutoradoOpt.flatMap(u -> tutoradoService.buscarPorUsuario(u)).get());
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("estadosAsistencia", estadosAsistencia);
        model.addAttribute("porcentaje", porcentaje);

        return "tutorado/miTutoria";
    }

    @GetMapping("/sesiones")
    public String sesiones(Principal principal, org.springframework.ui.Model model, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        Optional<Usuario> tutoradoOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (tutoradoOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No se pudo identificar tu cuenta de tutorado.");
            return "redirect:/";
        }
        
        Usuario tutorado = tutoradoOpt.get();

        PeriodoSemestral periodoActivo = periodoService.buscarActivo().orElse(null);
        if (periodoActivo == null) {
            attributes.addFlashAttribute("error", "No hay un periodo escolar activo en este momento.");
            return "redirect:/";
        }

        List<AsignacionTutorado> relaciones = new java.util.ArrayList<>();
        if (tutorado != null) {
            Optional<Tutorado> tOpt = tutoradoService.buscarPorUsuario(tutorado);
            if (tOpt.isPresent()) {
                relaciones = asignacionTutoradoService.buscarPorTutorado(tOpt.get());
            }
        }
        Asignacion asignacionActiva = relaciones.stream()
                .map(AsignacionTutorado::getAsignacion)
                .filter(a -> a.getPeriodo().getId().equals(periodoActivo.getId()))
                .findFirst()
                .orElse(null);

        if (asignacionActiva == null) {
            attributes.addFlashAttribute("error", "No tienes grupo de tutoría asignado en el periodo actual.");
            return "redirect:/";
        }

        List<com.itch.tutorias.model.Sesion> sesiones = sesionService.buscarPorAsignacion(asignacionActiva);
        java.util.Map<Integer, String> estadosAsistencia = new java.util.HashMap<>();
        
        for (com.itch.tutorias.model.Sesion s : sesiones) {
            java.util.List<com.itch.tutorias.model.RegistroAsistencia> asistenciasSesion = registroAsistenciaService.buscarPorSesion(s);
            for (com.itch.tutorias.model.RegistroAsistencia ra : asistenciasSesion) {
                if (ra.getTutorado().getId().equals(tutoradoOpt.flatMap(u -> tutoradoService.buscarPorUsuario(u)).get().getId())) {
                    estadosAsistencia.put(s.getId(), ra.getEstatusAsistencia().name());
                }
            }
        }

        model.addAttribute("asignacion", asignacionActiva);
        model.addAttribute("sesiones", sesiones);
        model.addAttribute("estadosAsistencia", estadosAsistencia);
        model.addAttribute("periodo", periodoActivo);

        return "tutorado/sesionesRegistradas";
    }
}
