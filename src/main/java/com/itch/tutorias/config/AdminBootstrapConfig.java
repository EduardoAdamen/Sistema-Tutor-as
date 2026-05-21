package com.itch.tutorias.config;

import com.itch.tutorias.model.Perfil;
import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.repository.PerfilRepository;
import com.itch.tutorias.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AdminBootstrapConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    private static final List<String> PERFILES_BASE = List.of(
            "ADMINISTRADOR",
            "TUTOR",
            "TUTORADO",
            "COORDINADOR_CARRERA"
    );

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap-enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.email:admin@itch.edu.mx}")
    private String adminEmail;

    @Value("${app.admin.full-name:Administrador del Sistema}")
    private String adminFullName;

    @Value("${app.admin.reset-password:false}")
    private boolean resetAdminPassword;

    public AdminBootstrapConfig(
            PerfilRepository perfilRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        PERFILES_BASE.forEach(this::obtenerOCrearPerfil);

        if (!bootstrapEnabled) {
            logger.info("Bootstrap del administrador inicial deshabilitado.");
            return;
        }

        Perfil perfilAdmin = obtenerOCrearPerfil("ADMINISTRADOR");
        Usuario admin = obtenerOCrearAdmin(perfilAdmin);

        boolean actualizado = false;
        if (!admin.hasPerfil("ADMINISTRADOR")) {
            Set<Perfil> perfiles = new HashSet<>(admin.getPerfiles());
            perfiles.add(perfilAdmin);
            admin.setPerfiles(perfiles);
            actualizado = true;
        }

        if (resetAdminPassword && StringUtils.hasText(adminPassword)) {
            admin.setContrasenaHash(passwordEncoder.encode(adminPassword));
            actualizado = true;
        }

        if (admin.getContrasenaHash() == null || !admin.getContrasenaHash().startsWith("{")) {
            logger.warn(
                    "El administrador {} tiene un hash sin prefijo de algoritmo. " +
                    "Define APP_ADMIN_PASSWORD y APP_ADMIN_RESET_PASSWORD=true una sola vez para normalizarlo.",
                    adminUsername
            );
        }

        if (actualizado) {
            usuarioRepository.save(admin);
            logger.info("Usuario administrador inicial verificado/actualizado: {}", adminUsername);
        }
    }

    private Perfil obtenerOCrearPerfil(String nombre) {
        return perfilRepository.findByNombre(nombre)
                .orElseGet(() -> perfilRepository.save(new Perfil(nombre)));
    }

    private Usuario obtenerOCrearAdmin(Perfil perfilAdmin) {
        return usuarioRepository.findByNumeroIdentificacion(adminUsername)
                .orElseGet(() -> usuarioRepository.findByCorreo(adminEmail)
                        .filter(usuario -> usuario.hasPerfil("ADMINISTRADOR"))
                        .map(usuario -> migrarUsernameAdmin(usuario, adminUsername))
                        .orElseGet(() -> crearAdminInicial(perfilAdmin)));
    }

    private Usuario migrarUsernameAdmin(Usuario admin, String nuevoUsername) {
        logger.info("Migrando username del administrador inicial de {} a {}", admin.getNumeroIdentificacion(), nuevoUsername);
        admin.setNumeroIdentificacion(nuevoUsername);
        return usuarioRepository.save(admin);
    }

    private Usuario crearAdminInicial(Perfil perfilAdmin) {
        if (!StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "No existe el administrador inicial y APP_ADMIN_PASSWORD no fue configurada. " +
                    "Define APP_ADMIN_PASSWORD como variable de entorno antes de arrancar en una base nueva."
            );
        }

        Usuario admin = new Usuario();
        admin.setNumeroIdentificacion(adminUsername);
        admin.setNombreCompleto(adminFullName);
        admin.setCorreo(adminEmail);
        admin.setContrasenaHash(passwordEncoder.encode(adminPassword));
        admin.setEstado(Usuario.EstadoUsuario.activo);
        admin.setPerfiles(Set.of(perfilAdmin));
        return usuarioRepository.save(admin);
    }
}
