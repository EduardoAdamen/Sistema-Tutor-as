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

import com.itch.tutorias.service.IUsuario;

@Controller
@RequestMapping("/tutor")
public class TutorController {

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private IPeriodoSemestral periodoService;
    
    @Autowired
    private IUsuario usuarioService;

    @GetMapping("/mis-tutorados")
    public String misTutorados(Principal principal, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        Optional<Usuario> tutorOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (tutorOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No se pudo identificar tu cuenta de tutor.");
            return "redirect:/";
        }
        
        Usuario tutor = tutorOpt.get();

        PeriodoSemestral periodoActivo = periodoService.buscarActivo().orElse(null);
        if (periodoActivo == null) {
            attributes.addFlashAttribute("error", "No hay un periodo escolar activo en este momento.");
            return "redirect:/";
        }

        // Buscar asignaciones del tutor y filtrar por el periodo activo
        List<Asignacion> asignaciones = new java.util.ArrayList<>();
        if (tutor != null) {
            asignaciones = asignacionService.buscarPorTutor(tutor);
        }
        Asignacion asignacionActiva = asignaciones.stream()
                .filter(a -> a.getPeriodo().getId().equals(periodoActivo.getId()))
                .findFirst()
                .orElse(null);

        if (asignacionActiva == null) {
            attributes.addFlashAttribute("error", "No tienes ning?n grupo asignado para el periodo actual.");
            return "redirect:/";
        }

        return "redirect:/asignacion/ver/" + asignacionActiva.getId();
    }
}
