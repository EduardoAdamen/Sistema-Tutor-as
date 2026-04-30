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

import com.itch.tutorias.service.IUsuario;

@Controller
@RequestMapping("/tutorado")
public class TutoradoController {

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private IUsuario usuarioService;

    @GetMapping("/mi-tutoria")
    public String miTutoria(Principal principal, RedirectAttributes attributes) {
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
            relaciones = asignacionTutoradoService.buscarPorTutorado(tutorado);
        }
        Asignacion asignacionActiva = relaciones.stream()
                .map(AsignacionTutorado::getAsignacion)
                .filter(a -> a.getPeriodo().getId().equals(periodoActivo.getId()))
                .findFirst()
                .orElse(null);

        if (asignacionActiva == null) {
            attributes.addFlashAttribute("error", "No tienes grupo de tutor?a asignado en el periodo actual.");
            return "redirect:/";
        }

        return "redirect:/asignacion/ver/" + asignacionActiva.getId();
    }
}
