package com.itch.tutorias.controller;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.service.ICarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrera")
public class CarreraController {

    @Autowired
    private ICarrera carreraService;

    @GetMapping("/carreras")
    public String listar(Model model) {
        model.addAttribute("carreras", carreraService.buscarTodas());
        return "carrera/listaCarreras";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@ModelAttribute Carrera carrera) {
        return "carrera/formCarrera";
    }

    @PostMapping("/guardar")
    public String guardar(Carrera carrera, RedirectAttributes ra) {
        carreraService.guardar(carrera);
        ra.addFlashAttribute("msg", "Carrera guardada correctamente");
        return "redirect:/carrera/carreras";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        model.addAttribute("carrera", carreraService.buscarPorId(id));
        return "carrera/detalleCarrera";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("carrera", carreraService.buscarPorId(id));
        return "carrera/formCarrera";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        carreraService.eliminar(id);
        ra.addFlashAttribute("msg", "Carrera eliminada correctamente");
        return "redirect:/carrera/carreras";
    }
}
