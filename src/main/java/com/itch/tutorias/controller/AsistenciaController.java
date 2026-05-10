package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {

    @Autowired
    private ISesion sesionService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/registrar/{sesionId}")
    public String registrarFormulario(@PathVariable Integer sesionId, Model model, RedirectAttributes attributes) {
        Sesion sesion = sesionService.buscarPorId(sesionId);
        if (sesion == null) {
            attributes.addFlashAttribute("error", "Sesión no encontrada.");
            return "redirect:/sesion/mis-sesiones";
        }

        List<AsignacionTutorado> asignacionTutorados = asignacionTutoradoService.buscarPorAsignacion(sesion.getAsignacion());
        List<RegistroAsistencia> asistenciasPrevias = registroAsistenciaService.buscarPorSesion(sesion);
        
        // Construir set de IDs con asistencia previa de tipo "presente"
        Set<Integer> presentesPrevios = new HashSet<>();
        for (RegistroAsistencia r : asistenciasPrevias) {
            if (r.getEstatusAsistencia() == RegistroAsistencia.EstatusAsistencia.presente) {
                presentesPrevios.add(r.getTutorado().getId());
            }
        }

        model.addAttribute("sesion", sesion);
        model.addAttribute("tutorados", asignacionTutorados);
        model.addAttribute("presentesPrevios", presentesPrevios);

        return "asistencia/registrarAsistencia";
    }

    @PostMapping("/guardar/{sesionId}")
    public String guardarAsistencias(
            @PathVariable Integer sesionId,
            @RequestParam(value = "presentes", required = false) List<Integer> presentes,
            RedirectAttributes attributes) {
        
        Sesion sesion = sesionService.buscarPorId(sesionId);
        if (sesion == null) {
            attributes.addFlashAttribute("error", "Sesión no encontrada.");
            return "redirect:/sesion/mis-sesiones";
        }

        Set<Integer> presentesSet = new HashSet<>();
        if (presentes != null) {
            presentesSet.addAll(presentes);
        }

        List<AsignacionTutorado> asignacionTutorados = asignacionTutoradoService.buscarPorAsignacion(sesion.getAsignacion());

        for (AsignacionTutorado at : asignacionTutorados) {
            Tutorado tutorado = at.getTutorado();
            
            // Si está en la lista de presentes → presente, si no → ausente
            RegistroAsistencia.EstatusAsistencia estado = presentesSet.contains(tutorado.getId())
                    ? RegistroAsistencia.EstatusAsistencia.presente
                    : RegistroAsistencia.EstatusAsistencia.ausente;
            
            // Si existe, actualizar; si no, crear
            RegistroAsistencia registro = registroAsistenciaService.buscarPorSesion(sesion).stream()
                    .filter(r -> r.getTutorado().getId().equals(tutorado.getId()))
                    .findFirst()
                    .orElse(new RegistroAsistencia());

            registro.setSesion(sesion);
            registro.setTutorado(tutorado);
            registro.setEstatusAsistencia(estado);
            
            registroAsistenciaService.guardar(registro);
        }

        attributes.addFlashAttribute("msg", "Asistencia registrada correctamente.");
        return "redirect:/sesion/ver/" + sesionId;
    }
}
