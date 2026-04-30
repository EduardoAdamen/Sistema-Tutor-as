package com.itch.tutorias.controller;

import com.itch.tutorias.model.PeriodoSemestral;
import com.itch.tutorias.service.IPeriodoSemestral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/periodo")
public class PeriodoController {

    @Autowired
    private IPeriodoSemestral periodoService;

    @GetMapping("/periodos")
    public String listar(Model model) {
        model.addAttribute("periodos", periodoService.buscarTodos());
        return "periodo/listaPeriodos";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@ModelAttribute PeriodoSemestral periodoSemestral) {
        return "periodo/formPeriodo";
    }

    @PostMapping("/guardar")
    public String guardar(PeriodoSemestral periodoSemestral, RedirectAttributes ra, Model model) {
        try {
            //Validar que no exista otro periodo activo
            if (periodoSemestral.getEstatus() == PeriodoSemestral.EstatusPeriodo.activo
                    && periodoSemestral.getId() == null
                    && periodoService.existePeriodoActivo()) {
                model.addAttribute("error", "Ya existe un periodo semestral activo.");
                return "periodo/formPeriodo";
            }
            periodoService.guardar(periodoSemestral);
            ra.addFlashAttribute("msg", "Periodo guardado correctamente");
            return "redirect:/periodo/periodos";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "periodo/formPeriodo";
        }
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        model.addAttribute("periodo", periodoService.buscarPorId(id));
        return "periodo/detallePeriodo";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("periodoSemestral", periodoService.buscarPorId(id));
        return "periodo/formPeriodo";
    }

    @GetMapping("/cerrar/{id}")
    public String cerrar(@PathVariable Integer id, RedirectAttributes ra) {
        periodoService.cerrar(id);
        ra.addFlashAttribute("msg", "Periodo cerrado correctamente");
        return "redirect:/periodo/periodos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            PeriodoSemestral periodo = periodoService.buscarPorId(id);
            //Solo permitir eliminar periodos cerrados
            periodoService.cerrar(id);
            ra.addFlashAttribute("msg", "Periodo eliminado correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/periodo/periodos";
    }
}
