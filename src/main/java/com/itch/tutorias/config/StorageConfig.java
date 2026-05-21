package com.itch.tutorias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;

/**
 * Expone los archivos guardados en storage.location bajo la URL /uploads/{subcarpeta}/**.
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
                .addResourceLocations(resourceLocation("usuarios"));

        // Carpeta de evidencias de sesiones
        registry.addResourceHandler("/uploads/sesiones/**")
                .addResourceLocations(resourceLocation("sesiones"));

        // Carpeta de evidencias de actividades PAT
        registry.addResourceHandler("/uploads/actividades/**")
                .addResourceLocations(resourceLocation("actividades"));
    }

    private String resourceLocation(String subcarpeta) {
        String location = Paths.get(storageLocation, subcarpeta).toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }
}
