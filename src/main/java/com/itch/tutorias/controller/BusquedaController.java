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
    private IActividadPat actividadPatService;

    @Autowired
    private ICarrera carreraService;

    @Autowired
    private IRegistroAsistencia registroAsistenciaService;

    @GetMapping("/busqueda/tutores") 
    public String buscarTutores(@RequestParam(required = false) Integer periodoId, Model model) {
        model.addAttribute("periodos", periodoService.buscarTodos());
        model.addAttribute("filtroPeriodoId", periodoId);

        if (periodoId != null) {
            PeriodoSemestral p = periodoService.buscarPorId(periodoId);
            List<Asignacion> asignaciones = asignacionService.buscarPorPeriodo(p);
            
            model.addAttribute("asignaciones", asignaciones);
        }

        return "busqueda/busquedaTutores";
    }

    @GetMapping("/busqueda/tutorado") 
    public String buscarTutorado(@RequestParam(required = false) String numeroControl, Model model) {
        model.addAttribute("filtroNumeroControl", numeroControl);

        if (numeroControl != null && !numeroControl.trim().isEmpty()) {
            Optional<Usuario> uOpt = usuarioService.buscarPorNumeroIdentificacion(numeroControl);
            if (uOpt.isPresent()) {
                Optional<Tutorado> tutoradoOpt = tutoradoService.buscarPorUsuario(uOpt.get());
                if (tutoradoOpt.isPresent()) {
                    Tutorado tutorado = tutoradoOpt.get();
                    model.addAttribute("tutorado", tutorado.getUsuario());
                    
                    List<AsignacionTutorado> historico = asignacionTutoradoService.buscarPorTutorado(tutorado);
                    
                    // Mapear cada asignación con su porcentaje de asistencia
                    java.util.Map<Integer, Double> promedios = new java.util.HashMap<>();
                    for (AsignacionTutorado at : historico) {
                        double pct = registroAsistenciaService.calcularPorcentajeAsistencia(at.getAsignacion().getId(), tutorado.getId());
                        promedios.put(at.getAsignacion().getId(), pct);
                    }
                    
                    model.addAttribute("historialAsignaciones", historico);
                    model.addAttribute("porcentajes", promedios);
                } else {
                    model.addAttribute("error", "No se encontró un tutorado con ese número de control.");
                }
            } else {
                model.addAttribute("error", "No se encontró un usuario con ese número de control.");
            }
        }

        return "busqueda/busquedaTutorado";
    }

    @GetMapping("/busqueda/pat") 
    public String buscarPat(
            @RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fin,
            @RequestParam(required = false) ActividadPat.TipoActividad tipo,
            @RequestParam(required = false) Integer carreraId,
            @RequestParam(required = false) Integer tutorId,
            Model model) {
            
        model.addAttribute("tiposActividad", ActividadPat.TipoActividad.values());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("tutores", tutorService.buscarTodos());
        
        model.addAttribute("filtroInicio", inicio);
        model.addAttribute("filtroFin", fin);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroCarreraId", carreraId);
        model.addAttribute("filtroTutorId", tutorId);
        
        List<ActividadPat> resultados;
        
        
        if (inicio != null && fin != null) {
            resultados = actividadPatService.buscarPorRangoFechas(inicio, fin);
        } else {
            resultados = actividadPatService.buscarTodas(); 
        }

    
        if (tipo != null) {
            resultados = resultados.stream().filter(a -> a.getTipoActividad() == tipo).collect(Collectors.toList());
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
