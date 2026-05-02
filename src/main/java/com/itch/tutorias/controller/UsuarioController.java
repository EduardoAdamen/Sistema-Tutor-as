package com.itch.tutorias.controller;

import com.itch.tutorias.model.Carrera;
import com.itch.tutorias.model.Perfil;
import com.itch.tutorias.model.Tutor;
import com.itch.tutorias.model.Tutorado;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.ICarrera;
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
    public String listaUsuarios(
            @RequestParam(required = false) String perfilNombre,
            @RequestParam(required = false) Integer carreraId,
            @RequestParam(required = false) Usuario.EstadoUsuario estado,
            Model model) {

        List<Usuario> listado = usuarioService.buscarTodos().stream()
                .filter(u -> u.getEstado() == Usuario.EstadoUsuario.activo)
                .collect(Collectors.toList());

        if (perfilNombre != null && !perfilNombre.isEmpty()) {
            listado = listado.stream().filter(u -> u.hasPerfil(perfilNombre)).collect(Collectors.toList());
        }
        if (carreraId != null) {
            listado = listado.stream().filter(u -> {
                Optional<Tutorado> tOpt = tutoradoService.buscarPorUsuario(u);
                return tOpt.isPresent() && tOpt.get().getCarrera() != null && tOpt.get().getCarrera().getId().equals(carreraId);
            }).collect(Collectors.toList());
        }
        if (estado != null) {
            listado = listado.stream().filter(u -> u.getEstado() == estado).collect(Collectors.toList());
        }

        model.addAttribute("usuarios", listado);
        model.addAttribute("perfiles", perfilService.buscarTodos());
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("filtroPerfilNombre", perfilNombre);
        model.addAttribute("filtroCarreraId", carreraId);
        model.addAttribute("filtroEstado", estado);

        return "usuario/listaUsuarios";
    }

    @GetMapping("/usuario/nuevo")
    public String formularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("perfiles", perfilService.buscarTodos());
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
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
                // Here we could find by id from a real implementation, 
                // but let's assume we find it from service
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
                    return "redirect:/usuario/usuarios";
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
        return "redirect:/usuario/usuarios";
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
    }

    @GetMapping("/usuario/ver/{id}")
    public String detalleUsuario(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        return "usuario/detalleUsuario";
    }

    @GetMapping("/usuario/editar/{id}")
    public String formularioEditarUsuario(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("carreras", carreraService.buscarTodas());
        model.addAttribute("perfiles", perfilService.buscarTodos());
        model.addAttribute("estados", Usuario.EstadoUsuario.values());
        
        Optional<Tutorado> tutoradoOpt = tutoradoService.buscarPorUsuario(usuario);
        if (tutoradoOpt.isPresent() && tutoradoOpt.get().getCarrera() != null) {
            model.addAttribute("carreraId", tutoradoOpt.get().getCarrera().getId());
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
                    response.put("carrera", "");
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
        }
        return "redirect:/usuario/usuarios";
    }
}
