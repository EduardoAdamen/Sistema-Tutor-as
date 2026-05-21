package com.itch.tutorias.config;

import com.itch.tutorias.model.Usuario;
import com.itch.tutorias.service.IUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;
import java.util.Optional;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private IUsuario usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            HttpSession session = request.getSession();
            if (session.getAttribute("usuarioLogueado") == null) {
                Optional<Usuario> usuarioOpt = usuarioService.buscarPorNumeroIdentificacion(principal.getName());
                if (usuarioOpt.isPresent()) {
                    session.setAttribute("usuarioLogueado", usuarioOpt.get());
                }
            }
        }
        return true;
    }
}
