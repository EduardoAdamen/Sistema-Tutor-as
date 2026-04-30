package com.itch.tutorias.service.implementjpa;

import com.itch.tutorias.service.IServicioAlmacenamiento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ServicioAlmacenamientoImpl implements IServicioAlmacenamiento {

    @Value("${storage.location}")
    private String storageLocation;

    @Override
    public String guardar(MultipartFile archivo, String subcarpeta) {
        try {
            Path dirDestino = Paths.get(storageLocation, subcarpeta);
            Files.createDirectories(dirDestino);

            String extension = "";
            String nombreOriginal = archivo.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreUnico = UUID.randomUUID().toString() + extension;
            Path destino = dirDestino.resolve(nombreUnico);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nombreUnico;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String nombreArchivo, String subcarpeta) {
        try {
            Path archivo = Paths.get(storageLocation, subcarpeta, nombreArchivo);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo: " + e.getMessage(), e);
        }
    }
}
