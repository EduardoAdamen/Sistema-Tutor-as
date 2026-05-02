package com.itch.tutorias.controller;

import com.itch.tutorias.model.Asignacion;
import com.itch.tutorias.model.AsignacionTutorado;
import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.IAsignacion;
import com.itch.tutorias.service.IAsignacionTutorado;
import com.itch.tutorias.service.IPeriodoSemestral;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.itch.tutorias.service.ITutor;
import com.itch.tutorias.model.Tutor;

@Controller
@RequestMapping("/tutor")
public class TutorController {

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private IPeriodoSemestral periodoService;
    
    @Autowired
    private ITutor tutorService;
    
    @Autowired
    private com.itch.tutorias.service.IUsuario usuarioService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private com.itch.tutorias.service.IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/mis-grupos")
    public String misGrupos(Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        Optional<Usuario> tutorOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (tutorOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No se pudo identificar tu cuenta de tutor.");
            return "redirect:/";
        }
        
        Usuario tutor = tutorOpt.get();

        List<Asignacion> asignaciones = new java.util.ArrayList<>();
        if (tutor != null) {
            Optional<Tutor> tOpt = tutorService.buscarPorUsuario(tutor);
            if (tOpt.isPresent()) {
                asignaciones = asignacionService.buscarPorTutor(tOpt.get());
            }
        }

        model.addAttribute("asignaciones", asignaciones);
        return "tutor/misGrupos";
    }

    @GetMapping("/grupo/{id}")
    public String detalleGrupoTutor(@org.springframework.web.bind.annotation.PathVariable Integer id, Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        Asignacion asignacion = asignacionService.buscarPorId(id);
        if (asignacion == null) {
            attributes.addFlashAttribute("error", "Grupo no encontrado.");
            return "redirect:/tutor/mis-grupos";
        }

        List<AsignacionTutorado> tutorados = asignacionTutoradoService.buscarPorAsignacion(asignacion);
        
        java.util.Map<Integer, Double> porcentajesAsistencia = new java.util.HashMap<>();
        for (AsignacionTutorado at : tutorados) {
            double porcentaje = registroAsistenciaService.calcularPorcentajeAsistencia(asignacion.getId(), at.getTutorado().getId());
            porcentajesAsistencia.put(at.getTutorado().getId(), porcentaje);
        }

        model.addAttribute("asignacion", asignacion);
        model.addAttribute("tutorados", tutorados);
        model.addAttribute("porcentajes", porcentajesAsistencia);

        return "tutor/detalleGrupo";
    }
}
