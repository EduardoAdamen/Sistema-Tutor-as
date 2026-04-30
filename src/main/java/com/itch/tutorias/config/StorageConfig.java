package com.itch.tutorias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Expone los archivos guardados en C:/Evidencias/ bajo la URL /uploads/{subcarpeta}/**
 * Ejemplo: /uploads/sesiones/uuid-archivo.jpg  →  C:/Evidencias/sesiones/uuid-archivo.jpg
 */
@Configuration
public class StorageConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation;
    
    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns("/css/**", "/images/**", "/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Carpeta de usuarios (fotos de perfil)
        registry.addResourceHandler("/uploads/usuarios/**")
                .addResourceLocations("file:///" + storageLocation + "/usuarios/");

        // Carpeta de evidencias de sesiones
        registry.addResourceHandler("/uploads/sesiones/**")
                .addResourceLocations("file:///" + storageLocation + "/sesiones/");

        // Carpeta de evidencias de actividades PAT
        registry.addResourceHandler("/uploads/actividades/**")
                .addResourceLocations("file:///" + storageLocation + "/actividades/");
    }
}
