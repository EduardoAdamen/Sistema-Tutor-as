package com.itch.tutorias.controller;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.CoordinadorCarrera;
import com.itch.tutorias.model.Perfil;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.ICarrera;
import com.itch.tutorias.service.ICoordinadorCarrera;
import com.itch.tutorias.service.IPerfil;
import com.itch.tutorias.service.IServicioAlmacenamiento;
import com.itch.tutorias.service.ITutor;
import com.itch.tutorias.service.ITutorado;
import com.itch.tutorias.service.IUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.springframework.http.ResponseEntity;

@Controller
public class UsuarioController {

    @Autowired
    private IUsuario usuarioService;

    @Autowired
    private ICarrera carreraService;
    
    @Autowired
    private IPerfil perfilService;
    
    @Autowired
    private ITutor tutorService;
    
    @Autowired
    private ITutorado tutoradoService;

    @Autowired
    private ICoordinadorCarrera coordinadorCarreraService;

    @Autowired
    private IServicioAlmacenamiento servicioAlmacenamiento;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("carreras", carreraService.buscarTodas());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam Integer carreraId,
            @RequestParam Integer semestre,
            RedirectAttributes attributes) {
        
        if (usuarioService.existeCorreo(email) || usuarioService.existeNumeroIdentificacion(username)) {
            attributes.addFlashAttribute("error", "El usuario o correo ya existe.");
            return "redirect:/registro";
        }
        
        Usuario usuario = new Usuario();
        usuario.setNumeroIdentificacion(username);
        usuario.setContrasenaHash(passwordEncoder.encode(password));
        usuario.setCorreo(email);
        usuario.setNombreCompleto(nombre);
        usuario.setEstado(Usuario.EstadoUsuario.activo);
        
        String role = "TUTORADO";
        Optional<Perfil> perfilOpt = perfilService.buscarPorNombre(role);
        if (perfilOpt.isPresent()) {
            usuario.setPerfiles(Set.of(perfilOpt.get()));
        } else {
            Perfil nuevoPerfil = new Perfil(role);
            perfilService.guardar(nuevoPerfil);
            usuario.setPerfiles(Set.of(nuevoPerfil));
        }

        usuario = usuarioService.guardar(usuario);
        
        Tutorado tutorado = new Tutorado();
        tutorado.setUsuario(usuario);
        tutorado.setCarrera(carreraService.buscarPorId(carreraId));
        tutorado.setSemestre(semestre);
        tutoradoService.guardar(tutorado);
        
        attributes.addFlashAttribute("msg", "Registro exitoso. Inicia sesión.");
        return "redirect:/login";
    }

    @GetMapping("/usuario/usuarios")
    public String listaUsuarios() {
        // Redirigir a la vista de administradores (ya no existe vista general)
        return "redirect:/usuario/administradores";
    }

    // ============ VISTAS SEPARADAS POR ROL ============

    @GetMapping("/usuario/administradores")
    public String listaAdministradores(Model model) {
        List<Usuario> admins = usuarioService.buscarPorPerfilYEstado("ADMINISTRADOR", Usuario.EstadoUsuario.activo);
        model.addAttribute("usuarios", admins);
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        return "usuario/listaAdministradores";
    }

    @GetMapping("/usuario/coordinadores")
    public String listaCoordinadores(Model model) {
        List<Usuario> coordinadores = usuarioService.buscarPorPerfilYEstado("COORDINADOR_CARRERA", Usuario.EstadoUsuario.activo);
        // Enriquecer con la carrera asignada
        java.util.Map<Integer, String> carrerasMap = new java.util.HashMap<>();
        for (Usuario u : coordinadores) {
            coordinadorCarreraService.buscarPorUsuario(u).ifPresent(c ->
                carrerasMap.put(u.getId(), c.getCarrera().getNombre())
            );
        }
        model.addAttribute("usuarios", coordinadores);
        model.addAttribute("carrerasMap", carrerasMap);
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        return "usuario/listaCoordinadores";
    }

    @GetMapping("/usuario/tutores")
    public String listaTutores(Model model) {
        List<Usuario> tutores = usuarioService.buscarPorPerfilYEstado("TUTOR", Usuario.EstadoUsuario.activo);
        model.addAttribute("usuarios", tutores);
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        return "usuario/listaTutores";
    }

    @GetMapping("/usuario/tutorados")
    public String listaTutorados(
            @RequestParam(required = false) Integer carreraId,
            Model model) {
        List<Usuario> tutorados = usuarioService.buscarPorPerfilYEstado("TUTORADO", Usuario.EstadoUsuario.activo);
        // Enriquecer con carrera y semestre
        java.util.Map<Integer, String> carrerasMap = new java.util.HashMap<>();
        java.util.Map<Integer, Integer> semestresMap = new java.util.HashMap<>();
        for (Usuario u : tutorados) {
            tutoradoService.buscarPorUsuario(u).ifPresent(t -> {
                if (t.getCarrera() != null) carrerasMap.put(u.getId(), t.getCarrera().getNombre());
                semestresMap.put(u.getId(), t.getSemestre());
            });
        }
        if (carreraId != null) {
            tutorados = tutorados.stream().filter(u -> {
                Optional<Tutorado> tOpt = tutoradoService.buscarPorUsuario(u);
                return tOpt.isPresent() && tOpt.get().getCarrera() != null && tOpt.get().getCarrera().getId().equals(carreraId);
            }).collect(Collectors.toList());
        }
        model.addAttribute("usuarios", tutorados);
        model.addAttribute("carrerasMap", carrerasMap);
        model.addAttribute("semestresMap", semestresMap);
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("filtroCarreraId", carreraId);
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        return "usuario/listaTutorados";
    }

    @GetMapping("/usuario/nuevo")
    public String formularioNuevoUsuario(
            @RequestParam(value = "rol", required = false) String rol,
            Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("perfiles", perfilService.buscarTodos());
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        model.addAttribute("rolPreseleccionado", rol);
        // Resolver el ID del perfil para el hidden input
        if (rol != null) {
            perfilService.buscarPorNombre(rol).ifPresent(p ->
                model.addAttribute("rolPerfilId", p.getId())
            );
        }
        return "usuario/formUsuario";
    }

    @PostMapping("/usuario/guardar")
    public String guardarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "perfilesIds", required = false) List<Integer> perfilesIds,
            @RequestParam(value = "carreraId", required = false) Integer carreraId,
            @RequestParam("archivoFoto") MultipartFile archivoFoto,
            RedirectAttributes attributes) {

        boolean esNuevo = (usuario.getId() == null);
        Usuario usuarioExistente = null;

        if (perfilesIds != null && !perfilesIds.isEmpty()) {
            Set<Perfil> pSet = new HashSet<>();
            for (Integer pId : perfilesIds) {
                
                perfilService.buscarTodos().stream().filter(p -> p.getId().equals(pId)).findFirst().ifPresent(pSet::add);
            }
            usuario.setPerfiles(pSet);
        }

        // Validaciones manuales
        if (esNuevo) {
            Usuario usuarioOpt = null;
            try {
                Optional<Usuario> o = usuarioService.buscarPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
                if (o.isPresent()) {
                    usuarioOpt = o.get();
                }
            } catch (Exception e) {}
            
            if (usuarioOpt != null) {
                if (usuarioOpt.getEstado() == Usuario.EstadoUsuario.inactivo) {
                    usuarioOpt.setEstado(Usuario.EstadoUsuario.activo);
                    usuarioOpt.setNombreCompleto(usuario.getNombreCompleto());
                    usuarioOpt.setCorreo(usuario.getCorreo());
                    usuarioOpt.setPerfiles(usuario.getPerfiles());
                    
                    if (!usuarioOpt.getCorreo().equals(usuario.getCorreo()) && usuarioService.existeCorreo(usuario.getCorreo())) {
                        attributes.addFlashAttribute("error", "El correo proporcionado ya está registrado por otro usuario.");
                        return "redirect:/usuario/nuevo";
                    }
                    
                    if (!archivoFoto.isEmpty()) {
                        String nombreArchivo = servicioAlmacenamiento.guardar(archivoFoto, "usuarios");
                        usuarioOpt.setFotoPerfil(nombreArchivo);
                    }
                    
                    if (usuario.getContrasenaHash() != null && !usuario.getContrasenaHash().isEmpty()) {
                        usuarioOpt.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
                    }

                    usuarioService.guardar(usuarioOpt);
                    actualizarTutorOTutorado(usuarioOpt, carreraId);
                    
                    attributes.addFlashAttribute("msg", "Registro existente en estado inactivo encontrado. El usuario ha sido reactivado y actualizado exitosamente.");
                    return "redirect:" + resolverRedirectPorPerfil(usuarioOpt);
                } else {
                    attributes.addFlashAttribute("error", "El número de identificación ya está registrado y se encuentra activo.");
                    return "redirect:/usuario/nuevo";
                }
            }

            if (usuarioService.existeCorreo(usuario.getCorreo())) {
                attributes.addFlashAttribute("error", "El correo ya está registrado.");
                return "redirect:/usuario/nuevo";
            }
        } else {
            usuarioExistente = usuarioService.buscarPorId(usuario.getId());
            if (!usuarioExistente.getNumeroIdentificacion().equals(usuario.getNumeroIdentificacion())) {
                Optional<Usuario> o = usuarioService.buscarPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
                if (o.isPresent()) {
                    attributes.addFlashAttribute("error", "El número de identificación ya está registrado.");
                    return "redirect:/usuario/editar/" + usuario.getId();
                }
            }
            if (!usuarioExistente.getCorreo().equals(usuario.getCorreo()) &&
                    usuarioService.existeCorreo(usuario.getCorreo())) {
                attributes.addFlashAttribute("error", "El correo ya está registrado.");
                return "redirect:/usuario/editar/" + usuario.getId();
            }
            // Mantener contraseña y otros valores si es edición
            usuario.setContrasenaHash(usuarioExistente.getContrasenaHash());
            
            // Mantener fotoPerfil actual si no se sube una nueva
            if (archivoFoto.isEmpty()) {
                usuario.setFotoPerfil(usuarioExistente.getFotoPerfil());
            }
        }

        if (!archivoFoto.isEmpty()) {
            String nombreArchivo = servicioAlmacenamiento.guardar(archivoFoto, "usuarios");
            usuario.setFotoPerfil(nombreArchivo);
        }

        if (esNuevo) {
            usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash())); 
        }

        usuarioService.guardar(usuario);
        actualizarTutorOTutorado(usuario, carreraId);
        
        attributes.addFlashAttribute("msg", "Usuario guardado exitosamente");
        return "redirect:" + resolverRedirectPorPerfil(usuario);
    }

    private String resolverRedirectPorPerfil(Usuario usuario) {
        if (usuario.hasPerfil("TUTORADO")) return "/usuario/tutorados";
        if (usuario.hasPerfil("TUTOR")) return "/usuario/tutores";
        if (usuario.hasPerfil("COORDINADOR_CARRERA")) return "/usuario/coordinadores";
        return "/usuario/administradores";
    }

    private void actualizarTutorOTutorado(Usuario usuario, Integer carreraId) {
        if (usuario.hasPerfil("TUTOR")) {
            if (tutorService.buscarPorUsuario(usuario).isEmpty()) {
                Tutor tutor = new Tutor();
                tutor.setUsuario(usuario);
                tutorService.guardar(tutor);
            }
        }
        if (usuario.hasPerfil("TUTORADO")) {
            Tutorado tutorado = tutoradoService.buscarPorUsuario(usuario).orElse(new Tutorado());
            tutorado.setUsuario(usuario);
            if (carreraId != null) {
                tutorado.setCarrera(carreraService.buscarPorId(carreraId));
            }
            tutoradoService.guardar(tutorado);
        }
        if (usuario.hasPerfil("COORDINADOR_CARRERA")) {
            CoordinadorCarrera coordinador = coordinadorCarreraService.buscarPorUsuario(usuario).orElse(new CoordinadorCarrera());
            coordinador.setUsuario(usuario);
            if (carreraId != null) {
                coordinador.setCarrera(carreraService.buscarPorId(carreraId));
            }
            coordinadorCarreraService.guardar(coordinador);
        }
    }

    @GetMapping("/usuario/ver/{id}")
    public String detalleUsuario(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        // Pasar el rol para el botón "Volver al listado"
        model.addAttribute("rolUsuario", resolverRedirectPorPerfil(usuario));
        if (usuario.hasPerfil("TUTORADO")) {
            Optional<Tutorado> tutoradoOpt = tutoradoService.buscarPorUsuario(usuario);
            if (tutoradoOpt.isPresent() && tutoradoOpt.get().getCarrera() != null) {
                model.addAttribute("carreraNombre", tutoradoOpt.get().getCarrera().getNombre());
            }
        }
        if (usuario.hasPerfil("COORDINADOR_CARRERA")) {
            coordinadorCarreraService.buscarPorUsuario(usuario).ifPresent(c ->
                model.addAttribute("carreraNombre", c.getCarrera().getNombre())
            );
        }
        return "usuario/detalleUsuario";
    }

    @GetMapping("/usuario/editar/{id}")
    public String formularioEditarUsuario(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("perfiles", perfilService.buscarTodos());
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        model.addAttribute("rolUsuario", resolverRedirectPorPerfil(usuario));
        
        Optional<Tutorado> tutoradoOpt = tutoradoService.buscarPorUsuario(usuario);
        if (tutoradoOpt.isPresent() && tutoradoOpt.get().getCarrera() != null) {
            model.addAttribute("carreraId", tutoradoOpt.get().getCarrera().getId());
        }
        Optional<CoordinadorCarrera> coordOpt = coordinadorCarreraService.buscarPorUsuario(usuario);
        if (coordOpt.isPresent() && coordOpt.get().getCarrera() != null) {
            model.addAttribute("carreraId", coordOpt.get().getCarrera().getId());
        }
        
        return "usuario/formUsuario";
    }

    @GetMapping(value = "/usuario/verificar-inactivo/{numeroIdentificacion}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> verificarUsuario(@PathVariable("numeroIdentificacion") String numeroIdentificacion) {
        Optional<Usuario> o = usuarioService.buscarPorNumeroIdentificacion(numeroIdentificacion);
        if (o.isPresent()) {
            Usuario u = o.get();
            if (u.getEstado() == Usuario.EstadoUsuario.inactivo) {
                Map<String, Object> response = new HashMap<>();
                response.put("encontrado", true);
                response.put("id", u.getId());
                response.put("nombreCompleto", u.getNombreCompleto());
                response.put("correo", u.getCorreo());
                
                String pNombre = "";
                if (u.getPerfiles() != null && !u.getPerfiles().isEmpty()) {
                    pNombre = u.getPerfiles().iterator().next().getNombre();
                }
                response.put("perfil", pNombre);
                
                Optional<Tutorado> tutoradoOpt = tutoradoService.buscarPorUsuario(u);
                if (tutoradoOpt.isPresent() && tutoradoOpt.get().getCarrera() != null) {
                    response.put("carrera", tutoradoOpt.get().getCarrera().getId());
                } else {
                    // También buscar carrera de coordinador
                    Optional<CoordinadorCarrera> coordOpt = coordinadorCarreraService.buscarPorUsuario(u);
                    if (coordOpt.isPresent() && coordOpt.get().getCarrera() != null) {
                        response.put("carrera", coordOpt.get().getCarrera().getId());
                    } else {
                        response.put("carrera", "");
                    }
                }
                
                response.put("fotoPerfil", u.getFotoPerfil());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.ok(Collections.singletonMap("encontrado", false));
    }

    @GetMapping("/usuario/desactivar/{id}")
    public String desactivarUsuario(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u != null) {
            u.setEstado(Usuario.EstadoUsuario.inactivo);
            usuarioService.guardar(u);
            attributes.addFlashAttribute("msg", "Usuario desactivado exitosamente");
            return "redirect:" + resolverRedirectPorPerfil(u);
        }
        return "redirect:/usuario/administradores";
    }
}
