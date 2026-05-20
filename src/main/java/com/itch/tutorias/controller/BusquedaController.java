package com.itch.tutorias.controller;

import com.itch.tutorias.model.*;
import com.itch.tutorias.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class BusquedaController {

    @Autowired
    private IUsuario usuarioService;

    @Autowired
    private ITutor tutorService;

    @Autowired
    private ITutorado tutoradoService;

    @Autowired
    private IPeriodoSemestral periodoService;

    @Autowired
    private IAsignacion asignacionService;

    @Autowired
    private IAsignacionTutorado asignacionTutoradoService;

    @Autowired
    private ISesionActividad sesionActividadService;

    @Autowired
    private ICarrera carreraService;

    @Autowired
    private IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/busqueda/tutores") 
    public String buscarTutores(
            @RequestParam(required = false) Integer periodoId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer carreraId,
            Model model) {
            
        PeriodoSemestral activo = periodoService.buscarActivo().orElse(null);
        if (periodoId == null && activo != null) {
            periodoId = activo.getId();
        }

        model.addAttribute("periodos", periodoService.buscarTodos());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("filtroPeriodoId", periodoId);
        model.addAttribute("filtroNombre", nombre);
        model.addAttribute("filtroCarreraId", carreraId);

        List<Asignacion> asignaciones = new java.util.ArrayList<>();
        if (periodoId != null) {
            PeriodoSemestral p = periodoService.buscarPorId(periodoId);
            if (p != null) {
                asignaciones = asignacionService.buscarPorPeriodo(p);
            }
        } else {
            asignaciones = asignacionService.buscarTodas();
        }

        if (nombre != null && !nombre.trim().isEmpty()) {
            String nameLower = nombre.toLowerCase().trim();
            asignaciones = asignaciones.stream()
                .filter(asig -> asig.getTutor().getUsuario().getNombreCompleto().toLowerCase().contains(nameLower))
                .collect(Collectors.toList());
        }

        if (carreraId != null) {
            asignaciones = asignaciones.stream()
                .filter(asig -> asig.getGrupo().getCarrera().getId().equals(carreraId))
                .collect(Collectors.toList());
        }

        model.addAttribute("asignaciones", asignaciones);
        return "busqueda/busquedaTutores";
    }

    @GetMapping("/busqueda/tutorado") 
    public String buscarTutorado(
            @RequestParam(required = false) String numeroControl,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer carreraId,
            Model model) {
            
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("filtroNumeroControl", numeroControl);
        model.addAttribute("filtroNombre", nombre);
        model.addAttribute("filtroCarreraId", carreraId);

        boolean tieneFiltro = (numeroControl != null && !numeroControl.trim().isEmpty()) 
            || (nombre != null && !nombre.trim().isEmpty()) 
            || (carreraId != null);
            
        if (tieneFiltro) {
            List<Tutorado> tutorados = tutoradoService.buscarTodos();

            if (numeroControl != null && !numeroControl.trim().isEmpty()) {
                String ncTrim = numeroControl.trim();
                tutorados = tutorados.stream()
                    .filter(t -> t.getUsuario().getNumeroIdentificacion().equals(ncTrim))
                    .collect(Collectors.toList());
            }

            if (nombre != null && !nombre.trim().isEmpty()) {
                String nameLower = nombre.toLowerCase().trim();
                tutorados = tutorados.stream()
                    .filter(t -> t.getUsuario().getNombreCompleto().toLowerCase().contains(nameLower))
                    .collect(Collectors.toList());
            }

            if (carreraId != null) {
                tutorados = tutorados.stream()
                    .filter(t -> t.getCarrera().getId().equals(carreraId))
                    .collect(Collectors.toList());
            }

            // Find current active assignment for each tutorado
            PeriodoSemestral periodoActivo = periodoService.buscarActivo().orElse(null);
            java.util.Map<Integer, Asignacion> asignacionesMap = new java.util.HashMap<>();
            if (periodoActivo != null) {
                for (Tutorado t : tutorados) {
                    List<AsignacionTutorado> relaciones = asignacionTutoradoService.buscarPorTutorado(t);
                    AsignacionTutorado activa = relaciones.stream()
                        .filter(r -> r.getAsignacion().getPeriodo().getId().equals(periodoActivo.getId()))
                        .findFirst()
                        .orElse(null);
                    if (activa != null) {
                        asignacionesMap.put(t.getId(), activa.getAsignacion());
                    }
                }
            }

            model.addAttribute("resultadosTutorados", tutorados);
            model.addAttribute("asignacionesMap", asignacionesMap);
        }

        return "busqueda/busquedaTutorado";
    }

    @GetMapping("/busqueda/pat") 
    public String buscarPat(
            @RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fin,
            @RequestParam(required = false) ActividadPatGeneral.TipoActividad tipo,
            @RequestParam(required = false) Integer carreraId,
            @RequestParam(required = false) Integer tutorId,
            Model model) {
            
        model.addAttribute("tiposActividad", ActividadPatGeneral.TipoActividad.values());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("tutores", tutorService.buscarTodos());
        
        model.addAttribute("filtroInicio", inicio);
        model.addAttribute("filtroFin", fin);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroCarreraId", carreraId);
        model.addAttribute("filtroTutorId", tutorId);
        
        List<SesionActividad> resultados;
        
        
        if (inicio != null && fin != null) {
            resultados = sesionActividadService.buscarPorRangoFechas(inicio, fin);
        } else {
            resultados = sesionActividadService.buscarTodas(); 
        }

    
        if (tipo != null) {
            resultados = resultados.stream().filter(a -> a.getActividadCarrera().getActividadGeneral().getTipoActividad() == tipo).collect(Collectors.toList());
        }
        if (carreraId != null) {
            resultados = resultados.stream().filter(a -> 
                a.getSesion().getAsignacion().getGrupo().getCarrera().getId().equals(carreraId)).collect(Collectors.toList());
        }
        if (tutorId != null) {
            resultados = resultados.stream().filter(a -> 
                a.getSesion().getAsignacion().getTutor().getId().equals(tutorId)).collect(Collectors.toList());
        }

        model.addAttribute("actividades", resultados);
        return "busqueda/busquedaPAT";
    }
}
