package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pat/general")
public class ActividadPatGeneralController {

    @Autowired
    private IActividadPatGeneral actividadPatGeneralService;

    @GetMapping("/actividades")
    public String listaActividades(Model model) {
        List<ActividadPatGeneral> actividades = actividadPatGeneralService.buscarTodas();
        model.addAttribute("actividades", actividades);
        model.addAttribute("tiposActividad", ActividadPatGeneral.TipoActividad.values());
        return "pat/general/listaActividadesGenerales";
    }

    @GetMapping("/nueva")
    public String nuevaActividad(Model model) {
        model.addAttribute("actividad", new ActividadPatGeneral());
        model.addAttribute("tiposActividad", ActividadPatGeneral.TipoActividad.values());
        return "pat/general/formActividadGeneral";
    }

    @PostMapping("/guardar")
    public String guardarActividad(@ModelAttribute ActividadPatGeneral actividad, RedirectAttributes attributes) {
        boolean esNuevo = (actividad.getId() == null);
        if (!esNuevo) {
            ActividadPatGeneral existente = actividadPatGeneralService.buscarPorId(actividad.getId());
            actividad.setFechaRegistro(existente.getFechaRegistro());
        }
        actividadPatGeneralService.guardar(actividad);
        attributes.addFlashAttribute("msg", "Actividad PAT General guardada exitosamente.");
        return "redirect:/pat/general/actividades";
    }

    @GetMapping("/editar/{id}")
    public String editarActividad(@PathVariable Integer id, Model model) {
        ActividadPatGeneral actividad = actividadPatGeneralService.buscarPorId(id);
        model.addAttribute("actividad", actividad);
        model.addAttribute("tiposActividad", ActividadPatGeneral.TipoActividad.values());
        return "pat/general/formActividadGeneral";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarActividad(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            actividadPatGeneralService.eliminar(id);
            attributes.addFlashAttribute("msg", "Actividad PAT General eliminada.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se puede eliminar la actividad porque ya tiene adaptaciones de carrera asociadas.");
        }
        return "redirect:/pat/general/actividades";
    }
}
