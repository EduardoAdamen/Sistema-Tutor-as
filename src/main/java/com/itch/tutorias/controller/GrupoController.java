package com.itch.tutorias.controller;

import com.itch.tutorias.model.Grupo;
import com.itch.tutorias.service.ICarrera;
import com.itch.tutorias.service.IGrupo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/grupo")
public class GrupoController {

    @Autowired
    private IGrupo grupoService;

    @Autowired
    private ICarrera carreraService;

    @GetMapping("/grupos")
    public String listaGrupos(Model model) {
        model.addAttribute("grupos", grupoService.buscarTodos());
        return "grupo/listaGrupos";
    }

    @GetMapping("/detalle/{id}")
    public String detalleGrupo(@PathVariable Integer id, Model model) {
        Grupo grupo = grupoService.buscarPorId(id).orElse(null);
        if (grupo == null) return "redirect:/grupo/grupos";
        
        model.addAttribute("grupo", grupo);
        return "grupo/detalleGrupo";
    }

    @GetMapping("/nuevo")
    public String nuevoGrupo(Model model) {
        model.addAttribute("grupo", new Grupo());
        model.addAttribute("carreras", carreraService.buscarTodas());
        return "grupo/formGrupo";
    }

    @PostMapping("/guardar")
    public String guardarGrupo(@ModelAttribute Grupo grupo, RedirectAttributes attributes) {
        grupoService.guardar(grupo);
        attributes.addFlashAttribute("msg", "Grupo guardado exitosamente.");
        return "redirect:/grupo/grupos";
    }

    @GetMapping("/editar/{id}")
    public String editarGrupo(@PathVariable Integer id, Model model) {
        Grupo grupo = grupoService.buscarPorId(id).orElse(null);
        if (grupo == null) return "redirect:/grupo/grupos";
        
        model.addAttribute("grupo", grupo);
        model.addAttribute("carreras", carreraService.buscarTodas());
        return "grupo/formGrupo";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarGrupo(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            grupoService.eliminar(id);
            attributes.addFlashAttribute("msg", "Grupo eliminado exitosamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se puede eliminar el grupo porque ya tiene asignaciones asociadas.");
        }
        return "redirect:/grupo/grupos";
    }
}
