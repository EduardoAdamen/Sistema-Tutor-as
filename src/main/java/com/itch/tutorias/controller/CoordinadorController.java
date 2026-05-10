package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    @Autowired
    private ICoordinadorCarrera coordinadorService;

    @Autowired
    private IActividadPatGeneral actividadPatGeneralService;

    @Autowired
    private IActividadPatCarrera actividadPatCarreraService;

    @Autowired
    private IPlanSesionCarrera planSesionCarreraService;

    @Autowired
    private IUsuario usuarioService;

    // ============ ACTIVIDADES ADAPTADAS ============

    @GetMapping("/actividades")
    public String listaActividades(Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (usuarioOpt.isEmpty()) return "redirect:/";

        Usuario usuario = usuarioOpt.get();
        Optional<CoordinadorCarrera> coordOpt = coordinadorService.buscarPorUsuario(usuario);
        if (coordOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No tienes una carrera asignada como coordinador.");
            return "redirect:/";
        }

        CoordinadorCarrera coordinador = coordOpt.get();
        Carrera carrera = coordinador.getCarrera();

        List<ActividadPatGeneral> generales = actividadPatGeneralService.buscarTodas();
        List<ActividadPatCarrera> adaptadas = actividadPatCarreraService.buscarPorCarrera(carrera);

        model.addAttribute("generales", generales);
        model.addAttribute("adaptadas", adaptadas);
        model.addAttribute("carrera", carrera);
        model.addAttribute("coordinador", coordinador);

        return "coordinador/listaActividadesCarrera";
    }

    @GetMapping("/adaptar/{generalId}")
    public String formAdaptar(@PathVariable Integer generalId, Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (usuarioOpt.isEmpty()) return "redirect:/";

        Optional<CoordinadorCarrera> coordOpt = coordinadorService.buscarPorUsuario(usuarioOpt.get());
        if (coordOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No tienes una carrera asignada como coordinador.");
            return "redirect:/";
        }

        CoordinadorCarrera coordinador = coordOpt.get();
        ActividadPatGeneral general = actividadPatGeneralService.buscarPorId(generalId);

        if (actividadPatCarreraService.existeAdaptacion(generalId, coordinador.getCarrera().getId())) {
            attributes.addFlashAttribute("error", "Esta actividad ya fue adaptada para tu carrera.");
            return "redirect:/coordinador/actividades";
        }

        ActividadPatCarrera adaptacion = new ActividadPatCarrera();
        adaptacion.setActividadGeneral(general);
        adaptacion.setCarrera(coordinador.getCarrera());
        adaptacion.setTituloAdaptado(general.getTitulo());
        adaptacion.setDescripcionAdaptada(general.getDescripcion());

        model.addAttribute("adaptacion", adaptacion);
        model.addAttribute("general", general);
        model.addAttribute("carrera", coordinador.getCarrera());

        return "coordinador/formAdaptacion";
    }

    @PostMapping("/guardar")
    public String guardarAdaptacion(@ModelAttribute ActividadPatCarrera adaptacion, RedirectAttributes attributes) {
        boolean esNuevo = (adaptacion.getId() == null);
        if (!esNuevo) {
            ActividadPatCarrera existente = actividadPatCarreraService.buscarPorId(adaptacion.getId());
            adaptacion.setFechaAdaptacion(existente.getFechaAdaptacion());
        }
        actividadPatCarreraService.guardar(adaptacion);
        attributes.addFlashAttribute("msg", "Actividad adaptada para la carrera exitosamente.");
        return "redirect:/coordinador/actividades";
    }

    @GetMapping("/editar/{id}")
    public String editarAdaptacion(@PathVariable Integer id, Model model) {
        ActividadPatCarrera adaptacion = actividadPatCarreraService.buscarPorId(id);
        model.addAttribute("adaptacion", adaptacion);
        model.addAttribute("general", adaptacion.getActividadGeneral());
        model.addAttribute("carrera", adaptacion.getCarrera());
        return "coordinador/formAdaptacion";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAdaptacion(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            actividadPatCarreraService.eliminar(id);
            attributes.addFlashAttribute("msg", "Adaptación eliminada exitosamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se puede eliminar la adaptación porque ya está vinculada a sesiones.");
        }
        return "redirect:/coordinador/actividades";
    }

    // ============ PLAN DE SESIONES (10 SEMANAS) ============

    @GetMapping("/plan-sesiones")
    public String planSesiones(Principal principal, Model model, RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (usuarioOpt.isEmpty()) return "redirect:/";

        Optional<CoordinadorCarrera> coordOpt = coordinadorService.buscarPorUsuario(usuarioOpt.get());
        if (coordOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "No tienes una carrera asignada como coordinador.");
            return "redirect:/";
        }

        Carrera carrera = coordOpt.get().getCarrera();
        List<ActividadPatCarrera> adaptadas = actividadPatCarreraService.buscarPorCarrera(carrera);
        List<PlanSesionCarrera> plan = planSesionCarreraService.buscarPorCarrera(carrera);

        // Mapa simple: "sesion_1" → actividadCarreraId (Integer)
        Map<String, Integer> planMap = new HashMap<>();
        for (PlanSesionCarrera p : plan) {
            planMap.put("sesion_" + p.getNumeroSesion(), p.getActividadCarrera().getId());
        }

        model.addAttribute("carrera", carrera);
        model.addAttribute("adaptadas", adaptadas);
        model.addAttribute("planMap", planMap);

        return "coordinador/planSesiones";
    }

    @PostMapping("/plan-sesiones/guardar")
    public String guardarPlanSesiones(
            @RequestParam Map<String, String> allParams,
            Principal principal,
            RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
        if (usuarioOpt.isEmpty()) return "redirect:/";

        Optional<CoordinadorCarrera> coordOpt = coordinadorService.buscarPorUsuario(usuarioOpt.get());
        if (coordOpt.isEmpty()) return "redirect:/";

        Carrera carrera = coordOpt.get().getCarrera();

        for (int i = 1; i <= 10; i++) {
            String paramKey = "sesion_" + i;
            String actividadIdStr = allParams.get(paramKey);

            Optional<PlanSesionCarrera> existente = planSesionCarreraService.buscarPorCarreraYSesion(carrera, i);

            if (actividadIdStr != null && !actividadIdStr.isEmpty()) {
                Integer actividadId = Integer.parseInt(actividadIdStr);
                ActividadPatCarrera actividad = actividadPatCarreraService.buscarPorId(actividadId);

                PlanSesionCarrera plan = existente.orElse(new PlanSesionCarrera());
                plan.setCarrera(carrera);
                plan.setNumeroSesion(i);
                plan.setActividadCarrera(actividad);
                planSesionCarreraService.guardar(plan);
            } else if (existente.isPresent()) {
                // Limpiar la sesión si se deseleccionó
                planSesionCarreraService.eliminarPorCarreraYSesion(carrera, i);
            }
        }

        attributes.addFlashAttribute("msg", "Plan de sesiones actualizado exitosamente.");
        return "redirect:/coordinador/plan-sesiones";
    }
}
